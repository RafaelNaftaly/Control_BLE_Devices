package stern.msapps.com.control_ble_devices.model.enums;

import stern.msapps.com.control_ble_devices.R;

public enum SettingButtons {

    RESET_TO_FACTORY_SETTINGS;


    public int getName(SettingButtons settingButtons) {
        switch (settingButtons) {
            case RESET_TO_FACTORY_SETTINGS:
                return R.string.settings_screen_reset_to_factory;
            default:
                return -1;
        }
    }

}
