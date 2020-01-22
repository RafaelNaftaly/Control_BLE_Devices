package stern.msapps.com.control_ble_devices.utils.dataParser;

import android.util.Log;

import com.crashlytics.android.Crashlytics;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import stern.msapps.com.control_ble_devices.model.dataTypes.Day;
import stern.msapps.com.control_ble_devices.model.enums.ScheduledDataTypes;


public class BleDataParser {


    private static BleDataParser instance;
    private Day.ScheduledData scheduledData;

    public static BleDataParser getInstance() {
        if (instance == null) {
            instance = new BleDataParser();
        }

        return instance;
    }

    private final String TAG = BleDataParser.this.getClass().getSimpleName();

    private static final Pattern REGEX_PATTERN = Pattern.compile("^#(?:[0-9a-fA-F]{3}){1,2}$");

    public BleDataParser() {
    }


    public Date getDate(String s, boolean isCalendarDate) throws NullPointerException {

        Date dater = null;

        try

        {


            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

            String[] hexArr = s.split(" ");


            StringBuilder stringBuilderDate = new StringBuilder();
            StringBuilder stringBuilderTime = new StringBuilder();

            String seconds;
            String minutes;
            String hours;
            String day;
            String month;
            String year;


            if (isCalendarDate) {

                seconds = String.valueOf(Integer.parseInt(hexArr[0], 16));

                minutes = String.valueOf(((Integer.parseInt(hexArr[1], 16) < 10) ? "0" + Integer.parseInt(hexArr[1], 16) : Integer.parseInt(hexArr[1], 16)));

                hours = String.valueOf(Integer.parseInt(hexArr[2], 16));

                day = String.valueOf(Integer.parseInt(hexArr[3], 16));

                month = String.valueOf(Integer.parseInt(hexArr[4], 16));

                year = String.valueOf(2000 + Integer.parseInt(hexArr[5], 16));
            } else {
                seconds = String.valueOf(Integer.parseInt(hexArr[1], 16));
                minutes = String.valueOf(((Integer.parseInt(hexArr[2], 16) < 10) ? "0" + Integer.parseInt(hexArr[2], 16) : Integer.parseInt(hexArr[2], 16)));
                hours = String.valueOf(Integer.parseInt(hexArr[3], 16));
                day = String.valueOf(Integer.parseInt(hexArr[4], 16));
                month = String.valueOf(Integer.parseInt(hexArr[5], 16));
                year = String.valueOf(2000 + Integer.parseInt(hexArr[6], 16));
            }


            stringBuilderDate.append(year);
            stringBuilderDate.append("/");
            stringBuilderDate.append(month);
            stringBuilderDate.append("/");
            stringBuilderDate.append(day);


            stringBuilderTime.append(hours);
            stringBuilderTime.append(":");
            stringBuilderTime.append(minutes);
            stringBuilderTime.append(":");
            stringBuilderTime.append(seconds);


            //  Log.d(TAG,"............The Date is = " + hours + ":" + minutes + ":" + seconds + " " + day + "/" + month + "/" + year);


            if (seconds.equals("255") && minutes.equals("255") && hours.equals("255")) {
                return null;
            }
            try {

                dater = simpleDateFormat.parse(stringBuilderDate.toString() + " " + stringBuilderTime.toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } catch (Exception e)

        {


        }
        return dater;
    }

    public String parseDate(Date date) {

        //date format is:  "Date-Month-Year Hour:Minutes am/pm"
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, yyyy HH:mma"); //Date and time
        String currentDate = sdf.format(date);


        return currentDate;

    }

    public Date intToDate(int d) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");


        return new Date(d);


    }

    public byte[] intToByte(int val) {

        return String.valueOf(val).getBytes();
    }

    public int byteToInt(byte[] val) {

        return Integer.valueOf(new String(val)).intValue();
    }


    public String parseSerialNumber(String str) {


        StringBuilder serialStringBuilder = new StringBuilder();

        try {


            String[] hexArr = str.split(" ");

            String[] finalHexArr = Arrays.copyOfRange(hexArr, 1, 5);

            for (int i = finalHexArr.length - 1; i >= 0; i--) {

                try {
                    serialStringBuilder.append(finalHexArr[i]);
                } catch (NumberFormatException ex) {
                    ex.printStackTrace();
                }

            }
        } catch (Exception e) {

            e.getStackTrace();

        } finally {

            return String.valueOf(Integer.parseInt(serialStringBuilder.toString(), 16));

        }

    }

    public Date getDateFromMilisec(String milliSec) {

        return new Date(Long.parseLong(milliSec));

    }

    public String getHourMillSecforScheduledDay(String milliSec) {

        SimpleDateFormat format = new SimpleDateFormat("h:mm a");


        return format.format(Long.parseLong(milliSec));

    }

    public String getDateMillSecforScheduledDay(String milliSec) {

        SimpleDateFormat format = new SimpleDateFormat("MMM d, yyyy");


        return format.format(Long.parseLong(milliSec));

    }


    public String getName(String str) {

        StringBuilder nameStringBuilder = new StringBuilder();

        String[] hexArr = str.split(" ");

        for (int i = 1; i < hexArr.length; i++) {
            if (!hexArr[i].equals("FF") && !hexArr[i].equals("00")) {

                String strr = hexToString(hexArr[i]);
                String temp = "";
                try {

                    temp = hexToString(hexArr[i]) + hexToString(hexArr[i + 1]);
                } catch (IndexOutOfBoundsException ex) {
                    ex.printStackTrace();
                } finally {

                    if (temp.equals("$&")) {
                        return nameStringBuilder.toString();
                    }

                    if (!strr.isEmpty()) {
                        nameStringBuilder.append(strr);
                    }

                }
            }

        }
        return nameStringBuilder.toString();


    }

    public String parseSW(String str) {
        StringBuilder swBuilder = new StringBuilder();

        String[] hexArr = str.split(" ");

        if (hexArr.length >= 3) {
            swBuilder.append(String.valueOf(Integer.parseInt(hexArr[3], 16)));
            swBuilder.append(".");
            swBuilder.append(String.valueOf(Integer.parseInt(hexArr[2], 16)));
            swBuilder.append(".");
            swBuilder.append(String.valueOf(Integer.parseInt(hexArr[1], 16)));
        }

        return swBuilder.toString();
    }

    /**
     * Converting String to Hex String
     *
     * @param str
     * @return
     */
    public String stringToHexString(String str) {

        return String.format("%x", new BigInteger(str.getBytes(StandardCharsets.UTF_8)));
    }


    /**
     * Converting Hex String to byte[]
     *
     * @param hex
     * @return
     */
    public byte[] hexStringToByteArray(String hex) {
        int l = hex.length();
        byte[] data = (hex.length() == 1) ? new byte[1] : new byte[l / 2];

        for (int i = 0; i < l - 1; i += 2) {
            data[i / 2] = (byte) (((Character.digit(hex.charAt(i), 16) & 0xFF) << 4)
                    + (Character.digit(hex.charAt(i + 1), 16) & 0xFF));
        }


        return data;
    }


    /**
     * Converting hexString to String
     *
     * @param hex
     * @return
     */
    public String hexToString(String hex) {
        int l = hex.length();
        byte[] data = new byte[l / 2];
        for (int i = 0; i < l; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }

        return new String(data, StandardCharsets.UTF_8);


    }

    public byte[] shortToHexArr(short val) {

        byte b = (byte) (val & 0xFF);
        byte[] bytes = new byte[1];
        bytes[0] = b;

        return bytes;
    }

    public byte[] longToHexArr(long val) {


        String hexaString = String.format("%04X", (0xFFFFFF & val));


        byte[] bytes = new byte[2];


        if (val <= 255) {
            bytes[1] = (byte) Long.parseLong(hexaString, 16);
            bytes[0] = (byte) 0;

        } else

        {

            String f = " ";
            char[] chars = hexaString.toCharArray();

            if (chars.length > 3) {
                f = String.valueOf(chars[2]) + String.valueOf(chars[3]);
                hexaString = String.valueOf(chars[0]) + String.valueOf(chars[1]);
                long t = Long.parseLong(hexaString, 16);
                long t2 = Long.parseLong(f, 16);
                bytes[1] = (byte) (t & 0xFF);
                bytes[0] = (byte) (t2 & 0xFF);
            } else {

                hexaString = String.valueOf(chars[0]) + String.valueOf(chars[1]);
                f = String.valueOf(chars[2]);

                long g = Long.parseLong(hexaString, 16);
                long g2 = Long.parseLong(f, 16);
                bytes[1] = (byte) (g & 0xFF);
                bytes[0] = (byte) (g2 & 0xFF);
            }


        }


        return bytes;
    }


    public byte intToByteHex(int val) {
        return (byte) (val & 0xFF);
    }


    public byte[] intTobyteArray(int integer) {

        byte[] bytes = new byte[2];
        bytes[0] = (byte) integer;

        return bytes;

    }

    public int bytesArrayToInteger(byte[] bytes) {


        int integer;

        integer = (int) bytes[0];


        return integer;

    }


    public byte[] stringToASCII(String str) {

        byte[] path = new byte[1];
        path[0] = (byte) 1;

        byte[] newByte = str.getBytes(StandardCharsets.US_ASCII);

        byte[] combined = Arrays.copyOf(path, path.length + newByte.length);
        System.arraycopy(newByte, 0, combined, path.length, newByte.length);


        // return str.getBytes(StandardCharsets.US_ASCII);
        return combined;


    }

    public String asciiToString(byte[] str) {

        return new String(str, StandardCharsets.US_ASCII);
    }


    public Calendar getCalendar(int year, int month, int dayOfMonth, int hour, int minutes, int seconds) {

        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minutes);

        return calendar;
    }

