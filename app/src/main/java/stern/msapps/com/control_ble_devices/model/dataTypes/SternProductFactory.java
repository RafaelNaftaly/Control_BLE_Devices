package stern.msapps.com.control_ble_devices.model.dataTypes;

import java.util.ArrayList;

import stern.msapps.com.control_ble_devices.model.dataTypes.ranges.FoamRanges;
import stern.msapps.com.control_ble_devices.model.dataTypes.ranges.Ranges;
import stern.msapps.com.control_ble_devices.model.enums.RangeTypes;
import stern.msapps.com.control_ble_devices.model.enums.SternTypes;


/**
 * A factory pattern
 * will return a SternProduct object within initial Ranges
 * must receive a SternProduct type enum
 */
public class SternProductFactory {
    ArrayList<Ranges> rangesArrayList;


    public SternProduct getSternType(SternTypes type) {
        switch (type) {
            case FAUCET:
                return getFaucetProduct(type);
            case WAVE_ON_OFF:
                return getWaveOnOff(type);
            case FOAM_SOAP_DISPENSER:
                return getFoamSoapDispenser(type);
            case SHOWER:
                return getShower(type);
            case URINAL:
                return getUrinal(type);
            case WC:
                return getToilet(type);
            case VALVE:
                return getValve(type);
            case SOAP_DISPENSER:
                return getSoapDispenser(type);
            case UNKNOWN:
                return new Unknown(type);

            default:
                return new Unknown(type);
        }
    }


    private SternProduct getFaucetProduct(SternTypes type) {
        rangesArrayList = new ArrayList<>();
        Ranges delayIn = new Ranges(RangeTypes.DELAY_IN, 0, 0, 0, 0);
        rangesArrayList.add(delayIn);

        Ranges delayOut = new Ranges(RangeTypes.DELAY_OUT, 0, 0, 0, 0);
        rangesArrayList.add(delayOut);

        Ranges detectionRange = new Ranges(RangeTypes.DETECTION_RANGE, 0, 0, 0, 0);
        rangesArrayList.add(detectionRange);


        Ranges securityTime = new Ranges(RangeTypes.SECURITY_TIME, 0, 0, 0, 0);
        rangesArrayList.add(securityTime);


        Faucet faucet = new Faucet(type);
        faucet.setRangesArrayList(rangesArrayList);

        return faucet;

    }

    private SternProduct getWaveOnOff(SternTypes type) {
        rangesArrayList = new ArrayList<>();
        Ranges delayIn = new Ranges(RangeTypes.DELAY_IN, 0, 0, 0, 0);
        rangesArrayList.add(delayIn);


        Ranges delayOut = new Ranges(RangeTypes.DELAY_OUT, 0, 0, 0, 0);
        rangesArrayList.add(delayOut);

        Ranges detectionRange = new Ranges(RangeTypes.DETECTION_RANGE, 0, 0, 0, 0);
        rangesArrayList.add(detectionRange);


        Ranges securityTime = new Ranges(RangeTypes.SECURITY_TIME, 0, 0, 0, 0);
        rangesArrayList.add(securityTime);


        WaweOnOff waweOnOff = new WaweOnOff(type);
        waweOnOff.setRangesArrayList(rangesArrayList);

        return waweOnOff;

    }

    private SternProduct getFoamSoapDispenser(SternTypes type) {
        rangesArrayList = new ArrayList<>();

        Ranges delayOut = new Ranges(RangeTypes.DELAY_OUT, 0, 0, 0, 0);
        rangesArrayList.add(delayOut);


        Ranges detectionRange = new Ranges(RangeTypes.DETECTION_RANGE, 0, 0, 0, 0);
        rangesArrayList.add(detectionRange);


        FoamRanges airMotor = new FoamRanges(RangeTypes.AIR_MOTOR, 0, 9, 0, 0, 0, 0);
        rangesArrayList.add(airMotor);


        FoamRanges soapMotor = new FoamRanges(RangeTypes.SOAP_MOTOR, 0, 9, 0, 0, 0, 0);
        rangesArrayList.add(soapMotor);

        FoamSoapDispenser foamSoapDispenser = new FoamSoapDispenser(type);
        foamSoapDispenser.setRangesArrayList(rangesArrayList);

        return foamSoapDispenser;

    }

