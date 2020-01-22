package stern.msapps.com.control_ble_devices.presenter.settingPresenter;


import android.app.Dialog;


import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Build;

import android.os.Handler;


import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import java.util.UUID;

import androidx.navigation.Navigation;

import stern.msapps.com.control_ble_devices.model.ble.BluetoothLeService;
import stern.msapps.com.control_ble_devices.model.dataTypes.SternProduct;
import stern.msapps.com.control_ble_devices.model.dataTypes.ranges.Ranges;
import stern.msapps.com.control_ble_devices.presenter.BasePresenter;
import stern.msapps.com.control_ble_devices.R;
import stern.msapps.com.control_ble_devices.model.dataTypes.PresetSternProduct;
import stern.msapps.com.control_ble_devices.model.dataTypes.SoapDispenser;
import stern.msapps.com.control_ble_devices.model.enums.DosageButtons;
import stern.msapps.com.control_ble_devices.model.enums.RangeTypes;
import stern.msapps.com.control_ble_devices.model.enums.SettingButtons;
import stern.msapps.com.control_ble_devices.eventBus.Events;
import stern.msapps.com.control_ble_devices.eventBus.GlobalBus;
import stern.msapps.com.control_ble_devices.model.ble.BLEDeviceConnectionManager;
import stern.msapps.com.control_ble_devices.model.ble.BLEGattAttributes;
import stern.msapps.com.control_ble_devices.model.roomDataBase.database.SternRoomDatabase;
import stern.msapps.com.control_ble_devices.presenter.settingPresetPresenter.SettingPresetPresenter;
import stern.msapps.com.control_ble_devices.utils.DialogHelper;
import stern.msapps.com.control_ble_devices.utils.dataParser.SettingsScreenBleDataParser;
import stern.msapps.com.control_ble_devices.view.activities.MainActivity;
import stern.msapps.com.control_ble_devices.view.adapters.DosageButtonsAdapter;
import stern.msapps.com.control_ble_devices.view.adapters.SettingsButtonsAdapter;
import stern.msapps.com.control_ble_devices.view.adapters.SettingsSeekBarsAdapter;
import stern.msapps.com.control_ble_devices.view.fragments.SettingPresetsFragment;

import static stern.msapps.com.control_ble_devices.model.enums.RangeTypes.AIR_MOTOR;


