package stern.msapps.com.control_ble_devices.model.ble;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.Toast;


import com.crashlytics.android.Crashlytics;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;


import static stern.msapps.com.control_ble_devices.model.ble.BLEDeviceConnectionManager.DataClass.CharacteristicIO.NOTIFICATION;
import static stern.msapps.com.control_ble_devices.model.ble.BLEDeviceConnectionManager.DataClass.CharacteristicIO.READABLE;
import static stern.msapps.com.control_ble_devices.model.ble.BLEDeviceConnectionManager.DataClass.CharacteristicIO.WRITABLE;


// Singletone Class.
public class BLEDeviceConnectionManager {


    private static BLEDeviceConnectionManager instance;


    private final String TAG = BLEDeviceConnectionManager.class.getSimpleName();


    private static final long SCAN_PERIOD = 10000;

    private Context context;
    private static ConnectionStatus connectionStatus;


    public boolean mConnected;
    private boolean isScanning;
    private long scanningStartTime; // = System.currentTimeMillis();
    //  private OnScanCounterChanges onScanCounterChangesListener;
    private int senDingCounterTime;
    private int lastSendingCounterTime;


    public BluetoothAdapter mBluetoothAdapter;
    private BluetoothManager bluetoothManager = null;
    private BluetoothDevice mBluetoothDevice;
    private BluetoothLeService bluetoothLeService;

    private OnScanConnectionResponse onScanConnectionResponse;

    private BluetoothLeScanner bluetoothLeScanner;

    private short sendedDataSize = 0;

    private Thread serviceThread;

    private DataWatchDog dataWatchDod;


    private BLEDeviceConnectionManager() {
    }

    public static BLEDeviceConnectionManager getInstance() {

        if (instance == null) {
            instance = new BLEDeviceConnectionManager();

        }

        return instance;
    }


    /**
     * Init method
     *
     * @param context
     * @param listener
     */
    public void init(Context context, OnScanConnectionResponse listener) {
        this.context = context;


        Log.d(TAG, "BLEDeviceConnectionManager init()....");

        //If the previous listener wasn't released
        if (onScanConnectionResponse != null) {
            setOnScanConnectionResponse(null);
        }

        this.setOnScanConnectionResponse(listener);
        bleConnectionInit();

        context.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());

