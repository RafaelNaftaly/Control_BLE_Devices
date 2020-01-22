package stern.msapps.com.control_ble_devices.model.dataTypes;

import stern.msapps.com.control_ble_devices.model.enums.SternTypes;

public class SoapDispenser extends SternProduct {

    private int dosage = -1;

    SoapDispenser(SternTypes type) {
        super(type);
    }


    public int getDosage() {
        return dosage;
    }

    public void setDosage(int dosage) {
        this.dosage = dosage;
    }
}
