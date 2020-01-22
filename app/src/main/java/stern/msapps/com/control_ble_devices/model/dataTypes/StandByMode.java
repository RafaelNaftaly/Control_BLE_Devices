package stern.msapps.com.control_ble_devices.model.dataTypes;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class StandByMode {

    private ArrayList<HashMap<ScheduledType.DayOfWeek, Integer>> scheduledByDays;
    private ArrayList<Date> dateArrayList;


    public ArrayList<HashMap<ScheduledType.DayOfWeek, Integer>> getScheduledByDays() {
        return scheduledByDays;
    }

    public void setScheduledByDays(ArrayList<HashMap<ScheduledType.DayOfWeek, Integer>> scheduledByDays) {
        this.scheduledByDays = scheduledByDays;
    }

    public ArrayList<Date> getDateArrayList() {
        return dateArrayList;
    }

    public void setDateArrayList(ArrayList<Date> dateArrayList) {
        this.dateArrayList = dateArrayList;
    }
}
