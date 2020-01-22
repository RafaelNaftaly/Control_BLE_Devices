package stern.msapps.com.control_ble_devices.presenter.informationScreenPresenter;


import android.app.Activity;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.le.ScanResult;
import android.content.Context;


import android.util.Log;
import android.view.View;
import android.widget.Toast;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;


import androidx.navigation.Navigation;

import stern.msapps.com.control_ble_devices.model.ble.BLEDeviceConnectionManager;
import stern.msapps.com.control_ble_devices.model.ble.BluetoothLeService;
import stern.msapps.com.control_ble_devices.model.dataTypes.SternProduct;
import stern.msapps.com.control_ble_devices.model.roomDataBase.dao.SternProductDao;
import stern.msapps.com.control_ble_devices.presenter.BasePresenter;
import stern.msapps.com.control_ble_devices.presenter.operateScreenPresenter.OperatePresenter;
import stern.msapps.com.control_ble_devices.R;

import stern.msapps.com.control_ble_devices.eventBus.Events;
import stern.msapps.com.control_ble_devices.eventBus.GlobalBus;
import stern.msapps.com.control_ble_devices.model.ble.BLEGattAttributes;
import stern.msapps.com.control_ble_devices.model.roomDataBase.database.SternRoomDatabase;
import stern.msapps.com.control_ble_devices.utils.dataParser.BleDataParser;
import stern.msapps.com.control_ble_devices.utils.DialogHelper;


