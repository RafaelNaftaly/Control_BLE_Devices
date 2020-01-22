package stern.msapps.com.control_ble_devices.model.dataTypes.ranges;

import stern.msapps.com.control_ble_devices.model.enums.RangeTypes;


public final class FoamRanges extends Ranges {


    private int foamSoapMotor;
    private int foamSoapAirMotor;

    public FoamRanges() {
    }


    public FoamRanges(RangeTypes rangeType, int currentValue, int maximumValue, int minimumValue, int defaultValue, int foamSoapMotor, int foamSoapAirMotor) {
        super(rangeType, currentValue, maximumValue, minimumValue, defaultValue);

        this.foamSoapMotor = foamSoapMotor;
        this.foamSoapAirMotor = foamSoapAirMotor;


    }

    public int getFoamSoapAirMotor() {
        return foamSoapAirMotor;
    }

    public void setFoamSoapAirMotor(int foamSoapAirMotor) {
        this.foamSoapAirMotor = foamSoapAirMotor;
    }

    public int getFoamSoapMotor() {
        return foamSoapMotor;
    }

    public void setFoamSoapMotor(int foamSoapMotor) {
        this.foamSoapMotor = foamSoapMotor;
    }
}