public class SettingPresenter extends BasePresenter<SettingsContract.View> implements SettingsContract.Presenter, View.OnClickListener
        , SettingPresetPresenter.SettingsPresetCommunication, View.OnScrollChangeListener {


    private final String TAG = SettingPresenter.class.getSimpleName();
    private final static int bleDataIdentifireInitial = 200;
    private final static int bleWriteDataIdentifireInitial = 300;
    private final static int bleDataNotificationIdentifireInitial = 33;
    private final static int bleFactoryReset = 22;
    private int comunicationIssueCounter = 0;
    private RangeTypes badRequestDataType;

    private RecyclerView buttonsRecyclerView, seekBarRecyclerView, dosageRecyclerView;
    private LinearLayoutManager buttonsLayoutManager, seekBarsLayoutManager, dosageLayoutManager;
    private ArrayList<Ranges> rangesArrayList;
    private int DELAY_TiME = 2000;
    private final int DEFAULT_DELAY_TIME = 2000;
    private SternProduct sternProduct;
    private SettingsSeekBarsAdapter settingsSeekBarsAdapter;


    private SettingPresetPresenter presetPresenter;
    public static SettingPresenter settingPresenter;

    private ArrayList<BLEDeviceConnectionManager.DataClass> sendingDataArr = new ArrayList<>();
    private ArrayList<BLEDeviceConnectionManager.DataClass> requestRanges = new ArrayList<>();
    private AppCompatActivity context;

    public SettingPresenter(Context context) {

        this.context = (AppCompatActivity) context;

        settingPresenter = this;
        presetPresenter = new SettingPresetPresenter(this.context, sternProduct);


        Log.d("", "");


    }

    @Override
    public void onStart() {
        GlobalBus.getBus().register(this);
        Log.d(TAG, "onStart()");
    }

    @Override
    public void onViewCreated() {
        Log.d(TAG, "onViewCreated()");

    }

    @Override
    public void onPause() {
        GlobalBus.getBus().unregister(this);
        Log.d(TAG, "onPause()");
        //TODO.... Unregister notifications
    }

    @Override
    public void detach() {
        GlobalBus.getBus().unregister(this);
        Log.d(TAG, "detach()");
        super.detach();
    }

    @Override
    public void onDestroy() {
        GlobalBus.getBus().unregister(this);
        Log.d(TAG, "onDestroy()");
        //TODO.... Unregister notifications
    }

    @Override
    public void onButtonClicked(SettingButtons button) {
        Snackbar.make(getmView().getView(), button.getName(button), Snackbar.LENGTH_SHORT).show();

        switch (button) {
            case RESET_TO_FACTORY_SETTINGS:
                resetToFactorySettings();
                break;
        }

//        presetPresenter.savePresets(this.sternProduct, false);
//        SettingPresetPresenter presetPresenter = new SettingPresetPresenter(context, sternProduct);
//        ArrayList<SternProduct> sternProducts = presetPresenter.retrievePresetsArray();
    }

    @Override
    public void onDosageButtonClicked(DosageButtons dosageButton) {
        Snackbar.make(getmView().getView(), dosageButton.getSternDavaValue() + "", Snackbar.LENGTH_SHORT).show();

        if (sternProduct instanceof SoapDispenser) {
            ((SoapDispenser) sternProduct).setDosage(dosageButton.getSternDavaValue());
        }
    }

    @Override
    public void settButtonsRecyclerView(RecyclerView buttonsRecyclerView) {
        this.buttonsRecyclerView = buttonsRecyclerView;
        buttonsLayoutManager = new LinearLayoutManager(getmView().getActivityContext(), LinearLayoutManager.VERTICAL, false);
        this.buttonsRecyclerView.setLayoutManager(buttonsLayoutManager);
        this.buttonsRecyclerView.setAdapter(new SettingsButtonsAdapter(this));

    }

    @Override
    public void setSeekBarsRecyclerView(RecyclerView seekBarRecyclerView) {


        this.seekBarRecyclerView = seekBarRecyclerView;

        seekBarsLayoutManager = new LinearLayoutManager(getmView().getActivityContext(), LinearLayoutManager.VERTICAL, false);
        this.seekBarRecyclerView.setLayoutManager(seekBarsLayoutManager);

        this.seekBarRecyclerView.setOnScrollChangeListener(this);


    }

    @Override
    public void setDatatoSternObject(RangeTypes types) {


        for (int i = 0; i < sternProduct.getRangesArrayList().size(); i++) {
            Ranges ranges = sternProduct.getRangesArrayList().get(i);

            //Removing the received Range from Changed range ArrayList in adapter
            settingsSeekBarsAdapter.removeFromChanged(ranges);

            if (ranges.getRangeType().equals(types)) {
                Ranges range = settingsSeekBarsAdapter.getModifiedRanges().get(i);
                ranges.setNewRange(range);
            }
        }


    }

    @Override
    public void setDosageRecyclerView(RecyclerView dosageRecyclerView) {

        this.dosageRecyclerView = dosageRecyclerView;


    }

    @Override
    public BLEDeviceConnectionManager.DataClass setRequestDataClass(String service, String characteristics, SettingsProperties requestID) {


        byte[] sendData = new byte[1];
        sendData[0] = (byte) 0x81;
        ArrayList<BLEDeviceConnectionManager.DataClass.CharacteristicIO> characteristicIO = new ArrayList<>();
        BLEDeviceConnectionManager.DataClass readRequestRanges = new BLEDeviceConnectionManager.DataClass();
        readRequestRanges.setRequestID(requestID.value);
        characteristicIO.add(BLEDeviceConnectionManager.DataClass.CharacteristicIO.WRITABLE);
        readRequestRanges.setCharacteristicIOArrayList(characteristicIO);
        readRequestRanges.setData(sendData);
        readRequestRanges.setServiceUUid(UUID.fromString(service));
        readRequestRanges.setCharacteristicsUUid(UUID.fromString(characteristics));

        return readRequestRanges;
    }


    @Override
    public void addNewPreset() {
        //Snackbar.make(getmView().getView(), "ADD PRESET", Snackbar.LENGTH_SHORT).show();


        settingsSeekBarsAdapter.setDisplayCheckBox();


    }

    @Override
    public void loadPreset() {

        if (SternRoomDatabase.getDatabase(context).presetSternProductDao().getPresetByType(sternProduct.getType()).isEmpty()) {
            DialogHelper.getInstance().displayOneButtonDialog(getmView().getContext(),
                    getmView().getContext().getResources().getString(R.string.settings_preset_no_preset),
                    getmView().getContext().getResources().getString(R.string.name_passkey_ok)).show();
        } else {
            new SettingPresetsFragment().show(context.getSupportFragmentManager(), "preset");
        }


        //  Snackbar.make(getmView().getView(), "LOAD PRESET", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void saveNewPreset() {


        if (settingsSeekBarsAdapter.getSelectedRages().isEmpty()) {
            DialogHelper.getInstance().displayOneButtonDialog(getmView().getContext(),
                    getmView().getContext().getResources().getString(R.string.settings_preset_no_data_chosen),
                    getmView().getContext().getResources().getString(R.string.name_passkey_ok)).show();
        } else {

            Dialog dialog = DialogHelper.getInstance().displayCutomDialog(context, R.layout.costume_dialog_presets_name_request_new);


            dialog.show();
            EditText editText = dialog.findViewById(R.id.presets_dialog_name_request_et);

            TextView textView = dialog.findViewById(R.id.presets_dialog_save_button_tv);
            textView.setOnClickListener(v -> {

                if (editText.getText().toString().isEmpty()) {
                    editText.setError(getmView().getContext().getResources().getString(R.string.operate_presets_no_name_error));
                } else {

                    PresetSternProduct presetSternProduct = new PresetSternProduct();
                    presetSternProduct.setType(sternProduct.getType());
                    presetSternProduct.setGetRangesList(settingsSeekBarsAdapter.getSelectedRages());
                    presetSternProduct.setPresetName(editText.getText().toString());
                    SternRoomDatabase.getDatabase(context).presetSternProductDao().insert(presetSternProduct);
                    //presetPresenter.savePresets(presetSternProduct, false);
                    dialog.dismiss();

                    settingsSeekBarsAdapter.setDisplayCheckBox();
                    settingsSeekBarsAdapter.notifyDataSetChanged();


                    getmView().setEnableDisableNewPresetButton();
                }


            });

            dialog.findViewById(R.id.presets_dialog_cancel_button_tv).setOnClickListener(v -> {
                dialog.dismiss();
            });
        }

    }


    @Override
    public void applySettings() {

        ArrayList<Ranges> ranges = settingsSeekBarsAdapter.getChangetRanges();
        if (ranges == null || ranges.size() == 0) {

            Snackbar.make(getmView().getView(), R.string.settings_screen_no_value_to_send, Snackbar.LENGTH_LONG).show();

            return;
        }


        String gattService = BLEGattAttributes.UUID_STERN_DATA__REMOTE_CONTROLS_SETTINGS_SERVICE;
        String gattCharacteristic = null;
        SettingsProperties id = null;
        byte[] data = null;


        for (int i = 0; i < ranges.size(); i++) {
            Ranges range = ranges.get(i);


            data = null;
            switch (range.getRangeType()) {
                case DELAY_IN:
                    gattCharacteristic = BLEGattAttributes.UUid_STERN_DATA_SETTINGS_GENERAL_READ_REQUEST_DELAY_IN;
                    id = SettingsProperties.DELAY_IN_WRITE_REQUEST;
                    break;

                case DELAY_OUT:
                    gattCharacteristic = BLEGattAttributes.UUID_STERN_DATA_SETTINGS_REMOTES_CONTROL_READ_REQUEST_DELAY_OUT;
                    id = SettingsProperties.DELAY_OUT_WRITE_REQUEST;
                    break;

                case SHORT_FLUSH:
                    gattCharacteristic = BLEGattAttributes.UUID_STERN_DATA_SETTINGS_REMOTES_CONTROL_READ_REQUEST_SHORT_WASH;
                    id = SettingsProperties.SHORT_FLUSH_WRITE_REQUEST;
                    break;

                case LONG_FLUSH:
                    gattCharacteristic = BLEGattAttributes.UUID_STERN_DATA_SETTINGS_REMOTES_CONTROL_READ_REQUEST_LONG_FLUSH;
                    id = SettingsProperties.LONG_FLUSH_WRITE_REQUEST;
                    break;

                case SECURITY_TIME:
                    gattCharacteristic = BLEGattAttributes.UUID_STERN_DATA_SETTINGS_REMOTES_CONTROL_READ_REQUEST_SECURITY_TIME;
                    id = SettingsProperties.SECURITY_TIME_WRITE_REQUEST;
                    break;

                case BETWEEN_TIME:
                    gattCharacteristic = BLEGattAttributes.UUID_STERN_DATA_SETTINGS_REMOTES_CONTROL_READ_REQUEST_BETWEEN_TIME;
                    id = SettingsProperties.BETWEEN_TIME_WRITE_REQUEST;
                    break;

                case DETECTION_RANGE:
                    gattCharacteristic = BLEGattAttributes.UUID_STERN_DATA_SETTINGS_REMOTES_CONTROL_READ_WRITE_NOTIFY_REQUEST_DETECTION_RANGE;
                    id = SettingsProperties.DETECTION_RANGE_WRITE_REQUEST;
                    break;

                case SOAP_DOSAGE:
                    gattCharacteristic = BLEGattAttributes.UUID_STERN_DATA_SETTINGS_REMOTES_CONTROL_SOAP_DOSAGE_CHARACTERISTIC;
                    id = SettingsProperties.SOAP_DISPENSER_DOSAGE_WRITE_REQUEST;
                    break;

                case SOAP_MOTOR:
                    gattCharacteristic = BLEGattAttributes.UUID_STERN_DATA_SETTINGS_REMOTES_CONTROL_FOAM_SOAP_CHARACTERISTIC;
                    id = SettingsProperties.FOAM_SOAP_MOTOR_WRITE_REQUEST;

                    Ranges airRange = null;

                    for (Ranges mr : ranges) {
                        if (mr.getRangeType() == AIR_MOTOR) {
                            airRange = mr;
                        }
                    }

                    //TODO... sending 0
                    data = SettingsScreenBleDataParser.getInstance().getFoamSoapRangeBytes((range.getCurrentValue()), (airRange.getCurrentValue()));
                    removeAriSoapMotorData(RangeTypes.AIR_MOTOR, ranges);
                    break;

                case AIR_MOTOR:
                    gattCharacteristic = BLEGattAttributes.UUID_STERN_DATA_SETTINGS_REMOTES_CONTROL_FOAM_SOAP_CHARACTERISTIC;
                    id = SettingsProperties.FOAM_SOAP_AIR_MOTOR_WRITE_REQUEST;

                    Ranges soapMotor = null;

                    for (Ranges mr : ranges) {
                        if (mr.getRangeType() == RangeTypes.SOAP_MOTOR) {
                            soapMotor = mr;
                        }
                    }

                    data = SettingsScreenBleDataParser.getInstance().getFoamSoapRangeBytes((soapMotor.getCurrentValue()), range.getCurrentValue());
                    removeAriSoapMotorData(RangeTypes.SOAP_MOTOR, ranges);
                    break;


            }

            //If the device is AIR_MOTOR or SOAP_MOTOR
            if (data != null) {
                Log.d("IDTEST", "The sendidng is not null  = " + range.rangeTypes());
                readWriteRequest(gattService, gattCharacteristic, id, data);

            } else {
                //DetectionRange data and Soap dosage is different, that's why we are using a different method for build a byte[]
                data = (range.getRangeType() == RangeTypes.DETECTION_RANGE || range.getRangeType() == RangeTypes.SOAP_DOSAGE) ? SettingsScreenBleDataParser.getInstance().getDetectionRangeBytes(range.getCurrentValue())
                        : SettingsScreenBleDataParser.getInstance().getRangeDataBytes(range.getCurrentValue() * 1000);

                Log.d("IDTEST", "The sendidng Type is  = " + range.rangeTypes());

                readWriteRequest(gattService, gattCharacteristic, id, data);

                //  data = null;

            }


//            if (sternProduct.getType() == SternTypes.SOAP_DISPENSER && dosageRecyclerView != null) {
//
//                gattCharacteristic = BLEGattAttributes.UUID_STERN_DATA_SETTINGS_REMOTES_CONTROL_SOAP_DOSAGE_CHARACTERISTIC;
//
//                data = SettingsScreenBleDataParser.getInstance().getDosageBytes(((DosageButtonsAdapter) dosageRecyclerView.getAdapter()).getChosenDosage());
//
//                readWriteRequest(gattService, gattCharacteristic, SettingsProperties.SOAP_DISPENSER_DOSAGE_REQUEST, data);
//            }


        }


        Log.d(TAG, ranges.toString());


    }


    @Subscribe(sticky = true)
    @Override
    public void getSternProduct(Events.SterProductTransmition sternProduct) {

        boolean isOnResumed = false;

        if (this.sternProduct != null) {
            isOnResumed = true;
        }
        this.sternProduct = sternProduct.getSternProduct();

        getmView().setTitle(this.sternProduct.getType().getName());

        // createRangeTest();

        settingsSeekBarsAdapter = new SettingsSeekBarsAdapter(this, this.sternProduct.getRangesArrayList(), this.sternProduct);


        seekBarRecyclerView.setAdapter(settingsSeekBarsAdapter);


        //If we received the Rage data already
        if (this.sternProduct.isRangesReceived()) {
            settingsSeekBarsAdapter.notifyDataSetChanged();
            return;
        }

        if (!isOnResumed) {
            getRangesData(null);
        }


    }


    @Override
    public void resetToFactorySettings() {

        byte[] data = new byte[1];
        data[0] = (byte) (4 & 0xFF);

        readWriteRequest(BLEGattAttributes.UUID_STERN_DATA__REMOTE_CONTROLS_SETTINGS_SERVICE,
                BLEGattAttributes.UUID_STERN_DATA_SETTINGS_REMOTES_SIMPLE_CONTROLS, SettingsProperties.RESET_TO_FACTORY_SETTINGS, data);
    }


    @Override
    public void getRangesData(RangeTypes rangeType) {
        for (int i = 0; i < sternProduct.getRangesArrayList().size(); i++) {

            Ranges ranges = sternProduct.getRangesArrayList().get(i);

            if (rangeType != null) {
                if (ranges.getRangeType() != rangeType) {
                    continue;
                }
            }

            switch (ranges.getRangeType()) {


                case DELAY_IN:

                    requestRanges.add(getNotificationDataClass(BLEGattAttributes.UUID_STERN_DATA__REMOTE_CONTROLS_SETTINGS_SERVICE,
                            BLEGattAttributes.UUID_STERN_DATA_SETTINGS_REMOTES_CONTROL_READ_REQUEST_DELAY_IN, SettingsProperties.DELAY_IN_REGISTER_NOTIFICATION_REQUEST));

                    requestRanges.add(getWriteRequestDataClass(BLEGattAttributes.UUID_STERN_DATA__REMOTE_CONTROLS_SETTINGS_SERVICE,
                            BLEGattAttributes.UUID_STERN_DATA_SETTINGS_REMOTES_CONTROL_READ_REQUEST_DELAY_IN, SettingsProperties.DELAY_IN_REQUEST));
                    break;

                case DELAY_OUT:

                    requestRanges.add(getNotificationDataClass(BLEGattAttributes.UUID_STERN_DATA__REMOTE_CONTROLS_SETTINGS_SERVICE,
                            BLEGattAttributes.UUID_STERN_DATA_SETTINGS_REMOTES_CONTROL_READ_REQUEST_DELAY_OUT, SettingsProperties.DELAY_OUT_REGISTER_NOTIFICATION_REQUEST));


                    requestRanges.add(getWriteRequestDataClass(BLEGattAttributes.UUID_STERN_DATA__REMOTE_CONTROLS_SETTINGS_SERVICE,
                            BLEGattAttributes.UUID_STERN_DATA_SETTINGS_REMOTES_CONTROL_READ_REQUEST_DELAY_OUT, SettingsProperties.DELAY_OUT_REQUEST));
                    break;

                case SHORT_FLUSH:

                    requestRanges.add(getNotificationDataClass(BLEGattAttributes.UUID_STERN_DATA__REMOTE_CONTROLS_SETTINGS_SERVICE,
                            BLEGattAttributes.UUID_STERN_DATA_SETTINGS_REMOTES_CONTROL_READ_REQUEST_SHORT_WASH, SettingsProperties.SHORT_FLUSH_REGISTER_NOTIFICATION_REQUEST));


                    requestRanges.add(getWriteRequestDataClass(BLEGattAttributes.UUID_STERN_DATA__REMOTE_CONTROLS_SETTINGS_SERVICE,
                            BLEGattAttributes.UUID_STERN_DATA_SETTINGS_REMOTES_CONTROL_READ_REQUEST_SHORT_WASH, SettingsProperties.SHORT_FLUSH_REQUEST));
                    break;

                case LONG_FLUSH:

                    requestRanges.add(getNotificationDataClass(BLEGattAttributes.UUID_STERN_DATA__REMOTE_CONTROLS_SETTINGS_SERVICE,
                            BLEGattAttributes.UUID_STERN_DATA_SETTINGS_REMOTES_CONTROL_READ_REQUEST_LONG_FLUSH, SettingsProperties.LONG_FLUSH_REGISTER_NOTIFICATION_REQUEST));


                    requestRanges.add(getWriteRequestDataClass(BLEGattAttributes.UUID_STERN_DATA__REMOTE_CONTROLS_SETTINGS_SERVICE,
                            BLEGattAttributes.UUID_STERN_DATA_SETTINGS_REMOTES_CONTROL_READ_REQUEST_LONG_FLUSH, SettingsProperties.LONG_FLUSH_REQUEST));
                    break;

                case SECURITY_TIME:

                    requestRanges.add(getNotificationDataClass(BLEGattAttributes.UUID_STERN_DATA__REMOTE_CONTROLS_SETTINGS_SERVICE,
                            BLEGattAttributes.UUID_STERN_DATA_SETTINGS_REMOTES_CONTROL_READ_REQUEST_SECURITY_TIME, SettingsProperties.SECURITY_TIME_REGISTER_NOTIFICATION_REQUEST));

                    requestRanges.add(getWriteRequestDataClass(BLEGattAttributes.UUID_STERN_DATA__REMOTE_CONTROLS_SETTINGS_SERVICE,
                            BLEGattAttributes.UUID_STERN_DATA_SETTINGS_REMOTES_CONTROL_READ_REQUEST_SECURITY_TIME, SettingsProperties.SECURITY_TIME_REQUEST));
                    break;

                case BETWEEN_TIME:

                    requestRanges.add(getNotificationDataClass(BLEGattAttributes.UUID_STERN_DATA__REMOTE_CONTROLS_SETTINGS_SERVICE,
                            BLEGattAttributes.UUID_STERN_DATA_SETTINGS_REMOTES_CONTROL_READ_REQUEST_BETWEEN_TIME, SettingsProperties.BETWEEN_TIME_REGISTER_NOTIFICATION_REQUEST));


                    requestRanges.add(getWriteRequestDataClass(BLEGattAttributes.UUID_STERN_DATA__REMOTE_CONTROLS_SETTINGS_SERVICE,
                            BLEGattAttributes.UUID_STERN_DATA_SETTINGS_REMOTES_CONTROL_READ_REQUEST_BETWEEN_TIME, SettingsProperties.BETWEEN_TIME_REQUEST));
                    break;

                case DETECTION_RANGE:


                    requestRanges.add(getNotificationDataClass(BLEGattAttributes.UUID_STERN_DATA__REMOTE_CONTROLS_SETTINGS_SERVICE,
                            BLEGattAttributes.UUID_STERN_DATA_SETTINGS_REMOTES_CONTROL_READ_WRITE_NOTIFY_REQUEST_DETECTION_RANGE, SettingsProperties.DETECTION_RANGE_REGISTER_NOTIFICATION_REQUEST));


                    requestRanges.add(getWriteRequestDataClass(BLEGattAttributes.UUID_STERN_DATA__REMOTE_CONTROLS_SETTINGS_SERVICE,
                            BLEGattAttributes.UUID_STERN_DATA_SETTINGS_REMOTES_CONTROL_READ_WRITE_NOTIFY_REQUEST_DETECTION_RANGE, SettingsProperties.DETECTION_RANGE_REQUEST));
                    break;


                case SOAP_DOSAGE:

                    requestRanges.add(getNotificationDataClass(BLEGattAttributes.UUID_STERN_DATA__REMOTE_CONTROLS_SETTINGS_SERVICE,
                            BLEGattAttributes.UUID_STERN_DATA_SETTINGS_REMOTES_CONTROL_SOAP_DOSAGE_CHARACTERISTIC, SettingsProperties.SOAP_DISPENSER_DOSAGE_REGISTER_NOTIFICATION_REQUEST));

                    requestRanges.add(getWriteRequestDataClass(BLEGattAttributes.UUID_STERN_DATA__REMOTE_CONTROLS_SETTINGS_SERVICE,
                            BLEGattAttributes.UUID_STERN_DATA_SETTINGS_REMOTES_CONTROL_SOAP_DOSAGE_CHARACTERISTIC, SettingsProperties.SOAP_DISPENSER_DOSAGE_REQUEST));

                    break;

                case AIR_MOTOR:

                    requestRanges.add(getNotificationDataClass(BLEGattAttributes.UUID_STERN_DATA__REMOTE_CONTROLS_SETTINGS_SERVICE,
                            BLEGattAttributes.UUID_STERN_DATA_SETTINGS_REMOTES_CONTROL_FOAM_SOAP_CHARACTERISTIC, SettingsProperties.FOAM_SOAP_AIR_MOTOR_REGISTER_NOTIFICATION_REQUEST));

                    requestRanges.add(getWriteRequestDataClass(BLEGattAttributes.UUID_STERN_DATA__REMOTE_CONTROLS_SETTINGS_SERVICE,
                            BLEGattAttributes.UUID_STERN_DATA_SETTINGS_REMOTES_CONTROL_FOAM_SOAP_CHARACTERISTIC, SettingsProperties.FOAM_SOAP_AIR_MOTOR_REQUEST));


                    break;


                case SOAP_MOTOR:


                    requestRanges.add(getNotificationDataClass(BLEGattAttributes.UUID_STERN_DATA__REMOTE_CONTROLS_SETTINGS_SERVICE,
                            BLEGattAttributes.UUID_STERN_DATA_SETTINGS_REMOTES_CONTROL_FOAM_SOAP_CHARACTERISTIC, SettingsProperties.FOAM_SOAP_MOTOR_REGISTER_NOTIFICATION_REQUEST));

                    requestRanges.add(getWriteRequestDataClass(BLEGattAttributes.UUID_STERN_DATA__REMOTE_CONTROLS_SETTINGS_SERVICE,
                            BLEGattAttributes.UUID_STERN_DATA_SETTINGS_REMOTES_CONTROL_FOAM_SOAP_CHARACTERISTIC, SettingsProperties.FOAM_SOAP_MOTOR_REQUEST));

                    break;
            }


        }

        sendData(requestRanges.remove(0), true);


        new Handler().postDelayed(() -> {

            if (requestRanges.size() > 0) {
                Log.d("IDTEST", ".....*********** sendData() " + rangeType);
                Log.d("IDTEST", ".....*********** sendData() from getRangesData()");
                sendData(requestRanges.remove(0), true);
            }
        }, DELAY_TiME);


    }


    @Override
    public void handleRangeReceived(String data, RangeTypes rangeType) {

        if (!data.contains("FF")) {
            Ranges ranges = null;
            Ranges soapRange = null;

            if (rangeType == AIR_MOTOR) {

                String[] hexArr = data.split(" ");
                ranges = new Ranges();
                ranges.setRangeType(AIR_MOTOR);
                ranges.setMinimumValue(0);
                ranges.setMaximumValue(9);
                ranges.setCurrentValue(Integer.parseInt(hexArr[2], 16));

                soapRange = new Ranges();
                soapRange.setRangeType(RangeTypes.SOAP_MOTOR);
                soapRange.setMinimumValue(0);
                soapRange.setMaximumValue(9);
                soapRange.setCurrentValue(Integer.parseInt(hexArr[1], 16));


            } else {

                ranges = SettingsScreenBleDataParser.getInstance().getRanges(data, rangeType);
            }

            Log.d("IDTEST", ranges.toString());

            Log.d(TAG, "RANGE " + ranges.toString());


            //Removing the received Range from Changed range ArrayList in adapter
            Ranges finalRanges = ranges;
            new Thread(() -> settingsSeekBarsAdapter.removeFromChanged(finalRanges)).start();


            for (Ranges r : sternProduct.getRangesArrayList()) {

                if (rangeType == AIR_MOTOR) {
                    if (r.getRangeType() == AIR_MOTOR) {
                        r.setNewRange(ranges);
                    } else if (soapRange != null && r.getRangeType() == RangeTypes.SOAP_MOTOR) {
                        r.setNewRange(soapRange);
                        soapRange = null;
                    }


                } else if (r.getRangeType() == rangeType) {
                    r.setNewRange(ranges);

                    break;
                }


            }


        }


        //  settingsSeekBarsAdapter.setNewData(sternProduct);


    }

    @Override
    public void handleComunicationDataDialog() {


        comunicationIssueCounter = 0;
        BLEDeviceConnectionManager.getInstance().setSendedDataSize((short) 0);

        if (DialogHelper.getInstance().isDialogShown()) {
            DialogHelper.getInstance().dismiss();

        }

        sendingDataArr.clear();
        requestRanges.clear();
        String message = context.getResources().getString(R.string.settings_screen_connection_issue);
        String positiveText = context.getResources().getString(R.string.settings_screen_connection_issue_retry);
        String negativeText = context.getResources().getString(R.string.settings_screen_connection_issue_cancel);

        DialogHelper.getInstance().DisplayMessageDialog(context, message, positiveText, negativeText, new DialogHelper.OnMessageDialogButtonsClickEvents() {
            @Override
            public void onPositiveClicked() {
                getRangesData(null);
            }

            @Override
            public void onNegativeClicked() {
                ((MainActivity) getmView().getActivityContext()).getBottomNavigationView().getMenu().getItem(1).setChecked(true);
                Navigation.findNavController((MainActivity) getmView().getActivityContext(), R.id.main_activity_host_fragment).navigate(R.id.ScannedProductsFragment);
            }
        }).show();

    }


    @Override
    public ArrayList<Ranges> getRangesFromAdapter() {
        ArrayList<Ranges> ranges = new ArrayList<>();

        for (int i = 0; i < seekBarRecyclerView.getChildCount(); i++) {
            SettingsSeekBarsAdapter.ViewHolder viewHolder = (SettingsSeekBarsAdapter.ViewHolder) seekBarRecyclerView.findViewHolderForAdapterPosition(i);
            Ranges r = new Ranges();

            r.setRangeType(viewHolder.getType());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                r.setMinimumValue(viewHolder.getSeekBar().getMin());
            }
            r.setMaximumValue(viewHolder.getSeekBar().getMax());
            r.setCurrentValue(viewHolder.getSeekBar().getProgress());
            r.setDefaultValue(viewHolder.getDefaultValue());

            ranges.add(r);
        }

        return ranges;
    }

    public ArrayList<Ranges> getSelectedRangesFromAdapter() {
        ArrayList<Ranges> ranges = new ArrayList<>();

        return ranges;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
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


            case R.id.settings_fragment_apply_BTN:
                applySettings();
                break;


        }
    }


    private void createRangeTest() {
        ArrayList<Ranges> ranges = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            ranges.add(setRangesTest(RangeTypes.values()[i], 15 + i));

        }
        sternProduct.setRangesArrayList(ranges);
    }

    private Ranges setRangesTest(RangeTypes types, int val) {

        int minVal = 1;
        int maxVal = val + 80;
        int defaultVal = maxVal - 65;

        return new Ranges(types, val, maxVal, minVal, defaultVal);

    }


    @Override
    public void readWriteRequest(String service, String characteristics, SettingsProperties
            requestID, byte[] data) {

//        Thread thread = new Thread() {
//            @Override
//            public void run() {
//                super.run();
        ArrayList<BLEDeviceConnectionManager.DataClass.CharacteristicIO> characteristicIO = new ArrayList<>();
        BLEDeviceConnectionManager.DataClass readRequestRanges = new BLEDeviceConnectionManager.DataClass();
        readRequestRanges.setRequestID(requestID.value);
        readRequestRanges.setAddToDaSize(true);
        characteristicIO.add(BLEDeviceConnectionManager.DataClass.CharacteristicIO.WRITABLE);
        readRequestRanges.setCharacteristicIOArrayList(characteristicIO);
        readRequestRanges.setData(data);
        readRequestRanges.setServiceUUid(UUID.fromString(service));
        readRequestRanges.setCharacteristicsUUid(UUID.fromString(characteristics));
        sendingDataArr.add(readRequestRanges);

        sendData(sendingDataArr, true);
//            }
//        };
//
//        thread.run();


    }

    @Override
    public BLEDeviceConnectionManager.DataClass getWriteRequestDataClass(String service, String characteristics, SettingsProperties requestID) {
        byte[] sendData = new byte[1];
        sendData[0] = (byte) 0x81;
        ArrayList<BLEDeviceConnectionManager.DataClass.CharacteristicIO> characteristicIO = new ArrayList<>();
        BLEDeviceConnectionManager.DataClass readRequestRanges = new BLEDeviceConnectionManager.DataClass();
        readRequestRanges.setRequestID(requestID.value);
        characteristicIO.add(BLEDeviceConnectionManager.DataClass.CharacteristicIO.WRITABLE);
        readRequestRanges.setCharacteristicIOArrayList(characteristicIO);
        readRequestRanges.setData(sendData);
        readRequestRanges.setServiceUUid(UUID.fromString(service));
        readRequestRanges.setCharacteristicsUUid(UUID.fromString(characteristics));

        return readRequestRanges;
    }


    @Override
    public void registeredToNotification(String service, String
            characteristics, SettingsProperties requestID) {

        ArrayList<BLEDeviceConnectionManager.DataClass.CharacteristicIO> characteristicIO = new ArrayList<>();
        BLEDeviceConnectionManager.DataClass registeredToNotification = new BLEDeviceConnectionManager.DataClass();
        registeredToNotification.setEnableNotification(true);
        registeredToNotification.setRequestID(requestID.value);
        characteristicIO.add(BLEDeviceConnectionManager.DataClass.CharacteristicIO.NOTIFICATION);

        registeredToNotification.setCharacteristicIOArrayList(characteristicIO);
        registeredToNotification.setServiceUUid(UUID.fromString(service));
        registeredToNotification.setCharacteristicsUUid(UUID.fromString(characteristics));
        sendingDataArr.add(registeredToNotification);
        sendData(sendingDataArr, false);


    }

    @Override
    public BLEDeviceConnectionManager.DataClass getNotificationDataClass(String service, String characteristics, SettingsProperties requestID) {
        ArrayList<BLEDeviceConnectionManager.DataClass.CharacteristicIO> characteristicIO = new ArrayList<>();
        BLEDeviceConnectionManager.DataClass registeredToNotification = new BLEDeviceConnectionManager.DataClass();
        registeredToNotification.setEnableNotification(true);
        registeredToNotification.setAddToDaSize(true);
        registeredToNotification.setRequestID(requestID.value);
        characteristicIO.add(BLEDeviceConnectionManager.DataClass.CharacteristicIO.NOTIFICATION);

        registeredToNotification.setCharacteristicIOArrayList(characteristicIO);
        registeredToNotification.setServiceUUid(UUID.fromString(service));
        registeredToNotification.setCharacteristicsUUid(UUID.fromString(characteristics));

        return registeredToNotification;
    }

    @Override
    public void unRegisteredToNotification(String service, String
            characteristics, SettingPresenter.SettingsProperties requestID) {

        ArrayList<BLEDeviceConnectionManager.DataClass.CharacteristicIO> characteristicIO = new ArrayList<>();
        BLEDeviceConnectionManager.DataClass unRegisteredToNotification = new BLEDeviceConnectionManager.DataClass();
        unRegisteredToNotification.setEnableNotification(false);
        unRegisteredToNotification.setRequestID(requestID.value);

        characteristicIO.add(BLEDeviceConnectionManager.DataClass.CharacteristicIO.NOTIFICATION);

        unRegisteredToNotification.setCharacteristicIOArrayList(characteristicIO);
        unRegisteredToNotification.setServiceUUid(UUID.fromString(service));
        unRegisteredToNotification.setCharacteristicsUUid(UUID.fromString(characteristics));
        sendingDataArr.add(unRegisteredToNotification);
        sendData(sendingDataArr, false);
    }

    @Override
    public void showSoapDosageButtons() {

        this.dosageRecyclerView.setHasFixedSize(false);
        dosageLayoutManager = new LinearLayoutManager(getmView().getActivityContext(), LinearLayoutManager.HORIZONTAL, false);
        this.dosageRecyclerView.setLayoutManager(dosageLayoutManager);
        this.dosageRecyclerView.setAdapter(new DosageButtonsAdapter(this));
    }

    @Override
    public void sendData(ArrayList<BLEDeviceConnectionManager.DataClass> sendingDataArr,
                         boolean displayLoader) {


        if (displayLoader) {
            if (!DialogHelper.getInstance().isDialogShown()) {

                DialogHelper.getInstance().displayLoaderProgressDialog(getmView().getActivityContext(), getmView().getActivityContext().getResources().getString(R.string.loading)).show();


            }
        }


        for (int i = 0; i < sendingDataArr.size(); i++) {
            BLEDeviceConnectionManager.DataClass dataClass = sendingDataArr.remove(0);

            Log.d("IDTEST", "The data ID that was sended is = " + dataClass.getRequestID());
            Log.d("TESTSended", "The data ID that was sended is = " + dataClass.getRequestID());
            BLEDeviceConnectionManager.getInstance().sendData(dataClass, SettingPresenter.this);
        }


    }

    @Override
    public void sendData(BLEDeviceConnectionManager.DataClass dataClass, boolean displayLoader) {
        if (displayLoader) {
            if (!DialogHelper.getInstance().isDialogShown()) {
                DialogHelper.getInstance().displayLoaderProgressDialog(getmView().getActivityContext(), getmView().getActivityContext().getResources().getString(R.string.loading)).show();

            }
        }

        BLEDeviceConnectionManager.getInstance().sendData(dataClass, SettingPresenter.this);
    }

    @Override
    public void removeAriSoapMotorData(RangeTypes rType, ArrayList<Ranges> ranges) {

        for (int i = 0; i < ranges.size(); i++) {
            Ranges r = ranges.get(i);
            if (r.getRangeType().equals(rType)) {
                ranges.remove(i);
                for (int j = 0; j < settingsSeekBarsAdapter.getChangetRanges().size(); j++) {
                    Ranges iR = settingsSeekBarsAdapter.getChangetRanges().get(j);
                    if (iR.getRangeType().equals(rType)) {
                        settingsSeekBarsAdapter.getChangetRanges().remove(j);
                    }
                }
            }
        }

    }


    @Override
    protected void parseOnDataReceived(String dataSTR, String uuid, int requestID) {
        RangeTypes onDataReceivedType = null;
        Log.d("IDTEST", " ...........onDataReceived() " + requestID + "\n" + "The data is: " + dataSTR + "The characteristic UUID is " + uuid);


        Log.d(TAG, dataSTR);


        switch (uuid) {
            case BLEGattAttributes.UUid_STERN_DATA_SETTINGS_GENERAL_READ_REQUEST_DELAY_IN:
                onDataReceivedType = RangeTypes.DELAY_IN;
                break;
            case BLEGattAttributes.UUID_STERN_DATA_SETTINGS_REMOTES_CONTROL_READ_REQUEST_DELAY_OUT:
                onDataReceivedType = RangeTypes.DELAY_OUT;
                break;

            case BLEGattAttributes.UUID_STERN_DATA_SETTINGS_REMOTES_CONTROL_READ_REQUEST_LONG_FLUSH:
                onDataReceivedType = RangeTypes.LONG_FLUSH;
                break;

            case BLEGattAttributes.UUID_STERN_DATA_SETTINGS_REMOTES_CONTROL_READ_REQUEST_SHORT_WASH:
                onDataReceivedType = RangeTypes.SHORT_FLUSH;
                break;

            case BLEGattAttributes.UUID_STERN_DATA_SETTINGS_REMOTES_CONTROL_READ_REQUEST_SECURITY_TIME:
                onDataReceivedType = RangeTypes.SECURITY_TIME;
                break;

            case BLEGattAttributes.UUID_STERN_DATA_SETTINGS_REMOTES_CONTROL_READ_REQUEST_BETWEEN_TIME:
                onDataReceivedType = RangeTypes.BETWEEN_TIME;
                break;

            case BLEGattAttributes.UUID_STERN_DATA_SETTINGS_REMOTES_CONTROL_READ_WRITE_NOTIFY_REQUEST_DETECTION_RANGE:
                onDataReceivedType = RangeTypes.DETECTION_RANGE;
                break;

            case BLEGattAttributes.UUID_STERN_DATA_SETTINGS_REMOTES_CONTROL_SOAP_DOSAGE_CHARACTERISTIC:
                onDataReceivedType = RangeTypes.SOAP_DOSAGE;
                break;

            case BLEGattAttributes.UUID_STERN_DATA_SETTINGS_REMOTES_CONTROL_FOAM_SOAP_CHARACTERISTIC:
                //TODO.. create a method to configure if it Air Motor or Soap Motor

                onDataReceivedType = AIR_MOTOR;
                break;


        }
        if (!dataSTR.equals(BluetoothLeService.ACTION_COMMUNICATION_ISSUE)) {


            if (requestID == 1 && !dataSTR.equals(BluetoothLeService.ACTION_DATA_AVAILABLE_WRITE_RANGES)) {
                handleRangeReceived(dataSTR, onDataReceivedType);
            } else if (requestID == bleFactoryReset) {
                new Handler().postDelayed(() -> getRangesData(null), DELAY_TiME);
                getRangesData(null);

                if (DialogHelper.getInstance().isDialogShown()) {
                    DialogHelper.getInstance().dismiss();

                }

                //TODO... must return
                return;

            } else if (requestID == 1 && dataSTR.equals(BluetoothLeService.ACTION_DATA_AVAILABLE_WRITE_RANGES)) {
                setDatatoSternObject(onDataReceivedType);
            }


        } else { //COMMUNICATION error
            //TODO... Handle COMMUNICATION_ISSUE

            if (comunicationIssueCounter == 3) {
                if (badRequestDataType != null && badRequestDataType == onDataReceivedType) {
                    DELAY_TiME += bleDataIdentifireInitial;
                    Log.e("IDTEST", "***---*** The DelayTime is = " + DELAY_TiME + "***---***");
                    handleComunicationDataDialog();
                }
                comunicationIssueCounter = 0;

                return;
            }
            badRequestDataType = onDataReceivedType;
            comunicationIssueCounter++;


            Log.i("IDTEST", "............. counter = " + comunicationIssueCounter + "\r\n" +
                    "badRequest = " + badRequestDataType + " \r\n" + "onDataReceivedType = " + onDataReceivedType);
            Log.d("IDTEST", " ...........COMMUNICATION_ISSUE " + " characteristic = " + uuid);
            Log.d("IDTEST", " ...........COMMUNICATION_ISSUE the type is " + onDataReceivedType);
            getRangesData(onDataReceivedType);
            return;
        }


        if (!requestRanges.isEmpty()) {
            Log.d("IDTEST", "********Sending Notification request");
            new Handler().postDelayed(() -> {
                if (requestRanges.size() > 0) {
                    sendData(requestRanges.remove(0), true);
                }
            }, 0);

            new Handler().postDelayed(() -> {
                if (requestRanges.size() > 0) {
                    sendData(requestRanges.remove(0), true);
                }
            }, DELAY_TiME);
        } else {
            Log.d("IDTEST", "********Request data is empty");
            if (!dataSTR.equals(BluetoothLeService.ACTION_DATA_AVAILABLE_WRITE_RANGES)) {
                seekBarRecyclerView.setAdapter(settingsSeekBarsAdapter);
            }

            if (DialogHelper.getInstance().isDialogShown()) {
                DialogHelper.getInstance().dismiss();

            }

        }

        if (BLEDeviceConnectionManager.getInstance().getSendedDataSize() == 0) {

            //    DialogHelper.getInstance().dismiss();
            if (DialogHelper.getInstance().isDialogShown()) {
                DialogHelper.getInstance().dismiss();
                DELAY_TiME = DEFAULT_DELAY_TIME;
                sternProduct.setRangesReceived(true);
                //  settingsSeekBarsAdapter.notifyDataSetChanged();

            }

        }

    }


    @Override
    public void onScannedDeviceData(ScanResult scanResults) {

    }

    @Override
    public void sendPresetObjectToSettings(PresetSternProduct presetSternProduct) {


        for (int i = 0; i < presetSternProduct.getGetRangesList().size(); i++) {

            Ranges range = presetSternProduct.getGetRangesList().get(i);

            for (int j = 0; j < SettingPresenter.this.sternProduct.getRangesArrayList().size(); j++) {
                if (range.getRangeType().equals(SettingPresenter.this.sternProduct.getRangesArrayList().get(j).getRangeType())) {
                    SettingPresenter.this.sternProduct.getRangesArrayList().get(j).setNewRange(range);
                }
            }
        }

        settingsSeekBarsAdapter.setNewData(SettingPresenter.this.sternProduct.getRangesArrayList());


    }

    @Override
    public void onScrollChange(View view, int i, int i1, int i2, int i3) {

    }


    enum SettingsProperties {


        SEND_READ_REQUEST(bleDataIdentifireInitial + 1),

        DELAY_IN_REQUEST(bleDataIdentifireInitial + 2),
        DELAY_IN_WRITE_REQUEST(bleWriteDataIdentifireInitial + 1),
        DELAY_IN_REGISTER_NOTIFICATION_REQUEST(bleDataNotificationIdentifireInitial),
        DELAY_IN_UNREGISTERED_NOTIFICATION_REQUEST(bleDataIdentifireInitial + 4),

        DELAY_OUT_REQUEST(bleDataIdentifireInitial + 5),
        DELAY_OUT_WRITE_REQUEST(bleWriteDataIdentifireInitial + 2),
        DELAY_OUT_REGISTER_NOTIFICATION_REQUEST(bleDataNotificationIdentifireInitial),
        DELAY_OUT_UNREGISTERED_NOTIFICATION_REQUEST(bleDataIdentifireInitial + 7),

        LONG_FLUSH_REQUEST(bleDataIdentifireInitial + 9),
        LONG_FLUSH_WRITE_REQUEST(bleWriteDataIdentifireInitial + 3),
        LONG_FLUSH_REGISTER_NOTIFICATION_REQUEST(bleDataNotificationIdentifireInitial),
        LONG_FLUSH_UNREGISTERED_NOTIFICATION_REQUEST(bleDataIdentifireInitial + 11),

        SHORT_FLUSH_REQUEST(bleDataIdentifireInitial + 12),
        SHORT_FLUSH_WRITE_REQUEST(bleWriteDataIdentifireInitial + 4),
        SHORT_FLUSH_REGISTER_NOTIFICATION_REQUEST(bleDataNotificationIdentifireInitial),
        SHORT_FLUSH_UNREGISTERED_NOTIFICATION_REQUEST(bleDataIdentifireInitial + 14),

        SECURITY_TIME_REQUEST(bleDataIdentifireInitial + 15),
        SECURITY_TIME_WRITE_REQUEST(bleWriteDataIdentifireInitial + 5),
        SECURITY_TIME_REGISTER_NOTIFICATION_REQUEST(bleDataNotificationIdentifireInitial),
        SECURITY_TIME_UNREGISTERED_NOTIFICATION_REQUEST(bleDataIdentifireInitial + 17),

        BETWEEN_TIME_REQUEST(bleDataIdentifireInitial + 18),
        BETWEEN_TIME_WRITE_REQUEST(bleWriteDataIdentifireInitial + 6),
        BETWEEN_TIME_REGISTER_NOTIFICATION_REQUEST(bleDataNotificationIdentifireInitial),
        BETWEEN_TIME_UNREGISTERED_NOTIFICATION_REQUEST(bleDataIdentifireInitial + 20),

        DETECTION_RANGE_REQUEST(bleDataIdentifireInitial + 21),
        DETECTION_RANGE_WRITE_REQUEST(bleWriteDataIdentifireInitial + 7),
        DETECTION_RANGE_REGISTER_NOTIFICATION_REQUEST(bleDataNotificationIdentifireInitial),
        DETECTION_RANGE_UNREGISTERED_NOTIFICATION_REQUEST(bleDataIdentifireInitial + 23),


        SOAP_DISPENSER_DOSAGE_REQUEST(bleDataIdentifireInitial + 24),
        SOAP_DISPENSER_DOSAGE_WRITE_REQUEST(bleWriteDataIdentifireInitial + 8),
        SOAP_DISPENSER_DOSAGE_REGISTER_NOTIFICATION_REQUEST(bleDataNotificationIdentifireInitial),
        SOAP_DISPENSER_DOSAGE_UNREGISTER_NOTIFICATION_REQUEST(bleDataIdentifireInitial + 26),

        FOAM_SOAP_MOTOR_REQUEST(bleDataIdentifireInitial + 27),
        FOAM_SOAP_MOTOR_WRITE_REQUEST(bleWriteDataIdentifireInitial + 9),
        FOAM_SOAP_MOTOR_REGISTER_NOTIFICATION_REQUEST(bleDataNotificationIdentifireInitial),
        FOAM_SOAP_MOTOR_UNREGISTER_NOTIFICATION_REQUEST(bleDataIdentifireInitial + 29),

        FOAM_SOAP_AIR_MOTOR_REQUEST(bleDataIdentifireInitial + 30),
        FOAM_SOAP_AIR_MOTOR_WRITE_REQUEST(bleWriteDataIdentifireInitial + 10),
        FOAM_SOAP_AIR_MOTOR_REGISTER_NOTIFICATION_REQUEST(bleDataNotificationIdentifireInitial),
        FOAM_SOAP_AIR_MOTOR_UNREGISTER_NOTIFICATION_REQUEST(bleDataIdentifireInitial + 32),


        RESET_TO_FACTORY_SETTINGS(bleFactoryReset);


        private int value;

        SettingsProperties(int val) {
            this.value = val;
        }


    }
}






