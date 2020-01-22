package stern.msapps.com.control_ble_devices.model.enums;

import stern.msapps.com.control_ble_devices.R;

public enum RangeTypes {

    DETECTION_RANGE,
    DELAY_IN,
    DELAY_OUT,
    SECURITY_TIME,
    SHORT_FLUSH,
    LONG_FLUSH,
    SOAP_DOSAGE,
    FOAM_SOAP,
    SOAP_MOTOR,
    AIR_MOTOR,
    BETWEEN_TIME;

    public int getName() {
        switch (this) {
            case DETECTION_RANGE:
                return R.string.range_type_detection_range;
            case DELAY_IN:
                return R.string.range_type_delay_in;
            case DELAY_OUT:
                return R.string.range_type_delay_out;
            case SECURITY_TIME:
                return R.string.range_type_security_time;
            case SHORT_FLUSH:
                return R.string.range_type_short_flush;
            case LONG_FLUSH:
                return R.string.range_type_long_flush;
            case SOAP_DOSAGE:
                return R.string.range_type_soap_dosage;
            case FOAM_SOAP:
                return R.string.range_type_foam_soap;
            case AIR_MOTOR:
                return R.string.range_type_air_motor;
            case SOAP_MOTOR:
                return R.string.range_type_soap_motor;
            case BETWEEN_TIME:
                return R.string.range_type_between_time;


            default:
                return -1;
        }
    }

    public static RangeTypes getType(String rangeType) {


        switch (rangeType) {
            case "Detection range":
                return DETECTION_RANGE;
            case "Delay in":
                return DELAY_IN;
            case "Delay out":
                return DELAY_OUT;
            case "Security time":
                return SECURITY_TIME;
            case "Short flush":
                return SHORT_FLUSH;
            case "Long flush":
                return LONG_FLUSH;
            case "Soap dosage":
                return SOAP_DOSAGE;
            case "Foam soap":
                return FOAM_SOAP;
            case "Air motor":
                return AIR_MOTOR;
            case "Soap motor":
                return SOAP_MOTOR;


            default:
                return null;
        }


    }
}
