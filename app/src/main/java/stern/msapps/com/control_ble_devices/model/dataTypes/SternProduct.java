package stern.msapps.com.control_ble_devices.model.dataTypes;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.bluetooth.BluetoothGattService;


import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import stern.msapps.com.control_ble_devices.model.dataTypes.ranges.Ranges;
import stern.msapps.com.control_ble_devices.model.enums.SternTypes;
import stern.msapps.com.control_ble_devices.model.roomDataBase.database.converters.SternTypeConverter;


@Entity(tableName = "stern_product")
public class SternProduct {


    @PrimaryKey(autoGenerate = true)
    protected int id;
    @TypeConverters(SternTypeConverter.class)
    public SternTypes type;
    @ColumnInfo(name = "name")
    protected String name;
    @ColumnInfo(name = "location_name")
    protected String locationNAme;
    @Ignore
    protected String presetName;
    @ColumnInfo(name = "image")
    protected int image;
    @ColumnInfo(name = "pairing_code")
    protected String pairingCode;
    @ColumnInfo(name = "mac_address")
    protected String macAddress;
    @ColumnInfo(name = "last_connected")
    protected String lastConnected;
    @ColumnInfo(name = "last_updated")
    protected String lastUpdate;
    @Ignore
    private boolean isRangesReceived;

    @ColumnInfo(name = "manifacturing_date")
    protected boolean isPreviouslyConnected;

    public boolean isNearby() {
        return nearby;
    }

    public void setNearby(boolean nearby) {
        this.nearby = nearby;
    }

    @ColumnInfo(name = "nearby")
    protected boolean nearby;

    @Ignore
    protected Date manifacturingDate;
    @Ignore
    protected Date unitDate;

    @Ignore
    protected Date initialParringDate;

    @ColumnInfo(name = "battery_voltage")
    protected String batteryVoltage;
    @ColumnInfo(name = "sw_version")
    protected String swVersion;
    @ColumnInfo(name = "serial_number")
    protected String serialNumber;
    @ColumnInfo(name = "valve_state")
    protected String valveState;
    @ColumnInfo(name = "dayle_usage")
    protected String dayleUsage;
    @ColumnInfo(name = "last_filter_clean")
    protected String lastFilterClean;
    @Ignore
    protected List<BluetoothGattService> bluetoothGattService;
    @Ignore
    protected ScheduledHygieneFlush scheduledHygieneFlush = ScheduledHygieneFlush.getInstance();
    @Ignore
    protected ScheduledStandByMode scheduledStandByMode = ScheduledStandByMode.getInstance();
    @Ignore
    protected ScheduledHygieneFlashRandomDay scheduledHygieneFlashRandomDay;
    @Ignore
    protected ScheduledStandByRandomDay scheduledStandByRandomDay;
    @Ignore
    protected ArrayList<Ranges> rangesArrayList;
    @Ignore
    protected SternProductStatistics sternProductStatistics;

    public SternProduct() {
    }


    public String getPresetName() {
        return presetName;
    }

    public void setPresetName(String presetName) {
        this.presetName = presetName;
    }

    public SternProductStatistics getSternProductStatistics() {
        return sternProductStatistics;
    }

    public void setSternProductStatistics(SternProductStatistics sternProductStatistics) {
        this.sternProductStatistics = sternProductStatistics;
    }


    @Ignore
    public SternProduct(SternTypes type) {
        this.type = type;
        setImage(type.getImagePath());
        this.name = type.getName();

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBatteryVoltage() {
        return batteryVoltage;
    }

    public void setBatteryVoltage(String batteryVoltage) {
        this.batteryVoltage = batteryVoltage;
    }

    public String getSwVersion() {
        return swVersion;
    }

    public void setSwVersion(String swVersion) {
        this.swVersion = swVersion;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getValveState() {
        return valveState;
    }

    public void setValveState(String valveState) {
        this.valveState = valveState;
    }

    public String getDayleUsage() {
        return dayleUsage;
    }

    public void setDayleUsage(String dayleUsage) {
        this.dayleUsage = dayleUsage;
    }

    public String getLastFilterClean() {
        return lastFilterClean;
    }

    public void setLastFilterClean(String lastFilterClean) {
        this.lastFilterClean = lastFilterClean;
    }

    public ScheduledHygieneFlush getScheduledHygieneFlush() {
        return scheduledHygieneFlush;
    }

    public void setScheduledHygieneFlush(ScheduledHygieneFlush scheduledHygieneFlush) {
        this.scheduledHygieneFlush = scheduledHygieneFlush;
    }

    public void setScheduledStandByMode(ScheduledStandByMode scheduledStandByMode) {
        this.scheduledStandByMode = scheduledStandByMode;
    }

    public Date getManifacturingDate() {
        return manifacturingDate;
    }

    public void setManifacturingDate(Date manifacturingDate) {
        this.manifacturingDate = manifacturingDate;
    }

    public SternTypes getType() {
        return type;
    }

    public void setType(SternTypes type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;

    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getLastConnected() {
        return lastConnected;
    }

    public void setLastConnected(String lastConnected) {
        this.lastConnected = lastConnected;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }


    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public List<BluetoothGattService> getBluetoothGattService() {
        return bluetoothGattService;
    }

    public void setBluetoothGattService(List<BluetoothGattService> bluetoothGattService) {
        this.bluetoothGattService = bluetoothGattService;
    }


    public ScheduledHygieneFlush getScheduledHygieneflush() {


        return scheduledHygieneFlush;
    }

    public ScheduledStandByMode getScheduledStandByMode() {
        return scheduledStandByMode;
    }

    public ScheduledHygieneFlashRandomDay getScheduledHygieneFlashRandomDay() {
        return scheduledHygieneFlashRandomDay;
    }

    public void setScheduledHygieneFlashRandomDay(ScheduledHygieneFlashRandomDay scheduledHygieneFlashRandomDay) {
        this.scheduledHygieneFlashRandomDay = scheduledHygieneFlashRandomDay;
    }

    public ScheduledStandByRandomDay getScheduledStandByRandomDay() {
        return scheduledStandByRandomDay;
    }

    public void setScheduledStandByRandomDay(ScheduledStandByRandomDay scheduledStandByRandomDay) {
        this.scheduledStandByRandomDay = scheduledStandByRandomDay;
    }


    public ArrayList<Ranges> getRangesArrayList() {
        return rangesArrayList;
    }

    public void setRangesArrayList(ArrayList<Ranges> rangesArrayList) {
        this.rangesArrayList = rangesArrayList;
    }

    public String getPairingCode() {
        return pairingCode;
    }

    public void setPairingCode(String pairingCode) {
        this.pairingCode = pairingCode;
    }

    public boolean isPreviouslyConnected() {
        return isPreviouslyConnected;
    }

    public void setPreviouslyConnected(boolean previouslyConnected) {
        isPreviouslyConnected = previouslyConnected;
    }

    public Date getUnitDate() {
        return unitDate;
    }

    public void setUnitDate(Date unitDate) {
        this.unitDate = unitDate;
    }

    public Date getInitialParringDate() {
        return initialParringDate;
    }

    public void setInitialParringDate(Date initialParringDate) {
        this.initialParringDate = initialParringDate;
    }

    public String getLocationNAme() {
        return locationNAme;
    }

    public void setLocationNAme(String locationNAme) {
        this.locationNAme = locationNAme;
    }

    public boolean isRangesReceived() {
        return isRangesReceived;
    }

    public void setRangesReceived(boolean rangesReceived) {
        isRangesReceived = rangesReceived;
    }
}