public class InformationPresenter extends BasePresenter<InformationContract.View> implements InformationContract.Presenter,
        DialogHelper.OnDateTimePikerListener, View.OnClickListener {

    private final String TAG = InformationContract.class.getSimpleName();
    private final static int bleDataIdentifireInitial = 100;


    private Context context;
    private SternProduct sternProduct;
    private BleDataParser bleDataParser;
    private ArrayList<BLEDeviceConnectionManager.DataClass> sendingDataArr = new ArrayList<>();
    private boolean isInitialParringWasDone;
    private boolean getDatafromObject;


    public InformationPresenter(Context context, boolean loadFromDevice) {
        this.context = context;


        bleDataParser = new BleDataParser();

        BLEDeviceConnectionManager.getInstance().setOnScanConnectionResponse(this);

        DialogHelper.getInstance().dismiss();

        getDatafromObject = !loadFromDevice;

        if (loadFromDevice) {
            getInitialParringDate();
        }
        // getCalenderDate();

        //TODO.. check if need to update Date
        //TODO... else call  getScheduledDataFromDevice();
        // getScheduledDataFromDevice();


    }


    @Override
    public void getAllData(boolean withDate) {
        //TODO sending a request to BLE... ++ Must create a List of

        ArrayList<BLEDeviceConnectionManager.DataClass.CharacteristicIO> characteristicIO = null;

//        //GetName
        BLEDeviceConnectionManager.DataClass name = new BLEDeviceConnectionManager.DataClass();
        name.setData(bleDataParser.shortToHexArr((short) 129));
        name.setRequestID(BleDataIdentifier.PRODUCT_NAME.value);
        characteristicIO = new ArrayList<>();
        name.setAddToDaSize(true);
        characteristicIO.add(BLEDeviceConnectionManager.DataClass.CharacteristicIO.WRITABLE);
        name.setCharacteristicIOArrayList(characteristicIO);
        name.setServiceUUid(UUID.fromString(BLEGattAttributes.UUID_STERN_DATA_INFORMATION_SERVICE));
        name.setCharacteristicsUUid(UUID.fromString(BLEGattAttributes.UUID_STERN_DATA_INFORMATION_CHARACTIRISTICS_READ));
        sendingDataArr.add(name);


        //Get PRODUCT_SERIAL_NUMBER date
        BLEDeviceConnectionManager.DataClass serialNumber = new BLEDeviceConnectionManager.DataClass();
        serialNumber.setData(bleDataParser.shortToHexArr((short) 131));
        serialNumber.setRequestID(BleDataIdentifier.PRODUCT_SERIAL_NUMBER.value);
        characteristicIO = new ArrayList<>();
        serialNumber.setAddToDaSize(true);
        characteristicIO.add(BLEDeviceConnectionManager.DataClass.CharacteristicIO.WRITABLE);
        serialNumber.setCharacteristicIOArrayList(characteristicIO);
        serialNumber.setServiceUUid(UUID.fromString(BLEGattAttributes.UUID_STERN_DATA_INFORMATION_SERVICE));
        serialNumber.setCharacteristicsUUid(UUID.fromString(BLEGattAttributes.UUID_STERN_DATA_INFORMATION_CHARACTIRISTICS_READ));
        sendingDataArr.add(serialNumber);


        if (withDate) {
            //Get production date
            BLEDeviceConnectionManager.DataClass manufactureDate = new BLEDeviceConnectionManager.DataClass();
            manufactureDate.setData(bleDataParser.shortToHexArr((short) 132));
            manufactureDate.setRequestID(BleDataIdentifier.MANUFACTURE_DATE.value);
            characteristicIO = new ArrayList<>();
            manufactureDate.setAddToDaSize(true);
            characteristicIO.add(BLEDeviceConnectionManager.DataClass.CharacteristicIO.WRITABLE);
            manufactureDate.setCharacteristicIOArrayList(characteristicIO);
            manufactureDate.setServiceUUid(UUID.fromString(BLEGattAttributes.UUID_STERN_DATA_INFORMATION_SERVICE));
            manufactureDate.setCharacteristicsUUid(UUID.fromString(BLEGattAttributes.UUID_STERN_DATA_INFORMATION_CHARACTIRISTICS_READ));
            sendingDataArr.add(manufactureDate);
        }


        //Get  BLE SW ver
        BLEDeviceConnectionManager.DataClass swVersion = new BLEDeviceConnectionManager.DataClass();
        swVersion.setData(bleDataParser.shortToHexArr((short) 133));
        swVersion.setRequestID(BleDataIdentifier.PRODUCT_SOFTWARE_VERSION.value);
        characteristicIO = new ArrayList<>();
        swVersion.setAddToDaSize(true);
        characteristicIO.add(BLEDeviceConnectionManager.DataClass.CharacteristicIO.WRITABLE);
        swVersion.setCharacteristicIOArrayList(characteristicIO);
        swVersion.setServiceUUid(UUID.fromString(BLEGattAttributes.UUID_STERN_DATA_INFORMATION_SERVICE));
        swVersion.setCharacteristicsUUid(UUID.fromString(BLEGattAttributes.UUID_STERN_DATA_INFORMATION_CHARACTIRISTICS_READ));
        sendingDataArr.add(swVersion);


        sendData(sendingDataArr);


    }

    private void sendData(ArrayList<BLEDeviceConnectionManager.DataClass> sendingDataArr) {


        if (!DialogHelper.getInstance().isDialogShown()) {
            DialogHelper.getInstance().displayLoaderProgressDialog(context, context.getResources().getString(R.string.loading)).show();

        }
        int arrSize = sendingDataArr.size();

        for (int i = 0; i < arrSize; i++) {

            BLEDeviceConnectionManager.DataClass dataClass = sendingDataArr.remove(0);
            Log.d("TEST", "The data ID that was sended is = " + dataClass.getRequestID());
            BLEDeviceConnectionManager.getInstance().sendData(dataClass, InformationPresenter.this);
        }

    }

    @Override
    public void sendName(String name) {

        ArrayList<BLEDeviceConnectionManager.DataClass.CharacteristicIO> characteristicIO = null;

        String nameStr = name + "$&";

        byte[] bytes = bleDataParser.stringToASCII(nameStr);


        BLEDeviceConnectionManager.DataClass nameData = new BLEDeviceConnectionManager.DataClass();
        nameData.setData(bytes);
        nameData.setRequestID(BleDataIdentifier.PRODUCT_NAME.value);
        characteristicIO = new ArrayList<>();
        nameData.setAddToDaSize(true);
        characteristicIO.add(BLEDeviceConnectionManager.DataClass.CharacteristicIO.WRITABLE);
        nameData.setCharacteristicIOArrayList(characteristicIO);
        nameData.setServiceUUid(UUID.fromString(BLEGattAttributes.UUID_STERN_DATA_INFORMATION_SERVICE));
        nameData.setCharacteristicsUUid(UUID.fromString(BLEGattAttributes.UUID_STERN_DATA_INFORMATION_CHARACTIRISTICS_READ));
        sendingDataArr.add(nameData);


        sendData(sendingDataArr);


    }


    @Override
    public void getCalenderDate() {

        ArrayList<BLEDeviceConnectionManager.DataClass.CharacteristicIO> characteristicIO = null;

        BLEDeviceConnectionManager.DataClass calendar = new BLEDeviceConnectionManager.DataClass();
        calendar.setRequestID(BleDataIdentifier.CALENDER_DATE_READ.value);
        characteristicIO = new ArrayList<>();
        calendar.setAddToDaSize(true);
        characteristicIO.add(BLEDeviceConnectionManager.DataClass.CharacteristicIO.READABLE);
        calendar.setCharacteristicIOArrayList(characteristicIO);
        calendar.setServiceUUid(UUID.fromString(BLEGattAttributes.UUID_STERN_CALENDER_SERVICE));
        calendar.setCharacteristicsUUid(UUID.fromString(BLEGattAttributes.UUID_STERN_CALENDER_CARACHTERICTICS_READ_WRITE));
        sendingDataArr.add(calendar);

        sendData(sendingDataArr);


    }

    @Override
    public void startEventBusEvents() {
        Log.d(TAG, "startEventBusEvents()");
        Events.PincodeIssue pincodeIssueEvent = new Events.PincodeIssue(false);
        GlobalBus.getBus().postSticky(pincodeIssueEvent);
    }

    @Override
    public void setCalenderDate(byte[] calenderDate) {

        ArrayList<BLEDeviceConnectionManager.DataClass.CharacteristicIO> characteristicIO = null;

        BLEDeviceConnectionManager.DataClass calendar = new BLEDeviceConnectionManager.DataClass();
        calendar.setRequestID(BleDataIdentifier.CALENDER_DATE_WRITE.value);
        calendar.setData(calenderDate);
        characteristicIO = new ArrayList<>();
        calendar.setAddToDaSize(true);
        characteristicIO.add(BLEDeviceConnectionManager.DataClass.CharacteristicIO.WRITABLE);
        calendar.setCharacteristicIOArrayList(characteristicIO);
        calendar.setServiceUUid(UUID.fromString(BLEGattAttributes.UUID_STERN_CALENDER_SERVICE));
        calendar.setCharacteristicsUUid(UUID.fromString(BLEGattAttributes.UUID_STERN_CALENDER_CARACHTERICTICS_READ_WRITE));
        sendingDataArr.add(calendar);

        sendData(sendingDataArr);

    }

    @Override
    public void sendInitialParringDate(byte[] date) {

        ArrayList<BLEDeviceConnectionManager.DataClass.CharacteristicIO> characteristicIO = null;

        byte[] request = new byte[1];
        request[0] = 2;
        byte[] combined = Arrays.copyOf(request, request.length + date.length);
        System.arraycopy(date, 0, combined, request.length, date.length);

        BLEDeviceConnectionManager.DataClass nameData = new BLEDeviceConnectionManager.DataClass();
        nameData.setData(combined);
        nameData.setRequestID(BleDataIdentifier.INITIAL_PAIRING_DATE.value);
        characteristicIO = new ArrayList<>();
        nameData.setAddToDaSize(true);
        characteristicIO.add(BLEDeviceConnectionManager.DataClass.CharacteristicIO.WRITABLE);
        nameData.setCharacteristicIOArrayList(characteristicIO);
        nameData.setServiceUUid(UUID.fromString(BLEGattAttributes.UUID_STERN_DATA_INFORMATION_SERVICE));
        nameData.setCharacteristicsUUid(UUID.fromString(BLEGattAttributes.UUID_STERN_DATA_INFORMATION_CHARACTIRISTICS_READ));
        sendingDataArr.add(nameData);


        sendData(sendingDataArr);

    }

    @Override
    public void setSerialNumber(String data) {
        String serialNumber = bleDataParser.parseSerialNumber(data);
        getmView().setSerialNumber(serialNumber);
        sternProduct.setSerialNumber(serialNumber);
    }

    @Override
    public void setDataFromSternObject(SternProduct sternProduct) {
        getmView().setNameText(sternProduct.getName());
        getmView().setSerialNumber(sternProduct.getSerialNumber());
        getmView().setProductionType(sternProduct.getType().getName());
        getmView().setSoftwareVersion(sternProduct.getSwVersion());
        Date manufactoryDate = sternProduct.getManifacturingDate();
        if (manufactoryDate != null) {
            getmView().setManufacturyDate(bleDataParser.parseDate(manufactoryDate));
        }


        Calendar calendar = Calendar.getInstance();
        if (sternProduct.getUnitDate() != null) {
            calendar.setTimeInMillis(sternProduct.getUnitDate().getTime());
            this.setDateOfUnit(calendar);
        }

        Date initialParingDate = sternProduct.getInitialParringDate();
        if (initialParingDate != null) {
            getmView().setInitialParringDate(bleDataParser.parseDate(initialParingDate));
        }


        DialogHelper.getInstance().dismiss();

    }

    @Override
    public void getInitialParringDate() {

        ArrayList<BLEDeviceConnectionManager.DataClass.CharacteristicIO> characteristicIO = null;

        //Get install date
        BLEDeviceConnectionManager.DataClass initialDate = new BLEDeviceConnectionManager.DataClass();
        initialDate.setData(bleDataParser.shortToHexArr((short) 130));
        initialDate.setRequestID(BleDataIdentifier.INITIAL_PAIRING_DATE.value);
        characteristicIO = new ArrayList<>();
        initialDate.setAddToDaSize(true);
        characteristicIO.add(BLEDeviceConnectionManager.DataClass.CharacteristicIO.WRITABLE);
        initialDate.setCharacteristicIOArrayList(characteristicIO);
        initialDate.setServiceUUid(UUID.fromString(BLEGattAttributes.UUID_STERN_DATA_INFORMATION_SERVICE));
        initialDate.setCharacteristicsUUid(UUID.fromString(BLEGattAttributes.UUID_STERN_DATA_INFORMATION_CHARACTIRISTICS_READ));
        sendingDataArr.add(initialDate);

        sendData(sendingDataArr);
    }

    @Override
    public void onStart() {
        /**
         * Check if previously wasn't Register
         */
        if (!EventBus.getDefault().isRegistered(this)) {
            GlobalBus.getBus().register(this);
        }
    }

    @Override
    public void onPause() {
        GlobalBus.getBus().unregister(this);
    }

    @Override
    public void onDestroy() {
        GlobalBus.getBus().unregister(this);
    }

    @Override
    public void displayNamePasswordDialog() {
        Log.d(TAG, ".........displayNamePasswordDialog().******");
        startEventBusEvents();

    }

    @Override
    public void saveModelToDB() {

        boolean contain = false;
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        String formatedDate = sdf.format(date);
      //  sternProduct.setLastConnected(formatedDate);
        SternRoomDatabase sternRoomDatabase = SternRoomDatabase.getDatabase(context);
        SternProductDao sternProductDao = sternRoomDatabase.sternProductDao();

        ArrayList<SternProduct> sternProductsArr = (ArrayList<SternProduct>) sternProductDao.getAll();


        for (SternProduct stp : sternProductsArr) {
            if (stp.getMacAddress().equals(sternProduct.getMacAddress())) {
                contain = true;

            }
        }

        if (!contain) {
            sternProduct.setPreviouslyConnected(true);
            sternProductDao.insert(sternProduct);

        } else {
            sternProduct.setPreviouslyConnected(true);
         //   sternProductDao.updateLastConnected(formatedDate, sternProduct.getMacAddress());

        }

    }



    @Override
    public void detach() {
        GlobalBus.getBus().unregister(this);
        super.detach();
    }

    @Subscribe(sticky = true)
    public void getSternProduct(Events.SterProductTransmition sterProduct) {
        this.sternProduct = sterProduct.getSternProduct();

        if (getDatafromObject) {
            setDataFromSternObject(this.sternProduct);

        }
        getmView().setLocationNAme(sternProduct.getLocationNAme());


    }


    @Override
    public void onBleConnectionStatus(BLEDeviceConnectionManager.ConnectionStatus status, List<BluetoothGattService> bluetoothGattService) {
        switch (status) {
            case DISCONNECTED:
                //TODO... Handle Disconnection


                DialogHelper.getInstance().dismiss();


                DialogHelper.getInstance().displayOneButtonDialog(getmView().getContext(), getmView().getContext().getResources().getString(R.string.dialog_disconnect),
                        getmView().getContext().getResources().getString(R.string.name_passkey_ok)).show();

                if (!Navigation.findNavController((Activity) context, R.id.main_activity_host_fragment).getCurrentDestination().getLabel().equals("fragment_scanned_products")) {

                    ((Activity) context).onBackPressed();

                }


                return;
            case CONNECTED:

                sternProduct.getType();
                //TODO... Handle Connected

                return;

            case NOT_FOUND:
                //TODO... Handle Not found
                return;

            case DISCONNECTING:
                //TODO... Handle Disconnecting

//
//                if (!Navigation.findNavController((Activity) context, R.id.main_activity_host_fragment).getCurrentDestination().getLabel().equals("fragment_scanned_products")) {
//
//                    ((Activity) context).onBackPressed();
//
//                }

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
    protected void parseOnDataReceived(String dataSTR, String uuid, int requestID) {

        Log.d("TEST", "The data ID that was Received is = " + requestID + "\n" + " The data is : " + dataSTR);

        getmView().setProductionType(sternProduct.getType().getName());

        if (requestID == OperatePresenter.BleDataIdentifier.OPEN_CLOSE_VALVE_NOTIFICATION.ordinal()) {
            Toast.makeText(context, "*******************The notification arrived to Information screen", Toast.LENGTH_SHORT).show();
        }


        if (requestID == BleDataIdentifier.PRODUCT_NAME.value) {
            if (dataSTR.equals(BluetoothLeService.ACTION_COMMUNICATION_ISSUE)) {

            } else {
                setName(dataSTR);
            }

        } else if (requestID == BleDataIdentifier.PRODUCT_SERIAL_NUMBER.value) {
            if (dataSTR.equals(BluetoothLeService.ACTION_COMMUNICATION_ISSUE)) {

            } else {
                setSerialNumber(dataSTR);

            }

        } else if (requestID == BleDataIdentifier.PRODUCT_SOFTWARE_VERSION.value) {
            if (dataSTR.equals(BluetoothLeService.ACTION_COMMUNICATION_ISSUE)) {

            } else {
                setSoftwareVersion(dataSTR);
            }

        } else if (requestID == BleDataIdentifier.MANUFACTURE_DATE.value) {

            if (dataSTR.equals(BluetoothLeService.ACTION_COMMUNICATION_ISSUE)) {

            } else {
                setManufacturingDate(dataSTR);
            }

        } else if (requestID == BleDataIdentifier.DATE_OF_UNIT.value) {
            if (dataSTR.equals(BluetoothLeService.ACTION_COMMUNICATION_ISSUE)) {

            } else {
                //TODO....
            }

        } else if (requestID == BleDataIdentifier.INITIAL_PAIRING_DATE.value) {
            if (dataSTR.equals(BluetoothLeService.ACTION_COMMUNICATION_ISSUE)) {

            } else {
                saveModelToDB();
                setInitialPairingDate(dataSTR);
            }

        } else if (requestID == BleDataIdentifier.CALENDER_DATE_READ.value) {
            if (dataSTR.equals(BluetoothLeService.ACTION_COMMUNICATION_ISSUE)) {

            } else {
                handleCalendarDate(dataSTR);
            }

        } else if (requestID == BleDataIdentifier.CALENDER_DATE_WRITE.value) {
            if (dataSTR.equals(BluetoothLeService.ACTION_COMMUNICATION_ISSUE)) {

            } else {
                handleCalendarDate(dataSTR);
            }


        }

        //The Pin code is incorrect
        if (requestID == 0 && dataSTR.equals(BluetoothLeService.ACTION_GATT_AUTH_FAIL)) {
            //TODO.... Display dialog
            DialogHelper.getInstance().dismiss();


            displayNamePasswordDialog();

            return;

        }


        if (BLEDeviceConnectionManager.getInstance().getSendedDataSize() == 0
                && requestID != BleDataIdentifier.CALENDER_DATE_READ.value
                && requestID != BleDataIdentifier.INITIAL_PAIRING_DATE.value) {

            //    DialogHelper.getInstance().dismiss();
            if (DialogHelper.getInstance().isDialogShown()) {
                DialogHelper.getInstance().dismiss();

            }

        }

    }


    private void handleCalendarDate(String data) {


        if (data != null) {

            Date date = bleDataParser.getDate(data, true);


            Calendar calendar = Calendar.getInstance();

            Calendar deviceDateCalendar = Calendar.getInstance(TimeZone.getDefault());
            deviceDateCalendar.setTime(date);


            if ((calendar.get(Calendar.YEAR) != deviceDateCalendar.get(Calendar.YEAR))
                    || (calendar.get(Calendar.MONTH) != (deviceDateCalendar.get(Calendar.MONTH)))
                    || (calendar.get(Calendar.DAY_OF_MONTH) != deviceDateCalendar.get(Calendar.DAY_OF_MONTH)) ||
                    (!equalsMinutes(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), deviceDateCalendar.get(Calendar.HOUR_OF_DAY), deviceDateCalendar.get(Calendar.MINUTE)))) {

                DialogHelper.getInstance().dialogCurrentDateAndTime(context, this).show();
            } else {
                isInitialParringWasDone = true;
                setDateOfUnit(deviceDateCalendar);
                getAllData(true);
            }
        } else { // It is first time
            DialogHelper.getInstance().dialogCurrentDateAndTime(context, this).show();

        }
    }

    /**
     * Calculating the Delta of Hour + minutes
     *
     * @param calendarHour
     * @param calendarMinutes
     * @param deviceHour
     * @param deviceMinutes
     * @return boolean if delta of time more than 1 minutes
     */
    private boolean equalsMinutes(int calendarHour, int calendarMinutes, int deviceHour, int deviceMinutes) {

        int newCalendarMinutes = (calendarHour * 60) + calendarMinutes;
        int newDeviceMinutes = (deviceHour * 60) + deviceMinutes;

        if (newCalendarMinutes == newDeviceMinutes) {
            return true;
        }

        if (Math.abs(newCalendarMinutes - newDeviceMinutes) <= 2) {
            return true;
        }

        return false;

    }

    private void setDateOfUnit(Calendar calendar) {


        String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        String year = String.valueOf(calendar.get(Calendar.YEAR));

        String hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        String minutes = (calendar.get(Calendar.MINUTE) < 10) ? "0" + String.valueOf(calendar.get(Calendar.MINUTE)) : String.valueOf(calendar.get(Calendar.MINUTE));
        String seconds = (calendar.get(Calendar.SECOND) < 10) ? "0" + String.valueOf(calendar.get(Calendar.SECOND)) : String.valueOf(calendar.get(Calendar.SECOND));


        getmView().setDateOfUnit(day + "/" + month + "/" + year);
        getmView().setTimeOfUnit(hour + ":" + minutes + ":" + seconds);

        sternProduct.setUnitDate(new java.sql.Date(calendar.getTimeInMillis()));

    }

    private void setSoftwareVersion(String str) {

        String version = bleDataParser.parseSW(str);
        sternProduct.setSwVersion(version);
        getmView().setSoftwareVersion(version);


    }

    @Override
    public void setName(String nameStr) {
        String name = bleDataParser.getName(nameStr);
        this.sternProduct.setName(name);
        getmView().setNameText(name);

        SternRoomDatabase.getDatabase(context).sternProductDao().updateName(name, sternProduct.getMacAddress());

        SternRoomDatabase.getDatabase(context).sternProductDao().updateName(name, sternProduct.getMacAddress());


    }

    private void setManufacturingDate(String str) {

        Date date = bleDataParser.getDate(str, false);
        String dateStr = bleDataParser.parseDate(date);

        getmView().setManufacturyDate(dateStr);

        sternProduct.setManifacturingDate(new java.sql.Date(date.getTime()));
    }

    private void setInitialPairingDate(String str) {

        Date date = bleDataParser.getDate(str, false);


        if (date != null) {
            String dateStr = bleDataParser.parseDate(date);
            getmView().setInitialParringDate(dateStr);
            isInitialParringWasDone = true;
            sternProduct.setInitialParringDate(new java.sql.Date(date.getTime()));
            getCalenderDate();
            // getAllData(false);

        } else {
            //TODO... Display Dialog of Date
            DialogHelper.getInstance().dialogCurrentDateAndTime(context,
                    this).show();
        }


    }


    @Override
    public void onScannedDeviceData(ScanResult scanResults) {

    }


    @Override
    public void onDateTimeReceived(int year, int month, int dayOfMonth, int hour, int minutes, int seconds, boolean isYesClicked) {


        byte[] dateAndTime = new byte[6];


        dateAndTime[0] = (byte) (seconds & 0xFF);
        dateAndTime[1] = (byte) (minutes & 0xFF);
        dateAndTime[2] = (byte) (hour & 0xFF);
        dateAndTime[3] = (byte) (dayOfMonth & 0xFF);
        dateAndTime[4] = (byte) ((month + 1) & 0xFF);
        dateAndTime[5] = (byte) ((year - 2000) & 0xFF);


        if (!isInitialParringWasDone) {
            sendInitialParringDate(dateAndTime);
            setCalenderDate(dateAndTime);
        } else {
            setCalenderDate(dateAndTime);
            // getAllData(false);
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {


        }
    }

    public enum BleDataIdentifier {

        PRODUCT_NAME(InformationPresenter.bleDataIdentifireInitial + 1),
        PRODUCT_SERIAL_NUMBER(InformationPresenter.bleDataIdentifireInitial + 2),
        PRODUCT_SOFTWARE_VERSION(InformationPresenter.bleDataIdentifireInitial + 3),
        MANUFACTURE_DATE(InformationPresenter.bleDataIdentifireInitial + 4),
        DATE_OF_UNIT(InformationPresenter.bleDataIdentifireInitial + 5),
        INITIAL_PAIRING_DATE(InformationPresenter.bleDataIdentifireInitial + 6),
        CALENDER_DATE_READ(InformationPresenter.bleDataIdentifireInitial + 7),
        CALENDER_DATE_WRITE(InformationPresenter.bleDataIdentifireInitial + 8);

        private int value;

        BleDataIdentifier(int val) {
            this.value = val;
        }


    }
}
