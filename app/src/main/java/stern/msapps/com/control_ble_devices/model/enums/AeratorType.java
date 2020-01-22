package stern.msapps.com.control_ble_devices.model.enums;

public enum AeratorType {
    ONE_POINT_THREE_LPM("1.3 lpm"),
    ONE_EIGHTY_NINE_LPM("1.89 lpm"),
    THREE_LPM("3 lpm"),
    FOUR_LPM("4 lpm"),
    SIX_LPM("6 lpm");


    private String name;


    AeratorType(String s) {
        this.name = s;
    }


    public double getNumericalValue() {

        switch (this) {
            case ONE_POINT_THREE_LPM:
                return 1.3;
            case ONE_EIGHTY_NINE_LPM:
                return 1.89;
            case THREE_LPM:
                return 3;
            case FOUR_LPM:
                return 4;
            case SIX_LPM:
                return 6;
            default:
                return -1;
        }

    }


}
