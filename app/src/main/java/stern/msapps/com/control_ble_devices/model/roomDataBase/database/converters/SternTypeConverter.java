package stern.msapps.com.control_ble_devices.model.roomDataBase.database.converters;

import android.arch.persistence.room.TypeConverter;

import stern.msapps.com.control_ble_devices.model.enums.SternTypes;

public class SternTypeConverter {


    @TypeConverter
    public static SternTypes nameToSternTypes(String value) {
        return value == null ? null : SternTypes.getType(value);
    }

    @TypeConverter
    public static String sternTypeToNameString(SternTypes sternTypes) {
        return sternTypes == null ? null : sternTypes.getName();
    }
}

