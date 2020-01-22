package stern.msapps.com.control_ble_devices.presenter.settingPresenter;


import android.support.v7.widget.RecyclerView;
import android.widget.ScrollView;


import java.util.ArrayList;

import stern.msapps.com.control_ble_devices.model.ble.BLEDeviceConnectionManager;
import stern.msapps.com.control_ble_devices.model.dataTypes.ranges.Ranges;
import stern.msapps.com.control_ble_devices.presenter.BaseMvpPresenter;
import stern.msapps.com.control_ble_devices.model.enums.DosageButtons;
import stern.msapps.com.control_ble_devices.model.enums.RangeTypes;
import stern.msapps.com.control_ble_devices.model.enums.SettingButtons;
import stern.msapps.com.control_ble_devices.eventBus.Events;
import stern.msapps.com.control_ble_devices.view.BaseView;

public interface SettingsContract {


    interface Presenter extends BaseMvpPresenter<View> {

        void onStart();

        void onViewCreated();

        void onPause();

        void onDestroy();

        void onButtonClicked(SettingButtons button);

        void onDosageButtonClicked(DosageButtons dosageButton);

        void settButtonsRecyclerView(RecyclerView buttonsRecyclerView);

        void setSeekBarsRecyclerView(RecyclerView seekBarRecyclerView);

        void setDatatoSternObject(RangeTypes types);

        void setDosageRecyclerView(RecyclerView dosageRecyclerView);

        void sendData(ArrayList<BLEDeviceConnectionManager.DataClass> sendingDataArr,
                      boolean displayLoader);

        void sendData(BLEDeviceConnectionManager.DataClass dataClass, boolean displayLoader);

        void removeAriSoapMotorData(RangeTypes rType, ArrayList<Ranges> ranges);


        BLEDeviceConnectionManager.DataClass setRequestDataClass(String service, String characteristics, SettingPresenter.SettingsProperties requestID);


        void addNewPreset();

        void loadPreset();

        void saveNewPreset();

        void applySettings();

        void getSternProduct(Events.SterProductTransmition sternProduct);

        void resetToFactorySettings();

        void getRangesData(RangeTypes rangeType);


        void handleRangeReceived(String data, RangeTypes rangeType);

        void handleComunicationDataDialog();

        ArrayList<Ranges> getRangesFromAdapter();

        void readWriteRequest(String service, String characteristics, SettingPresenter.SettingsProperties requestID, byte[] data);

        BLEDeviceConnectionManager.DataClass getWriteRequestDataClass(String service, String characteristics, SettingPresenter.SettingsProperties requestID);


        void registeredToNotification(String service, String characteristics, SettingPresenter.SettingsProperties requestID);

        BLEDeviceConnectionManager.DataClass getNotificationDataClass(String service, String characteristics, SettingPresenter.SettingsProperties requestID);


        void unRegisteredToNotification(String service, String characteristics, SettingPresenter.SettingsProperties requestID);

        void showSoapDosageButtons();

    }


    interface View extends BaseView {

        void viewInit();

        void setPresenter();

        void setTitle(String title);

        void onEventListeners();

        void setEnableDisableNewPresetButton();

        void setEnableDisableSavePresetButton();

        ScrollView getMainScrollView();


    }
}
