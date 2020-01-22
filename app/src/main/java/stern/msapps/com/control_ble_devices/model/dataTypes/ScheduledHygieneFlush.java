package stern.msapps.com.control_ble_devices.model.dataTypes;



public class ScheduledHygieneFlush extends Schedule {

    private static ScheduledHygieneFlush instance;


    public static ScheduledHygieneFlush getInstance() {
        if (instance == null) {
            instance = new ScheduledHygieneFlush();

        }

        return instance;
    }

}
