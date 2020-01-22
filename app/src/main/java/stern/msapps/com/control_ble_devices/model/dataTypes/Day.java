package stern.msapps.com.control_ble_devices.model.dataTypes;

import java.util.ArrayList;

import stern.msapps.com.control_ble_devices.model.enums.ScheduledDataTypes;


/**
 * This class representing a scheduled day
 */
public class Day {

    private ArrayList<ScheduledData> scheduledDataArrayList = new ArrayList<>(2);


    public Day(long duration, long time, ScheduledDataTypes type) {

        scheduledDataArrayList.add(new ScheduledData(duration, time, type));


    }

    public Day(long duration, long time, ScheduledDataTypes type, long handleID, boolean isSent) {
        scheduledDataArrayList.add(new ScheduledData(duration, time, type, handleID, isSent));
    }

    public Day(ScheduledData data) {
        scheduledDataArrayList.add(data);
    }


    public void adTime(long duration, long time, ScheduledDataTypes type) {
        this.scheduledDataArrayList.add(new ScheduledData(duration, time, type));

    }

    public void addScheduleData(ScheduledData scheduledData) {
        scheduledDataArrayList.add(scheduledData);
    }


    public boolean isBothDaysAreSet() {

        return scheduledDataArrayList.size() == 2;
    }

    public void setScheduledDataHandleID(int position, long handleID) {
        scheduledDataArrayList.get(position).setHandleID(handleID);
    }


    public ArrayList<ScheduledData> getScheduledDataArrayList() {
        return scheduledDataArrayList;
    }

    public void deleteEvent(int pos) {

        scheduledDataArrayList.remove(pos);

    }

    public boolean deleteEventByID(int id) {
        for (ScheduledData sd : scheduledDataArrayList) {
            if (sd.getHandleID() == id) {
                scheduledDataArrayList.remove(sd);
                return true;
            }
        }
        return false;
    }


    /**
     * This class is Storing the Scheduled day data
     */
    public static class ScheduledData {
        private long duration;                                      // duration of the event
        private long time;                                          // date in milliseconds when current event should be happen
        private long handleID = -1;                                 // id which was store in Stern BLE device for scheduled event
        private ArrayList<Integer> randomArr = new ArrayList<>();   //
        private boolean wasSent;                                    // if the dada was already sent
        private ScheduledDataTypes type;                            // enum for scheduled data type
        private boolean wasChosenForPreset;                         // is current day was chosen from Preset


        public ScheduledData(long duration, long time, ScheduledDataTypes type) {
            this.duration = duration;
            this.time = time;
            handleID = (handleID == -1) ? generateHandleID() : handleID;
            this.type = type;
        }

        public ScheduledData(long duration, long time, ScheduledDataTypes type, long handleID, boolean issent) {
            this.duration = duration;
            this.time = time;
            this.handleID = handleID;
            this.type = type;
            setWasSent(issent);
        }

        private long generateHandleID() {
            int num = (int) (Math.random() * (100 - 1)) + 1;

            for (int i = 0; i < randomArr.size(); i++) {
                if (randomArr.get(i) == num) {
                    generateHandleID();
                    break;
                }
            }
            randomArr.add(num);

            return num;

        }

        public long getDuration() {
            return duration;
        }

        public void setDuration(long duration) {
            this.duration = duration;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public long getHandleID() {
            return handleID;
        }

        public void setHandleID(long handleID) {
            this.handleID = handleID;
        }

        public boolean isWasSent() {
            return wasSent;
        }

        public void setWasSent(boolean wasSent) {
            this.wasSent = wasSent;
        }

        public ScheduledDataTypes getType() {
            return type;
        }

        public void setType(ScheduledDataTypes type) {
            this.type = type;
        }


        public boolean isWasChosenForPreset() {
            return wasChosenForPreset;
        }

        public void setWasChosenForPreset(boolean wasChosenForPreset) {
            this.wasChosenForPreset = wasChosenForPreset;
        }
    }
}