    public int getEventHandleID(String data) {
        StringBuilder handelIdlStringBuilder = new StringBuilder();

        String[] hexArr = data.split(" ");

        Log.d("parsingEventId", "The data was received is = " + data);
        if (hexArr.length >= 13) {
            String[] finalHexArr = Arrays.copyOfRange(hexArr, 11, 13);


            for (int i = finalHexArr.length - 1; i >= 0; i--) {

                try {
                    handelIdlStringBuilder.append(finalHexArr[i]);
                } catch (NumberFormatException ex) {
                    ex.printStackTrace();
                }

            }
        } else {
            return -1;
        }

        return Integer.valueOf(Integer.parseInt(handelIdlStringBuilder.toString(), 16));

    }


    public ScheduledDataTypes getScheduledDataTypesFromData(String data, boolean isRead) {

        ScheduledDataTypes scheduledDataTypes = null;


        String[] hexArr = data.split(" ");


        int type = isRead ? Integer.valueOf(Integer.parseInt(hexArr[1], 16)) : Integer.valueOf(Integer.parseInt(hexArr[0], 16));

        switch (type) {
            case 2:
                scheduledDataTypes = ScheduledDataTypes.HYGIENE_FLUSH;
                break;
            case 3:
                scheduledDataTypes = ScheduledDataTypes.STANDBY_MODE;
                break;
        }

        return scheduledDataTypes;

    }


