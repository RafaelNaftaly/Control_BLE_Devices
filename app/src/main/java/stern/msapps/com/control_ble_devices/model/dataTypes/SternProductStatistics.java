package stern.msapps.com.control_ble_devices.model.dataTypes;

import android.content.Context;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

import stern.msapps.com.control_ble_devices.utils.AppSharedPref;
import stern.msapps.com.control_ble_devices.R;
import stern.msapps.com.control_ble_devices.utils.Constants;
import stern.msapps.com.control_ble_devices.utils.dataParser.BleDataParser;

public class SternProductStatistics {


    private long duration;
    private long rawValue;
    private StatisticTypes type;
    private String rawDataStr;
    private String parsedStatisticsTypeStringValue = "";
    private Context context;
    private String macAddress;

    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mma"); //Date and time


    public SternProductStatistics(StatisticTypes type, String dataStr, Context context) {

        this.type = type;
        this.rawDataStr = dataStr;
        this.context = context;
        parsedStatisticsTypeStringValue = getStringData(this.type);


    }

    public SternProductStatistics(StatisticTypes type, String dataStr, Context context, String macAddress) {

        this.type = type;
        this.rawDataStr = dataStr;
        this.context = context;
        this.macAddress = macAddress;
        parsedStatisticsTypeStringValue = getStringData(this.type);


    }


    public long getRawValue() {
        return rawValue;
    }

    public void setRawValue(long rawValue) {
        this.rawValue = rawValue;
    }

    public String getParsedStatisticsTypeStringValue() {
        return parsedStatisticsTypeStringValue;
    }

    public void setParsedStatisticsTypeStringValue(String parsedStatisticsTypeStringValue) {
        this.parsedStatisticsTypeStringValue = parsedStatisticsTypeStringValue;
    }

    public StatisticTypes getType() {
        return type;
    }

    public void setType(StatisticTypes type) {
        this.type = type;
    }

    private String getStringData(StatisticTypes type) {


        switch (type) {
            case TIME_SINCE_LAST_POWERED:
                return parseTimeSinceLastPowered(rawDataStr);
            case NUMBER_OF_ACTIVATIONS:
                return parseNumberOfActivations(rawDataStr);
            case AVERAGE_OPEN_TIME:
                return parseAndCalculateAvarageTimeTapWasOpened(rawDataStr);
            case FULL_FLUSH_PERCENTAGE:
                return parseAndCalculatefullFlashPercentage(rawDataStr);
            case HALF_FLUSH_PERCENTAGE:
                return parseAndCalculateHalfFlushPercentage(rawDataStr);
            case LAST_HYGIENE_FLUSH:
                return parseSetHygieneLastEvent(rawDataStr);
            case NEXT_HYGIENE_FLUSH:
                return parseSetHygieneNextEvent(rawDataStr);
            case LAST_STANDBY:
                return parseSetStandByLastEvent(rawDataStr);
            case NEXT_STANDBY:
                return parseSetStandByNextEvent(rawDataStr);
            case LAST_FILTER_CLEANED:
                return parseSetLastFilterCleanEvent(rawDataStr);
            case LAST_SOAP_REFILLED:
                return parseSetLastSoapRefillEvent(rawDataStr);
            case BATTERY_STATE:
                return parseSetBatteryState(rawDataStr);
            default:
                return "No information";

        }


    }


    public long getDuration() {
        return duration;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }


    public enum StatisticTypes {
        TIME_SINCE_LAST_POWERED("Run time since power up"),
        BATTERY_STATE("Battery Level"),
        LAST_HYGIENE_FLUSH("Last Hygiene Flush"),
        NEXT_HYGIENE_FLUSH("Next Hygiene Flush"),
        LAST_STANDBY("Last Standby"),
        NEXT_STANDBY("Next Standby"),
        LAST_FILTER_CLEANED("Last Filter Clean"),
        LAST_SOAP_REFILLED("Last Soap Refill"),
        NUMBER_OF_ACTIVATIONS("Activations since power up"),
        AVERAGE_OPEN_TIME("Average open time"),
        HALF_FLUSH_PERCENTAGE("Half Flush Percentage"),
        FULL_FLUSH_PERCENTAGE("Full Flush Percentage");


        public String name;

