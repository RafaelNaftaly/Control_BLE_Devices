package stern.msapps.com.control_ble_devices.model.dataTypes;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import java.util.List;

import stern.msapps.com.control_ble_devices.model.dataTypes.ranges.Ranges;
import stern.msapps.com.control_ble_devices.model.enums.SternTypes;
import stern.msapps.com.control_ble_devices.model.roomDataBase.database.converters.RangesArrayListTypeConverter;
import stern.msapps.com.control_ble_devices.model.roomDataBase.database.converters.SternTypeConverter;

@Entity(tableName = "presets_stern_product")
public class PresetSternProduct {


    public List<Ranges> getGetRangesList() {
        return getRangesList;
    }

    public void setGetRangesList(List<Ranges> getRangesList) {
        this.getRangesList = getRangesList;
    }

    @TypeConverters(RangesArrayListTypeConverter.class)
    public List<Ranges> getRangesList;
    @PrimaryKey(autoGenerate = true)
    private int presetID;
    @ColumnInfo(name = "preset_name")
    private String presetName;
    @ColumnInfo(name = "is_checked")
    private boolean isChecked;
    @TypeConverters(SternTypeConverter.class)
    public SternTypes type;

    /* protected List<Ranges> rangesList;*/

    public SternTypes getType() {
        return type;
    }

    public void setType(SternTypes type) {
        this.type = type;
    }


    public String getPresetName() {
        return presetName;

    }


    public void setPresetName(String presetName) {
        this.presetName = presetName;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public int getPresetID() {
        return presetID;
    }

    public void setPresetID(int presetID) {
        this.presetID = presetID;
    }


}
