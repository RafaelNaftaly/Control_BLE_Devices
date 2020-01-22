package stern.msapps.com.control_ble_devices.model.roomDataBase.database.converters;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import stern.msapps.com.control_ble_devices.model.dataTypes.ranges.Ranges;


public class RangesArrayListTypeConverter {

    static Gson gson = new Gson();

    @TypeConverter
    public static List<Ranges> stringToRangesObjectList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<Ranges>>() {}.getType();

        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String RangesObjectListToString(List<Ranges> someObjects) {
        return gson.toJson(someObjects);
    }
}


