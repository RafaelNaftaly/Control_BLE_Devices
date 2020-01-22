package stern.msapps.com.control_ble_devices.model.dataTypes;

import stern.msapps.com.control_ble_devices.model.enums.ScheduledDataTypes;

public class ScheduledHygieneFlashRandomDay extends Day {

    public ScheduledHygieneFlashRandomDay(long duration, long time, ScheduledDataTypes type) {
        super(duration, time, type);
    }

    public ScheduledHygieneFlashRandomDay(ScheduledData data) {
        super(data);
    }
}
