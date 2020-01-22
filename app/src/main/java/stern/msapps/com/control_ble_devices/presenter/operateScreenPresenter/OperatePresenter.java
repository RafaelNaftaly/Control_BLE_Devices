package stern.msapps.com.control_ble_devices.presenter.operateScreenPresenter;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import androidx.navigation.Navigation;

import stern.msapps.com.control_ble_devices.model.ble.BLEDeviceConnectionManager;
import stern.msapps.com.control_ble_devices.model.dataTypes.SternProduct;
import stern.msapps.com.control_ble_devices.presenter.BasePresenter;
import stern.msapps.com.control_ble_devices.R;
import stern.msapps.com.control_ble_devices.model.dataTypes.Day;
import stern.msapps.com.control_ble_devices.model.dataTypes.FoamSoapDispenser;
import stern.msapps.com.control_ble_devices.model.dataTypes.OperatePreset;
import stern.msapps.com.control_ble_devices.model.dataTypes.Schedule;
import stern.msapps.com.control_ble_devices.model.dataTypes.ScheduledDay;
import stern.msapps.com.control_ble_devices.model.dataTypes.ScheduledHygieneFlashRandomDay;
import stern.msapps.com.control_ble_devices.model.dataTypes.ScheduledStandByRandomDay;
import stern.msapps.com.control_ble_devices.model.dataTypes.SoapDispenser;
import stern.msapps.com.control_ble_devices.model.dataTypes.User;
import stern.msapps.com.control_ble_devices.model.enums.DaysOfWeek;
import stern.msapps.com.control_ble_devices.model.enums.ScheduledDataTypes;
import stern.msapps.com.control_ble_devices.eventBus.Events;
import stern.msapps.com.control_ble_devices.eventBus.GlobalBus;
import stern.msapps.com.control_ble_devices.model.ble.BLEGattAttributes;
import stern.msapps.com.control_ble_devices.model.roomDataBase.database.SternRoomDatabase;
import stern.msapps.com.control_ble_devices.utils.AppSharedPref;
import stern.msapps.com.control_ble_devices.utils.Constants;
import stern.msapps.com.control_ble_devices.utils.DialogHelper;
import stern.msapps.com.control_ble_devices.utils.dataParser.BleDataParser;
import stern.msapps.com.control_ble_devices.view.activities.MainActivity;
import stern.msapps.com.control_ble_devices.view.adapters.DaysAdapter;
import stern.msapps.com.control_ble_devices.view.adapters.ScheduledItemAdapter;
import stern.msapps.com.control_ble_devices.view.fragments.OperatePresetsFragment;
import stern.msapps.com.control_ble_devices.view.fragments.StatisticsFragment;

import static stern.msapps.com.control_ble_devices.model.enums.ScheduledDataTypes.HYGIENE_FLUSH;
import static stern.msapps.com.control_ble_devices.model.enums.ScheduledDataTypes.STANDBY_MODE;

public class OperatePresenter extends BasePresenter<OperateContract.View> implements OperateContract.Presenter, View.OnClickListener {


    private final int weekMinutes = ((60 * 24) * 7);

    //TODO... Unregister all registered notifications !!!!!!!!!!!!!!!
    private final String TAG = OperatePresenter.class.getSimpleName();
    private final static int bleDataIdentifireInitial = 30;

    private AppCompatActivity context;
    private SternProduct sternProduct;
    private DaysAdapter hygieneDaysAdapter, standByDaysAdapter;
    private RecyclerView hygieneDaysRecyclerView, standByDaysRecyclerView;
    private ArrayList<DaysOfWeek> hygieneDayOfWeeksArr, stanByDayOfWeeksArr;
    private static LinearLayoutManager daysLinearLayout;
    private ScheduledItemAdapter scheduledHygieneFlashItemAdapter, scheduledStanByItemAdapter;
    private LinearLayoutManager linearLayoutManager;
    private Day.ScheduledData deletedScheduledDay;
    private DaysOfWeek selectedDayOfweek;
    private Object tag;

    private ArrayList<BLEDeviceConnectionManager.DataClass> sendingDataArr = new ArrayList<>();


    public OperatePresenter(Context context) {

        this.context = (AppCompatActivity) context;
        hygieneDayOfWeeksArr = new ArrayList<>();
        stanByDayOfWeeksArr = new ArrayList<>();


    }

    @Override
    public void getScheduledDataFromDevice() {
        ArrayList<DataTest> sternTempClass = retriveHandleIDsFromStorage();

        for (DataTest tempClass : sternTempClass) {


            if (tempClass.getMacAddress().equals(sternProduct.getMacAddress()))
                readEvent(tempClass.getScheduledType(), tempClass.handleID, BleDataIdentifier.READ_SCHEDULED_EVENTS_FROM_DEVICE);
        }


    }

    @Override
    public void parseHygieneFlushLastActivation(String data) {
        Log.d(TAG, "");

        int handleID = BleDataParser.getInstance().getEventHandleID(data);

        sternProduct.getScheduledHygieneflush().getFromLastActivationHours().setHandleID(handleID);
        sternProduct.getScheduledHygieneflush().getFromLastActivationHours().setWasSent(true);
        saveDeleteHandleIDsInStorage(BLEDeviceConnectionManager.getInstance().getmBluetoothDevice().getAddress(), handleID, false, ScheduledDataTypes.HYGIENE_FROM_LAST_ACTIVATION);
    }

    @Override
    public void parseHygieneFlushDays(String data) {
        Log.d(TAG, "");

        //TODO... if there is no last parameter ID - this is not a new event, must set the data to object.
        int handleID = BleDataParser.getInstance().getEventHandleID(data);

        int sentID = BleDataParser.getInstance().getScheduledSendedID(data);

        for (ScheduledDay scheduledDay : sternProduct.getScheduledHygieneflush().getScheduledDaysArr()) {
            for (Day.ScheduledData scheduledData : scheduledDay.getScheduledDataArrayList()) {
                if (scheduledData.getHandleID() == sentID) {
                    scheduledData.setHandleID(handleID);
                    Log.d("DELETETEST", "parseHygieneFlushDays SvcheduledEvetID = " + handleID);
                    saveDeleteHandleIDsInStorage(BLEDeviceConnectionManager.getInstance().getmBluetoothDevice().getAddress(), handleID, false, ScheduledDataTypes.HYGIENE_FLUSH);
                    scheduledData.setWasSent(true);
                }
            }
        }


    }

    @Override
    public void parseStandByDays(String data) {
        //TODO... if there is no last parameter ID - this is not a new event, must set the data to object.
        int handleID = BleDataParser.getInstance().getEventHandleID(data);


        int sentID = BleDataParser.getInstance().getScheduledSendedID(data);

        for (ScheduledDay scheduledDay : sternProduct.getScheduledStandByMode().getScheduledDaysArr()) {
            for (Day.ScheduledData scheduledData : scheduledDay.getScheduledDataArrayList()) {
                if (scheduledData.getHandleID() == sentID) {
                    scheduledData.setHandleID(handleID);
                    Log.d("DELETETEST", "parseStandByDays SvcheduledEvetID = " + handleID);
                    saveDeleteHandleIDsInStorage(BLEDeviceConnectionManager.getInstance().getmBluetoothDevice().getAddress(), handleID, false, ScheduledDataTypes.STANDBY_MODE);
                    scheduledData.setWasSent(true);
                }
            }
        }

    }

    @Override
    public void parseHygieneFlushRandomDays(String data) {
        int handleID = BleDataParser.getInstance().getEventHandleID(data);


        int sentID = BleDataParser.getInstance().getScheduledSendedID(data);

        for (Day.ScheduledData scheduledData : sternProduct.getScheduledHygieneFlashRandomDay().getScheduledDataArrayList()) {
            if (scheduledData.getHandleID() == sentID) {
                scheduledData.setHandleID(handleID);
                Log.d("DELETETEST", "parseHygieneFlushRandomDays SvcheduledEvetID = " + handleID);
                saveDeleteHandleIDsInStorage(BLEDeviceConnectionManager.getInstance().getmBluetoothDevice().getAddress(), handleID, false, ScheduledDataTypes.HYGIENE_FLUSH);
                scheduledData.setWasSent(true);
            }

        }
    }

    @Override
    public void parseStandByRandomDays(String data) {
        int handleID = BleDataParser.getInstance().getEventHandleID(data);

        int sentID = BleDataParser.getInstance().getScheduledSendedID(data);

        for (Day.ScheduledData scheduledData : sternProduct.getScheduledStandByRandomDay().getScheduledDataArrayList()) {
            if (scheduledData.getHandleID() == sentID) {
                scheduledData.setHandleID(handleID);
                Log.d("DELETETEST", "parseStandByRandomDays SvcheduledEvetID = " + handleID);
                saveDeleteHandleIDsInStorage(BLEDeviceConnectionManager.getInstance().getmBluetoothDevice().getAddress(), handleID, false, ScheduledDataTypes.STANDBY_MODE);
                scheduledData.setWasSent(true);
            }

        }
    }


