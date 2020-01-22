package stern.msapps.com.control_ble_devices.presenter.statisticsPresenter;

import android.bluetooth.BluetoothGattService;
import android.bluetooth.le.ScanResult;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import stern.msapps.com.control_ble_devices.model.ble.BLEDeviceConnectionManager;
import stern.msapps.com.control_ble_devices.model.ble.BluetoothLeService;
import stern.msapps.com.control_ble_devices.model.dataTypes.SternProduct;
import stern.msapps.com.control_ble_devices.model.enums.SternTypes;
import stern.msapps.com.control_ble_devices.presenter.BasePresenter;
import stern.msapps.com.control_ble_devices.R;
import stern.msapps.com.control_ble_devices.model.dataTypes.SternProductStatistics;
import stern.msapps.com.control_ble_devices.eventBus.Events;
import stern.msapps.com.control_ble_devices.eventBus.GlobalBus;
import stern.msapps.com.control_ble_devices.model.ble.BLEGattAttributes;
import stern.msapps.com.control_ble_devices.model.roomDataBase.database.SternRoomDatabase;
import stern.msapps.com.control_ble_devices.utils.AppSharedPref;
import stern.msapps.com.control_ble_devices.utils.Constants;
import stern.msapps.com.control_ble_devices.utils.DialogHelper;
import stern.msapps.com.control_ble_devices.utils.dataParser.BleDataParser;
import stern.msapps.com.control_ble_devices.view.adapters.StatisticsAdapter;

import static stern.msapps.com.control_ble_devices.model.dataTypes.SternProductStatistics.StatisticTypes.AVERAGE_OPEN_TIME;
import static stern.msapps.com.control_ble_devices.model.dataTypes.SternProductStatistics.StatisticTypes.BATTERY_STATE;
import static stern.msapps.com.control_ble_devices.model.dataTypes.SternProductStatistics.StatisticTypes.FULL_FLUSH_PERCENTAGE;
import static stern.msapps.com.control_ble_devices.model.dataTypes.SternProductStatistics.StatisticTypes.HALF_FLUSH_PERCENTAGE;
import static stern.msapps.com.control_ble_devices.model.dataTypes.SternProductStatistics.StatisticTypes.NUMBER_OF_ACTIVATIONS;
import static stern.msapps.com.control_ble_devices.model.dataTypes.SternProductStatistics.StatisticTypes.TIME_SINCE_LAST_POWERED;


public class StatisticsPresenter extends BasePresenter<StatisticsContract.View> implements StatisticsContract.Presenter, View.OnClickListener {

    private final static int STATISTICS_DATA_REQEST_CONSTANT = 600;
    private Context context;
    private ArrayList<BLEDeviceConnectionManager.DataClass> sendingDataArr = new ArrayList<>();
    private ArrayList<SternProductStatistics> sternProductStatistics = new ArrayList<>();
    private SternProduct sternProduct;

    private StatisticsAdapter statisticsAdapter;
    private RecyclerView statisticsRecyclerView;
    private LinearLayoutManager layoutManager;