        StatisticTypes(String s) {
            this.name = s;
        }


    }


    private String parseSetHygieneLastEvent(String data) {
        Day.ScheduledData scheduledData = BleDataParser.getInstance().getScheduledDataFromBytes(data);


        if (scheduledData == null) {
            return "No information";
        }
        String currentDate = getDate(scheduledData.getTime());
        this.duration = scheduledData.getDuration();

        this.rawValue = scheduledData.getTime();


        return currentDate;
    }


    private String parseSetHygieneNextEvent(String data) {

        Day.ScheduledData scheduledData = BleDataParser.getInstance().getScheduledDataFromBytes(data);

        if (scheduledData == null) {
            return "No information";
        }
        this.duration = scheduledData.getDuration();


        String currentDate = getDate(scheduledData.getTime());
        this.rawValue = scheduledData.getTime();


        Log.d("Rafael", currentDate);
        return currentDate;
    }


    private String parseSetStandByLastEvent(String data) {

        Day.ScheduledData scheduledData = BleDataParser.getInstance().getScheduledDataFromBytes(data);

        if (scheduledData == null) {
            return "No information";
        }

        this.duration = scheduledData.getDuration();
        String currentDate = getDate(scheduledData.getTime());
        this.rawValue = scheduledData.getTime();


        Log.d("Rafael", currentDate);
        return currentDate;
    }


    private String parseSetStandByNextEvent(String data) {
        Day.ScheduledData scheduledData = BleDataParser.getInstance().getScheduledDataFromBytes(data);

        if (scheduledData == null) {
            return "No information";
        }


        this.duration = scheduledData.getDuration();
        String currentDate = getDate(scheduledData.getTime());
        this.rawValue = scheduledData.getTime();


        Log.d("Rafael", currentDate);
        return currentDate;
    }


    private String parseSetLastFilterCleanEvent(String data) {


        String dataStr[] = data.split(" ");

        AppSharedPref.getInstance(context).savePrefString(macAddress + Constants.SHARED_PREF_CLEAN_FILTER, dataStr[12] + dataStr[11]);

        Day.ScheduledData scheduledData = BleDataParser.getInstance().getScheduledDataFromBytes(data);

        if (scheduledData == null) {
            return "No information";
        }

        this.duration = scheduledData.getDuration();
        String currentDate = getDate(scheduledData.getTime());
        this.rawValue = scheduledData.getTime();


        Log.d("Rafael", currentDate);
        return currentDate;
    }


    private String parseSetLastSoapRefillEvent(String data) {

        String dataStr[] = data.split(" ");
        AppSharedPref.getInstance(context).savePrefString(macAddress + Constants.SHARED_PREF_SOAP_REFILL, dataStr[12] + dataStr[11]);
        Day.ScheduledData scheduledData = BleDataParser.getInstance().getScheduledDataFromBytes(data);

        if (scheduledData == null) {
            return "No information";
        }
        this.duration = scheduledData.getDuration();
        String currentDate = getDate(scheduledData.getTime());
        this.rawValue = scheduledData.getTime();


        Log.d("Rafael", currentDate);
        return currentDate;

    }


    private String parseTimeSinceLastPowered(String data)

    {

        String[] dataArray = data.split(" ");
        String timeSinceLastPoweredTemp = dataArray[3] + dataArray[2] + dataArray[1] + dataArray[0];
        String timeSinceLastPowered = "";
        Long aLong = Long.parseLong(timeSinceLastPoweredTemp, 16);
        aLong = aLong / 10;

        if (aLong < 60) {
            timeSinceLastPowered = String.valueOf(aLong) + " " + context.getResources().getString(R.string.statistics_seconds);
            this.rawValue = aLong;
        } else {

            if (aLong < 3600) {

                timeSinceLastPowered = String.valueOf(aLong / 60) + " " + context.getResources().getString(R.string.statistics_minutes);
                this.rawValue = aLong;
            } else {
                timeSinceLastPowered = String.valueOf(aLong / 3600) + " " + context.getResources().getString(R.string.statistics_hours);
                this.rawValue = aLong;
            }
        }

        this.duration = -1;
        return timeSinceLastPowered;
    }

    private String parseNumberOfActivations(String data)