    @Override
    public void parseNotificationTapState(String data) {

        int tapState = Integer.parseInt(data.trim(), 16);

        Log.e("IDTEST", "The tap state is = " + tapState);

        if (tapState == 1) {
            //CLOSED
            getmView().getOpenCloseValveButton().setText(context.getString(R.string.operate_screen_turn_on_now));
            getmView().getOpenCloseValveButton().setSelected(false);

        } else if (tapState == 2) {
            //OPENED
            getmView().getOpenCloseValveButton().setText(context.getString(R.string.operate_screen_turn_off_now));
            getmView().getOpenCloseValveButton().setSelected(true);
        } else if (tapState == 0) {

            DialogHelper.getInstance().dismiss();


            new Handler().postDelayed(() -> {
                ((MainActivity) getmView().getActivityContext()).getBottomNavigationView().getMenu().getItem(1).setChecked(true);
                ((MainActivity) getmView().getActivityContext()).onBackPressed();
                DialogHelper.getInstance().dismiss();
                DialogHelper.getInstance().displayOneButtonDialog(context, context.getResources().getString(R.string.dialog_disconnect),
                        getmView().getContext().getResources().getString(R.string.name_passkey_ok)).show();

            }, 1500);


        }
    }

    @Override
    public void parseDeleteEvent(String data) {
        Log.d(TAG, "");
        int handleID = BleDataParser.getInstance().getEventHandleID(data);

        if (handleID == deletedScheduledDay.getHandleID()) {
            Snackbar.make(getmView().getView(), "The Event may not be deleted", Snackbar.LENGTH_SHORT).show();
            return;
        }
        ScheduledDataTypes scheduledDataTypes = deletedScheduledDay.getType();
        // saveDeleteHandleIDsInStorage(handleID, true);

        handleID = (int) deletedScheduledDay.getHandleID();


        switch (scheduledDataTypes) {
            case STANDBY_MODE:


                if (sternProduct.getScheduledStandByRandomDay() == null || !sternProduct.getScheduledStandByRandomDay().deleteEventByID(handleID)) {
                    sternProduct.getScheduledStandByMode().deleteEventByID(handleID);

                }
                scheduledStanByItemAdapter.notifyDataSetChanged();
                break;

            case HYGIENE_FLUSH:

                if (sternProduct.getScheduledHygieneFlashRandomDay() == null || !sternProduct.getScheduledHygieneFlashRandomDay().deleteEventByID(handleID)) {
                    sternProduct.getScheduledHygieneflush().deleteEventByID(handleID);

                }
                scheduledHygieneFlashItemAdapter.notifyDataSetChanged();

                break;
        }


    }