    private SternProduct getShower(SternTypes type) {
        rangesArrayList = new ArrayList<>();

        Ranges delayIn = new Ranges(RangeTypes.DELAY_IN, 0, 0, 0, 0);
        rangesArrayList.add(delayIn);


        Ranges delayOut = new Ranges(RangeTypes.DELAY_OUT, 0, 0, 0, 0);
        rangesArrayList.add(delayOut);

        Ranges detectionRange = new Ranges(RangeTypes.DETECTION_RANGE, 0, 0, 0, 0);
        rangesArrayList.add(detectionRange);

        Ranges securityTime = new Ranges(RangeTypes.SECURITY_TIME, 0, 0, 0, 0);
        rangesArrayList.add(securityTime);


        Shower shower = new Shower(type);
        shower.setRangesArrayList(rangesArrayList);

        return shower;

    }

    private SternProduct getUrinal(SternTypes type) {
        rangesArrayList = new ArrayList<>();

        Ranges detectionRange = new Ranges(RangeTypes.DETECTION_RANGE, 0, 0, 0, 0);
        rangesArrayList.add(detectionRange);

        Ranges delayIn = new Ranges(RangeTypes.DELAY_IN, 0, 0, 0, 0);
        rangesArrayList.add(delayIn);


        Ranges delayOut = new Ranges(RangeTypes.DELAY_OUT, 0, 0, 0, 0);
        rangesArrayList.add(delayOut);

        Ranges longFlush = new Ranges(RangeTypes.LONG_FLUSH, 0, 0, 0, 0);
        rangesArrayList.add(longFlush);


        Urinal urinal = new Urinal(type);
        urinal.setRangesArrayList(rangesArrayList);

        return urinal;

    }

    private SternProduct getToilet(SternTypes type) {
        rangesArrayList = new ArrayList<>();

        Ranges detectionRange = new Ranges(RangeTypes.DETECTION_RANGE, 0, 0, 0, 0);
        rangesArrayList.add(detectionRange);

        Ranges delayIn = new Ranges(RangeTypes.DELAY_IN, 0, 0, 0, 0);
        rangesArrayList.add(delayIn);


        Ranges delayOut = new Ranges(RangeTypes.DELAY_OUT, 0, 0, 0, 0);
        rangesArrayList.add(delayOut);

        Ranges shortFlush = new Ranges(RangeTypes.SHORT_FLUSH, 0, 0, 0, 0);
        rangesArrayList.add(shortFlush);

        Ranges longFlush = new Ranges(RangeTypes.LONG_FLUSH, 0, 0, 0, 0);
        rangesArrayList.add(longFlush);


        Toilet toilet = new Toilet(type);
        toilet.setRangesArrayList(rangesArrayList);

        return toilet;

    }

    private SternProduct getValve(SternTypes type) {
        rangesArrayList = new ArrayList<>();


        Valve valve = new Valve(type);
        valve.setRangesArrayList(rangesArrayList);

        return valve;

    }


    private SternProduct getSoapDispenser(SternTypes type) {
        rangesArrayList = new ArrayList<>();

        Ranges detectionRange = new Ranges(RangeTypes.DETECTION_RANGE, 0, 0, 0, 0);
        rangesArrayList.add(detectionRange);

        Ranges soapDosage = new Ranges(RangeTypes.SOAP_DOSAGE, 0, 4, 1, 0);
        rangesArrayList.add(soapDosage);


        Ranges delayOut = new Ranges(RangeTypes.DELAY_OUT, 0, 0, 0, 0);
        rangesArrayList.add(delayOut);


        SoapDispenser soapDispenser = new SoapDispenser(type);
        soapDispenser.setRangesArrayList(rangesArrayList);

        return soapDispenser;

    }


}
