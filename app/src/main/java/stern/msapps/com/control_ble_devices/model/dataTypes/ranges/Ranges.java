package stern.msapps.com.control_ble_devices.model.dataTypes.ranges;

import stern.msapps.com.control_ble_devices.model.enums.RangeTypes;


/**
 * This class is a ranges of
 *     DETECTION_RANGE, DELAY_IN, DELAY_OUT, SECURITY_TIME, SHORT_FLUSH, LONG_FLUSH, SOAP_DOSAGE, FOAM_SOAP, SOAP_MOTOR, AIR_MOTOR, BETWEEN_TIME;
 *
 */
public class Ranges {

    protected RangeTypes rangeType;
    protected int currentValue;
    protected int maximumValue;
    protected int minimumValue;
    protected int defaultValue;

    public Ranges() {
    }

    public Ranges(RangeTypes rangeType, int currentValue, int maximumValue, int minimumValue, int defaultValue) {
        this.maximumValue = maximumValue;
        this.minimumValue = minimumValue;
        this.defaultValue = defaultValue;
        this.currentValue = currentValue;
        this.rangeType = rangeType;
    }

    public Ranges(RangeTypes types) {
        this.rangeType = types;
    }



    public int getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(int currentValue) {
        this.currentValue = currentValue;

    }

    public int getMaximumValue() {
        return maximumValue;
    }

    public void setMaximumValue(int maximumValue) {
        this.maximumValue = maximumValue;
    }

    public int getMinimumValue() {
        return minimumValue;
    }

    public void setMinimumValue(int minimumValue) {
        this.minimumValue = minimumValue;
    }

    public int getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(int defaultValue) {
        this.defaultValue = defaultValue;
    }


    public RangeTypes getRangeType() {
        return rangeType;
    }


    public void setNewRange(Ranges range) {
        this.setDefaultValue(range.getDefaultValue());
        this.setMinimumValue(range.getMinimumValue());
        this.setMaximumValue(range.getMaximumValue());
        this.setCurrentValue(range.getCurrentValue());
        this.setRangeType(range.getRangeType());


    }

    public void setRangeType(RangeTypes rangeType) {
        this.rangeType = rangeType;
    }

    public RangeTypes rangeTypes() {
        return this.rangeType;
    }

    @Override
    public String toString() {
        return "rangeType = " + rangeType + "\n" + "Current Value = " + currentValue + "\n" + "maximumValue = " + maximumValue + "\n" +
                "minimumValue = " + minimumValue + "\n" + "defaultValue = " + defaultValue;
    }
}