    @Override
    public void parseReadDeviceEvent(String data) {


        Day.ScheduledData scheduledData = BleDataParser.getInstance().getScheduledDataFromBytes(data);

        if (scheduledData == null) {
            deleteSharedPrefScheduledEvents(sternProduct.getMacAddress());
            return;
        }
        Calendar calendar = Calendar.getInstance();

        new Date(scheduledData.getTime());
        calendar.setTime(new Date(scheduledData.getTime()));
        DaysOfWeek daysOfWeek = DaysOfWeek.getDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK));

        switch (scheduledData.getType()) {

            case HYGIENE_FLUSH:

                setReadScheduledDaysData(calendar, daysOfWeek, scheduledData, sternProduct.getScheduledHygieneflush(), scheduledHygieneFlashItemAdapter);


                return;

            case STANDBY_MODE:

                setReadScheduledDaysData(calendar, daysOfWeek, scheduledData, sternProduct.getScheduledStandByMode(), scheduledStanByItemAdapter);

                return;

            case HYGIENE_RANDOM_DAY:
                setReadScheduledRandomDaysData(sternProduct.getScheduledHygieneFlashRandomDay(), scheduledHygieneFlashItemAdapter, scheduledData, calendar, ScheduledDataTypes.HYGIENE_RANDOM_DAY);

                return;

            case STANDBY_RANDOM_DAY:
                setReadScheduledRandomDaysData(sternProduct.getScheduledStandByRandomDay(), scheduledStanByItemAdapter, scheduledData, calendar, ScheduledDataTypes.STANDBY_RANDOM_DAY);
                return;

            case STANDBY_FROM_LAST_ACTIVATION:
                setReadScheduledFromLastActovation(scheduledData.getDuration(), scheduledData.getTime(), scheduledData.getType());
                return;

            case HYGIENE_FROM_LAST_ACTIVATION:
                setReadScheduledFromLastActovation(scheduledData.getDuration(), scheduledData.getTime(), scheduledData.getType());
                return;
        }


    }

    public void setReadScheduledRandomDaysData(Day day, ScheduledItemAdapter adapter, Day.ScheduledData scheduledData, Calendar calendar, ScheduledDataTypes dataTypes) {
        if (day == null || !day.isBothDaysAreSet()) {

            if (day == null) {
                if (scheduledData.getType() == ScheduledDataTypes.HYGIENE_RANDOM_DAY) {
                    day = new ScheduledHygieneFlashRandomDay(scheduledData);
                    sternProduct.setScheduledHygieneFlashRandomDay((ScheduledHygieneFlashRandomDay) day);
                } else {
                    day = new ScheduledStandByRandomDay(scheduledData);
                    sternProduct.setScheduledStandByRandomDay((ScheduledStandByRandomDay) day);
                }


            } else {
                day.addScheduleData(scheduledData);
            }

        } else if (day.isBothDaysAreSet()) {
            Snackbar.make(getmView().getView(), R.string.operate_screen_scheduled_no_more_room, Snackbar.LENGTH_SHORT).show();
            adapter.setDay(day);

        }
        adapter.notifyDataSetChanged();
        adapter.setDay(day);
    }

    @Override
    public void setReadScheduledDaysData(Calendar calendar, DaysOfWeek daysOfWeek, Day.ScheduledData scheduledData, Schedule schedualeMode, ScheduledItemAdapter adapter) {

        if (schedualeMode.getScheduledDaysArr().isEmpty()) {
            schedualeMode.getScheduledDaysArr().add(new ScheduledDay(daysOfWeek, scheduledData.getDuration(), scheduledData.getTime(), scheduledData.getType(), scheduledData.getHandleID(), true));

        } else {
            if (schedualeMode.containsDay(daysOfWeek)) {
                ScheduledDay scheduledDay = schedualeMode.getScheduledDay(daysOfWeek);
                if (!scheduledDay.isBothDaysAreSet()) {
                    if (scheduledDay.getScheduledDataArrayList().isEmpty()) {
                        scheduledDay.getScheduledDataArrayList().add(scheduledData);
                    } else {
                        for (Day.ScheduledData sc : scheduledDay.getScheduledDataArrayList()) {
                            if (sc.getHandleID() != scheduledData.getHandleID()) {
                                scheduledDay.getScheduledDataArrayList().add(scheduledData);
                                break;
                            }
                        }
                    }

                }
            } else {
                schedualeMode.getScheduledDaysArr().add(new ScheduledDay(daysOfWeek, scheduledData.getDuration(), scheduledData.getTime(), scheduledData.getType(), scheduledData.getHandleID(), true));

            }
        }
        adapter.notifyDataSetChanged();
        adapter.setDay(schedualeMode.getScheduledDay(daysOfWeek));
    }


    @Override
    public void openCloseValve(View v) {

        boolean hygiene = false;

        if (getmView().getHygieneView() != null) {
            if (v.getTag() == getmView().getHygieneView()) {
                hygiene = true;
            }
        } else {
            if (v.getTag().equals(getmView().getHygieneViewTag())) {
                hygiene = true;
            }
        }

        if (hygiene) {
            if (getmView().getHygieneFlushDuration() == 0) {
                Snackbar.make(getmView().getView(), R.string.operate_screen_scheduled_duration_is_zero, Snackbar.LENGTH_SHORT).show();
            } else {
                v.setSelected(!v.isSelected());
                if (v.isSelected()) {
                    ((TextView) v).setText(context.getString(R.string.operate_screen_turn_off_now));
                    ((TextView) v).setEnabled(false);
                    sendOpenCloseValveData(getmView().getOpenCloseValveDuration());


                } else {
                    ((TextView) v).setText(context.getString(R.string.operate_turn_on));
                    ((TextView) v).setEnabled(false);
                    closeValve();
                }
            }
        } else {
            if (getmView().getStandByoPenCloseDuration() == 0) {
                Snackbar.make(getmView().getView(), R.string.operate_screen_scheduled_duration_is_zero, Snackbar.LENGTH_SHORT).show();
            } else {
                sendStandByImidiatlyDuration(getmView().getStandByoPenCloseDuration());
            }
        }

    }



    @Override
    public void setHygieneDaysRecyclerView(RecyclerView hygieneDaysRecyclerView) {
        this.hygieneDaysRecyclerView = hygieneDaysRecyclerView;
        //this.hygieneDaysRecyclerView.setNestedScrollingEnabled(false);
        daysLinearLayout = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        this.hygieneDaysRecyclerView.setLayoutManager(daysLinearLayout);
        hygieneDaysAdapter = new DaysAdapter(this, HYGIENE_FLUSH);
        this.hygieneDaysRecyclerView.setAdapter(hygieneDaysAdapter);


    }

    @Override
    public void setStandByDaysRecyclerView(RecyclerView standByDaysRecyclerView) {
        this.standByDaysRecyclerView = standByDaysRecyclerView;
        daysLinearLayout = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        this.standByDaysRecyclerView.setLayoutManager(daysLinearLayout);
        standByDaysAdapter = new DaysAdapter(this, STANDBY_MODE);
        this.standByDaysRecyclerView.setAdapter(standByDaysAdapter);


    }

    @Override
    public void setScheduledHygieneRecyclerView(RecyclerView scheduledHygieneRecyclerView) {
        linearLayoutManager = new LinearLayoutManager(context);

        scheduledHygieneRecyclerView.setLayoutManager(linearLayoutManager);

        scheduledHygieneFlashItemAdapter = new ScheduledItemAdapter(OperatePresenter.this, HYGIENE_FLUSH);
        scheduledHygieneRecyclerView.setAdapter(scheduledHygieneFlashItemAdapter);
    }

    @Override
    public void setScheduledStandByRecyclerView(RecyclerView scheduledStandByRecyclerView) {

        linearLayoutManager = new LinearLayoutManager(context);

        scheduledStandByRecyclerView.setLayoutManager(linearLayoutManager);

        scheduledStanByItemAdapter = new ScheduledItemAdapter(OperatePresenter.this, STANDBY_MODE);
        scheduledStandByRecyclerView.setAdapter(scheduledStanByItemAdapter);

    }




    @Override
    public void setValeStateNotification(boolean enableNotification) {
        ArrayList<BLEDeviceConnectionManager.DataClass.CharacteristicIO> characteristicI = null;

        BLEDeviceConnectionManager.DataClass notify = new BLEDeviceConnectionManager.DataClass();
        notify.setRequestID(OperatePresenter.BleDataIdentifier.OPEN_CLOSE_VALVE_NOTIFICATION.value);
        characteristicI = new ArrayList<>();
        characteristicI.add(BLEDeviceConnectionManager.DataClass.CharacteristicIO.NOTIFICATION);
        notify.setEnableNotification(enableNotification);
        notify.setAddToDaSize(false);
        notify.setCharacteristicIOArrayList(characteristicI);
        notify.setServiceUUid(UUID.fromString(BLEGattAttributes.UUID_STERN_DATA_OPERATE_SERVICE));
        notify.setCharacteristicsUUid(UUID.fromString(BLEGattAttributes.UUID_STERN_DATA_OPEN_CLOSE_VALVE_CHARACTIRISTICS_NOTIFICATION));
        sendingDataArr.add(notify);

        sendData(sendingDataArr, false);

    }


    @Override
    public void onDaySelected(DaysOfWeek dayOfWeek, ScheduledDataTypes scheduledType) {


        if (scheduledType == HYGIENE_FLUSH) {

            scheduledHygieneFlashItemAdapter.setDay(sternProduct.getScheduledHygieneflush().getScheduledDay(dayOfWeek));



        } else {

            scheduledStanByItemAdapter.setDay(sternProduct.getScheduledStandByMode().getScheduledDay(dayOfWeek));
        }

        this.selectedDayOfweek = dayOfWeek;
    }

    @Override
    public void settingDataOnDateSelected(DaysOfWeek dayOfWeek, Schedule scheduled, ScheduledItemAdapter adapter, long duration, ScheduledDataTypes type) {

        if (scheduled.isRoom(dayOfWeek)) {
            if (duration != 0) {

                DialogHelper.getInstance().timePicker(getmView().getContext(), (year, month, dayOfMonth, hour, minutes, seconds, isYesClicked) -> {
                    Log.d(TAG, "dlvhg");

                    Calendar calendar = BleDataParser.getInstance().getCalendar(year, month - 1, dayOfMonth, hour, minutes, seconds);
                    calendar = getCalendarBySpecificDat(dayOfWeek, calendar);


                    scheduled.setScheduledDaysArr(dayOfWeek, duration, calendar.getTimeInMillis(), type);
                    adapter.setDay(scheduled.getScheduledDay(dayOfWeek));

                    Log.d(TAG, "The Scheduled time is = " + calendar.getTimeInMillis());

                    Snackbar.make(getmView().getView(), R.string.operate_screen_scheduled_successfully_added, Snackbar.LENGTH_SHORT).show();

                }, null);
            } else {//If the value of StandBy is 0 display SnackBar
                Snackbar.make(getmView().getView(), R.string.operate_screen_scheduled_duration_is_zero, Snackbar.LENGTH_SHORT).show();
            }
        } else {
            Snackbar.make(getmView().getView(), R.string.operate_screen_scheduled_no_more_room, Snackbar.LENGTH_SHORT).show();
        }


        adapter.setDay(scheduled.getScheduledDay(dayOfWeek));
    }

    @Override
    public void setReadScheduledFromLastActovation(long duration, long time, ScheduledDataTypes dataTypes) {


        switch (dataTypes) {
            case HYGIENE_FROM_LAST_ACTIVATION:
                getmView().setHygieneFlushFromLastActivationDurationSeekBarValue((int) duration);
                getmView().setHygieneFlushFromLastActivationDurationTV(String.valueOf(duration));
                getmView().setHygieneFlushFromLastActivationSeekBarValue((int) time);
                getmView().setHygieneFlushFromLastActivationRadioButton(true);
                break;

            case STANDBY_FROM_LAST_ACTIVATION:
                getmView().setStandByFromLastActivationDurationSeekBarValue((int) duration);
                getmView().setStandByFromLastActivationDurationTV(String.valueOf(duration));
                getmView().setStandByFromLastActivationSeekBarValue((int) time);
                getmView().setStandByromLastActivationRadioButton(true);
                break;
        }
    }


    @Subscribe(sticky = true)
    public void getSternProduct(Events.SterProductTransmition sterProduct) {
        this.sternProduct = sterProduct.getSternProduct();

        if (User.getUserInstance().getUserType().equals(User.UserType.TECHNICIAN)) {
            getScheduledDataFromDevice();
        }
        getmView().setTitle(this.sternProduct.getType().getName());

        changeTextViewTextByObjectType(sternProduct);


    }

    @Subscribe(sticky = true)
    public void getOperatePreset(Events.OperatePresetTransmission operatePreset) {

        OperatePreset op = operatePreset.getOperatePreset();

        Calendar calendar = Calendar.getInstance();


        for (ScheduledDay scheduledDay : op.getScheduledData()) {
            for (Day.ScheduledData scheduledData : scheduledDay.getScheduledDataArrayList()) {
                scheduledData.setWasSent(false);
                switch (scheduledData.getType()) {
                    case HYGIENE_FLUSH:
                        setReadScheduledDaysData(calendar, scheduledDay.getDayOfWeek(), scheduledData, sternProduct.getScheduledHygieneflush(), scheduledHygieneFlashItemAdapter);
                        break;

                    case STANDBY_MODE:
                        setReadScheduledDaysData(calendar, scheduledDay.getDayOfWeek(), scheduledData, sternProduct.getScheduledStandByMode(), scheduledStanByItemAdapter);
                        break;
                }
            }
        }


    }


    @Override
    public void onStart() {

    }

    @Override
    public void onResume() {
        GlobalBus.getBus().register(this);
    }

    @Override
    public void onPause() {
        GlobalBus.getBus().unregister(this);
        Log.d(TAG, "onPause()");
        //TODO.... Unregister notifications
    }



    @Override
    public OperatePresenter.DataTest getFromLastActivationID() {
        ArrayList<DataTest> data = retriveHandleIDsFromStorage();

        if (data.isEmpty()) {
            return null;
        }

        for (int i = 0; i < data.size(); i++) {
            DataTest dataTest = data.get(i);
            if (dataTest.getScheduledType().equals(ScheduledDataTypes.HYGIENE_FROM_LAST_ACTIVATION)) {
                return dataTest;
            }
        }

        return null;
    }


    @Override
    public void onClickRadioButton(View v) {

        //If it's Hygiene
        if (v.getTag() == getmView().getHygieneView()) {
            if (!((AppCompatCheckBox) v).isChecked()) {
                getmView().setHygieneFlushFromLastActivationTimeTV("0");
                getmView().setHygieneFlushFromLastActivationSeekBarValue(0);
                getmView().setHygieneFlushFromLastActivationDurationTV("0");
                getmView().setHygieneFlushFromLastActivationDurationSeekBarValue(0);
                sternProduct.getScheduledHygieneflush().setFromLastActivationHours(-1, -1, ScheduledDataTypes.HYGIENE_FLUSH);
            } else {
                if (Integer.parseInt(getmView().getHygieneFlushFromLastActivationTimeTV().getText().toString()) == 0
                        || Integer.parseInt(getmView().getHygieneFlushFromLastActivationDurationTV().getText().toString()) == 0) {
                    Snackbar.make(getmView().getView(), R.string.operate_screen_scheduled_duration_is_zero, Snackbar.LENGTH_SHORT).show();
                    ((AppCompatCheckBox) v).setChecked(false);
                } else {
                    sternProduct.getScheduledHygieneflush().setFromLastActivationHours(Long.parseLong(getmView().getHygieneFlushFromLastActivationTimeTV().getText().toString()),
                            Long.valueOf(getmView().getHygieneFlushFromLastActivationDurationTV().getText().toString()), ScheduledDataTypes.HYGIENE_FLUSH);
                }
            }

        } else { // If it StandBy
            if (!((AppCompatCheckBox) v).isChecked()) {
                getmView().setStanByFromLastActivationDurationTV("0");
                getmView().setStandByFromLastActivationSeekBarValue(0);
                sternProduct.getScheduledStandByMode().setFromLastActivationHours(-1, -1, ScheduledDataTypes.STANDBY_MODE);
            } else {
                if (Integer.parseInt(getmView().getStandByFromLastActivationDurationTV().getText().toString()) == 0) {
                    Snackbar.make(getmView().getView(), R.string.operate_screen_scheduled_duration_is_zero, Snackbar.LENGTH_SHORT).show();
                    ((AppCompatCheckBox) v).setChecked(false);
                }
            }

        }
    }

    @Override
    public void setHygieneDatePicker() {


        if (sternProduct.getScheduledHygieneFlashRandomDay() == null || !((Day) sternProduct.getScheduledHygieneFlashRandomDay()).isBothDaysAreSet()) {

            if (getmView().getFlashScheduledDuration() != 0) {

                DialogHelper.getInstance().dateTimeDialog(context, (year, month, dayOfMonth, hour, minutes, seconds, isYesClicked) -> {

                    Calendar calendar = BleDataParser.getInstance().getCalendar(year, month - 1, dayOfMonth, hour, minutes, seconds);


                    if (sternProduct.getScheduledHygieneFlashRandomDay() == null) {
                        sternProduct.setScheduledHygieneFlashRandomDay(new ScheduledHygieneFlashRandomDay(getmView().getFlashScheduledDuration(),
                                calendar.getTimeInMillis(), ScheduledDataTypes.HYGIENE_FLUSH));
                    } else {

                        sternProduct.getScheduledHygieneFlashRandomDay().adTime(getmView().getFlashScheduledDuration(),
                                calendar.getTimeInMillis(), ScheduledDataTypes.HYGIENE_FLUSH);

                    }

                    scheduledHygieneFlashItemAdapter.setDay(sternProduct.getScheduledHygieneFlashRandomDay());
                });
            } else {
                Snackbar.make(getmView().getView(), R.string.operate_screen_scheduled_duration_is_zero, Snackbar.LENGTH_SHORT).show();
            }
        } else if (sternProduct.getScheduledHygieneFlashRandomDay().isBothDaysAreSet()) {
            scheduledHygieneFlashItemAdapter.setDay(sternProduct.getScheduledHygieneFlashRandomDay());
            Snackbar.make(getmView().getView(), R.string.operate_screen_scheduled_no_more_room_random_day, Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void setStandByDatePicker() {
        if (sternProduct.getScheduledStandByRandomDay() == null || !sternProduct.getScheduledStandByRandomDay().isBothDaysAreSet()) {

            if (getmView().getStandByScheduledDuration() != 0) {

                DialogHelper.getInstance().dateTimeDialog(context, (year, month, dayOfMonth, hour, minutes, seconds, isYesClicked) -> {

                    Calendar calendar = BleDataParser.getInstance().getCalendar(year, month - 1, dayOfMonth, hour, minutes, seconds);


                    if (sternProduct.getScheduledStandByRandomDay() == null) {
                        sternProduct.setScheduledStandByRandomDay(new ScheduledStandByRandomDay(getmView().getStandByScheduledDuration(),
                                calendar.getTimeInMillis(), ScheduledDataTypes.STANDBY_MODE));
                    } else {

                        sternProduct.getScheduledStandByRandomDay().adTime(getmView().getStandByScheduledDuration(),
                                calendar.getTimeInMillis(), ScheduledDataTypes.STANDBY_MODE);

                    }

                    scheduledStanByItemAdapter.setDay(sternProduct.getScheduledStandByRandomDay());
                });
            } else {
                Snackbar.make(getmView().getView(), R.string.operate_screen_scheduled_duration_is_zero, Snackbar.LENGTH_SHORT).show();
            }
        } else if (sternProduct.getScheduledStandByRandomDay().isBothDaysAreSet()) {
            scheduledStanByItemAdapter.setDay(sternProduct.getScheduledStandByRandomDay());
            Snackbar.make(getmView().getView(), R.string.operate_screen_scheduled_no_more_room_random_day, Snackbar.LENGTH_SHORT).show();
        }
    }


    @Override
    public void sendOpenCloseValveData(long durationVal) {

        setValeStateNotification(true);

        new Handler().postDelayed(() -> {
            byte[] sendData = new byte[2];
            sendData[0] = BleDataParser.getInstance().hexStringToByteArray(String.format("%02X", (0xFFFFFF & durationVal)))[0];


            ArrayList<BLEDeviceConnectionManager.DataClass.CharacteristicIO> characteristicIO = null;

            BLEDeviceConnectionManager.DataClass calendarEvent = new BLEDeviceConnectionManager.DataClass();
            calendarEvent.setRequestID(BleDataIdentifier.VALVE_OPEN_CLOSE_STATE.value);
            characteristicIO = new ArrayList<>();
            characteristicIO.add(BLEDeviceConnectionManager.DataClass.CharacteristicIO.WRITABLE);
            calendarEvent.setCharacteristicIOArrayList(characteristicIO);
            calendarEvent.setAddToDaSize(true);
            calendarEvent.setServiceUUid(UUID.fromString(BLEGattAttributes.UUID_STERN_DATA_OPERATE_SERVICE));
            calendarEvent.setCharacteristicsUUid(UUID.fromString(BLEGattAttributes.UUID_STERN_DATA_OPEN_CLOSE_VALVE_CHARACTIRISTICS_WRITE));
            calendarEvent.setData(sendData);
            sendingDataArr.add(calendarEvent);

            sendData(sendingDataArr, false);
        }, 1300);




    }

    @Override
    public void sendStandByImidiatlyDuration(long duration) {

        Day.ScheduledData scheduledData = new Day.ScheduledData(duration, Calendar.getInstance().getTimeInMillis(), ScheduledDataTypes.STANDBY_MODE);

        sendScheduled(ScheduledDataTypes.STANDBY_MODE, false, false, false, true, scheduledData);


    }

    @Override
    public void prepareScheduledDataToSend(ScheduledDataTypes scheduledDataTypes) {


        switch (scheduledDataTypes) {
            case HYGIENE_FLUSH:
                //If there is a scheduled data for Last Activation.
                if (sternProduct.getScheduledHygieneflush().getFromLastActivationHours() != null) {

                    Day.ScheduledData scheduledData = sternProduct.getScheduledHygieneflush().getFromLastActivationHours();
                    if (!scheduledData.isWasSent()) {
                        sendScheduled(scheduledDataTypes,
                                false, false, true, false, scheduledData);
                    }
                }

                //Scheduled data by Days of week
                if (!sternProduct.getScheduledHygieneflush().getScheduledDaysArr().isEmpty()) {
                    for (ScheduledDay scheduledDay : sternProduct.getScheduledHygieneflush().getScheduledDaysArr()) {
                        for (Day.ScheduledData scheduledData : scheduledDay.getScheduledDataArrayList()) {
                            //Checking if the event was already sent
                            if (!scheduledData.isWasSent()) {
                                sendScheduled(scheduledDataTypes,
                                        false, true, false, false, scheduledData);
                            }
                        }
                    }
                }
                //Scheduled data by specific day
                if (sternProduct.getScheduledHygieneFlashRandomDay() != null && !sternProduct.getScheduledHygieneFlashRandomDay().getScheduledDataArrayList().isEmpty()) {


                    for (Day.ScheduledData scheduledData : sternProduct.getScheduledHygieneFlashRandomDay().getScheduledDataArrayList()) {

                        if (!scheduledData.isWasSent()) {
                            sendScheduled(scheduledDataTypes, true, false,
                                    false, false, scheduledData);
                        }
                    }

                }
                return;

            case STANDBY_MODE:
                //Scheduled data by Days of week
                if (!sternProduct.getScheduledStandByMode().getScheduledDaysArr().isEmpty()) {
                    for (ScheduledDay scheduledDay : sternProduct.getScheduledStandByMode().getScheduledDaysArr()) {
                        for (Day.ScheduledData scheduledData : scheduledDay.getScheduledDataArrayList()) {
                            if (!scheduledData.isWasSent()) {
                                sendScheduled(scheduledDataTypes, false, true,
                                        false, false, scheduledData);
                            }
                        }
                    }
                }
                //Scheduled data by specific day
                if (sternProduct.getScheduledStandByRandomDay() != null && !sternProduct.getScheduledStandByRandomDay().getScheduledDataArrayList().isEmpty()) {
                    for (Day.ScheduledData scheduledData : sternProduct.getScheduledStandByRandomDay().getScheduledDataArrayList()) {

                        if (!scheduledData.isWasSent()) {
                            sendScheduled(scheduledDataTypes, true, false,
                                    false, false, scheduledData);
                        }
                    }
                }
                return;
        }


    }




    @Override
    public void sendScheduled(ScheduledDataTypes scheduledDataTypes, boolean isRandomDay,
                              boolean isDays, boolean isLastActivation, boolean immediatelyStandBy, Day.ScheduledData scheduledData) {

        byte[] sendingData = new byte[14];
        Calendar calendar = (getCalendarFromMilisec(scheduledData.getTime()) != null) ? getCalendarFromMilisec(scheduledData.getTime()) : Calendar.getInstance();
        int month = -1;
        int type = -1;
        byte firstByteRepeat = -1;
        byte secondByteRepeat = -1;
        BleDataIdentifier bleDataIdentifier = null;
        BLEDeviceConnectionManager.DataClass.CharacteristicIO characteristicIO = null;


        int writeOrRead = 1;


        switch (scheduledDataTypes) {
            case HYGIENE_FLUSH:
                type = 2;
                characteristicIO = BLEDeviceConnectionManager.DataClass.CharacteristicIO.WRITABLE;
                if (isRandomDay) {
                    firstByteRepeat = BleDataParser.getInstance().hexStringToByteArray(String.format("%02X", (0xFFFFFF & 0)))[0];
                    secondByteRepeat = BleDataParser.getInstance().hexStringToByteArray(String.format("%02X", (0xFFFFFF & 0)))[0];
                    month = calendar.get(Calendar.MONTH) + 1;
                    bleDataIdentifier = BleDataIdentifier.SCHEDULED_HYGIENE_FLASH_RANDOM_DAY;

                } else if (isDays) {
                    month = calendar.get(Calendar.MONTH) + 1;
                    bleDataIdentifier = BleDataIdentifier.SCHEDULED_HYGIENE_FLASH_DAYS;
                    firstByteRepeat = 60;
                    secondByteRepeat = 27;
                } else if (isLastActivation) {
                    month = 99;

                    long fromLastActivation = scheduledData.getTime() * 60;
                    byte[] repeted = BleDataParser.getInstance().longToHexArr(fromLastActivation);

                    firstByteRepeat = repeted[0];
                    secondByteRepeat = repeted[1];
                    bleDataIdentifier = BleDataIdentifier.SCHEDULED_HYGIENE_FLASH_LAST_ACTIVATION;
                }

                break;

            case STANDBY_MODE:
                type = 3;
                characteristicIO = BLEDeviceConnectionManager.DataClass.CharacteristicIO.WRITABLE;
                if (isRandomDay) {
                    firstByteRepeat = BleDataParser.getInstance().hexStringToByteArray(String.format("%02X", (0xFFFFFF & 0)))[0];
                    secondByteRepeat = BleDataParser.getInstance().hexStringToByteArray(String.format("%02X", (0xFFFFFF & 0)))[0];
                    month = calendar.get(Calendar.MONTH) + 1;
                    bleDataIdentifier = BleDataIdentifier.SCHEDULED_STANDBY_MODE_RANDOM_DAY;
                } else if (isDays) {
                    month = calendar.get(Calendar.MONTH) + 1;
                    bleDataIdentifier = BleDataIdentifier.SCHEDULED_STANDBY_MODE_DAYS;
                    firstByteRepeat = 60;
                    secondByteRepeat = 27;

                } else if (immediatelyStandBy) {

                    //TODO... Check with Niv
                    firstByteRepeat = BleDataParser.getInstance().hexStringToByteArray(String.format("%02X", (0xFFFFFF & 0)))[0];
                    secondByteRepeat = BleDataParser.getInstance().hexStringToByteArray(String.format("%02X", (0xFFFFFF & 0)))[0];
                    bleDataIdentifier = BleDataIdentifier.SCHEDULED_STANDBY_MODE_ONCE;
                }
                break;

        }


        sendingData[0] = BleDataParser.getInstance().hexStringToByteArray(String.format("%02X", (0xFFFFFF & writeOrRead)))[0];
        sendingData[1] = BleDataParser.getInstance().hexStringToByteArray(String.format("%02X", (0xFFFFFF & type)))[0];
        sendingData[2] = BleDataParser.getInstance().hexStringToByteArray(String.format("%02X", (0xFFFFFF & scheduledData.getDuration())))[0];
        sendingData[3] = firstByteRepeat;
        sendingData[4] = secondByteRepeat;
        sendingData[5] = BleDataParser.getInstance().hexStringToByteArray(String.format("%02X", (0xFFFFFF & calendar.get(Calendar.SECOND))))[0];
        sendingData[6] = BleDataParser.getInstance().hexStringToByteArray(String.format("%02X", (0xFFFFFF & calendar.get(Calendar.MINUTE))))[0];
        sendingData[7] = BleDataParser.getInstance().hexStringToByteArray(String.format("%02X", (0xFFFFFF & calendar.get(Calendar.HOUR_OF_DAY))))[0];
        sendingData[8] = BleDataParser.getInstance().hexStringToByteArray(String.format("%02X", (0xFFFFFF & calendar.get(Calendar.DAY_OF_MONTH))))[0];
        sendingData[9] = BleDataParser.getInstance().hexStringToByteArray(String.format("%02X", (0xFFFFFF & (month))))[0];
        sendingData[10] = BleDataParser.getInstance().hexStringToByteArray(String.format("%02X", (0xFFFFFF & (calendar.get(Calendar.YEAR) - 2000))))[0];
        sendingData[13] = BleDataParser.getInstance().hexStringToByteArray(String.format("%02X", (0xFFFFFF & scheduledData.getHandleID())))[0];


        ArrayList<BLEDeviceConnectionManager.DataClass.CharacteristicIO> characteristicIOArr = null;

        BLEDeviceConnectionManager.DataClass calendarEvent = new BLEDeviceConnectionManager.DataClass();
        calendarEvent.setRequestID(bleDataIdentifier.value);
        characteristicIOArr = new ArrayList<>();
        calendarEvent.setAddToDaSize(true);
        characteristicIOArr.add(characteristicIO);
        calendarEvent.setCharacteristicIOArrayList(characteristicIOArr);
        calendarEvent.setServiceUUid(UUID.fromString(BLEGattAttributes.UUID_STERN_DATA_INFORMATION_SERVICE));
        calendarEvent.setCharacteristicsUUid(UUID.fromString(BLEGattAttributes.UUID_STERN_DATA_INFORMATION_SCHEDUALED_CHARACTERISTIC));
        calendarEvent.setData(sendingData);
        sendingDataArr.add(calendarEvent);

        sendData(sendingDataArr, true);

    }


    @Override
    public void deleteScheduledHygieneFlush(ScheduledDataTypes scheduledType, DaysOfWeek
            daysOfWeek, int position) {

        ScheduledDay scheduledDay = null;

        //TODO... Must delete from DataBase only when receive OK from device + identify if event was sent

        if (scheduledType == HYGIENE_FLUSH) {

            scheduledDay = (ScheduledDay) sternProduct.getScheduledHygieneflush().getScheduleDay(daysOfWeek, position);

        } else if (scheduledType == STANDBY_MODE) {
            scheduledDay = (ScheduledDay) sternProduct.getScheduledStandByMode().getScheduleDay(daysOfWeek, position);
        }

        Day.ScheduledData scheduledData = scheduledDay.getScheduledDataArrayList().get(position);

        if (scheduledData.isWasSent()) {
            long handleID = scheduledData.getHandleID();
            int type = -1;
            switch (scheduledType) {
                case HYGIENE_FLUSH:
                    type = 2;
                    break;

                case STANDBY_MODE:
                    type = 3;
                    break;

            }

            sendDeleteEvent(type, scheduledData);


            //Must send read to verify if the Event was deleted
            readEvent(scheduledType, handleID, BleDataIdentifier.CHECK_DELETE_EVENT_READ);
        } else {
            if (scheduledType == HYGIENE_FLUSH) {
                sternProduct.getScheduledHygieneflush().deleteScheduledEvent(daysOfWeek, position);
                scheduledHygieneFlashItemAdapter.notifyDataSetChanged();
            } else {
                sternProduct.getScheduledStandByMode().deleteScheduledEvent(daysOfWeek, position);
                scheduledStanByItemAdapter.notifyDataSetChanged();
            }

        }


    }

    @Override
    public void deleteScheduledRandomDay(ScheduledDataTypes scheduledType, int position) {

        Day.ScheduledData scheduledData = null;


        if (scheduledType == ScheduledDataTypes.HYGIENE_RANDOM_DAY) {

            scheduledData = sternProduct.getScheduledHygieneFlashRandomDay().getScheduledDataArrayList().get(position);
        } else {

            scheduledData = sternProduct.getScheduledStandByRandomDay().getScheduledDataArrayList().get(position);
        }

        if (scheduledData.isWasSent()) {
            if (scheduledType == ScheduledDataTypes.HYGIENE_RANDOM_DAY) {
                sendDeleteEvent(2, scheduledData);

            } else {
                sendDeleteEvent(3, scheduledData);
            }

            readEvent(scheduledType, scheduledData.getHandleID(), BleDataIdentifier.CHECK_DELETE_EVENT_READ);
        } else {
            if (scheduledType == ScheduledDataTypes.HYGIENE_RANDOM_DAY) {
                sternProduct.getScheduledHygieneFlashRandomDay().deleteEvent(position);
                scheduledHygieneFlashItemAdapter.notifyDataSetChanged();
            } else {
                sternProduct.getScheduledStandByRandomDay().deleteEvent(position);
                scheduledStanByItemAdapter.notifyDataSetChanged();
            }
        }


    }

    @Override
    public void sendDeleteEvent(int scheduledType, Day.ScheduledData scheduledData) {

        Log.d("DELETETEST", "DeleteEvetID = " + scheduledData.getHandleID());

        deletedScheduledDay = scheduledData;

        String hedHandleID = String.format("%04X", (0xFFFFFF & scheduledData.getHandleID()));
        String result = BleDataParser.getInstance().addSpaceEveryXchars(hedHandleID, "2");
        String[] dataID = result.split(" ");

        byte[] sendingData = new byte[4];
        sendingData[0] = BleDataParser.getInstance().hexStringToByteArray(String.format("%02X", (0xFFFFFF & 2)))[0];
        sendingData[1] = BleDataParser.getInstance().hexStringToByteArray(String.format("%02X", (0xFFFFFF & 0)))[0];

        if (dataID.length > 1) {
            if (dataID[0].equals("00")) {
                sendingData[2] = BleDataParser.getInstance().hexStringToByteArray(dataID[1])[0];
            } else {
                sendingData[3] = BleDataParser.getInstance().hexStringToByteArray(dataID[1])[0];
                sendingData[2] = BleDataParser.getInstance().hexStringToByteArray(dataID[0])[0];
            }

        } else {
            sendingData[2] = BleDataParser.getInstance().hexStringToByteArray(dataID[0])[0];
        }

        ArrayList<BLEDeviceConnectionManager.DataClass.CharacteristicIO> characteristicIO = null;

        BLEDeviceConnectionManager.DataClass deleteEvent = new BLEDeviceConnectionManager.DataClass();
        deleteEvent.setRequestID(BleDataIdentifier.DELETE_EVENT.value);
        characteristicIO = new ArrayList<>();
        deleteEvent.setAddToDaSize(true);
        characteristicIO.add(BLEDeviceConnectionManager.DataClass.CharacteristicIO.WRITABLE);
        deleteEvent.setCharacteristicIOArrayList(characteristicIO);
        deleteEvent.setServiceUUid(UUID.fromString(BLEGattAttributes.UUID_STERN_DATA_INFORMATION_SERVICE));
        deleteEvent.setCharacteristicsUUid(UUID.fromString(BLEGattAttributes.UUID_STERN_DATA_INFORMATION_SCHEDUALED_CHARACTERISTIC));
        deleteEvent.setData(sendingData);
        sendingDataArr.add(deleteEvent);

        sendData(sendingDataArr, true);
    }

    @Override
    public void sendDeleteEventById(int scheduledType, long handleId) {


        String hedHandleID = String.format("%04X", (0xFFFFFF & handleId));
        String result = BleDataParser.getInstance().addSpaceEveryXchars(hedHandleID, "2");
        String[] dataID = result.split(" ");

        byte[] sendingData = new byte[4];
        sendingData[0] = BleDataParser.getInstance().hexStringToByteArray(String.format("%02X", (0xFFFFFF & 2)))[0];
        sendingData[1] = BleDataParser.getInstance().hexStringToByteArray(String.format("%02X", (0xFFFFFF & 0)))[0];

        if (dataID.length > 1) {
            if (dataID[0].equals("00")) {
                sendingData[2] = BleDataParser.getInstance().hexStringToByteArray(dataID[1])[0];
            } else {
                sendingData[3] = BleDataParser.getInstance().hexStringToByteArray(dataID[1])[0];
                sendingData[2] = BleDataParser.getInstance().hexStringToByteArray(dataID[0])[0];
            }

        } else {
            sendingData[2] = BleDataParser.getInstance().hexStringToByteArray(dataID[0])[0];
        }

        ArrayList<BLEDeviceConnectionManager.DataClass.CharacteristicIO> characteristicIO = null;

        BLEDeviceConnectionManager.DataClass deleteEvent = new BLEDeviceConnectionManager.DataClass();
        deleteEvent.setRequestID(BleDataIdentifier.DELETE_EVENT.value);
        characteristicIO = new ArrayList<>();
        deleteEvent.setAddToDaSize(true);
        characteristicIO.add(BLEDeviceConnectionManager.DataClass.CharacteristicIO.WRITABLE);
        deleteEvent.setCharacteristicIOArrayList(characteristicIO);
        deleteEvent.setServiceUUid(UUID.fromString(BLEGattAttributes.UUID_STERN_DATA_INFORMATION_SERVICE));
        deleteEvent.setCharacteristicsUUid(UUID.fromString(BLEGattAttributes.UUID_STERN_DATA_INFORMATION_SCHEDUALED_CHARACTERISTIC));
        deleteEvent.setData(sendingData);
        sendingDataArr.add(deleteEvent);

        sendData(sendingDataArr, true);
    }



    @Override
    public void readEvent(ScheduledDataTypes scheduledType, long handleID, BleDataIdentifier bleDataIdentifier) {
        byte[] sendingData = new byte[4];
        int type = -1;
        long id = handleID - 1;

        Log.d("DELETETEST", "ReadteEvetID = " + id);

        String hedHandleID = String.format("%04X", (0xFFFFFF & id));

        String result = BleDataParser.getInstance().addSpaceEveryXchars(hedHandleID, "2");

        String[] dataID = result.split(" ");


        switch (scheduledType) {
            case HYGIENE_FLUSH:
            case HYGIENE_FROM_LAST_ACTIVATION:
                type = 2;
                break;

            case STANDBY_MODE:
            case STANDBY_FROM_LAST_ACTIVATION:
                type = 3;
                break;
        }


        sendingData[0] = BleDataParser.getInstance().hexStringToByteArray(String.format("%02X", (0xFFFFFF & 129)))[0];
        sendingData[1] = BleDataParser.getInstance().hexStringToByteArray(String.format("%02X", (0xFFFFFF & type)))[0];

        if (dataID.length > 1) {
            if (dataID[0].equals("00")) {
                sendingData[2] = BleDataParser.getInstance().hexStringToByteArray(dataID[1])[0];
            } else {
                sendingData[3] = BleDataParser.getInstance().hexStringToByteArray(dataID[1])[0];
                sendingData[2] = BleDataParser.getInstance().hexStringToByteArray(dataID[0])[0];
            }

        } else {
            sendingData[2] = BleDataParser.getInstance().hexStringToByteArray(dataID[0])[0];
        }


        ArrayList<BLEDeviceConnectionManager.DataClass.CharacteristicIO> characteristicIOArr = null;

        BLEDeviceConnectionManager.DataClass readEvent = new BLEDeviceConnectionManager.DataClass();
        readEvent.setRequestID(bleDataIdentifier.value);
        characteristicIOArr = new ArrayList<>();
        readEvent.setAddToDaSize(true);
        characteristicIOArr.add(BLEDeviceConnectionManager.DataClass.CharacteristicIO.WRITABLE);
        readEvent.setCharacteristicIOArrayList(characteristicIOArr);
        readEvent.setServiceUUid(UUID.fromString(BLEGattAttributes.UUID_STERN_DATA_INFORMATION_SERVICE));
        readEvent.setCharacteristicsUUid(UUID.fromString(BLEGattAttributes.UUID_STERN_DATA_INFORMATION_SCHEDUALED_CHARACTERISTIC));
        readEvent.setData(sendingData);
        sendingDataArr.add(readEvent);

        sendData(sendingDataArr, true);
    }


    /**
     * Sending a command to BLE device to close the Valve
     */
    @Override
    public void closeValve() {
        byte[] sendData = new byte[2];
        sendData[0] = BleDataParser.getInstance().hexStringToByteArray(String.format("%02X", (0xFFFFFF & 0)))[0];


        ArrayList<BLEDeviceConnectionManager.DataClass.CharacteristicIO> characteristicIO = null;

        BLEDeviceConnectionManager.DataClass calendarEvent = new BLEDeviceConnectionManager.DataClass();
        calendarEvent.setRequestID(OperatePresenter.BleDataIdentifier.VALVE_OPEN_CLOSE_STATE.value);
        characteristicIO = new ArrayList<>();
        calendarEvent.setAddToDaSize(false);
        characteristicIO.add(BLEDeviceConnectionManager.DataClass.CharacteristicIO.WRITABLE);
        calendarEvent.setCharacteristicIOArrayList(characteristicIO);
        calendarEvent.setServiceUUid(UUID.fromString(BLEGattAttributes.UUID_STERN_DATA_OPERATE_SERVICE));
        calendarEvent.setCharacteristicsUUid(UUID.fromString(BLEGattAttributes.UUID_STERN_DATA_OPEN_CLOSE_VALVE_CHARACTIRISTICS_WRITE));
        calendarEvent.setData(sendData);
        sendingDataArr.add(calendarEvent);

        sendData(sendingDataArr, false);




    }

    @Override
    public void handleOnScheduledBTNClick(android.view.View v) {

        if (v.getTag() == getmView().getHygieneView()) {//Hygiene clicked
            if (sternProduct.getScheduledHygieneflush().getScheduledDaysArr().isEmpty()
                    && sternProduct.getScheduledHygieneflush().getFromLastActivationHours() == null
                    && (sternProduct.getScheduledHygieneFlashRandomDay() == null
                    || sternProduct.getScheduledHygieneFlashRandomDay().getScheduledDataArrayList().isEmpty())) {
                //There is noting Scheduled
                Snackbar.make(getmView().getView(), R.string.operate_screen_no_scheduled, Snackbar.LENGTH_SHORT).show();
            } else {
                //TODO... send the Scheduled data to device...
                prepareScheduledDataToSend(HYGIENE_FLUSH);
            }
        } else {
            if (sternProduct.getScheduledStandByMode().getScheduledDaysArr().isEmpty()
                    && sternProduct.getScheduledStandByMode().getFromLastActivationHours() == null
                    && (sternProduct.getScheduledStandByRandomDay() == null
                    || sternProduct.getScheduledStandByRandomDay().getScheduledDataArrayList().isEmpty())) {
                //There is noting Scheduled
                Snackbar.make(getmView().getView(), R.string.operate_screen_no_scheduled, Snackbar.LENGTH_SHORT).show();
            } else {
                //TODO... send the Scheduled data to device...

                prepareScheduledDataToSend(STANDBY_MODE);
            }
        }

    }

    @Override
    public void changeTextViewTextByObjectType(SternProduct sternProduct) {

        if (sternProduct instanceof SoapDispenser || sternProduct instanceof FoamSoapDispenser) {

            getmView().changeOpenValveTextViewTextByObjectType(R.string.operate_fragment_refill_soap);
        } else {
            getmView().changeOpenValveTextViewTextByObjectType(R.string.operate_screen_open_valve_time);
        }

    }

    @Override
    public void saveNewPreset() {

        List<ScheduledDay> scheduledDays = new ArrayList<>();


        for (ScheduledDay scheduledDay : sternProduct.getScheduledHygieneflush().getScheduledDaysArr()) {
            for (Day.ScheduledData scheduledData : scheduledDay.getScheduledDataArrayList()) {
                if (scheduledData.isWasChosenForPreset()) {

                    scheduledDays.add(new ScheduledDay(scheduledDay.getDayOfWeek(), scheduledData.getDuration(), scheduledData.getTime(), scheduledData.getType()));
                }
            }
        }
        for (ScheduledDay scheduledDay : sternProduct.getScheduledStandByMode().getScheduledDaysArr()) {
            for (Day.ScheduledData scheduledData : scheduledDay.getScheduledDataArrayList()) {
                if (scheduledData.isWasChosenForPreset()) {

                    scheduledDays.add(new ScheduledDay(scheduledDay.getDayOfWeek(), scheduledData.getDuration(), scheduledData.getTime(), scheduledData.getType()));
                }
            }
        }

        if (!scheduledDays.isEmpty()) {
            Dialog dialog = DialogHelper.getInstance().displayCutomDialog(context, R.layout.costume_dialog_presets_name_request_new);


            dialog.show();
            EditText editText = dialog.findViewById(R.id.presets_dialog_name_request_et);

            TextView textView = dialog.findViewById(R.id.presets_dialog_save_button_tv);
            textView.setOnClickListener(v -> {

                if (editText.getText().toString().isEmpty()) {
                    editText.setError(getmView().getContext().getResources().getString(R.string.operate_presets_no_name_error));
                } else {

                    OperatePreset op = new OperatePreset();
                    op.setPresetName(editText.getText().toString());
                    op.setScheduledData(scheduledDays);
                    op.setSternType(sternProduct.getType());


                    SternRoomDatabase.getDatabase(getmView().getContext()).operatePresetDao().insert(op);

                    dialog.dismiss();

                    scheduledStanByItemAdapter.setDisplayCheckBox();
                    scheduledStanByItemAdapter.notifyDataSetChanged();

                    scheduledHygieneFlashItemAdapter.setDisplayCheckBox();
                    scheduledHygieneFlashItemAdapter.notifyDataSetChanged();

                    getmView().setEnableDisableNewPresetButton();


                }


            });

            //Cancel button
            dialog.findViewById(R.id.presets_dialog_cancel_button_tv).setOnClickListener(v -> dialog.dismiss());
        }


    }

    @Override
    public void loadPreset() {

        List<OperatePreset> operatePresets = SternRoomDatabase.getDatabase(getmView().getContext()).operatePresetDao().getPresetByType(sternProduct.getType());
        if (operatePresets.isEmpty()) {
            DialogHelper.getInstance().displayOneButtonDialog(getmView().getContext(),
                    getmView().getContext().getResources().getString(R.string.settings_preset_no_preset),
                    getmView().getContext().getResources().getString(R.string.name_passkey_ok)).show();

        } else {

            new OperatePresetsFragment().show(context.getSupportFragmentManager(), "operate_preset");
        }
    }

    @Override
    public void addNewPreset() {
        scheduledStanByItemAdapter.setDisplayCheckBox();
        scheduledStanByItemAdapter.notifyDataSetChanged();

        scheduledHygieneFlashItemAdapter.setDisplayCheckBox();
        scheduledHygieneFlashItemAdapter.notifyDataSetChanged();

    }




    @Override
    public Calendar getCalendarFromMilisec(long mSec) {

        Date date = new Date(mSec);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }





    @Override
    public void onDestroy() {
        GlobalBus.getBus().unregister(this);
        Log.d(TAG, "onDestroy()");
        //TODO.... Unregister notifications
    }

    @Override
    public void detach() {

        Log.d(TAG, "detach()");
        super.detach();
    }

    @Override
    public void onBleConnectionStatus(BLEDeviceConnectionManager.ConnectionStatus
                                              status, List<BluetoothGattService> bluetoothGattService) {
        switch (status) {
            case DISCONNECTED:
                //TODO... Handle Disconnection
                DialogHelper.getInstance().dismiss();


                DialogHelper.getInstance().displayOneButtonDialog(getmView().getContext(), getmView().getContext().getResources().getString(R.string.dialog_disconnect),
                        getmView().getContext().getResources().getString(R.string.name_passkey_ok)).show();


                if (!Navigation.findNavController((Activity) context, R.id.main_activity_host_fragment).getCurrentDestination().getLabel().equals("fragment_scanned_products")) {

                    ((Activity) context).onBackPressed();

                }

                return;
            case CONNECTED:
                //TODO... Handle Connected

                return;

            case NOT_FOUND:
                //TODO... Handle Not found
                return;

            case DISCONNECTING:
                //TODO... Handle Disconnecting
                if (!Navigation.findNavController((Activity) context, R.id.main_activity_host_fragment).getCurrentDestination().getLabel().equals("fragment_scanned_products")) {

                    ((Activity) context).onBackPressed();

                }
                return;

            case NOT_CONNECTED:
                //TODO... Handle Not Connected
                return;

            case RECONNECT:
                //TODO... Handle Reconnect
                return;


        }

    }


    @Override
    protected void parseOnDataReceived(String dataSTR, String uuid, int requestID) {

        Log.w("IDTEST", " ...........onDataReceived() " + requestID + "\n" + "The data is: " + dataSTR + "The characteristic UUID is " + uuid);


        if (requestID == BleDataIdentifier.OPEN_CLOSE_VALVE_NOTIFICATION.value) {

            parseNotificationTapState(dataSTR);
        }


        if (requestID == BleDataIdentifier.SCHEDULED_HYGIENE_FLASH_LAST_ACTIVATION.value) {
            parseHygieneFlushLastActivation(dataSTR);
        }

        if (requestID == BleDataIdentifier.SCHEDULED_HYGIENE_FLASH_DAYS.value) {
            parseHygieneFlushDays(dataSTR);


        }

        if (requestID == BleDataIdentifier.SCHEDULED_HYGIENE_FLASH_RANDOM_DAY.value) {
            parseHygieneFlushRandomDays(dataSTR);
        }

        if (requestID == BleDataIdentifier.SCHEDULED_STANDBY_MODE_DAYS.value) {
            parseStandByDays(dataSTR);
        }

        if (requestID == BleDataIdentifier.SCHEDULED_STANDBY_MODE_ONCE.value) {

        }

        if (requestID == BleDataIdentifier.SCHEDULED_STANDBY_MODE_RANDOM_DAY.value) {
            parseStandByRandomDays(dataSTR);
        }

        if (requestID == BleDataIdentifier.VALVE_OPEN_CLOSE_STATE.value) {
            getmView().getOpenCloseValveButton().setEnabled(true);
        }

        if (requestID == BleDataIdentifier.DELETE_EVENT.value) {
            Log.d(TAG, "");


        }

        if (requestID == BleDataIdentifier.CHECK_DELETE_EVENT_READ.value) {
            parseDeleteEvent(dataSTR);
        }
        if (requestID == BleDataIdentifier.READ_SCHEDULED_EVENTS_FROM_DEVICE.value) {
            parseReadDeviceEvent(dataSTR);
        }
        if (requestID == BleDataIdentifier.READ_STATISTICS.value) {


        }

        if (BLEDeviceConnectionManager.getInstance().getSendedDataSize() == 0) {

            //    DialogHelper.getInstance().dismiss();
            if (DialogHelper.getInstance().isDialogShown()) {
                DialogHelper.getInstance().dismiss();

            }

        }

    }


    @Override
    public void onScannedDeviceData(ScanResult scanResults) {

    }


    private void sendData(ArrayList<BLEDeviceConnectionManager.DataClass> sendingDataArr, boolean displayLoader) {


        if (displayLoader) {
            if (!DialogHelper.getInstance().isDialogShown()) {
                DialogHelper.getInstance().displayLoaderProgressDialog(context, context.getResources().getString(R.string.loading)).show();

            }
        }

        int arrSize = sendingDataArr.size();

        for (int i = 0; i < arrSize; i++) {

            BLEDeviceConnectionManager.DataClass dataClass = sendingDataArr.remove(0);
            Log.d("TEST", "The data ID that was sended is = " + dataClass.getRequestID());
            BLEDeviceConnectionManager.getInstance().sendData(dataClass, OperatePresenter.this);
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {


            case R.id.scheduled_turn_on_now_BTN:
            case R.id.scheduled_hygiene_turn_on_now_BTN:
            case R.id.scheduled_standBy_turn_on_now_BTN:


                openCloseValve(v);
                return;


            case R.id.choose_calendar_day_BTN:
                selectedDayOfweek = null;
                this.tag = v.getTag();
                //TODO... unCheck the days


                if (v.getTag() == getmView().getHygieneView()) {
                    scheduledHygieneFlashItemAdapter.setDay(sternProduct.getScheduledHygieneFlashRandomDay());

                    int lastClicked = hygieneDaysAdapter.getLastSelectedPosition();
                    if (lastClicked != -1) {
                        //unselect if dayOfWeek is selected
                        DaysAdapter.ViewHolder holder = (DaysAdapter.ViewHolder) hygieneDaysRecyclerView.findViewHolderForAdapterPosition(lastClicked);
                        holder.unSelect();
                    }

                } else {

                    scheduledStanByItemAdapter.setDay(sternProduct.getScheduledStandByRandomDay());
                    int lastClicked = standByDaysAdapter.getLastSelectedPosition();
                    if (lastClicked != -1) {
                        // deselect if dayOfWeek is selected
                        DaysAdapter.ViewHolder holder = (DaysAdapter.ViewHolder) standByDaysRecyclerView.findViewHolderForAdapterPosition(lastClicked);
                        holder.unSelect();
                    }
                }

                return;

            case R.id.operate_fragment_activate_radio_BTN:
                onClickRadioButton(v);
                break;

            case R.id.operate_fragment_scheduled_BTN:

                DataTest dataTest = getFromLastActivationID();


                // If the user want to delete fromLastActivation event
                if (sternProduct.getScheduledHygieneflush().getFromLastActivationHours() == null && dataTest != null) {
                    sendDeleteEventById(dataTest.getScheduledType().ordinal(), dataTest.getHandleID());
                    //TODO... delete from SharedPref
                    deleteSharedPrefScheduledEvents(dataTest.getMacAddress());
                } else {
                    handleOnScheduledBTNClick(v);
                }

                break;

            case R.id.operate_fragment_show_statistics_button_tv:


                new StatisticsFragment().show(context.getSupportFragmentManager(), "statistics");

                break;

            case R.id.operate_fragment_set_time_BTN:
                if (selectedDayOfweek != null) {
                    if (v.getTag() == getmView().getHygieneView()) {
                        settingDataOnDateSelected(selectedDayOfweek, sternProduct.getScheduledHygieneflush(), scheduledHygieneFlashItemAdapter, getmView().getFlashScheduledDuration(), ScheduledDataTypes.HYGIENE_FLUSH);

                    } else {

                        settingDataOnDateSelected(selectedDayOfweek, sternProduct.getScheduledStandByMode(), scheduledStanByItemAdapter, getmView().getStandByScheduledDuration(), ScheduledDataTypes.STANDBY_MODE);

                    }
                } else {
                    if (this.tag == getmView().getHygieneView()) {
                        setHygieneDatePicker();
                    } else {
                        setStandByDatePicker();
                    }
                }
                break;


            case R.id.single_item_settings_recycler_triple_button_save_preset_BTN:
                saveNewPreset();
                break;

            case R.id.single_item_settings_recycler_triple_button_load_preset_BTN:
                loadPreset();
                break;

            case R.id.single_item_settings_recycler_triple_button_new_preset_BTN:
                addNewPreset();
                getmView().setEnableDisableNewPresetButton();


                break;
        }

    }


    public enum BleDataIdentifier {

        OPEN_CLOSE_VALVE_NOTIFICATION(OperatePresenter.bleDataIdentifireInitial + 1),
        SCHEDULED_HYGIENE_FLASH_LAST_ACTIVATION(OperatePresenter.bleDataIdentifireInitial + 2),
        SCHEDULED_HYGIENE_FLASH_DAYS(OperatePresenter.bleDataIdentifireInitial + 3),
        SCHEDULED_HYGIENE_FLASH_RANDOM_DAY(OperatePresenter.bleDataIdentifireInitial + 4),
        SCHEDULED_STANDBY_MODE_DAYS(OperatePresenter.bleDataIdentifireInitial + 5),
        SCHEDULED_STANDBY_MODE_RANDOM_DAY(OperatePresenter.bleDataIdentifireInitial + 6),
        SCHEDULED_STANDBY_MODE_ONCE(OperatePresenter.bleDataIdentifireInitial + 7),
        VALVE_OPEN_CLOSE_STATE(OperatePresenter.bleDataIdentifireInitial + 8),
        DELETE_EVENT(OperatePresenter.bleDataIdentifireInitial + 9),
        CHECK_DELETE_EVENT_READ(OperatePresenter.bleDataIdentifireInitial + 10),
        READ_SCHEDULED_EVENTS_FROM_DEVICE(OperatePresenter.bleDataIdentifireInitial + 11),
        READ_STATISTICS(OperatePresenter.bleDataIdentifireInitial + 12);


        private int value;

        BleDataIdentifier(int val) {
            this.value = val;
        }


    }


    private Calendar getCalendarBySpecificDat(DaysOfWeek days, Calendar calendar) {


        if (days == DaysOfWeek.SUNDAY) {
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        } else if (days == DaysOfWeek.MONDAY) {
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        } else if (days == DaysOfWeek.TUESDAY) {
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
        } else if (days == DaysOfWeek.WEDNESDAY) {
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
        } else if (days == DaysOfWeek.THURSDAY) {
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
        } else if (days == DaysOfWeek.FRIDAY) {
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
        } else if (days == DaysOfWeek.SATURDAY) {
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        }


        return calendar;
    }


    public void saveDeleteHandleIDsInStorage(String macAddress, long handleID, boolean delete, ScheduledDataTypes scheduledType) {

        Log.d("DELETETEST", "The event " + handleID + " Was saved");

        Set<String> iDsSet = AppSharedPref.getInstance(context).getPrefSet(Constants.SHARED_PREF_USER_HANDLE_ID);
        if (!delete) {
            if (iDsSet == null) {
                iDsSet = new HashSet<>();

            }

            ArrayList<String> handleIDList = new ArrayList<>();
            handleIDList.addAll(iDsSet);
            handleIDList.add(String.valueOf(handleID) + "_" + String.valueOf(scheduledType.ordinal() + 2) + "_" + macAddress);
            iDsSet.addAll(handleIDList);

        } else {
            for (String id : iDsSet) {
                if (id.contains(String.valueOf(handleID))) {
                    iDsSet.remove(id);
                }
            }
        }
        AppSharedPref.getInstance(context).savePrefSet(Constants.SHARED_PREF_USER_HANDLE_ID, iDsSet);


    }

    private ArrayList<DataTest> retriveHandleIDsFromStorage() {
        ArrayList<Long> idsList = new ArrayList<>();
        ArrayList<DataTest> dataTests = new ArrayList<>();
        Set<String> IdsSet = AppSharedPref.getInstance(context).getPrefSet(Constants.SHARED_PREF_USER_HANDLE_ID);
        ArrayList<String> handleIDList = new ArrayList<>();
        if (IdsSet == null) {
            IdsSet = new HashSet<>();
        }
        handleIDList.addAll(IdsSet);
        if (!handleIDList.isEmpty()) {
            for (int i = 0; i < handleIDList.size(); i++) {
                DataTest dataTest = new DataTest();
                String s[] = handleIDList.get(i).split("_");
                dataTest.setHandleID(Long.valueOf(s[0]));
                dataTest.setScheduledType(ScheduledDataTypes.getType(Integer.valueOf(s[1])));
                dataTest.setMacAddress(s[2]);
                dataTests.add(dataTest);


            }
        }
        return dataTests;
    }

    public class DataTest {
        private long handleID;
        private ScheduledDataTypes scheduledType;
        private String macAddress;


        public long getHandleID() {
            return handleID;
        }

        public void setHandleID(long handleID) {
            this.handleID = handleID;
        }

        public ScheduledDataTypes getScheduledType() {
            return scheduledType;
        }

        public void setScheduledType(ScheduledDataTypes scheduledType) {
            this.scheduledType = scheduledType;
        }


        public String getMacAddress() {
            return macAddress;
        }

        public void setMacAddress(String macAddress) {
            this.macAddress = macAddress;
        }
    }


}
