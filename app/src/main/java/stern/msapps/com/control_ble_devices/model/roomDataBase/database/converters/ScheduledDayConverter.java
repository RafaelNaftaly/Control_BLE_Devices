package stern.msapps.com.control_ble_devices.model.roomDataBase.database.converters;

import android.arch.persistence.room.TypeConverter;


import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import stern.msapps.com.control_ble_devices.model.dataTypes.ScheduledDay;

public class ScheduledDayConverter {

    static Gson gson = new Gson();


    @TypeConverter
    public static List<ScheduledDay> stringToScheduleDayArr(String data) {
        if (data == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<ScheduledDay>>() {
        }.getType();

        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String ScheduledOBJtoString(List<ScheduledDay> scheduledDays) {
        return gson.toJson(scheduledDays);
    }

}