    public StatisticsPresenter(Context context) {
        this.context = context;


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.statistics_fragment_close_button_tv:
                getmView().hideFragment();
                break;
            case R.id.statistics_fragment_sendReport_BTN:
                sendReport();
                break;

        }
    }

    @Override
    public void onBleConnectionStatus(BLEDeviceConnectionManager.ConnectionStatus status, List<BluetoothGattService> bluetoothGattService) {

    }

    @Subscribe(sticky = true)
    public void getSternProduct(Events.SterProductTransmition sterProduct) {
        this.sternProduct = sterProduct.getSternProduct();

        getAllData();

    }

    @Override
    protected void parseOnDataReceived(String dataSTR, String uuid, int requestID) {
        Log.e("IDTEST", " ...........onDataReceived() " + requestID + "\n" + "The data is: " + dataSTR + "The characteristic UUID is " + uuid);


        if (!dataSTR.equals(BluetoothLeService.ACTION_COMMUNICATION_ISSUE)) {

            if (requestID == StatisticsDataRequest.LAST_HYGIENE_FLUSH.value) {

                SternProductStatistics lastHygieneFlash = new SternProductStatistics(SternProductStatistics.StatisticTypes.LAST_HYGIENE_FLUSH, dataSTR, getmView().getContext());
                sternProductStatistics.add(lastHygieneFlash);


            } else if (requestID == StatisticsDataRequest.NEXT_HYGIENE_FLUSH.value) {


                SternProductStatistics nextHygieneFlash = new SternProductStatistics(SternProductStatistics.StatisticTypes.NEXT_HYGIENE_FLUSH, dataSTR, getmView().getContext());

                sternProductStatistics.add(nextHygieneFlash);

            } else if (requestID == StatisticsDataRequest.LAST_STAND_DY.value) {
                SternProductStatistics lastStandBy = new SternProductStatistics(SternProductStatistics.StatisticTypes.LAST_STANDBY, dataSTR, getmView().getContext());

                sternProductStatistics.add(lastStandBy);

            } else if (requestID == StatisticsDataRequest.NEXT_STAND_BY.value) {

                SternProductStatistics nextStandBy = new SternProductStatistics(SternProductStatistics.StatisticTypes.NEXT_STANDBY, dataSTR, getmView().getContext());

                sternProductStatistics.add(nextStandBy);


            } else if (requestID == StatisticsDataRequest.LAST_FILTER__CLEAN_READ.value) {


                SternProductStatistics lastFilterClean = new SternProductStatistics(SternProductStatistics.StatisticTypes.LAST_FILTER_CLEANED, dataSTR, getmView().getContext());

                sternProductStatistics.add(lastFilterClean);

            } else if (requestID == StatisticsDataRequest.LAST_SOAP_REFILL_READ.value) {


                SternProductStatistics lastSoapRefill = new SternProductStatistics(SternProductStatistics.StatisticTypes.LAST_SOAP_REFILLED, dataSTR, getmView().getContext());

                sternProductStatistics.add(lastSoapRefill);

            } else if (requestID == StatisticsDataRequest.STATISTICS_INFO_READ_REQUEST.value) {

                saveLastUpdated();
                parseSetStatisticsInfo(dataSTR);

            } else if (requestID == StatisticsDataRequest.LAST_FILTER__CLEAN_WRITE.value) {

                SternProductStatistics lastFilterClean = new SternProductStatistics(SternProductStatistics.StatisticTypes.LAST_FILTER_CLEANED, dataSTR, getmView().getContext(), sternProduct.getMacAddress());

                boolean contains = false;

                for (int i = 0; i < sternProductStatistics.size(); i++) {

                    if (sternProductStatistics.get(i).getType() == lastFilterClean.getType()) {
                        sternProductStatistics.remove(sternProductStatistics.get(i));
                        sternProductStatistics.add(i, lastFilterClean);
                        contains = true;
                    }
                }

                if (!contains) {
                    sternProductStatistics.add(lastFilterClean);
                }

            } else if (requestID == StatisticsDataRequest.LAST_SOAP_REFILL_WRITE.value) {

                SternProductStatistics lastSoapRefilled = new SternProductStatistics(SternProductStatistics.StatisticTypes.LAST_SOAP_REFILLED, dataSTR, getmView().getContext(), sternProduct.getMacAddress());

                boolean contains = false;

                for (int i = 0; i < sternProductStatistics.size(); i++) {

                    if (sternProductStatistics.get(i).getType() == lastSoapRefilled.getType()) {
                        sternProductStatistics.remove(sternProductStatistics.get(i));
                        sternProductStatistics.add(i, lastSoapRefilled);
                        contains = true;
                    }

                }

                if (!contains) {
                    sternProductStatistics.add(lastSoapRefilled);

                }

            }
        }


        if (BLEDeviceConnectionManager.getInstance().getSendedDataSize() == 0) {

            //saveLastUpdated();

            if (DialogHelper.getInstance().isDialogShown()) {
                DialogHelper.getInstance().dismiss();
                sendingDataArr.clear();

            }

            this.statisticsRecyclerView.setAdapter(new StatisticsAdapter(this, sternProductStatistics));
            getmView().setClickableFloatingButton(true);

        }


        Log.d("RafaelTest", "The data was received is = " + dataSTR);

    }


    @Override
    public void onScannedDeviceData(ScanResult scanResults) {

    }

    @Override
    public void onStart() {
        GlobalBus.getBus().register(this);
    }

    @Override
    public void onViewCreated() {

    }


    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void sendReadRequestLastEventDone(int event) {


    }

    @Override
    public void getAllData() {

        sendingDataArr.add(getStatisticsInfo());
        sendingDataArr.add(getHygieneFlushLastEvent());
        sendingDataArr.add(getHygieneFlushNextEvent());
        sendingDataArr.add(getStandByLastEvent());
        sendingDataArr.add(getStandByNextEvent());


        String handleID = AppSharedPref.getInstance(context).getPrefString(sternProduct.getMacAddress() + Constants.SHARED_PREF_CLEAN_FILTER);

        if (sternProduct.getType() != SternTypes.SOAP_DISPENSER) {

            if (!handleID.isEmpty()) {
                sendingDataArr.add(getLastFilterClean(handleID));

            } else {
                sendingDataArr.add(resetLastFilterClean());
            }
        }


        String soapHandleID = AppSharedPref.getInstance(context).getPrefString(sternProduct.getMacAddress() + Constants.SHARED_PREF_SOAP_REFILL);

        if (sternProduct.getType() == SternTypes.SOAP_DISPENSER || sternProduct.getType() == SternTypes.FOAM_SOAP_DISPENSER) {

            if (!soapHandleID.isEmpty()) {
                sendingDataArr.add(getLastSoapRefill(soapHandleID));

            } else {
                sendingDataArr.add(resetLastSoapRefill());
            }
        }

        sendData(sendingDataArr);

    }

    @Override
    public void sendReport() {

        StringBuilder data = new StringBuilder();

        for (SternProductStatistics statistics : sternProductStatistics) {

            data.append(statistics.getType().name + ":" + " ");
            data.append(" ");
            data.append(statistics.getParsedStatisticsTypeStringValue());
            data.append("\n");


        }


        List<Intent> targetShareIntents = new ArrayList<Intent>();
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        PackageManager pm = getmView().getContext().getPackageManager();
        List<ResolveInfo> resInfos = pm.queryIntentActivities(shareIntent, 0);
        if (!resInfos.isEmpty()) {
            System.out.println("Have package");
            for (ResolveInfo resInfo : resInfos) {
                String packageName = resInfo.activityInfo.packageName;
                Log.i("Package Name", packageName);

                if (packageName.contains("com.twitter.android") || packageName.contains("com.facebook.katana")
                        || packageName.contains("com.whatsapp") || packageName.contains("com.google.android.apps.plus")
                        || packageName.contains("com.google.android.talk") || packageName.contains("com.slack")
                        || packageName.contains("com.google.android.gm") || packageName.contains("com.facebook.orca")
                        || packageName.contains("com.yahoo.mobile") || packageName.contains("com.skype.raider")
                        || packageName.contains("com.android.mms") || packageName.contains("com.linkedin.android")
                        || packageName.contains("com.google.android.apps.messaging")) {
                    Intent intent = new Intent();

                    intent.setComponent(new ComponentName(packageName, resInfo.activityInfo.name));
                    intent.putExtra("AppName", resInfo.loadLabel(pm).toString());
                    intent.setAction(Intent.ACTION_SEND);
                    intent.setType("text/plain/image");
                    intent.putExtra(Intent.EXTRA_TEXT, data.toString());
                    intent.putExtra(Intent.EXTRA_SUBJECT, "SHARE");
                    intent.setPackage(packageName);
                    targetShareIntents.add(intent);
                }
            }
            if (!targetShareIntents.isEmpty()) {
                Collections.sort(targetShareIntents, (o1, o2) -> o1.getStringExtra("AppName").compareTo(o2.getStringExtra("AppName")));
                Intent chooserIntent = Intent.createChooser(targetShareIntents.remove(0), "Select app to share");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetShareIntents.toArray(new Parcelable[]{}));
                getmView().getContext().startActivity(chooserIntent);
            } else {
                Toast.makeText(getmView().getContext(), "No app to share.", Toast.LENGTH_LONG).show();
            }
        }

    }


    @Override
    public void onResetStatisticClicked(SternProductStatistics.StatisticTypes type) {


        switch (type) {
            case NUMBER_OF_ACTIVATIONS:
                //   resetNumberOfActivations();
                break;

            case LAST_FILTER_CLEANED:

                String handleIDFilterClean = AppSharedPref.getInstance(context).getPrefString(sternProduct.getMacAddress() + Constants.SHARED_PREF_CLEAN_FILTER);

                if (!handleIDFilterClean.isEmpty()) {
                    sendingDataArr.add(deleteEventbyID(handleIDFilterClean, StatisticsDataRequest.LAST_FILTER_CLEAN_DELETE));

                }
                sendingDataArr.add(resetLastFilterClean());

                break;
            case LAST_SOAP_REFILLED:
                String handleIDSoapRefill = AppSharedPref.getInstance(context).getPrefString(sternProduct.getMacAddress() + Constants.SHARED_PREF_SOAP_REFILL);

                if (!handleIDSoapRefill.isEmpty()) {
                    sendingDataArr.add(deleteEventbyID(handleIDSoapRefill, StatisticsDataRequest.LAST_SOAP_REFILL_DELETE));
                }

                sendingDataArr.add(resetLastSoapRefill());
                break;
        }

        DialogHelper.getInstance().displayLoaderProgressDialog(getmView().getContext(), getmView().getContext().getResources().getString(R.string.loading)).show();
        sendData(sendingDataArr);
    }

    @Override
    public void resetNumberOfActivations() {

    }

    @Override
    public BLEDeviceConnectionManager.DataClass resetLastFilterClean() {


        ArrayList<BLEDeviceConnectionManager.DataClass.CharacteristicIO> characteristicIO = new ArrayList<>();
        BLEDeviceConnectionManager.DataClass dataClass = new BLEDeviceConnectionManager.DataClass();
        characteristicIO.add(BLEDeviceConnectionManager.DataClass.CharacteristicIO.WRITABLE);

        byte[] sendingData = new byte[14];

        Calendar calendar = Calendar.getInstance();
        sendingData[0] = BleDataParser.getInstance().hexStringToByteArray(String.format("%02X", (0xFFFFFF & 1)))[0];
        sendingData[1] = BleDataParser.getInstance().hexStringToByteArray(String.format("%02X", (0xFFFFFF & 132)))[0];
        sendingData[2] = BleDataParser.getInstance().hexStringToByteArray(String.format("%02X", (0xFFFFFF & 0)))[0];
        sendingData[3] = 0;
        sendingData[4] = 0;
        sendingData[5] = BleDataParser.getInstance().hexStringToByteArray(String.format("%02X", (0xFFFFFF & calendar.get(Calendar.SECOND))))[0];
        sendingData[6] = BleDataParser.getInstance().hexStringToByteArray(String.format("%02X", (0xFFFFFF & calendar.get(Calendar.MINUTE))))[0];
        sendingData[7] = BleDataParser.getInstance().hexStringToByteArray(String.format("%02X", (0xFFFFFF & calendar.get(Calendar.HOUR_OF_DAY))))[0];
        sendingData[8] = BleDataParser.getInstance().hexStringToByteArray(String.format("%02X", (0xFFFFFF & calendar.get(Calendar.DAY_OF_MONTH))))[0];


        sendingData[9] = BleDataParser.getInstance().hexStringToByteArray(String.format("%02X", (0xFFFFFF & (calendar.get(Calendar.MONTH) + 1))))[0];
        sendingData[10] = BleDataParser.getInstance().hexStringToByteArray(String.format("%02X", (0xFFFFFF & (calendar.get(Calendar.YEAR) - 2000))))[0];
        sendingData[11] = 0;
        sendingData[12] = 0;
        sendingData[13] = 0;


        dataClass.setAddToDaSize(true);
        dataClass.setServiceUUid(UUID.fromString(BLEGattAttributes.UUID_STERN_DATA_EVENTS_AND_DATA_SERVICE));
        dataClass.setCharacteristicsUUid(UUID.fromString(BLEGattAttributes.UUID_STERN_DATA_EVENT_CHARACTERISTIC));
        dataClass.setData(sendingData);
        dataClass.setCharacteristicIOArrayList(characteristicIO);
        dataClass.setRequestID(StatisticsDataRequest.LAST_FILTER__CLEAN_WRITE.value);


        return dataClass;

    }

    @Override
    public BLEDeviceConnectionManager.DataClass getLastSoapRefill(String handleID) {


        byte[] sendingData = new byte[4];
        int type = -1;
        int id = Integer.valueOf(Integer.parseInt(handleID, 16)) - 1;

        //    Log.d("DELETETEST", "ReadteEvetID = " + id);

        String hedHandleID = String.format("%04X", (0xFFFFFF & id));

        String result = BleDataParser.getInstance().addSpaceEveryXchars(hedHandleID, "2");

        String[] dataID = result.split(" ");


        sendingData[0] = BleDataParser.getInstance().hexStringToByteArray(String.format("%02X", (0xFFFFFF & 129)))[0];
        sendingData[1] = BleDataParser.getInstance().hexStringToByteArray(String.format("%02X", (0xFFFFFF & 132)))[0];

        if (dataID.length > 1) {
            if (dataID[0].equals("00")) {
                sendingData[2] = BleDataParser.getInstance().hexStringToByteArray(dataID[1])[0];
            } else {
                sendingData[3] = BleDataParser.getInstance().hexStringToByteArray(dataID[1])[0];
                sendingData[2] = BleDataParser.getInstance().hexStringToByteArray(dataID[0])[0];
            }

        } else {
            sendingData[2] = BleDataParser.getInstance().hexStringToByteArray(dataID[0])[0];
        }


        ArrayList<BLEDeviceConnectionManager.DataClass.CharacteristicIO> characteristicIOArr = null;

        BLEDeviceConnectionManager.DataClass readEvent = new BLEDeviceConnectionManager.DataClass();
        readEvent.setRequestID(StatisticsDataRequest.LAST_SOAP_REFILL_READ.value);
        characteristicIOArr = new ArrayList<>();
        readEvent.setAddToDaSize(true);
        characteristicIOArr.add(BLEDeviceConnectionManager.DataClass.CharacteristicIO.WRITABLE);
        readEvent.setCharacteristicIOArrayList(characteristicIOArr);
        readEvent.setServiceUUid(UUID.fromString(BLEGattAttributes.UUID_STERN_DATA_INFORMATION_SERVICE));
        readEvent.setCharacteristicsUUid(UUID.fromString(BLEGattAttributes.UUID_STERN_DATA_INFORMATION_SCHEDUALED_CHARACTERISTIC));
        readEvent.setData(sendingData);

        return readEvent;
    }

    @Override
    public BLEDeviceConnectionManager.DataClass resetLastSoapRefill() {

        ArrayList<BLEDeviceConnectionManager.DataClass.CharacteristicIO> characteristicIO = new ArrayList<>();
        BLEDeviceConnectionManager.DataClass dataClass = new BLEDeviceConnectionManager.DataClass();
        characteristicIO.add(BLEDeviceConnectionManager.DataClass.CharacteristicIO.WRITABLE);

        byte[] sendingData = new byte[14];

        Calendar calendar = Calendar.getInstance();
        sendingData[0] = BleDataParser.getInstance().hexStringToByteArray(String.format("%02X", (0xFFFFFF & 1)))[0];
        sendingData[1] = BleDataParser.getInstance().hexStringToByteArray(String.format("%02X", (0xFFFFFF & 132)))[0];
        sendingData[2] = BleDataParser.getInstance().hexStringToByteArray(String.format("%02X", (0xFFFFFF & 0)))[0];
        sendingData[3] = 0;
        sendingData[4] = 0;
        sendingData[5] = BleDataParser.getInstance().hexStringToByteArray(String.format("%02X", (0xFFFFFF & calendar.get(Calendar.SECOND))))[0];
        sendingData[6] = BleDataParser.getInstance().hexStringToByteArray(String.format("%02X", (0xFFFFFF & calendar.get(Calendar.MINUTE))))[0];
        sendingData[7] = BleDataParser.getInstance().hexStringToByteArray(String.format("%02X", (0xFFFFFF & calendar.get(Calendar.HOUR_OF_DAY))))[0];
        sendingData[8] = BleDataParser.getInstance().hexStringToByteArray(String.format("%02X", (0xFFFFFF & calendar.get(Calendar.DAY_OF_MONTH))))[0];


        sendingData[9] = BleDataParser.getInstance().hexStringToByteArray(String.format("%02X", (0xFFFFFF & (calendar.get(Calendar.MONTH) + 1))))[0];
        sendingData[10] = BleDataParser.getInstance().hexStringToByteArray(String.format("%02X", (0xFFFFFF & (calendar.get(Calendar.YEAR) - 2000))))[0];
        sendingData[11] = 0;
        sendingData[12] = 0;
        sendingData[13] = 0;


        dataClass.setAddToDaSize(true);
        dataClass.setServiceUUid(UUID.fromString(BLEGattAttributes.UUID_STERN_DATA_EVENTS_AND_DATA_SERVICE));
        dataClass.setCharacteristicsUUid(UUID.fromString(BLEGattAttributes.UUID_STERN_DATA_EVENT_CHARACTERISTIC));
        dataClass.setData(sendingData);
        dataClass.setCharacteristicIOArrayList(characteristicIO);
        dataClass.setRequestID(StatisticsDataRequest.LAST_SOAP_REFILL_WRITE.value);


        return dataClass;


    }

    @Override
    public void setStatisticsRecyclerView(RecyclerView statisticsRecyclerView) {
        this.statisticsRecyclerView = statisticsRecyclerView;
        this.layoutManager = new LinearLayoutManager(getmView().getContext(), LinearLayoutManager.VERTICAL, false);
        this.statisticsRecyclerView.setLayoutManager(this.layoutManager);


    }

    @Override
    public BLEDeviceConnectionManager.DataClass getHygieneFlushLastEvent() {

        ArrayList<BLEDeviceConnectionManager.DataClass.CharacteristicIO> characteristicIO = new ArrayList<>();
        BLEDeviceConnectionManager.DataClass dataClass = new BLEDeviceConnectionManager.DataClass();
        characteristicIO.add(BLEDeviceConnectionManager.DataClass.CharacteristicIO.WRITABLE);
        byte[] data = new byte[2];
        data[0] = (byte) 0x83;
        data[1] = (byte) 2;
        dataClass.setAddToDaSize(true);
        dataClass.setServiceUUid(UUID.fromString(BLEGattAttributes.UUID_STERN_DATA_EVENTS_AND_DATA_SERVICE));
        dataClass.setCharacteristicsUUid(UUID.fromString(BLEGattAttributes.UUID_STERN_DATA_EVENT_CHARACTERISTIC));
        dataClass.setData(data);
        dataClass.setCharacteristicIOArrayList(characteristicIO);
        dataClass.setRequestID(StatisticsDataRequest.LAST_HYGIENE_FLUSH.value);

        return dataClass;
    }

    @Override
    public BLEDeviceConnectionManager.DataClass getHygieneFlushNextEvent() {

        ArrayList<BLEDeviceConnectionManager.DataClass.CharacteristicIO> characteristicIO = new ArrayList<>();
        BLEDeviceConnectionManager.DataClass dataClass = new BLEDeviceConnectionManager.DataClass();
        characteristicIO.add(BLEDeviceConnectionManager.DataClass.CharacteristicIO.WRITABLE);
        byte[] data = new byte[2];
        data[0] = (byte) 0x84;
        data[1] = (byte) 2;
        dataClass.setAddToDaSize(true);
        dataClass.setServiceUUid(UUID.fromString(BLEGattAttributes.UUID_STERN_DATA_EVENTS_AND_DATA_SERVICE));
        dataClass.setCharacteristicsUUid(UUID.fromString(BLEGattAttributes.UUID_STERN_DATA_EVENT_CHARACTERISTIC));
        dataClass.setData(data);
        dataClass.setCharacteristicIOArrayList(characteristicIO);
        dataClass.setRequestID(StatisticsDataRequest.NEXT_HYGIENE_FLUSH.value);

        return dataClass;

    }

    @Override
    public BLEDeviceConnectionManager.DataClass getStandByLastEvent() {

        ArrayList<BLEDeviceConnectionManager.DataClass.CharacteristicIO> characteristicIO = new ArrayList<>();
        BLEDeviceConnectionManager.DataClass dataClass = new BLEDeviceConnectionManager.DataClass();
        characteristicIO.add(BLEDeviceConnectionManager.DataClass.CharacteristicIO.WRITABLE);
        byte[] data = new byte[2];
        data[0] = (byte) 0x83;
        data[1] = (byte) 3;
        dataClass.setAddToDaSize(true);
        dataClass.setServiceUUid(UUID.fromString(BLEGattAttributes.UUID_STERN_DATA_EVENTS_AND_DATA_SERVICE));
        dataClass.setCharacteristicsUUid(UUID.fromString(BLEGattAttributes.UUID_STERN_DATA_EVENT_CHARACTERISTIC));
        dataClass.setData(data);
        dataClass.setCharacteristicIOArrayList(characteristicIO);
        dataClass.setRequestID(StatisticsDataRequest.LAST_STAND_DY.value);

        return dataClass;
    }

    @Override
    public BLEDeviceConnectionManager.DataClass getStandByNextEvent() {

        ArrayList<BLEDeviceConnectionManager.DataClass.CharacteristicIO> characteristicIO = new ArrayList<>();
        BLEDeviceConnectionManager.DataClass dataClass = new BLEDeviceConnectionManager.DataClass();
        characteristicIO.add(BLEDeviceConnectionManager.DataClass.CharacteristicIO.WRITABLE);
        byte[] data = new byte[2];
        data[0] = (byte) 0x84;
        data[1] = (byte) 3;
        dataClass.setAddToDaSize(true);
        dataClass.setServiceUUid(UUID.fromString(BLEGattAttributes.UUID_STERN_DATA_EVENTS_AND_DATA_SERVICE));
        dataClass.setCharacteristicsUUid(UUID.fromString(BLEGattAttributes.UUID_STERN_DATA_EVENT_CHARACTERISTIC));
        dataClass.setData(data);
        dataClass.setCharacteristicIOArrayList(characteristicIO);
        dataClass.setRequestID(StatisticsDataRequest.NEXT_STAND_BY.value);

        return dataClass;
    }

    @Override
    public BLEDeviceConnectionManager.DataClass getStatisticsInfo() {

        ArrayList<BLEDeviceConnectionManager.DataClass.CharacteristicIO> characteristicIO = new ArrayList<>();
        BLEDeviceConnectionManager.DataClass dataClass = new BLEDeviceConnectionManager.DataClass();
        characteristicIO.add(BLEDeviceConnectionManager.DataClass.CharacteristicIO.READABLE);
        dataClass.setAddToDaSize(true);
        dataClass.setServiceUUid(UUID.fromString(BLEGattAttributes.UUID_STERN_STATISTICS_INFO_SERVICE));
        dataClass.setCharacteristicsUUid(UUID.fromString(BLEGattAttributes.UUID_STERN_DATA_STATISTICS_INFO_CHARACTIRISTICS));
        dataClass.setCharacteristicIOArrayList(characteristicIO);
        dataClass.setRequestID(StatisticsDataRequest.STATISTICS_INFO_READ_REQUEST.value);
        return dataClass;
    }


    public BLEDeviceConnectionManager.DataClass getTest(String handleID) {
        byte[] sendingData = new byte[4];
        int type = -1;
        int id = Integer.valueOf(Integer.parseInt(handleID, 16)) - 1;

        //    Log.d("DELETETEST", "ReadteEvetID = " + id);

        String hedHandleID = String.format("%04X", (0xFFFFFF & id));

        String result = BleDataParser.getInstance().addSpaceEveryXchars(hedHandleID, "2");

        String[] dataID = result.split(" ");


        sendingData[0] = BleDataParser.getInstance().hexStringToByteArray(String.format("%02X", (0xFFFFFF & 129)))[0];
        sendingData[1] = BleDataParser.getInstance().hexStringToByteArray(String.format("%02X", (0xFFFFFF & 132)))[0];

        if (dataID.length > 1) {
            if (dataID[0].equals("00")) {
                sendingData[2] = BleDataParser.getInstance().hexStringToByteArray(dataID[1])[0];
            } else {
                sendingData[3] = BleDataParser.getInstance().hexStringToByteArray(dataID[1])[0];
                sendingData[2] = BleDataParser.getInstance().hexStringToByteArray(dataID[0])[0];
            }

        } else {
            sendingData[2] = BleDataParser.getInstance().hexStringToByteArray(dataID[0])[0];
        }


        ArrayList<BLEDeviceConnectionManager.DataClass.CharacteristicIO> characteristicIOArr = null;

        BLEDeviceConnectionManager.DataClass readEvent = new BLEDeviceConnectionManager.DataClass();
        readEvent.setRequestID(StatisticsDataRequest.LAST_FILTER__CLEAN_READ.value);
        characteristicIOArr = new ArrayList<>();
        readEvent.setAddToDaSize(true);
        characteristicIOArr.add(BLEDeviceConnectionManager.DataClass.CharacteristicIO.WRITABLE);
        readEvent.setCharacteristicIOArrayList(characteristicIOArr);
        readEvent.setServiceUUid(UUID.fromString(BLEGattAttributes.UUID_STERN_DATA_INFORMATION_SERVICE));
        readEvent.setCharacteristicsUUid(UUID.fromString(BLEGattAttributes.UUID_STERN_DATA_INFORMATION_SCHEDUALED_CHARACTERISTIC));
        readEvent.setData(sendingData);

        return readEvent;
    }

    @Override
    public BLEDeviceConnectionManager.DataClass getLastFilterClean(String handleID) {


        byte[] sendingData = new byte[4];
        int type = -1;
        int id = Integer.valueOf(Integer.parseInt(handleID, 16)) - 1;

        //    Log.d("DELETETEST", "ReadteEvetID = " + id);

        String hedHandleID = String.format("%04X", (0xFFFFFF & id));

        String result = BleDataParser.getInstance().addSpaceEveryXchars(hedHandleID, "2");

        String[] dataID = result.split(" ");


        sendingData[0] = BleDataParser.getInstance().hexStringToByteArray(String.format("%02X", (0xFFFFFF & 129)))[0];
        sendingData[1] = BleDataParser.getInstance().hexStringToByteArray(String.format("%02X", (0xFFFFFF & 132)))[0];

        if (dataID.length > 1) {
            if (dataID[0].equals("00")) {
                sendingData[2] = BleDataParser.getInstance().hexStringToByteArray(dataID[1])[0];
            } else {
                sendingData[3] = BleDataParser.getInstance().hexStringToByteArray(dataID[1])[0];
                sendingData[2] = BleDataParser.getInstance().hexStringToByteArray(dataID[0])[0];
            }

        } else {
            sendingData[2] = BleDataParser.getInstance().hexStringToByteArray(dataID[0])[0];
        }


        ArrayList<BLEDeviceConnectionManager.DataClass.CharacteristicIO> characteristicIOArr = null;

        BLEDeviceConnectionManager.DataClass readEvent = new BLEDeviceConnectionManager.DataClass();
        readEvent.setRequestID(StatisticsDataRequest.LAST_FILTER__CLEAN_READ.value);
        characteristicIOArr = new ArrayList<>();
        readEvent.setAddToDaSize(true);
        characteristicIOArr.add(BLEDeviceConnectionManager.DataClass.CharacteristicIO.WRITABLE);
        readEvent.setCharacteristicIOArrayList(characteristicIOArr);
        readEvent.setServiceUUid(UUID.fromString(BLEGattAttributes.UUID_STERN_DATA_INFORMATION_SERVICE));
        readEvent.setCharacteristicsUUid(UUID.fromString(BLEGattAttributes.UUID_STERN_DATA_INFORMATION_SCHEDUALED_CHARACTERISTIC));
        readEvent.setData(sendingData);

        return readEvent;
    }


    @Override
    public BLEDeviceConnectionManager.DataClass deleteEventbyID(String handleID, StatisticsDataRequest statisticsDataRequest) {


        String result = BleDataParser.getInstance().addSpaceEveryXchars(handleID, "2");
        String[] dataID = result.split(" ");

        byte[] sendingData = new byte[4];
        sendingData[0] = BleDataParser.getInstance().hexStringToByteArray(String.format("%02X", (0xFFFFFF & 2)))[0];
        sendingData[1] = BleDataParser.getInstance().hexStringToByteArray(String.format("%02X", (0xFFFFFF & 0)))[0];

        if (dataID.length > 1) {
            if (dataID[0].equals("00")) {
                sendingData[2] = BleDataParser.getInstance().hexStringToByteArray(dataID[1])[0];
            } else {
                sendingData[3] = BleDataParser.getInstance().hexStringToByteArray(dataID[1])[0];
                sendingData[2] = BleDataParser.getInstance().hexStringToByteArray(dataID[0])[0];
            }

        } else {
            sendingData[2] = BleDataParser.getInstance().hexStringToByteArray(dataID[0])[0];
        }

        ArrayList<BLEDeviceConnectionManager.DataClass.CharacteristicIO> characteristicIO = null;

        BLEDeviceConnectionManager.DataClass deleteEvent = new BLEDeviceConnectionManager.DataClass();
        deleteEvent.setRequestID(statisticsDataRequest.value);
        characteristicIO = new ArrayList<>();
        deleteEvent.setAddToDaSize(true);
        characteristicIO.add(BLEDeviceConnectionManager.DataClass.CharacteristicIO.WRITABLE);
        deleteEvent.setCharacteristicIOArrayList(characteristicIO);
        deleteEvent.setServiceUUid(UUID.fromString(BLEGattAttributes.UUID_STERN_DATA_INFORMATION_SERVICE));
        deleteEvent.setCharacteristicsUUid(UUID.fromString(BLEGattAttributes.UUID_STERN_DATA_INFORMATION_SCHEDUALED_CHARACTERISTIC));
        deleteEvent.setData(sendingData);
        return deleteEvent;
    }


    @Override
    public void sendData(ArrayList<BLEDeviceConnectionManager.DataClass> sendingDataArr) {

        int arrSize = sendingDataArr.size();

        for (int i = 0; i < arrSize; i++) {

            BLEDeviceConnectionManager.DataClass dataClass = sendingDataArr.get(i);
            Log.i("TEST", "The data ID that was sended is = " + dataClass.getRequestID());
            BLEDeviceConnectionManager.getInstance().sendData(dataClass, StatisticsPresenter.this);
        }

    }


    @Override
    public void parseSetStatisticsInfo(String data) {

        SternProductStatistics timeSinceLastPowered = new SternProductStatistics(TIME_SINCE_LAST_POWERED, data, getmView().getContext());
        sternProductStatistics.add(timeSinceLastPowered);

        SternProductStatistics batteryState = new SternProductStatistics(BATTERY_STATE, data, getmView().getContext());
        sternProductStatistics.add(batteryState);

        if (sternProduct.getType() == SternTypes.WC)

        {
            SternProductStatistics fullFlash = new SternProductStatistics(FULL_FLUSH_PERCENTAGE, data, getmView().getContext());
            sternProductStatistics.add(fullFlash);
        }

        if (sternProduct.getType() == SternTypes.WC) {
            SternProductStatistics halfFlash = new SternProductStatistics(HALF_FLUSH_PERCENTAGE, data, getmView().getContext());
            sternProductStatistics.add(halfFlash);
        }
        SternProductStatistics numberOfActivation = new SternProductStatistics(NUMBER_OF_ACTIVATIONS, data, getmView().getContext());
        sternProductStatistics.add(numberOfActivation);

        if (!sternProduct.getType().equals(SternTypes.FOAM_SOAP_DISPENSER) && !sternProduct.getType().equals(SternTypes.SOAP_DISPENSER)) {
            SternProductStatistics averageOpenTime = new SternProductStatistics(AVERAGE_OPEN_TIME, data, getmView().getContext());
            sternProductStatistics.add(averageOpenTime);
        }


    }

    @Override
    public void saveLastUpdated() {

        Date date = new Date();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        String udtatedDate = sdf.format(date);
        sternProduct.setLastUpdate(udtatedDate);

        SternRoomDatabase.getDatabase(context).sternProductDao().updateLastUpdated(udtatedDate, sternProduct.getMacAddress());


    }


    enum StatisticsDataRequest {

        LAST_HYGIENE_FLUSH(STATISTICS_DATA_REQEST_CONSTANT + 1),
        NEXT_HYGIENE_FLUSH(STATISTICS_DATA_REQEST_CONSTANT + 2),
        LAST_STAND_DY(STATISTICS_DATA_REQEST_CONSTANT + 3),
        NEXT_STAND_BY(STATISTICS_DATA_REQEST_CONSTANT + 4),
        STATISTICS_INFO_READ_REQUEST(STATISTICS_DATA_REQEST_CONSTANT + 5),
        LAST_FILTER__CLEAN_WRITE(STATISTICS_DATA_REQEST_CONSTANT + 6),
        LAST_SOAP_REFILL_WRITE(STATISTICS_DATA_REQEST_CONSTANT + 7),
        LAST_FILTER__CLEAN_READ(STATISTICS_DATA_REQEST_CONSTANT + 8),
        LAST_SOAP_REFILL_READ(STATISTICS_DATA_REQEST_CONSTANT + 9),
        LAST_FILTER_CLEAN_DELETE(STATISTICS_DATA_REQEST_CONSTANT + 10),
        LAST_SOAP_REFILL_DELETE(STATISTICS_DATA_REQEST_CONSTANT + 11);


        private int value;

        StatisticsDataRequest(int val) {

            this.value = val;
        }
    }


}
