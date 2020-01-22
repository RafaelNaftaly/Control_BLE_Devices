package stern.msapps.com.control_ble_devices.model.dataTypes;



public class ScheduledStandByMode extends Schedule {

    private static ScheduledStandByMode instance;


    public static ScheduledStandByMode getInstance() {
        if (instance == null) {
            instance = new ScheduledStandByMode();
        }

        return instance;
    }


}