    {

        String[] dataArray = data.split(" ");
        String numberOfActivations = dataArray[7] + dataArray[6] + dataArray[5] + dataArray[4];
        Long aLong = Long.parseLong(numberOfActivations, 16);
        this.rawValue = aLong;

        this.duration = -1;
        return String.valueOf(aLong);


    }


    private String parseAndCalculateAvarageTimeTapWasOpened(String data)

    {
        String[] dataArray = data.split(" ");
        String totalTimeTapWasOpened = dataArray[11] + dataArray[10] + dataArray[9] + dataArray[8];
        long aLong = Long.parseLong(totalTimeTapWasOpened, 16);
        long totalTimeWasOpened = 0;


        String numberOfActivations = dataArray[7] + dataArray[6] + dataArray[5] + dataArray[4];
        long aLongNumberOfActivations = Long.parseLong(numberOfActivations, 16);
        String averageOpenTime = "";


        totalTimeWasOpened = aLong / 10;

        if (aLongNumberOfActivations != 0)

        {

            this.rawValue = totalTimeWasOpened / aLongNumberOfActivations;
            averageOpenTime = String.valueOf(rawValue) + " " + context.getResources().getString(R.string.statistics_lpc);
        } else {

            this.rawValue = 0;
            averageOpenTime = String.valueOf(rawValue) + " " + context.getResources().getString(R.string.statistics_lpc);

        }

        this.duration = -1;
        return averageOpenTime;
    }


    private String parseAndCalculatefullFlashPercentage(String data)


    {


        String[] dataArray = data.split(" ");
        String numberOfTimesToiletWasOpenedTap = dataArray[15] + dataArray[14] + dataArray[13] + dataArray[12];
        String numberOfActivations = dataArray[7] + dataArray[6] + dataArray[5] + dataArray[4];
        Long aLong = Long.parseLong(numberOfTimesToiletWasOpenedTap, 16);
        Long aLongNumberOfActivations = Long.parseLong(numberOfActivations, 16);
        String fullFlushPercentage = "";

        if (aLongNumberOfActivations != 0) {


            this.rawValue = aLong / aLongNumberOfActivations;
            fullFlushPercentage = String.valueOf(this.rawValue) + "%";
        } else {

            fullFlushPercentage = String.valueOf(0) + "%";
        }

        this.duration = -1;
        return fullFlushPercentage;
    }

    private String parseAndCalculateHalfFlushPercentage(String data)

    {
        String[] dataArray = data.split(" ");
        String numberOfTimesToiletWasOpenedTap = dataArray[15] + dataArray[14] + dataArray[13] + dataArray[12];
        String numberOfActivations = dataArray[7] + dataArray[6] + dataArray[5] + dataArray[4];
        String hulfFlushSTR = "";
        Long aLong = Long.parseLong(numberOfTimesToiletWasOpenedTap, 16);
        Long aLongNumberOfActivations = Long.parseLong(numberOfActivations, 16);
        Long halfFlushPercentage;
        if (aLongNumberOfActivations != 0) {
            halfFlushPercentage = (aLong - aLongNumberOfActivations) / aLongNumberOfActivations;
            this.rawValue = halfFlushPercentage;
            hulfFlushSTR = String.valueOf(halfFlushPercentage);
        } else {
            halfFlushPercentage = Long.valueOf(0);
            hulfFlushSTR = String.valueOf(halfFlushPercentage) + "%";

        }

        this.duration = -1;
        return hulfFlushSTR;
    }

    private String parseSetBatteryState(String data) {


        String[] dataArray = data.split(" ");

        String battaryStateStr = dataArray[16];

        this.duration = -1;

        int aLong = Integer.parseInt(battaryStateStr, 16);
        this.rawValue = aLong;


        switch (aLong) {
            case 0:
                return context.getResources().getString(R.string.statistics_battery_low);
            case 1:
                return context.getResources().getString(R.string.statistics_battery_medium);
            case 2:
                return context.getResources().getString(R.string.statistics_battery_ok);
            default:
                return context.getResources().getString(R.string.statistics_no_info);

        }

    }

    private String getDate(long data) {

        Date date = new Date(data);
        if (date.getTime() == 0) {
            return context.getResources().getString(R.string.statistics_no_date);
        } else {

            return sdf.format(date);
        }


    }


}
