package stern.msapps.com.control_ble_devices.model.dataTypes;

import stern.msapps.com.control_ble_devices.model.enums.ScheduledDataTypes;

public class ScheduledStandByRandomDay extends Day {


    public ScheduledStandByRandomDay(long duration, long time, ScheduledDataTypes type) {
        super(duration, time, type);
    }

    public ScheduledStandByRandomDay(ScheduledData data) {
        super(data);
    }


}
