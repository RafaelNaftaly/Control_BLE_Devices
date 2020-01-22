package stern.msapps.com.control_ble_devices.model.enums;

import android.content.Context;

import stern.msapps.com.control_ble_devices.R;

public enum DaysOfWeek {
    SUNDAY,
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY;


    public String getName(Context context) {
        switch (this) {
            case SUNDAY:
                return context.getResources().getString(R.string.day_of_week_sunday);
            case MONDAY:
                return context.getResources().getString(R.string.day_of_week_monday);
            case TUESDAY:
                return context.getResources().getString(R.string.day_of_week_tuesday);
            case WEDNESDAY:
                return context.getResources().getString(R.string.day_of_week_wednesday);
            case THURSDAY:
                return context.getResources().getString(R.string.day_of_week_thursday);
            case FRIDAY:
                return context.getResources().getString(R.string.day_of_week_friday);
            case SATURDAY:
                return context.getResources().getString(R.string.day_of_week_saturday);
            default:
                return "";
        }
    }

    public static DaysOfWeek getDayOfWeek(String dayOfWeek) {
        switch (dayOfWeek.toLowerCase()) {
            case "sunday":
                return SUNDAY;
            case "monday":
                return MONDAY;
            case "tuesday":
                return TUESDAY;
            case "wednesday":
                return WEDNESDAY;
            case "thursday":
                return THURSDAY;
            case "friday":
                return FRIDAY;
            case "saturday":
                return SATURDAY;

            default:
                return MONDAY;

        }
    }

    public static DaysOfWeek getDayOfWeek(int dayOfWeek) {
        switch (dayOfWeek) {
            case 1:
                return SUNDAY;
            case 2:
                return MONDAY;
            case 3:
                return TUESDAY;
            case 4:
                return WEDNESDAY;
            case 5:
                return THURSDAY;
            case 6:
                return FRIDAY;
            case 7:
                return SATURDAY;

            default:
                return MONDAY;

        }
    }


}

