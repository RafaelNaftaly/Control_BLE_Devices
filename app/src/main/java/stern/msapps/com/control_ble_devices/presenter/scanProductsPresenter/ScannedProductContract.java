package stern.msapps.com.control_ble_devices.presenter.scanProductsPresenter;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import stern.msapps.com.control_ble_devices.model.dataTypes.SternProduct;
import stern.msapps.com.control_ble_devices.presenter.BaseMvpPresenter;
import stern.msapps.com.control_ble_devices.view.BaseView;

public interface ScannedProductContract {


    interface Presenter extends BaseMvpPresenter<View> {
        void initBle();

        void bleStartScan() throws Exception;

        void onStart();

        void onPause();

        void onDestroy();


        void connectToDevice();


        void displayWrongPassKeyDilog();

        SternProduct getDataFromPreviouslyConnected(SternProduct sternProduct);

        ArrayList<SternProduct> getSternProductsFromDB();


        String getParringCode();


        boolean isScanning();

        void displayNamePasswordDialog(SternProduct sternProduct);

        void refreshDBforScannedProduct();

        void deleteSharedPrefScheduledEvents(String macAddress);


    }


    interface View extends BaseView {


        void bleScanningResult(SternProduct product);

        void onNearByNewFilterClicked(android.view.View v);

        boolean checkIfContains(String macAddress);


        void addSwipeHelperToRecyclerView(RecyclerView recyclerView);

        void logOut();


    }
}
