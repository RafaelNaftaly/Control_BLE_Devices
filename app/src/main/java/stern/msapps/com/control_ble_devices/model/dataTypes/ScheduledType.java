package stern.msapps.com.control_ble_devices.model.dataTypes;

/**
 *
 */
public class ScheduledType {

    private String title;
    private long time;
    private short duration;
    private String durationName;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public short getDuration() {
        return duration;
    }

    public void setDuration(short duration) {
        this.duration = duration;
    }

    public String getDurationName() {
        return durationName;
    }

    public void setDurationName(String durationName) {
        this.durationName = durationName;
    }



    public enum DayOfWeek {
        MONDAY("Monday"),
        TUESDAY("Tuesday"),
        WEDNESDAY("Wednesday"),
        THURSDAY("Thursday"),
        FRIDAY("Friday"),
        SATURDAY("Saturday");

        private String name;

        DayOfWeek(String s) {
            this.name = s;

        }


        public String getNAme() {
            return this.name;
        }


    }

}
