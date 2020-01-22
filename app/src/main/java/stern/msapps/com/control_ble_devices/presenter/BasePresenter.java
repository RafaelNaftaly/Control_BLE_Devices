package stern.msapps.com.control_ble_devices.presenter;

import android.app.Activity;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.le.ScanResult;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import androidx.navigation.Navigation;
import stern.msapps.com.control_ble_devices.R;
import stern.msapps.com.control_ble_devices.model.ble.BLEDeviceConnectionManager;
import stern.msapps.com.control_ble_devices.model.ble.BluetoothLeService;
import stern.msapps.com.control_ble_devices.utils.AppSharedPref;
import stern.msapps.com.control_ble_devices.utils.Constants;
import stern.msapps.com.control_ble_devices.utils.DialogHelper;
import stern.msapps.com.control_ble_devices.view.BaseView;

public class BasePresenter<V extends BaseView> implements BaseMvpPresenter<V>, BLEDeviceConnectionManager.OnScanConnectionResponse {

    private V mView;

    @Override
    public void attach(V view) {
        this.mView = view;

    }

    @Override
    public void detach() {
        this.mView = null;
    }

    @Override
    public boolean isAttached() {
        return mView != null;
    }

    public V getmView() {
        return mView;
    }


    @Override
    public void onBleConnectionStatus(BLEDeviceConnectionManager.ConnectionStatus status, List<BluetoothGattService> bluetoothGattService) {
        switch (status) {
            case DISCONNECTED:
                //TODO... Handle Disconnection


                DialogHelper.getInstance().dismiss();


                DialogHelper.getInstance().displayOneButtonDialog(getmView().getContext(), getmView().getContext().getResources().getString(R.string.dialog_disconnect),
                        getmView().getContext().getResources().getString(R.string.name_passkey_ok)).show();

                if (!Navigation.findNavController((Activity) mView.getContext(), R.id.main_activity_host_fragment).getCurrentDestination().getLabel().equals("fragment_scanned_products")) {

                    ((Activity) mView.getContext()).onBackPressed();

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


                if (!Navigation.findNavController((Activity) mView.getContext(), R.id.main_activity_host_fragment).getCurrentDestination().getLabel().equals("fragment_scanned_products")) {

                    ((Activity) mView.getContext()).onBackPressed();

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
    public void onDataReceived(Bundle data) {
        String dataSTR = data.getString(BluetoothLeService.EXTRA_DATA);

        if (dataSTR == BluetoothLeService.ACTION_COMMUNICATION_ISSUE_NO_DATA_RECEIVED) {

            Log.d("CError", dataSTR);

            DialogHelper.getInstance().dismiss();
            DialogHelper.getInstance().displayDataReceiveError(getmView().getContext()).show();

            return;
        }
        String uuid = data.getString(BluetoothLeService.EXTRA_CHARAC);
        int requestID = data.getInt(BluetoothLeService.EXTRA_ID);

        parseOnDataReceived(dataSTR, uuid, requestID);
    }

    @Override
    public void onScannedDeviceData(ScanResult scanResults) {

    }

    protected void parseOnDataReceived(String dataSTR, String uuid, int requestID) {

    }


    public void deleteSharedPrefScheduledEvents(String macAddress) {
        Log.d("DeleteTest", "deleteEvetsByMacAddressInSharedPref()");

        Set<String> iDsSet = AppSharedPref.getInstance(mView.getContext()).getPrefSet(Constants.SHARED_PREF_USER_HANDLE_ID);


        if (iDsSet != null) {


            if (iDsSet.size() == 0) {
                return;
            }

            for (String str : new ArrayList<String>(iDsSet)) {
                if (str.contains(macAddress)) {
                    iDsSet.remove(str);
                }
            }

            if (iDsSet.size() > 0) {
                AppSharedPref.getInstance(mView.getContext()).

                        savePrefSet(Constants.SHARED_PREF_USER_HANDLE_ID, iDsSet);
            }
        }
    }


}
