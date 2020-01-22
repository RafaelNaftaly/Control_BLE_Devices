package stern.msapps.com.control_ble_devices.model.enums;

import stern.msapps.com.control_ble_devices.R;

public enum DosageButtons {
    DOSAGE_ONE,
    DOSAGE_TWO,
    DOSAGE_THREE,
    DOSAGE_FOUR;


    private int sternDavaValue;

    public int getNAme() {
        switch (this) {
            case DOSAGE_ONE:
                return R.string.range_type_dosage_one;
            case DOSAGE_TWO:
                return R.string.range_type_dosage_two;
            case DOSAGE_THREE:
                return R.string.range_type_dosage_four;
            case DOSAGE_FOUR:
                return R.string.range_type_dosage_two;

            default:
                return -1;
        }
    }

    public int getSternDavaValue() {

        switch (this) {
            case DOSAGE_ONE:
                return 8;
            case DOSAGE_TWO:
                return 9;
            case DOSAGE_THREE:
                return 10;
            case DOSAGE_FOUR:
                return 11;
            default:
                return -1;
        }


    }
}
