package stern.msapps.com.control_ble_devices.presenter.informationScreenPresenter;

import stern.msapps.com.control_ble_devices.model.dataTypes.SternProduct;
import stern.msapps.com.control_ble_devices.presenter.BaseMvpPresenter;
import stern.msapps.com.control_ble_devices.view.BaseView;


public interface InformationContract {


    interface Presenter extends BaseMvpPresenter<View> {
        void getAllData(boolean withDate);

        void sendName(String name);

        void setName(String name);

        void getCalenderDate();

        void startEventBusEvents();

        void setCalenderDate(byte[] calenderDate);

        void sendInitialParringDate(byte[] date);

        void setSerialNumber(String data);

        void setDataFromSternObject(SternProduct sternProduct);


        void getInitialParringDate();

        void onStart();

        void onPause();

        void onDestroy();

        void displayNamePasswordDialog();

        void saveModelToDB();




    }


    interface View extends BaseView {
        void setNameText(String name);

        void setSerialNumber(String sNumber);

        void setProductionType(String str);

        void setSoftwareVersion(String version);

        void setManufacturyDate(String date);

        void setDateOfUnit(String date);

        void setTimeOfUnit(String time);

        void setInitialParringDate(String date);

        void setLocationNAme(String name);


    }
}
