package stern.msapps.com.control_ble_devices.model.dataTypes;

import stern.msapps.com.control_ble_devices.model.enums.SternTypes;

public class FoamSoapDispenser extends SternProduct {
    public FoamSoapDispenser(SternTypes type) {
        super(type);

    }

    enum RangeTypes{
        SOAP_MOTOR,
        AIR_MOTOR;
    }
}
