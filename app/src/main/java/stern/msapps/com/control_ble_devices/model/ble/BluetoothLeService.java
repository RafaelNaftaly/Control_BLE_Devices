package stern.msapps.com.control_ble_devices.model.ble;

import android.annotation.TargetApi;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import stern.msapps.com.control_ble_devices.utils.dataParser.BleDataParser;

import static android.bluetooth.BluetoothDevice.TRANSPORT_LE;
import static stern.msapps.com.control_ble_devices.model.ble.BLEDeviceConnectionManager.DataClass.CharacteristicIO.NOTIFICATION;
import static stern.msapps.com.control_ble_devices.model.ble.BLEDeviceConnectionManager.DataClass.CharacteristicIO.READABLE;
import static stern.msapps.com.control_ble_devices.model.ble.BLEDeviceConnectionManager.DataClass.CharacteristicIO.WRITABLE;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BluetoothLeService extends Service {
    private final static String TAG = BluetoothLeService.class.getSimpleName();


    private final int TIME_OUT = 20000;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private int mConnectionState = STATE_DISCONNECTED;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;
    public static BluetoothLeService instance;

    private BluetoothGattCharacteristic lastCharacteristic;


    private int dataChunkCounter = 0;
    private int requestID;


    private int queClearCounter;
    private Handler handler;


    public final static String ACTION_GATT_CONNECTED = "com.stern.stern.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED = "com.stern.stern.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_DISCONNECTING = "com.stern.stern.ACTION_GATT_DISCONNECTING";
    public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.stern.stern.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE = "com.stern.stern.ACTION_DATA_AVAILABLE";
    public final static String ACTION_DATA_AVAILABLE_WRITE_RANGES = "com.stern.stern.ACTION_DATA_AVAILABLE__WRITE_RANGES";
    public final static String ACTION_COMMUNICATION_ISSUE = "com.stern.stern.COMMUNICATION_ISSUE";
    public final static String ACTION_GATT_AUTH_FAIL = "com.stern.stern.ACTION_GATT_AUTH_FAIL";
    public final static String ACTION_COMMUNICATION_ISSUE_NO_DATA_RECEIVED = "com.stern.stern.ACTION_COMMUNICATION_ISSUE_NO_DATA_RECEIVED";


    public final static String EXTRA_ID = "com.stern.stern.extra_id";


    public final static String EXTRA_DATA = "com.stern.stern.extra_data";
    public final static String EXTRA_CHARAC = "com.stern.stern.extra_charac";


    //The data queues Collection
    private DataQueues dataQueues;


    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {


        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {

            Log.d("CoonectionState", "onConnectionStateChange " + "Status = " + status + " newState = " + newState);

            String intentActionSTR;


            if (newState == BluetoothProfile.STATE_CONNECTED) {

                dataQueues.isFirstInQueues = true;

                intentActionSTR = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentActionSTR);

                Log.i(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                Log.i(TAG, "Attempting to start service discovery:" +
                        getmBluetoothGatt().discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {

                queClearCounter = 0;

                intentActionSTR = ACTION_GATT_DISCONNECTED;
                //   close();
                // disconnect();

                mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
                broadcastUpdate(intentActionSTR);

                //Must clear the queue on Disconnect
                dataQueues.clearQueue();
            }


        }


        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {

            Log.d(TAG, "onServicesDiscovered " + "Status = " + status);


            if (status == BluetoothGatt.GATT_SUCCESS) {

                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);

            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }


        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

            Log.d(TAG, "******onCharacteristicRead " + "BluetoothGattCharacteristic = " + characteristic.toString() + " status " + status);


            if (status == BluetoothGatt.GATT_SUCCESS) {

                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);

            } else if (status == 137) { //GATT_AUTH_FAIL
                broadcastUpdate(ACTION_GATT_AUTH_FAIL);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);

            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "****onCharacteristicWrite   -- BluetoothGatt.GATT_SUCCESS");
                Log.w("IDTEST", "..........onCharacteristicWrite........");




                if ((requestID < 200) || requestID > 600) {
                    readCharacteristic(characteristic);
                    if (requestID == 38) {
                        broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
                    }
                } else if (requestID > 300) {
                    broadcastUpdate(ACTION_DATA_AVAILABLE_WRITE_RANGES, characteristic);

                } else {
                    checkQueueNext();
                }


            } else {
                Log.d(TAG, "****onCharacteristicWrite   -- BluetoothGatt.GATT_FAILURE");

            }


        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Log.d(TAG, "onCharacteristicChanged " + "BluetoothGattCharacteristic = " + characteristic.toString());
            Log.d("IDTEST", "..........NOTIFICATION...RECEIVED........");

            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }


        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);

            if (status == 0) {//SUCCESS
                Log.d("DecriptorTest", "..............onDescriptorWrite()........//SUCCESS");
                Log.d("IDTEST", "..........NOTIFICATION Descriptor is written........");
                checkQueueNext();
            } else {
                Log.d("DecriptorTest", "..............onDescriptorWrite()........//UNSUCCESS");
            }


        }


    };

    private void broadcastUpdate(final String action) {

        final Intent intent = new Intent(action);


        sendBroadcast(intent);

    }


    private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic) {

        Intent intent = new Intent(action);

        String dataSTR = "";


        // For all other profiles, writes the data formatted in HEX.

        final byte[] data = characteristic.getValue();

        if (data != null && data.length > 0) {

            dataSTR = new String(data);


            final StringBuilder stringBuilder = new StringBuilder(data.length);
            for (byte byteChar : data) {
                stringBuilder.append(String.format("%02X ", byteChar));
            }

            //  convertToHEX(stringBuilder.toString());

            Bundle extraData = new Bundle();
            int handleID = -2;
            if (characteristic.getDescriptors().size() > 0) {

                BluetoothGattDescriptor descriptor = characteristic.getDescriptors().get(0);


                if (descriptor != null && descriptor.getValue() != null) {
                    handleID = BleDataParser.getInstance().bytesArrayToInteger(descriptor.getValue());
                }
            }

            extraData.putString(EXTRA_DATA, stringBuilder.toString());
            extraData.putString(EXTRA_CHARAC, characteristic.getUuid().toString());
            extraData.putInt(EXTRA_ID, (handleID != -2) ? handleID : requestID);

            if (handleID != -2) {
                handleID = -2;
                mBluetoothGatt.setCharacteristicNotification(characteristic, false);
            }

            Log.d("IDTEST", "............................BroadCast ID = " + String.valueOf((handleID != -1) ? handleID : requestID));


            intent.putExtra(EXTRA_DATA, extraData);

        }

        sendBroadcast(intent);
        checkQueueNext();


    }


    public BluetoothGatt getmBluetoothGatt() {
        return mBluetoothGatt;
    }


    public class LocalBinder extends Binder {

        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        dataQueues = new DataQueues();
        Log.d(TAG, "Binded");
        return mBinder;
    }


    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        close();
        dataQueues = null;
        Log.d(TAG, "onUnbind");
        return super.onUnbind(intent);
    }


    private final IBinder mBinder = new LocalBinder();

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        Log.d(TAG, "initialized");

        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {

            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();

        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }


    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     * @return Return true if the connection is initiated successfully. The connection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public boolean connect(final String address, final Context inContext) {

        if (mBluetoothAdapter == null || address == null) {

            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress) && getmBluetoothGatt() != null) {

            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");

            if (getmBluetoothGatt().connect()) {

                mConnectionState = STATE_CONNECTING;

                return true;
            } else {
                return false;
            }
        }

        BluetoothDevice device = null;
        if (address != null && !address.isEmpty()) {
            try {
                device = mBluetoothAdapter.getRemoteDevice(address);
            } catch (IllegalArgumentException ex) {

            }
        }


        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }


        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.

        final BluetoothDevice finalDevice = device;
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Log.d("UI thread", "I am the UI thread");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //For Version 6 and above
                mBluetoothGatt = finalDevice.connectGatt(BluetoothLeService.instance,
                        false, mGattCallback, TRANSPORT_LE);
            } else if (Build.VERSION.SDK_INT >= 21) {
                Method connectGattMethod = null;
                try {
                    //Using Reflection to OverLoading connectGatt() method
                    connectGattMethod = finalDevice.getClass().getMethod("connectGatt", Context.class, boolean.class, BluetoothGattCallback.class, int.class);
                    mBluetoothGatt = (BluetoothGatt) connectGattMethod.invoke(finalDevice, BluetoothLeService.this, false, mGattCallback, BluetoothDevice.TRANSPORT_LE);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }


            } else {
                mBluetoothGatt = finalDevice.connectGatt(BluetoothLeService.this, false, mGattCallback);
            }
            refreshDeviceCache(getmBluetoothGatt());

        }, 2000);


        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        return true;
    }


    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        if (mBluetoothAdapter == null || getmBluetoothGatt() == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        getmBluetoothGatt().disconnect();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {


        if (getmBluetoothGatt() == null) {
            return;
        }
        try {
            getmBluetoothGatt().close();
            mBluetoothGatt = null;

        } catch (Exception ex) {
            ex.printStackTrace();
            onDestroy();
        }

        onDestroy();
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {

        if (mBluetoothAdapter == null || getmBluetoothGatt() == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }


        boolean isCharacteristicReaded = getmBluetoothGatt().readCharacteristic(characteristic);

        if (!isCharacteristicReaded) {
            checkQueueNext();
        }

        Log.d(TAG, "Is Characteristic for (NO DATACLASS) = " + requestID + " = " + isCharacteristicReaded);


    }


    public void readCharacteristic(BLEDeviceConnectionManager.DataClass dataClass) {

        Log.d("IDTEST", "......readCharacteristic ID is = " + dataClass.getRequestID());

        if (mBluetoothAdapter == null || getmBluetoothGatt() == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }

        // this.requestID = dataClass.getRequestID();


        BluetoothGattCharacteristic characteristic = mBluetoothGatt.getService
                (dataClass.getServiceUUid()).getCharacteristic(dataClass.getCharacteristicsUUid());

        boolean isCharacteristicReaded = mBluetoothGatt.readCharacteristic(characteristic);

        if (!isCharacteristicReaded) {
            //  sendOnFailBroadcast(dataClass);
            checkQueueNext();
        }


        Log.d(TAG, "Is Characteristic for = " + dataClass.getRequestID() + " = " + isCharacteristicReaded);
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled        If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {


        if (mBluetoothAdapter == null || getmBluetoothGatt() == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }

        boolean isRegistered = getmBluetoothGatt().setCharacteristicNotification(characteristic, enabled);

        // Log.d(TAG, "Is the Notification registered = " + isRegistered + " ID is = " + dataClass.getRequestID());

    }


    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (getmBluetoothGatt() == null) return null;

        return getmBluetoothGatt().getServices();
    }


    /**
     * writeCharacteristic wrapper. If there are many readCharacteristic() the method add them to Queue.
     * @param dataClass
     */
    public void setWriteData(BLEDeviceConnectionManager.DataClass dataClass) {
        Log.d("IDTEST", "............................setWriteData");
        Log.d("IDTEST", "............................setWriteData characteristics ID is = " + dataClass.getCharacteristicsUUid());
        Log.d("IDTEST", "............................setWriteData ID is " + dataClass.getRequestID() + "\n" + "............................setWriteData Data is "
                + dataClass.getDataString());
        if (dataClass.getRequestID() == 0) {
            Log.d("IDTEST", "............................setWriteData");
        }

        if ((dataClass.getRequestID() > 200 && dataClass.getData().length == 1)) {
            writeToCharacteristic(dataClass);
            this.requestID = dataClass.getRequestID();
        } else if (dataQueues.isFirstInQueues()) {
            dataQueues.isFirstInQueues = false;
            writeToCharacteristic(dataClass);
            this.requestID = dataClass.getRequestID();

        } else {
            dataQueues.addToQueue(dataClass);


        }

        //  checkIfClearQueue(dataQueues);
    }

    /**
     * readCharacteristic wrapper. If there are many readCharacteristic() the method add them to Queue.
     *
     * @param dataClass
     */
    public void setReadCharacterictics(BLEDeviceConnectionManager.DataClass dataClass) {


        if (dataQueues.isFirstInQueues()) {
            dataQueues.isFirstInQueues = false;
            readCharacteristic(dataClass);

            this.requestID = dataClass.getRequestID();

        } else {
            dataQueues.addToQueue(dataClass);

        }

    }


    /**
     * setCharacteristicNotification wrapper. If there are many readCharacteristic() the method add them to Queue.
     * @param dataClass
     */
    public void setRegisterToNotification(BLEDeviceConnectionManager.DataClass dataClass) {

        Log.d("IDTEST", "............................setRegisterToNotification");
        Log.d("IDTEST", "............................Characteristic ID " + dataClass.getCharacteristicsUUid());

        Log.d("IDTEST", "......setRegisterToNotification ID = " + dataClass.getRequestID());


        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        BluetoothGattCharacteristic characteristic = getmBluetoothGatt().getService(dataClass.getServiceUUid()).getCharacteristic(dataClass.getCharacteristicsUUid());

        boolean isRegistered = mBluetoothGatt.setCharacteristicNotification(characteristic, dataClass.isEnableNotification());
        Log.w("IDTEST", "............................Is Notification registered? = " + isRegistered);

        BluetoothGattDescriptor descriptor = characteristic.getDescriptors().get(0);

        if (descriptor != null) {
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);

            if (dataClass.getRequestID() != 33) {
                descriptor.setValue(BleDataParser.getInstance().intTobyteArray(dataClass.getRequestID()));
            }

            boolean isDescriptorRegistered = mBluetoothGatt.writeDescriptor(descriptor);

            if (!isDescriptorRegistered) {
                //    sendOnFailBroadcast(dataClass);
            }

            Log.d("IDTEST", "............................Is descriptor registered? = " + isDescriptorRegistered + " / Notification ID = " + dataClass.getRequestID());

        } else {
            Log.d("IDTEST", "......setRegisterToNotification Descriptor is Null");
        }

    }


    private void writeToCharacteristic(BLEDeviceConnectionManager.DataClass dataClass) {


        if (dataClass != null) {


            this.requestID = dataClass.getRequestID();
            //   setTimeOut(this.requestID);
            boolean isRegistered = false;

            if (mBluetoothGatt != null) {
                BluetoothGattCharacteristic characteristic = mBluetoothGatt.getService(dataClass.getServiceUUid()).getCharacteristic(dataClass.getCharacteristicsUUid());
                if (characteristic != null) {
                    characteristic.setValue(dataClass.getData());


                    isRegistered = mBluetoothGatt.writeCharacteristic(characteristic);
                    Log.w("IDTEST", "......writeToCharacteristic() isRegistered = " + isRegistered + " ID is = " + dataClass.getRequestID());
                    if (!isRegistered) {

                        //  sendOnFailBroadcast(dataClass);
                        checkQueueNext();
                    }
                } else {
                    sendOnFailBroadcast(dataClass);
                }
            }


            Log.d(TAG, "--------Bytes size is:" + dataClass.getData().length);
            Log.w(TAG, "The writeCharacteristic was Registered well - " + isRegistered + " For Call =  " + dataClass.getRequestID());

        }


    }


    /**
     * When we did not succeed to writeToCharacteristic / readCharacteristic or register to Notification we must inform the BLEDeviceConnectionManager using Broadcast
     *
     * @param dataClass
     */
    private void sendOnFailBroadcast(BLEDeviceConnectionManager.DataClass dataClass) {

        Bundle extraData = new Bundle();

        extraData.putString(EXTRA_DATA, ACTION_COMMUNICATION_ISSUE);
        extraData.putString(EXTRA_CHARAC, dataClass.getCharacteristicsUUid().toString());
        extraData.putInt(EXTRA_ID, dataClass.getRequestID());

        Intent intent = new Intent(ACTION_COMMUNICATION_ISSUE);

        intent.putExtra(EXTRA_DATA, extraData);

        sendBroadcast(intent);
    }


    /**
     * If que size is bigger than 6 and it contain the same data (in our case is StatusGet) clear it all.
     *
     * @param dataQueues
     */
    private void checkIfClearQueue(DataQueues dataQueues) {

        Log.d(TAG, "DataQue size is : " + dataQueues.getArrSize());
        if (dataQueues.getArrSize() >= 6) {

            try {
                for (int i = 0; i < dataQueues.getArrSize() - 1; i++) {
                    byte[] dadaQ = dataQueues.getQueueArray().get(i).getData();
                    byte[] dataNextQ = dataQueues.getQueueArray().get(i + 1).getData();
                    if (Arrays.equals(dadaQ, dataNextQ)) {
                        ++queClearCounter;
                        if (queClearCounter == 5 && dataQueues.getArrSize() <= 6) {
                            queClearCounter = 0;
                            dataQueues.clearQueue();

                        } else if (queClearCounter > 5 && dataQueues.getArrSize() > 20) {
                            queClearCounter = 0;
                            dataQueues.clearQueue();
                        }
                    }

                }
            } catch (IndexOutOfBoundsException ex) {
                ex.printStackTrace();
            }
        }


    }


    /**
     * Checking if there is data in queue
     */
    private void checkQueueNext() {
        //After receiving characteristic reply, check if there is data in queues. If does, send it.
        if (dataQueues.getArrSize() >= 1) {

            BLEDeviceConnectionManager.DataClass dataClass = dataQueues.popQueue();

            //  requestID = dataClass.getRequestID();

            if (dataClass != null) { //Checking if there is a data in queue

                if (dataClass.getCharacteristicIOArrayList() != null) {
                    for (int i = 0; i < dataClass.getCharacteristicIOArrayList().size(); i++) {
                        if (dataClass.getCharacteristicIOArrayList().get(i) == READABLE) { //If the dataClass is READABLE
                            Log.d(TAG, "The data in IF id = " + dataClass.getRequestID() + "Has READABLE ");
                            readCharacteristic(dataClass);
                        }
                        if (dataClass.getCharacteristicIOArrayList().get(i) == WRITABLE) {//If the dataClass is WRITABLE
                            Log.d(TAG, "The data in IF id = " + dataClass.getRequestID() + "Has WRITABLE ");

                            writeToCharacteristic(dataClass);

                        }
                        if (dataClass.getCharacteristicIOArrayList().get(i) == NOTIFICATION) { //If the dataClass is NOTIFICATION
                            Log.d(TAG, "The data in IF id = " + dataClass.getRequestID() + "Has NOTIFICATION ");
                            // writeToDescriptor(dataClass);
                        }
                    }
                }


                if (dataQueues.getArrSize() == 0) {
                    dataQueues.isFirstInQueues = true;
                }
            }


        } else {
            dataQueues.isFirstInQueues = true;
        }
    }

//    private void setTimeOut(int id) {
//
//        int newId = id;
//
//        if(handler != null){
//            handler.removeCallbacks(null);
//        }
//
//        handler = new Handler(Looper.getMainLooper());
//
//        handler.postDelayed(() -> {
//
//            Log.d("IDTEST", "before id = " + id + " the requestID = " + requestID);
//
//            if (newId == requestID) {
//                broadcastUpdate(ACTION_GATT_DISCONNECTED);
//            }
//
//        }, TIME_OUT);
//
//
//    }


    /**
     * Creating a queue for sending data
     */
    class DataQueues {
        private ArrayList<BLEDeviceConnectionManager.DataClass> queueArr;
        private boolean isFirstInQueues = true;


        protected DataQueues() {
            queueArr = new ArrayList<BLEDeviceConnectionManager.DataClass>();
        }

        protected void addToQueue(BLEDeviceConnectionManager.DataClass dataClass) {
            queueArr.add(dataClass);
            isFirstInQueues = false;
            Log.e("IDTEST", "............................Data is ADDED TO QUEUE...." + " data id is" + dataClass.getRequestID());

        }


        protected BLEDeviceConnectionManager.DataClass popQueue() {

            if (queueArr.size() >= 1) {


                if (queueArr.size() == 1) { //If we are going to pop the last element...
                    isFirstInQueues = true;
                }
                Log.e("IDTEST", "............................Data is POPPED FROM QUEUE...." + " data id is" + queueArr.get(0).getRequestID());
                return queueArr.remove(0);

            } else {
                isFirstInQueues = true;
                return null;
            }

        }

        protected void clearQueue() {
            queueArr.clear();
        }

        protected int getArrSize() {
            return queueArr.size();
        }

        protected ArrayList<BLEDeviceConnectionManager.DataClass> getQueueArray() {
            return queueArr;
        }

        public boolean isFirstInQueues() {
            return isFirstInQueues;
        }
    }

    /**
     * invoke an hide method for clear the device cache, in this way we can have device with same
     * name and mac that export different service/char in different connection (maybe because we
     * are developing on it)
     *
     * @param gatt connection with the device
     * @return tue il the call is invoke correctly
     */
    private static boolean refreshDeviceCache(BluetoothGatt gatt) {
        try {
            Method localMethod = gatt.getClass().getMethod("refresh");
            if (localMethod != null) {
                boolean done = false;
                int nTry = 0;
                while (!done && nTry < 10) {
                    done = ((Boolean) localMethod.invoke(gatt));
                    nTry++;
                }//while
                Log.d(TAG, "Refreshing Device Cache: " + done);
                return done;
            }//if
        } catch (Exception localException) {
            Log.e(TAG, "An exception occurred while refreshing device cache.");
        }//try-catch
        return false;
    }//refreshDeviceCache


    @Override
    public void onCreate() {
        instance = this;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        dataQueues = null;
        instance = null;

    }

}
