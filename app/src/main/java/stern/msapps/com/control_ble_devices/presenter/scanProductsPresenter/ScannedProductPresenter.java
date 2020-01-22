package stern.msapps.com.control_ble_devices.presenter.scanProductsPresenter;


import android.app.Activity;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import org.greenrobot.eventbus.Subscribe;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import androidx.navigation.Navigation;
import butterknife.ButterKnife;
import stern.msapps.com.control_ble_devices.model.ble.BLEDeviceConnectionManager;
import stern.msapps.com.control_ble_devices.model.broadcasts.BleParringBroadcast;
import stern.msapps.com.control_ble_devices.model.dataTypes.SternProduct;
import stern.msapps.com.control_ble_devices.model.enums.SternTypes;
import stern.msapps.com.control_ble_devices.presenter.BasePresenter;
import stern.msapps.com.control_ble_devices.R;
import stern.msapps.com.control_ble_devices.model.dataTypes.SternProductFactory;
import stern.msapps.com.control_ble_devices.eventBus.Events;
import stern.msapps.com.control_ble_devices.eventBus.GlobalBus;
import stern.msapps.com.control_ble_devices.model.roomDataBase.database.SternRoomDatabase;
import stern.msapps.com.control_ble_devices.utils.DialogHelper;
import stern.msapps.com.control_ble_devices.view.fragments.ProductInformationFragment;

public class ScannedProductPresenter extends BasePresenter<ScannedProductContract.View> implements ScannedProductContract.Presenter {

    private final String TAG = ScannedProductPresenter.class.getSimpleName();

    private Context context;
    private SternProduct sternProduct;
    private boolean isNotFirst;
    private ArrayList<SternProduct> sternProductsFromDb;


    public ScannedProductPresenter(Context context) {
        super();
        this.context = context;

        ButterKnife.bind(((Activity) context));


        sternProductsFromDb = getSternProductsFromDB();


    }

    @Override
    public void initBle() {

        BLEDeviceConnectionManager.getInstance().stopAll(context);


        BLEDeviceConnectionManager.getInstance().init(getmView().getActivityContext(), this);


    }

