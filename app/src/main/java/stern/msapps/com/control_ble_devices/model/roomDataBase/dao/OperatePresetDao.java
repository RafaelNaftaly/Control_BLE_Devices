package stern.msapps.com.control_ble_devices.model.roomDataBase.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import stern.msapps.com.control_ble_devices.model.dataTypes.OperatePreset;
import stern.msapps.com.control_ble_devices.model.enums.SternTypes;

 // Data Access Object -  Room DB.

@Dao
public interface OperatePresetDao {

    @Insert
    long insert(OperatePreset operatePreset);

    @Query("SELECT * FROM operate_presets_stern_product")
    List<OperatePreset> getAll();

    @Query("DELETE FROM operate_presets_stern_product WHERE preset_ID = :presetId")
    void deletById(int presetId);

    @Query("SELECT * FROM operate_presets_stern_product WHERE sternType = :types")
    List<OperatePreset> getPresetByType(SternTypes types);


}
