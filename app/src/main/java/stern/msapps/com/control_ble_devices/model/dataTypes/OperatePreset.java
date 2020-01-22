package stern.msapps.com.control_ble_devices.model.dataTypes;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import java.util.List;

import stern.msapps.com.control_ble_devices.model.enums.SternTypes;
import stern.msapps.com.control_ble_devices.model.roomDataBase.database.converters.ScheduledDayConverter;
import stern.msapps.com.control_ble_devices.model.roomDataBase.database.converters.SternTypeConverter;

@Entity(tableName = "operate_presets_stern_product")
public class OperatePreset {


    @ColumnInfo(name = "preset_name")
    private String presetName;

    @TypeConverters(SternTypeConverter.class)
    private SternTypes sternType;

    @TypeConverters(ScheduledDayConverter.class)
    private List<ScheduledDay> scheduledData;

    @PrimaryKey(autoGenerate = true)
    private int preset_ID;


    public String getPresetName() {
        return presetName;
    }

    public void setPresetName(String presetName) {
        this.presetName = presetName;
    }

    public SternTypes getSternType() {
        return sternType;
    }

    public void setSternType(SternTypes sternType) {
        this.sternType = sternType;
    }

    public List<ScheduledDay> getScheduledData() {
        return scheduledData;
    }

    public void setScheduledData(List<ScheduledDay> scheduledData) {
        this.scheduledData = scheduledData;
    }

    public int getPreset_ID() {
        return preset_ID;
    }

    public void setPreset_ID(int presetID) {
        this.preset_ID = presetID;
    }
}