    @Override
    public void bleStartScan() {

        try {
            if (BLEDeviceConnectionManager.getInstance().mBluetoothAdapter.isEnabled()) {
                BLEDeviceConnectionManager.getInstance().startScan();

            } else {
                Toast.makeText(context, "Please turn on the bluetooth", Toast.LENGTH_SHORT).show();

                context.startActivity(new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS));
            }

        } catch (Exception e) {

            e.printStackTrace();

        }
    }

    @Override
    public void onStart() {
        if (!isNotFirst) {
            GlobalBus.getBus().register(this);
        }

        Log.d(TAG, "onStart()");
    }

    @Override
    public void onPause() {
        GlobalBus.getBus().unregister(this);
    }

    @Override
    public void onDestroy() {
        GlobalBus.getBus().unregister(this);
        Log.d("", "");
    }


    @Override
    public void connectToDevice() {


        BleParringBroadcast.setParringKey(getParringCode());
        // BleParringBroadcast.setParringKey("123456");
        DialogHelper.getInstance().displayLoaderProgressDialog(context, context.getResources().getString(R.string.app_dialog_connecting)).show();
        BLEDeviceConnectionManager.getInstance().connectDevice(sternProduct.getMacAddress());
    }


    @Subscribe(sticky = true)
    public void wrongPasskey(Events.PincodeIssue isPasskeyOk) {
        displayWrongPassKeyDilog();
        isNotFirst = true;

    }

    @Override
    public void displayWrongPassKeyDilog() {


        DialogHelper.getInstance().displayOneButtonDialog(getmView().getContext(),
                getmView().getContext().getString(R.string.name_passkey_wrong_passkey),
                getmView().getContext().getString(R.string.name_passkey_ok)).show();
    }

    @Override
    public SternProduct getDataFromPreviouslyConnected(SternProduct sternProduct) {


        for (SternProduct stp : sternProductsFromDb) {
            if (sternProduct.getMacAddress().equals(stp.getMacAddress())) {

                sternProduct.setPreviouslyConnected(true);
                sternProduct.setLastConnected(stp.getLastConnected());
                sternProduct.setName(stp.getName());
                sternProduct.setPairingCode(stp.getPairingCode());
                sternProduct.setId(stp.getId());
                sternProduct.setLastUpdate(stp.getLastUpdate());
                sternProduct.setLocationNAme(stp.getLocationNAme());


            }
        }
        return sternProduct;
    }

    @Override
    public ArrayList<SternProduct> getSternProductsFromDB() {

        return (ArrayList<SternProduct>) SternRoomDatabase.getDatabase(context).sternProductDao().getAll();
    }

    @Override
    public String getParringCode() {

        if (sternProduct.getPairingCode() != null)

        {

            if (!sternProduct.getPairingCode().isEmpty()) {
                return sternProduct.getPairingCode();
            }
        }
        ArrayList<SternProduct> sternProductArrayList = (ArrayList<SternProduct>) SternRoomDatabase.getDatabase(getmView().getContext()).sternProductDao().getAll();

        for (SternProduct stp : sternProductArrayList) {
            if (stp.getMacAddress().equals(sternProduct.getMacAddress())) {
                return stp.getPairingCode();
            }
        }

        return "";
    }


    @Override
    public boolean isScanning() {
        return BLEDeviceConnectionManager.getInstance().isScanning();
    }

    @Override
    public void displayNamePasswordDialog(SternProduct sternProduct) {
        View view = LayoutInflater.from(context).inflate(R.layout.name_pass_key_layout, null);

        EditText nameEDT = (EditText) view.findViewById(R.id.name_pass_layout_recognition_name_value);
        EditText pasKeyEDT = (EditText) view.findViewById(R.id.name_pass_layout_recognition_paskey_value);


        (view.findViewById(R.id.name_pass_layout_confirm_button)).setOnClickListener(view1 -> {


            String locationNAme = nameEDT.getText().toString();
            String passKey = pasKeyEDT.getText().toString();


            if (locationNAme.isEmpty() || passKey.isEmpty()) {

                if (locationNAme.isEmpty()) {
                    nameEDT.setError(getmView().getContext().getString(R.string.name_passkey_name_empty_error));
                }

                if (passKey.isEmpty()) {
                    pasKeyEDT.setError(getmView().getContext().getString(R.string.name_passkey_pass_key_empty_error));
                }


            } else if (pasKeyEDT.getText().toString().length() > 6) {

                pasKeyEDT.setError(getmView().getContext().getString(R.string.name_passkey_pass_length_error));


            } else { //Connect
                sternProduct.setLocationNAme(locationNAme);
                //   SternRoomDatabase.getDatabase(context).sternProductDao().setLocationNAme(name, sternProduct.getMacAddress());
                sternProduct.setPairingCode(passKey);

                DialogHelper.getInstance().dismiss();
                connectToDevice();

            }


        });


        DialogHelper.getInstance().displayCustomLayoutDialog(context, view).show();

    }

    @Override
    public void refreshDBforScannedProduct() {
        sternProductsFromDb = getSternProductsFromDB();
    }

    @Override
    public void deleteSharedPrefScheduledEvents(String macAddress) {
        super.deleteSharedPrefScheduledEvents(macAddress);
    }

    @Override
    public void onBleConnectionStatus(BLEDeviceConnectionManager.ConnectionStatus status, List<BluetoothGattService> bluetoothGattService) {
        handleConnectionStatus(status, bluetoothGattService);
    }


    @Override
    protected void parseOnDataReceived(String dataSTR, String uuid, int requestID) {
        handleData(dataSTR, uuid, requestID);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onScannedDeviceData(ScanResult results) {
        handleScanningResults(results);
    }


    /**
     * Handle connectionStatus
     *
     * @param status
     */
    private void handleConnectionStatus(BLEDeviceConnectionManager.ConnectionStatus status, List<BluetoothGattService> bluetoothGattService) {

        switch (status) {
            case DISCONNECTED:

                DialogHelper.getInstance().dismiss();
                DialogHelper.getInstance().displayOneButtonDialog(getmView().getContext(), getmView().getContext().getResources().getString(R.string.dialog_disconnect),
                        getmView().getContext().getResources().getString(R.string.name_passkey_ok)).show();

                Log.d(TAG, ".......+++++++*******Are we on Fragment = "
                        + Navigation.findNavController((Activity) context, R.id.main_activity_host_fragment).getCurrentDestination().getLabel());


                Log.d(TAG, ".......+++++++*******Are we on scanning Fragment = "
                        + Navigation.findNavController((Activity) context, R.id.main_activity_host_fragment).getCurrentDestination().getLabel().equals("fragment_scanned_products"));

                if (!Navigation.findNavController((Activity) context, R.id.main_activity_host_fragment).getCurrentDestination().getLabel().equals("fragment_scanned_products")) {

                    ((Activity) context).onBackPressed();

                }

                return;
            case CONNECTED:
                //TODO... Handle Connected
                onConnect(bluetoothGattService);
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

    private void onConnect(List<BluetoothGattService> bluetoothGattService) {


        try {

            sternProduct.setMacAddress(BLEDeviceConnectionManager.getInstance().getmBluetoothDevice().getAddress());

            Events.SterProductTransmition event = new Events.SterProductTransmition(sternProduct);
            GlobalBus.getBus().postSticky(event);


            ProductInformationFragment.getInstance();

            Navigation.findNavController((Activity) context, R.id.main_activity_host_fragment).navigate(R.id.productInformationFragment);
        } catch (Exception ex) {

        }


    }

    public void connectToBle(SternProduct sternProduct) {
        Log.d("DialogHelper", "displayLoaderProgressDialog " + " connectToBle");


        this.sternProduct = sternProduct;


        if (sternProduct.getType() == SternTypes.UNKNOWN) {

            String message = context.getResources().getString(R.string.scanned_product_fragment_check_connection);
            String bntMessage = context.getResources().getString(R.string.scanned_product_fragment_check_connection_ok);

            DialogHelper.getInstance().displayOneButtonDialog(context, message, bntMessage).show();
        } else if (!this.sternProduct.isPreviouslyConnected()) {
            BLEDeviceConnectionManager.getInstance().unPairDevice(sternProduct.getMacAddress());
            displayNamePasswordDialog(sternProduct);


        } else {
            connectToDevice();
        }
    }


    private void handleData(String dataSTR, String uuid, int requestID) {
        //No Data here

    }

    /**
     * Retrieving SternProduct MacAddress and advertised UUID Service
     *
     * @param result scanning result from BLEDeviceConnectionManager
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void handleScanningResults(ScanResult result) {

        UUID uuid = null;
        SternProduct sternProduct = null;


        if (result.getScanRecord().getServiceUuids().size() > 0) {
            uuid = result.getScanRecord().getServiceUuids().get(0).getUuid();
        }

        if (uuid != null && !getmView().checkIfContains(result.getDevice().getAddress())) {

            SternTypes sternTypes = SternTypes.getType(uuid);

            if (sternTypes != null) {

                SternProductFactory factory = new SternProductFactory();
                sternProduct = factory.getSternType(sternTypes);
                sternProduct.setMacAddress(result.getDevice().getAddress());
                sternProduct.setLastConnected(result.getScanRecord().getDeviceName());

            }
        } else {
            return;
        }

        sternProduct = getDataFromPreviouslyConnected(sternProduct);


        getmView().bleScanningResult(sternProduct);


    }

}
