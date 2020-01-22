package stern.msapps.com.control_ble_devices.eventBus;

import stern.msapps.com.control_ble_devices.model.dataTypes.OperatePreset;
import stern.msapps.com.control_ble_devices.model.dataTypes.SternProduct;

public class Events {


    public static class SterProductTransmition {
        private SternProduct sternProduct;

        public SterProductTransmition(SternProduct sternProduct) {
            this.sternProduct = sternProduct;
        }


        public SternProduct getSternProduct() {
            return sternProduct;
        }

    }


    public static class SterProductStatistics {
        private SternProduct sternProduct;

        public SterProductStatistics(SternProduct sternProduct) {
            this.sternProduct = sternProduct;
        }


        public SternProduct getSternProduct() {
            return sternProduct;
        }

    }

    public static class PincodeIssue {
        boolean isPasskeyOk;

        public PincodeIssue(boolean isPasskeyOk) {
            this.isPasskeyOk = isPasskeyOk;
        }

        public boolean wrongPasskey() {
            return this.isPasskeyOk;
        }
    }

    public static class OperatePresetTransmission {
        private OperatePreset operatePreset;

        public OperatePresetTransmission(OperatePreset operatePreset) {
            this.operatePreset = operatePreset;
        }

        public OperatePreset getOperatePreset() {
            return this.operatePreset;
        }
    }

}