        serviceThread = new Thread() {
            @Override
            public void run() {
                Intent gattServiceIntent = new Intent(context, BluetoothLeService.class);
                context.bindService(gattServiceIntent, serviceConnectionLifeCycle, Context.BIND_AUTO_CREATE);

            }
        };
        serviceThread.start();


        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        dataWatchDod = new DataWatchDog();

    }


    /**
     * Sett Scan filters to detect only Stern products
     *
     * @return
     */

    private List<ScanFilter> getScanFilter() {


        ScanFilter filter = null;
        List<ScanFilter> filters = new ArrayList<>();

        ArrayList<UUID> filterListUUID = new ArrayList<>();
        filterListUUID.add(BLEGattAttributes.STERN_SOAP_UUID);
        filterListUUID.add(BLEGattAttributes.STERN_FOAM_SOAP_UUID);
        filterListUUID.add(BLEGattAttributes.STERN_FAUCET_UUID);
        filterListUUID.add(BLEGattAttributes.STERN_SHOWER_UUID);
        filterListUUID.add(BLEGattAttributes.STERN_URINAL_UUID);
        filterListUUID.add(BLEGattAttributes.STERN_WC_UUID);
        filterListUUID.add(BLEGattAttributes.STERN_FAUCET_UUID);
        filterListUUID.add(BLEGattAttributes.STERN_WAWE_ON_OFF_UUID);
        filterListUUID.add(BLEGattAttributes.STERN_UNKNOWN_UUID);

        for (UUID uuid : filterListUUID) {
            filter = new ScanFilter.Builder().setServiceUuid(new ParcelUuid(uuid)).build();
            filters.add(filter);
        }


        return filters;


    }


    //Initialize BLE components
    private void bleConnectionInit() {
        bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        bluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();

    }


    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    /**
     * This BroadcastReceiver receiving Data (Ble connection status + BLE data) from BluetoothLeService.
     */
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            final String action = intent.getAction();


            if (BluetoothLeService.ACTION_COMMUNICATION_ISSUE.equals(action)
                    || BluetoothLeService.ACTION_GATT_AUTH_FAIL.equals(action)) {
                dataWatchDod.stopWatch();
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action) || BluetoothLeService.ACTION_DATA_AVAILABLE_WRITE_RANGES.equals(action)) {
                dataWatchDod.removeFromWatch();
            }


            Log.d(TAG, TAG + " Event from Service ......mGattUpdateReceiver = " + action);

            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {

                sendDataToPresenter();
                setConnectionStatus(ConnectionStatus.CONNECTED);


                mConnected = true;
                //  updateConnectionState(R.string.connected);
                //  invalidateOptionsMenu();
                Log.d(TAG, action);


            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {

                mConnected = false;
                dataWatchDod.stopWatch();

                if (onScanConnectionResponse != null) {
                    onScanConnectionResponse.onBleConnectionStatus(ConnectionStatus.DISCONNECTED, null);
                }
                setConnectionStatus(ConnectionStatus.DISCONNECTED);


                //If the Device was disconnected, Start to scan for the device
                //  scanBleDevice(true);

                Log.d(TAG, "+++++++++++++Device Disconnected");

                //   updateConnectionState(R.string.disconnected);
                //   invalidateOptionsMenu();
                //    clearUI();

                Log.d(TAG, action);
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTING.equals(action)) {

                if (onScanConnectionResponse != null) {
                    onScanConnectionResponse.onBleConnectionStatus(ConnectionStatus.DISCONNECTING, null);
                }
                setConnectionStatus(ConnectionStatus.DISCONNECTING);

            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {

                // Show all the supported services and characteristics on the user interface.
                //    displayGattServices(bluetoothLeService.getSupportedGattServices());

                Log.d(TAG, action);
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {


                if (onScanConnectionResponse != null) {
                    if (sendedDataSize > 0) {
                        sendedDataSize--;
                    }
                    onScanConnectionResponse.onDataReceived(intent.getBundleExtra(BluetoothLeService.EXTRA_DATA));
                }


            } else if (BluetoothLeService.ACTION_COMMUNICATION_ISSUE.equals(action)) { //There was an issue write or read data to BLE Gatt
                if (sendedDataSize > 0) {
                    sendedDataSize--;
                }


                int requestID = intent.getBundleExtra(BluetoothLeService.EXTRA_DATA).getInt(BluetoothLeService.EXTRA_ID);
                Log.d(TAG, TAG + " Event from Service ......mGattUpdateReceiver = " + action + "The requestId is " + requestID);
                onScanConnectionResponse.onDataReceived(intent.getBundleExtra(BluetoothLeService.EXTRA_DATA));
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE_WRITE_RANGES.equals(action)) {
                if (onScanConnectionResponse != null) {
                    if (sendedDataSize > 0) {
                        sendedDataSize--;
                    }

                    Bundle bundle = intent.getBundleExtra(BluetoothLeService.EXTRA_DATA);
                    bundle.remove(BluetoothLeService.EXTRA_DATA);
                    bundle.putString(BluetoothLeService.EXTRA_DATA, action);
                    onScanConnectionResponse.onDataReceived(intent.getBundleExtra(BluetoothLeService.EXTRA_DATA));
                }
            } else if (BluetoothLeService.ACTION_GATT_AUTH_FAIL.equals(action)) {
                Bundle bundle = (intent.getBundleExtra(BluetoothLeService.EXTRA_DATA) == null) ? new Bundle() : intent.getBundleExtra(BluetoothLeService.EXTRA_DATA);

                bundle.putString(BluetoothLeService.EXTRA_DATA, action);
                onScanConnectionResponse.onDataReceived(bundle);
            }


        }
    };


    /**
     * Waiting for bluetoothLeService.getSupportedGattServices() to be full and then sending the ServerConnection status
     * after 6 unsuccessfully tries, sending ConnectionStatus.DISCONNECTED.
     */
    private void sendDataToPresenter() {


        final int[] counter = {0}; // Counter to count the numbers of attempts


        Handler handler = new Handler();
        Runnable task = new Runnable() {
            @Override
            public void run() {

                if (counter[0] < 6 && !Thread.interrupted()) {

                    //If we have the Gatt Services, pass them forward
                    if (bluetoothLeService.getSupportedGattServices().size() != 0) {

                        onScanConnectionResponse.onBleConnectionStatus(ConnectionStatus.CONNECTED, bluetoothLeService.getSupportedGattServices());

                        Thread.interrupted();

                    } else { // if the don't have the Gatt Services yet, go for another loop
                        counter[0]++;

                        handler.postDelayed(this::run, 500L);
                    }

                } else {
                    //If we didn't received the Services, send DISCONNECTED to the user.
                    onScanConnectionResponse.onBleConnectionStatus(ConnectionStatus.DISCONNECTED, null);
                    bluetoothLeService.disconnect();
                }

            }
        };

        handler.post(task);

    }


    /**
     * This ServiceConnection class is responsible for Service lifecycle.
     */

    private final ServiceConnection serviceConnectionLifeCycle = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {

            bluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();


            if (!bluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");

            }


        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {


            bluetoothLeService = null;
        }
    };


    public void connectDevice(String macAddress) {
        try {

            stopScan();
            mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(macAddress);

            // mBluetoothAdapter.stopLeScan(mLeScanCallback);


            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Log.d("UI thread", "I am the UI thread");
                    // Automatically connects to the device upon successful start-up initialization.
                    bluetoothLeService.connect(macAddress, context);


                }
            });

        } catch (IllegalArgumentException ex) {
            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();

        }
    }


    /**
     * Scanning BLE devise callBack
     */
    private ScanCallback leScanCallback = new ScanCallback() {

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onScanResult(int callbackType, ScanResult result) {


            Log.d(TAG, "... BLE Device was found = " + result.getDevice().getAddress());

            if (onScanConnectionResponse != null) {
                onScanConnectionResponse.onScannedDeviceData(result);
            }

        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);

            switch (errorCode) {
                case ScanCallback.SCAN_FAILED_ALREADY_STARTED:
                    /**
                     * Fails to start scan as BLE scan with the same settings is already started by the app.
                     */

                    //TODO... Display Alert Scan already started, please wait
                    break;

                case ScanCallback.SCAN_FAILED_APPLICATION_REGISTRATION_FAILED:
                    /**
                     * Fails to start scan as app cannot be registered.
                     */


                    Toast.makeText(context, "There is an issue registering the app with Android while requesting to start a BLE scan. Please restart your device", Toast.LENGTH_SHORT).show();

                    /**
                     * TODO....
                     * Display a dialog -> Title - Unable to start scan
                     * Message - There is an issue registering the app with Android while requesting to start a BLE scan. Please restart your device.
                     */

                    break;

                case ScanCallback.SCAN_FAILED_FEATURE_UNSUPPORTED:
                    /**
                     * Fails to start power optimized scan as this feature is not supported.
                     */
                    break;

                case ScanCallback.SCAN_FAILED_INTERNAL_ERROR:
                    /**
                     * Fails to start scan due an internal error
                     */

                    /**
                     * TODO....
                     * Display a dialog -> Title - Unable to start scan
                     * Message - There is an issue registering the app with Android while requesting to start a BLE scan. Please restart your device.
                     */
                    break;


            }


        }

    };


    /**
     * Start scanning for BLE Devices
     */
    public void startScan() {

        if (mBluetoothAdapter != null) {


            Handler handler = new Handler();

            handler.postDelayed(() -> {
                stopScan();


            }, SCAN_PERIOD);

            Thread thread = new Thread() {
                @Override
                public void run() {

                    Looper.prepare();
                    Log.d(TAG, "Scan Started..." + Thread.currentThread().getName());


                    //enable for onBatchScanResults for receiving a list of results
                    // ScanSettings scanSettings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).setReportDelay(1000).build();


                    ScanSettings scanSettings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
                    // bluetoothLeScanner.startScan(getScanFilter(), scanSettings, leScanCallback);

                    try {
                        bluetoothLeScanner.startScan(getScanFilter(), scanSettings, leScanCallback);
                        //  bluetoothLeScanner.startScan(leScanCallback);
                        scanningStartTime = System.currentTimeMillis();


                    } catch (Exception e) {
                        Log.d(TAG, "OnScan failed exception = " + e.getMessage());

                    }

                    isScanning = true;


                }
            };
            thread.start();


        }


    }


    /**
     * Stop scanning for BLE Devices
     */
    public void stopScan() {
        AsyncTask.execute(() -> {
            Log.d(TAG, "Scan Stopped...");

            try {
                bluetoothLeScanner.stopScan(leScanCallback);
                scanningStartTime = 0;


                isScanning = false;
            } catch (Exception e) {


            }
        });
    }

    private static IntentFilter makeGattUpdateIntentFilter() {

        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTING);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.ACTION_COMMUNICATION_ISSUE);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE_WRITE_RANGES);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_AUTH_FAIL);



        return intentFilter;
    }


    /**
     * @param data     DataClass object with all the necessary data to be send to BluetoothLeService
     * @param listener listener for response from BluetoothLeService
     */
    public void sendData(DataClass data, OnScanConnectionResponse listener) {

        this.setOnScanConnectionResponse(listener);
        Log.d("IDTEST", "*********____ sendData " + data.getCharacteristicIOArrayList().get(0));
        Log.i("IDTEST", ".........the sending data is: ");
        if (data.getData() != null) {
            for (byte b : data.getData()) {
                Log.i("IDTEST", "\n");
                Log.i("IDTEST", String.valueOf(Integer.valueOf(b)) + "\n");
            }
        }


        if (data.getCharacteristicIOArrayList() != null) {


            for (int i = 0; i < data.getCharacteristicIOArrayList().size(); i++) {
                if (data.getCharacteristicIOArrayList().get(i) == READABLE) {
                    Log.d(TAG, "The data in IF id = " + data.getRequestID() + "Has READABLE ");
                    if (data.isAddToDaSize()) {
                        sendedDataSize++;
                    }

                    bluetoothLeService.setReadCharacterictics(data);
                    dataWatchDod.addToWatch(data);
                }
                if (data.getCharacteristicIOArrayList().get(i) == WRITABLE) {
                    Log.d(TAG, "The data in IF id = " + data.getRequestID() + "Has WRITABLE ");
                    if (data.isAddToDaSize()) {
                        sendedDataSize++;
                    }

                    bluetoothLeService.setWriteData(data);
                    dataWatchDod.addToWatch(data);
                }
                if (data.getCharacteristicIOArrayList().get(i) == NOTIFICATION) {
                    Log.d(TAG, "The data in IF id = " + data.getRequestID() + "Has NOTIFICATION ");

                    bluetoothLeService.setRegisterToNotification(data);

                }
            }
        }

    }

    /**
     * @param macAddress :String
     * @return :BluetoothDevice.
     */
    public BluetoothDevice getBluetoothDeviceByMacAddress(String macAddress) {
        return mBluetoothAdapter.getRemoteDevice(macAddress);
    }

    public void unPairDevice(String macAddress) {

        BluetoothDevice device = getBluetoothDeviceByMacAddress(macAddress);

        try {
            //Reflection of the removeBond() method from BluetoothDevice
            Method m = device.getClass()
                    .getMethod("removeBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }


    public static ConnectionStatus getConnectionStatus() {
        return connectionStatus;
    }

    public void setConnectionStatus(ConnectionStatus connectionStatus) {
        this.connectionStatus = connectionStatus;
    }

    public BLEDeviceConnectionManager setOnScanConnectionResponse(OnScanConnectionResponse onScanConnectionResponse) {

        if (this.onScanConnectionResponse != null) {
            this.onScanConnectionResponse = null;
        }

        this.onScanConnectionResponse = onScanConnectionResponse;

        return this;
    }

    public short getSendedDataSize() {
        return sendedDataSize;
    }

    public void setSendedDataSize(short sendedDataSize) {
        this.sendedDataSize = sendedDataSize;
    }

    public BluetoothDevice getmBluetoothDevice() {
        return mBluetoothDevice;
    }

    public void setmBluetoothDevice(BluetoothDevice mBluetoothDevice) {
        this.mBluetoothDevice = mBluetoothDevice;
    }

    public boolean isScanning() {
        return isScanning;
    }

    public void setScanning(boolean scanning) {
        isScanning = scanning;
    }





    /**
     * BLE communication interface
     * -onBleConnectionStatus - passig the connection status
     * -onDataReceived - passing the data from device
     * -onScannedDeviceData - passing the Mac Address from scanning
     */
    public interface OnScanConnectionResponse {

        void onBleConnectionStatus(ConnectionStatus status, List<BluetoothGattService> bluetoothGattService);

        void onDataReceived(Bundle data);

        void onScannedDeviceData(ScanResult scanResults);
    }


    public void stopAll(Context context) {
        //   context.unregisterReceiver(mGattUpdateReceiver);
//        context.unbindService(serviceConnectionLifeCycle);


        try {
            context.unbindService(serviceConnectionLifeCycle);
            bluetoothLeService.close();
            sendedDataSize = 0;
            mBluetoothAdapter = null;
            bluetoothManager = null;
            mBluetoothDevice = null;
            onScanConnectionResponse = null;
            mConnected = false;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public enum ConnectionStatus {
        CONNECTED, NOT_CONNECTED, NOT_FOUND, DISCONNECTED, RECONNECT, DISCONNECTING, PHONE_BL_STATE_OFF, PHONE_BL_STATE_ON,
    }


    /**
     * This class is holding all the necessary information regarding the sending data and been past to BluetoothLeService
     */
    public static class DataClass {

        private UUID serviceUUid;                                       // Service UUID
        private UUID characteristicsUUid;                               // Characteristic UUID
        private int requestID;                                          // request id
        private byte[] data;                                            // the data we are want to send to BLE device
        private ArrayList<CharacteristicIO> characteristicIOArrayList;  // array of CharacteristicIO
        private boolean enableNotification;                             // if set to true, this object will register to notification
        private boolean addToDaSize;                                    // if set to true this dataclass will be add to stuck to indicate if all data was received
        private HashMap<String, String> dataMap;                        // Handle Strings of current Service & Characteristic UUID's + request ID


        public UUID getServiceUUid() {
            return serviceUUid;
        }

        public void setServiceUUid(UUID serviceUUid) {
            this.serviceUUid = serviceUUid;
        }

        public UUID getCharacteristicsUUid() {
            return characteristicsUUid;
        }

        public void setCharacteristicsUUid(UUID characteristicsUUid) {
            this.characteristicsUUid = characteristicsUUid;
        }

        public int getRequestID() {
            return requestID;
        }

        public void setRequestID(int requestID) {
            this.requestID = requestID;
        }

        public byte[] getData() {
            return data;
        }

        public void setData(byte[] data) {
            this.data = data;
        }


        public ArrayList<CharacteristicIO> getCharacteristicIOArrayList() {
            return characteristicIOArrayList;
        }


        public void setCharacteristicIOArrayList(ArrayList<CharacteristicIO> characteristicIOArrayList) {
            this.characteristicIOArrayList = characteristicIOArrayList;
        }

        public boolean isEnableNotification() {
            return enableNotification;
        }

        public void setEnableNotification(boolean enableNotification) {
            this.enableNotification = enableNotification;
        }

        public boolean isAddToDaSize() {
            return addToDaSize;
        }

        public void setAddToDaSize(boolean addToDaSize) {
            this.addToDaSize = addToDaSize;
        }

        public HashMap<String, String> getDataMap() {

            dataMap = new HashMap<>();
            dataMap.put("serviceUUid", serviceUUid.toString());
            dataMap.put("characteristicsUUid", characteristicsUUid.toString());
            dataMap.put("requestID", String.valueOf(requestID));

            final StringBuilder stringBuilder = new StringBuilder(data.length);
            for (byte byteChar : data) {
                stringBuilder.append(String.format("%02X ", byteChar));
            }

            dataMap.put("data", stringBuilder.toString());

            for (CharacteristicIO ca : characteristicIOArrayList) {
                dataMap.put("CharacteristicIO", ca.name);
            }


            return dataMap;
        }

        /**
         * enum of characteristics functions (Read, Write, Notify)
         */
        public enum CharacteristicIO {

            WRITABLE("Writable"),
            READABLE("Readble"),
            NOTIFICATION("Notification");

            private String name;

            CharacteristicIO(String str) {
                this.name = str;
            }

            public String getName() {
                return name;
            }
        }

        public String getDataString() {

            String str = "";
            for (int i = 0; i < data.length; i++) {
                str += data[i] + "\n";
            }

            return str;
        }


    }

    public interface OnScanCounterChanges {
        void onChange(int counter);
    }


    /**
     * A watchdog class
     * Sending .ACTION_COMMUNICATION_ISSUE_NO_DATA_RECEIVED if the data was't received after  *waitingTime
     */
    class DataWatchDog {
        private DataWatchDog dataWatchDod;
        private Handler handler = new Handler();
        private Runnable runnable;
        private final int waitingTime = 20000;
        private int counter;
        private int counterWatcher;
        private ArrayList<DataClass> dataClassArrayList = new ArrayList<>();


        public void addToWatch(DataClass dataClass) {
            counter++;
            counterWatcher = counter;
            dataClassArrayList.add(dataClass);
            startWatch();
            Log.d("WATCH", "...........THE DATA WAS added to Watch");

        }

        private void startWatch() {

            if (runnable != null) {
                handler.removeCallbacks(runnable);
                runnable = null;

            }
            runnable = () -> {
                Log.d("WATCH", "...........ITS TIME TO CHECK .......re");
                Log.d("WATCH", "The counter is " + counter + " The counterWatcher is " + counterWatcher);

                if ((counterWatcher + counter) == 0) {
                    removeFromWatch();
                }

                if (runnable != null && counter > 0 && counterWatcher == counter) {
                    Bundle bundle = new Bundle();
                    bundle.putString(BluetoothLeService.EXTRA_DATA, BluetoothLeService.ACTION_COMMUNICATION_ISSUE_NO_DATA_RECEIVED);
                    onScanConnectionResponse.onDataReceived(bundle);
                    sendDataToCrashlytics(dataClassArrayList.get(counter - 1).getDataMap());
                    Log.d("WATCH", "Running.........." + " the Counter is " + counter + "The counterWatcher is " + counterWatcher);
                    Log.d("WATCH", "...........THE DATA WAS NOT RECEIVED");
                }
            };
            handler.postDelayed(runnable, waitingTime);


        }

        public void removeFromWatch() {
            if (counter > 0) {
                dataClassArrayList.remove(counter - 1);
                counter--;
            }
            if (counter == 0) {
                dataClassArrayList.clear();
                stopWatch();
            }
            Log.d("WATCH", "...........THE DATA WAS removed from Watch");
        }

        private void stopWatch() {
            try {
                handler.removeCallbacks(runnable);
                runnable = null;
                counterWatcher = 0;
                counter = 0;
            } catch (Exception ex) {

            }
        }


    }

    /**
     * Sending data to Crashlytics
     *
     * @param data which will be send to Crashlytics
     */
    private void sendDataToCrashlytics(HashMap<String, String> data) {


        Crashlytics.logException(new RuntimeException("ex"));
        Crashlytics.setString("new Set String", "Value");
        Crashlytics.setString("serviceUUid", data.get("serviceUUid"));
        Crashlytics.setString("characteristicsUUid", data.get("characteristicsUUid"));
        Crashlytics.setString("requestID", data.get("requestID"));
        Crashlytics.setString("data", data.get("data"));
        Crashlytics.setString("CharacteristicIO", data.get("CharacteristicIO"));

        Log.i("crash", ".....sendDataToCrashlytics(");

    }


}
