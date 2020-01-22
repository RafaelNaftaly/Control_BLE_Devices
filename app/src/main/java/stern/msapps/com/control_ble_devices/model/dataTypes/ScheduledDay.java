package stern.msapps.com.control_ble_devices.model.dataTypes;

import stern.msapps.com.control_ble_devices.model.enums.DaysOfWeek;
import stern.msapps.com.control_ble_devices.model.enums.ScheduledDataTypes;

public class ScheduledDay extends Day {


    private DaysOfWeek dayOfWeek;


    public ScheduledDay(DaysOfWeek day, long duration, long time, ScheduledDataTypes type) {
        super(duration, time, type);
        this.dayOfWeek = day;

    }

    public ScheduledDay(DaysOfWeek day, long duration, long time, ScheduledDataTypes type, long handleID, boolean isSent) {
        super(duration, time, type, handleID, isSent);
        this.dayOfWeek = day;

    }


    public DaysOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DaysOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }


}