    public int getScheduledSendedID(String data) {

        String[] hexArr = data.split(" ");

        return Integer.valueOf(Integer.parseInt(hexArr[13], 16));

    }

    public String addSpaceEveryXchars(String str, String times) {
        String val = times;   // use 4 here to insert spaces every 4 characters
        return str.replaceAll("(.{" + val + "})", "$1 ").trim();
    }

    public Day.ScheduledData getScheduledDataFromBytes(String data) {
        Calendar calendar = Calendar.getInstance();
        ScheduledDataTypes dataTypes = null;
        long time = 0;


        String[] bytesArr = data.split(" ");

        //There is no scheduled data in the BLE device
        if (!checkIfScheduledHasData(bytesArr)) {
            return null;
        }


        try {

            if (bytesArr[8].equals("00") || bytesArr[9].equals("00") || bytesArr[10].equals("00")) {
                calendar.setTimeInMillis(0);
            } else if (!bytesArr[9].equals("63")) { // If it not FROM_LAST_ACTIVATION
                int sec = Integer.parseInt(bytesArr[5], 16);
                int minutes = Integer.parseInt(bytesArr[6], 16);
                int hours = Integer.parseInt(bytesArr[7], 16);
                int day = Integer.parseInt(bytesArr[8], 16);
                int month = Integer.parseInt(bytesArr[9], 16) - 1;
                int year = Integer.parseInt(bytesArr[10], 16) + 2000;
                calendar.set(year, month, day, hours, minutes, sec);

                time = calendar.getTimeInMillis();
            }


            long duration = Integer.parseInt(bytesArr[2], 16);
            if (bytesArr[9].equals("63")) {

                dataTypes = ScheduledDataTypes.getType(Integer.parseInt(bytesArr[1], 16) + 4);
                time = (Integer.parseInt((bytesArr[4] + bytesArr[3]), 16) / 60);
            } else {
                dataTypes = ScheduledDataTypes.getType(Integer.parseInt(bytesArr[1], 16));
            }


            if ((Integer.parseInt(bytesArr[3], 16) == 0 || Integer.parseInt(bytesArr[4], 16) == 0) && !bytesArr[9].equals("63")) {
                if (dataTypes == ScheduledDataTypes.HYGIENE_FLUSH) {
                    dataTypes = ScheduledDataTypes.HYGIENE_RANDOM_DAY;
                } else {
                    dataTypes = ScheduledDataTypes.STANDBY_RANDOM_DAY;
                }
            }

            long hadleID = getEventHandleID(data);

            return new Day.ScheduledData(duration, time, dataTypes, hadleID, true);
        } catch (Exception ex) {


            Crashlytics.logException(new RuntimeException(ex.getMessage() + data));
            return null;
        }


    }

    private boolean checkIfScheduledHasData(String[] data) {

        short hasNoDataCounter = 0;

        for (int i = 1; i < data.length; i++) {
            String str = data[i];

            if (str.equals("00")) {
                hasNoDataCounter++;
            }

        }

        return (hasNoDataCounter < (data.length - 1));

    }


}
