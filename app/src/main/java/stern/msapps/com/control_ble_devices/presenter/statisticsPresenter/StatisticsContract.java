package stern.msapps.com.control_ble_devices.presenter.statisticsPresenter;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import stern.msapps.com.control_ble_devices.model.ble.BLEDeviceConnectionManager;
import stern.msapps.com.control_ble_devices.presenter.BaseMvpPresenter;
import stern.msapps.com.control_ble_devices.model.dataTypes.SternProductStatistics;
import stern.msapps.com.control_ble_devices.view.BaseView;

public interface StatisticsContract {


    interface Presenter extends BaseMvpPresenter<View> {

        void onStart();

        void onViewCreated();

        void onPause();

        void onDestroy();

        void sendReadRequestLastEventDone(int event);

        void getAllData();

        void sendReport();

        void onResetStatisticClicked(SternProductStatistics.StatisticTypes type);

        void resetNumberOfActivations();

        BLEDeviceConnectionManager.DataClass resetLastFilterClean();

        BLEDeviceConnectionManager.DataClass resetLastSoapRefill();


        void setStatisticsRecyclerView(RecyclerView statisticsRecyclerView);

        BLEDeviceConnectionManager.DataClass getHygieneFlushLastEvent();

        BLEDeviceConnectionManager.DataClass getHygieneFlushNextEvent();

        BLEDeviceConnectionManager.DataClass getStandByLastEvent();

        BLEDeviceConnectionManager.DataClass getStandByNextEvent();

        BLEDeviceConnectionManager.DataClass getStatisticsInfo();

        BLEDeviceConnectionManager.DataClass getLastFilterClean(String handleID);

        BLEDeviceConnectionManager.DataClass getLastSoapRefill(String handleID);


        BLEDeviceConnectionManager.DataClass deleteEventbyID(String handleID, StatisticsPresenter.StatisticsDataRequest statisticsDataRequest);


        void sendData(ArrayList<BLEDeviceConnectionManager.DataClass> sendingDataArr);

        void parseSetStatisticsInfo(String data);

        void saveLastUpdated();


    }


    interface View extends BaseView

    {

        void viewInit();

        void setPresenter();


        void hideFragment();

        void onEventListeners();

        void setClickableFloatingButton(boolean enable);


    }
}
