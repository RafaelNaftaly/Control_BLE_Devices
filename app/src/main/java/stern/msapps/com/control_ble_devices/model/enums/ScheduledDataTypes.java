package stern.msapps.com.control_ble_devices.model.enums;


/**
 * Current enum is type for ScheduledData class
 */
public enum ScheduledDataTypes {

    HYGIENE_FLUSH,
    STANDBY_MODE,
    HYGIENE_RANDOM_DAY,
    STANDBY_RANDOM_DAY,
    HYGIENE_FROM_LAST_ACTIVATION,
    STANDBY_FROM_LAST_ACTIVATION;


    public static ScheduledDataTypes getType(int val) {
        switch (val) {
            case 2:
                return ScheduledDataTypes.HYGIENE_FLUSH;
            case 3:
                return ScheduledDataTypes.STANDBY_MODE;
            case 6:
                return ScheduledDataTypes.HYGIENE_FROM_LAST_ACTIVATION;
            case 7:
                return ScheduledDataTypes.STANDBY_FROM_LAST_ACTIVATION;

            default:
                return ScheduledDataTypes.HYGIENE_FLUSH;
        }
    }

}
