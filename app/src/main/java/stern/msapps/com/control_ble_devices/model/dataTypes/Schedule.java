package stern.msapps.com.control_ble_devices.model.dataTypes;

import java.util.ArrayList;

import stern.msapps.com.control_ble_devices.model.enums.DaysOfWeek;
import stern.msapps.com.control_ble_devices.model.enums.ScheduledDataTypes;


public class Schedule {


    private ArrayList<ScheduledDay> scheduledDaysArr;
    private Day.ScheduledData fromLastActivation;


    public Schedule() {
        scheduledDaysArr = new ArrayList<>();

    }


    public ArrayList<ScheduledDay> getScheduledDaysArr() {
        return scheduledDaysArr;
    }


    public void setScheduledDaysArr(DaysOfWeek day, long duration, long time, ScheduledDataTypes type) {


        //TODO.......
        if (!scheduledDaysArr.isEmpty()) {

            if (containsDay(day)) {
                for (ScheduledDay scheduledDay : scheduledDaysArr) {
                    if (scheduledDay.getDayOfWeek() == day) {
                        scheduledDay.adTime(duration, time, type);

                    }
                }
            } else {
                scheduledDaysArr.add(new ScheduledDay(day, duration, time, type));
            }


        } else {
            scheduledDaysArr.add(new ScheduledDay(day, duration, time, type));
        }

    }

    public void setScheduledDaysArr(DaysOfWeek day, long duration, long time, ScheduledDataTypes type, long handleID) {


        //TODO.......
        if (!scheduledDaysArr.isEmpty()) {

            if (containsDay(day)) {
                for (ScheduledDay scheduledDay : scheduledDaysArr) {

                    if (scheduledDay.getDayOfWeek() == day) {
                        scheduledDay.adTime(duration, time, type);
                    }
                }
            } else {

                scheduledDaysArr.add(new ScheduledDay(day, duration, time, type));
            }


        } else {
            scheduledDaysArr.add(new ScheduledDay(day, duration, time, type));
        }

    }


    public boolean containsDay(DaysOfWeek day) {
        for (ScheduledDay d : scheduledDaysArr) {
            if (d.getDayOfWeek() == day) {

                return true;
            }
        }

        return false;
    }

    public boolean isRoom(DaysOfWeek day) {

        boolean isRoom = true;

        if (scheduledDaysArr.isEmpty()) {
            return true;
        } else {


            for (int i = 0; i < scheduledDaysArr.size(); i++) {
                ScheduledDay d = scheduledDaysArr.get(i);
                if (d.getDayOfWeek() == day) {
                    isRoom = !d.isBothDaysAreSet();
                }
            }


        }
        return isRoom;
    }


    public ScheduledDay getScheduledDay(DaysOfWeek day) {

        for (ScheduledDay d : scheduledDaysArr) {
            if (d.getDayOfWeek() == day) {
                return d;
            }
        }
        return null;
    }

    public void deleteScheduledEvent(DaysOfWeek day, int pos) {
        for (int i = 0; i < scheduledDaysArr.size(); i++) {
            if (scheduledDaysArr.get(i).getDayOfWeek() == day) {
                scheduledDaysArr.get(i).deleteEvent(pos);
//                if (!scheduledDaysArr.get(i).getScheduledDataArrayList().isEmpty()) {
//                    scheduledDaysArr.remove(pos);
//                }

            }
        }
    }

    public boolean deleteEventByID(int id) {

        boolean isDeleted = false;
        for (ScheduledDay scheduledDay : scheduledDaysArr) {
            isDeleted = scheduledDay.deleteEventByID(id);
        }

        return isDeleted;
    }


    public Day getScheduleDay(DaysOfWeek day, int pos) {
        for (ScheduledDay d : scheduledDaysArr) {
            if (d.getDayOfWeek() == day) {
                return d;
            }
        }

        return null;
    }


    public Day.ScheduledData getFromLastActivationHours() {
        return this.fromLastActivation;
    }

    public void setFromLastActivationHours(long fromLastActivationHours, long duration, ScheduledDataTypes type) {
        if (fromLastActivationHours == -1 || duration == -1) {
            this.fromLastActivation = null;
            return;
        }
        this.fromLastActivation = new Day.ScheduledData(duration, fromLastActivationHours, type);
        //this.fromLastActivationHours = fromLastActivationHours;
    }
}
