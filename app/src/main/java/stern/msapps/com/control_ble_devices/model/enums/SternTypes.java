package stern.msapps.com.control_ble_devices.model.enums;

import java.util.UUID;

import stern.msapps.com.control_ble_devices.R;


import static stern.msapps.com.control_ble_devices.model.ble.BLEGattAttributes.*;


public enum SternTypes {

    SOAP_DISPENSER("Soap Dispenser"),
    FOAM_SOAP_DISPENSER("Foam Soap Dispenser"),
    FAUCET("Faucet"),
    SHOWER("Shower"),
    URINAL("Urinal"),
    WC("WC"),
    WAVE_ON_OFF("Wave on off"),
    VALVE("Valve"),
    UNKNOWN("Unknown");


    private final String name;

     SternTypes(String s) {
        this.name = s;
    }


    public int getImagePath() {

        //TODO... implement all types

        switch (this) {
            case SOAP_DISPENSER:
                return R.mipmap.soapel;

            case FOAM_SOAP_DISPENSER:
                return R.mipmap.foam;

            case FAUCET:
                return R.mipmap.sinkel;
            case SHOWER:
                return R.mipmap.shower;

            case URINAL:
                return R.mipmap.urinal;
            case WC:
                return R.mipmap.toilet;
            case WAVE_ON_OFF:
                return R.mipmap.hand;

            case VALVE:
                return R.mipmap.valve;
            case UNKNOWN:
                return R.mipmap.unknown;

            default:
                return 0;
        }
    }


    //Returning the value of enum Names
    public String getName() {
        return name;
    }

    public static SternTypes getType(UUID uuid) {

        if (uuid.equals(STERN_SOAP_UUID)) {
            return SOAP_DISPENSER;
        } else if (uuid.equals(STERN_FOAM_SOAP_UUID)) {
            return FOAM_SOAP_DISPENSER;
        } else if (uuid.equals(STERN_FAUCET_UUID)) {
            return FAUCET;
        } else if (uuid.equals(STERN_SHOWER_UUID)) {
            return SHOWER;
        } else if (uuid.equals(STERN_URINAL_UUID)) {
            return URINAL;
        } else if (uuid.equals(STERN_WC_UUID)) {
            return WC;
        } else if (uuid.equals(STERN_WAWE_ON_OFF_UUID)) {
            return WAVE_ON_OFF;
        }
        return UNKNOWN;

    }

    public static SternTypes getType(String name){

        if (name.equals(SOAP_DISPENSER.name)) {
            return SOAP_DISPENSER;
        } else if (name.equals(FOAM_SOAP_DISPENSER.name)) {
            return FOAM_SOAP_DISPENSER;
        } else if (name.equals(FAUCET.name)) {
            return FAUCET;
        } else if (name.equals(SHOWER.name)) {
            return SHOWER;
        } else if (name.equals(URINAL.name)) {
            return URINAL;
        } else if (name.equals(WC.name)) {
            return WC;
        } else if (name.equals(WAVE_ON_OFF.name)) {
            return WAVE_ON_OFF;
        }
        return UNKNOWN;


    }


}
