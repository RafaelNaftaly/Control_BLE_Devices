package stern.msapps.com.control_ble_devices.utils.dataParser;

import android.util.Log;

import java.math.BigInteger;

import stern.msapps.com.control_ble_devices.model.dataTypes.ranges.Ranges;
import stern.msapps.com.control_ble_devices.model.enums.RangeTypes;
import stern.msapps.com.control_ble_devices.view.adapters.SettingsSeekBarsAdapter;

public class SettingsScreenBleDataParser extends BleDataParser {

    private final String TAG = SettingsSeekBarsAdapter.class.getSimpleName();


    private static SettingsScreenBleDataParser instance;

    private SettingsScreenBleDataParser() {
    }

    public static SettingsScreenBleDataParser getInstance() {

        if (instance == null) {
            instance = new SettingsScreenBleDataParser();
        }

        return instance;
    }

    public Ranges getRanges(String data, RangeTypes rangeType) {

        Log.d("", "");

        String[] hexArr = data.split(" ");
        Ranges ranges = new Ranges();
        ranges.setRangeType(rangeType);

        if (hexArr.length < 4) {
            Log.d(TAG, "RangeType is = " + rangeType);
        }

        if (rangeType == RangeTypes.SOAP_DOSAGE) { //If it Soap Dispenser Dosage range
            if (!hexArr[0].isEmpty() && !hexArr[1].isEmpty()) {
                ranges.setMinimumValue(1);
                ranges.setMaximumValue(4);
                ranges.setDefaultValue(1);
                ranges.setCurrentValue(Integer.parseInt(hexArr[1], 16));
                return ranges;
            } else {
                return null;
            }
        }

        if (rangeType == RangeTypes.DETECTION_RANGE) {

            int currentVAl = Integer.parseInt(hexArr[1], 16);
            int maxStep = Integer.parseInt(hexArr[2], 16);
            ranges.setMinimumValue(1);
            ranges.setMaximumValue(maxStep);
            ranges.setDefaultValue(0);
            ranges.setCurrentValue(currentVAl);


        } else {

            int currentValue = Integer.parseInt(hexArr[4] + hexArr[3] + hexArr[2] + hexArr[1], 16) / 1000;
            int maxValue = Integer.parseInt(hexArr[8] + hexArr[7] + hexArr[6] + hexArr[5], 16) / 1000;
            int minValue = Integer.parseInt(hexArr[12] + hexArr[11] + hexArr[10] + hexArr[9], 16) / 1000;
            int defaultValue = Integer.parseInt(hexArr[16] + hexArr[15] + hexArr[14] + hexArr[13], 16) / 1000;


            ranges.setCurrentValue(currentValue);
            ranges.setMaximumValue(maxValue);
            ranges.setMinimumValue(minValue);
            ranges.setDefaultValue(defaultValue);
        }


        return ranges;
    }

    public byte[] getRangeDataBytes(long millisec) {


        String hex = String.format("%08X", (0xFFFFFF & millisec));

        String[] splitedHex = addSpaceEveryXchars(hex, "2").split(" ");

        byte fourByte = ((new BigInteger(splitedHex[0], 16).toByteArray().length) > 1) ? (new BigInteger(splitedHex[0], 16).toByteArray()[1])
                : (new BigInteger(splitedHex[0], 16).toByteArray()[0]);
        byte thirdByte = ((new BigInteger(splitedHex[1], 16).toByteArray().length) > 1) ? (new BigInteger(splitedHex[1], 16).toByteArray()[1])
                : (new BigInteger(splitedHex[1], 16).toByteArray()[0]);
        byte secondByte = ((new BigInteger(splitedHex[2], 16).toByteArray().length) > 1) ? (new BigInteger(splitedHex[2], 16).toByteArray()[1])
                : (new BigInteger(splitedHex[2], 16).toByteArray()[0]);
        byte firsByte = ((new BigInteger(splitedHex[3], 16).toByteArray().length) > 1) ? (new BigInteger(splitedHex[3], 16).toByteArray()[1])
                : (new BigInteger(splitedHex[3], 16).toByteArray()[0]);

        byte[] data = new byte[5];
        data[0] = (byte) 0x1;
        data[1] = firsByte;
        data[2] = secondByte;
        data[3] = thirdByte;
        data[4] = fourByte;


        return data;
    }


    public byte[] getDetectionRangeBytes(int steps) {

        String hex = String.format("%02X", (0xFFFFFF & steps));

        String[] splitedHex = addSpaceEveryXchars(hex, "2").split(" ");

        byte byteData = ((new BigInteger(splitedHex[0], 16).toByteArray().length) > 1) ? (new BigInteger(splitedHex[0], 16).toByteArray()[1])
                : (new BigInteger(splitedHex[0], 16).toByteArray()[0]);


        byte[] data = new byte[2];
        data[0] = (byte) 0x1;
        data[1] = byteData;

        return data;

    }

    public byte[] getFoamSoapRangeBytes(int soapMotor, int airMotor) {

        String soapHex = String.format("%02X", (0xFFFFFF & soapMotor));
        String airpHex = String.format("%02X", (0xFFFFFF & airMotor));

        String[] splitedSoapHex = addSpaceEveryXchars(soapHex, "2").split(" ");
        String[] splitedAirHex = addSpaceEveryXchars(airpHex, "2").split(" ");

        byte[] data = new byte[3];
        data[0] = (byte) 0x1;
        data[1] = new BigInteger(splitedSoapHex[0], 16).toByteArray()[0];
        data[2] = new BigInteger(splitedAirHex[0], 16).toByteArray()[0];

        return data;

    }

    public byte[] getDosageBytes(int dosage) {


        byte[] dosageBytesValue = new byte[2];

        dosageBytesValue[0] = 1;
        dosageBytesValue[1] = (byte) dosage;

        return dosageBytesValue;


    }


}
