package stern.msapps.com.control_ble_devices.presenter.operateScreenPresenter;

import android.support.v7.widget.RecyclerView;
import android.widget.SeekBar;
import android.widget.TextView;


import java.util.Calendar;

import stern.msapps.com.control_ble_devices.model.dataTypes.SternProduct;
import stern.msapps.com.control_ble_devices.presenter.BaseMvpPresenter;
import stern.msapps.com.control_ble_devices.model.dataTypes.Day;
import stern.msapps.com.control_ble_devices.model.dataTypes.Schedule;
import stern.msapps.com.control_ble_devices.model.enums.DaysOfWeek;
import stern.msapps.com.control_ble_devices.model.enums.ScheduledDataTypes;

import stern.msapps.com.control_ble_devices.view.BaseView;
import stern.msapps.com.control_ble_devices.view.adapters.ScheduledItemAdapter;


public interface OperateContract {

    interface Presenter extends BaseMvpPresenter<View> {

        void getScheduledDataFromDevice();

        void parseHygieneFlushLastActivation(String data);

        void parseHygieneFlushDays(String data);

        void parseStandByDays(String data);

        void parseHygieneFlushRandomDays(String data);

        void parseStandByRandomDays(String data);

        void parseNotificationTapState(String data);

        void parseDeleteEvent(String data);

        void parseReadDeviceEvent(String data);

        void openCloseValve(android.view.View v);


        void setHygieneDaysRecyclerView(RecyclerView hygieneDaysRecyclerView);

        void setStandByDaysRecyclerView(RecyclerView standByDaysRecyclerView);

        void setScheduledHygieneRecyclerView(RecyclerView scheduledHygieneRecyclerView);

        void setScheduledStandByRecyclerView(RecyclerView scheduledStandByRecyclerView);


        void setValeStateNotification(boolean enableNotification);

        void setReadScheduledDaysData(Calendar calendar, DaysOfWeek daysOfWeek, Day.ScheduledData scheduledData, Schedule schedualeMode, ScheduledItemAdapter adapter);

        void settingDataOnDateSelected(DaysOfWeek dayOfWeek, Schedule scheduled, ScheduledItemAdapter adapter, long duration, ScheduledDataTypes type);

        void setReadScheduledFromLastActovation(long duration, long time, ScheduledDataTypes dataTypes);

        void onDaySelected(DaysOfWeek day, ScheduledDataTypes scheduledType);

        void onStart();

        void onResume();

        void onDestroy();

        void onPause();


        OperatePresenter.DataTest getFromLastActivationID();


        void onClickRadioButton(android.view.View v);

        void setHygieneDatePicker();

        void setStandByDatePicker();

        void sendOpenCloseValveData(long durationVal);

        void sendStandByImidiatlyDuration(long duration);

        /**
         * Checking the SternProduct for scheduled dada and passing it to sendScheduled() Method
         *
         * @param scheduledDataTypes
         */
        void prepareScheduledDataToSend(ScheduledDataTypes scheduledDataTypes);

        void sendScheduled(ScheduledDataTypes scheduledDataTypes, boolean isOnce, boolean isDays, boolean isLastActivation, boolean immediatelyStandBy, Day.ScheduledData scheduledData);

        void deleteScheduledHygieneFlush(ScheduledDataTypes scheduledType, DaysOfWeek daysOfWeek, int position);

        void deleteScheduledRandomDay(ScheduledDataTypes scheduledType, int position);

        void sendDeleteEvent(int scheduledType, Day.ScheduledData scheduledData);

        void sendDeleteEventById(int scheduledType, long handleId);


        void readEvent(ScheduledDataTypes scheduledType, long handleID, OperatePresenter.BleDataIdentifier bleDataIdentifier);

        void closeValve();

        void handleOnScheduledBTNClick(android.view.View v);

        void changeTextViewTextByObjectType(SternProduct sternProduct);

        void saveNewPreset();

        void loadPreset();

        void addNewPreset();

        Calendar getCalendarFromMilisec(long mSec);



    }


    interface View extends BaseView {

        void technicianViewInit();

        void cleanerViewInit();

        void technicianViewsSetListeners();

        void cleanerViewsSetListeners();

        void setEnableDisableNewPresetButton();

        void setTitle(String title);

        void setPresenter();

        void setAdapter();

        long getFlashScheduledDuration();

        long getStandByScheduledDuration();

        long getOpenCloseValveDuration();

        int getHygieneFlushDuration();

        int getStandByoPenCloseDuration();


        TextView getHygieneFlushFromLastActivationTimeTV();

        TextView getHygieneFlushFromLastActivationDurationTV();


        TextView getStandByFromLastActivationDurationTV();

        void setHygieneFlushFromLastActivationTimeTV(String val);

        void setHygieneFlushFromLastActivationDurationTV(String val);

        void setStandByFromLastActivationDurationTV(String val);


        void setHygieneFlushFromLastActivationSeekBarValue(int val);

        void setHygieneFlushFromLastActivationDurationSeekBarValue(int val);

        void setStandByFromLastActivationDurationSeekBarValue(int val);

        void setStanByFromLastActivationDurationTV(String val);

        void setStandByFromLastActivationSeekBarValue(int val);

        void setHygieneFlushFromLastActivationRadioButton(boolean set);

        void setStandByromLastActivationRadioButton(boolean set);

        void hideStandByLastActivation();

        TextView getOpenCloseValveButton();

        android.view.View getHygieneView();

        String getHygieneViewTag();


        void setDefaultValueForViews();

        void changeOpenValveTextViewTextByObjectType(int str);

        int setMinimumValueToSeekBar(SeekBar seekBar, int minimumValue, int currentValue);


    }


}
