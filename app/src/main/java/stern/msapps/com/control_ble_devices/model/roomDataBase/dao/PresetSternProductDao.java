package stern.msapps.com.control_ble_devices.model.roomDataBase.dao;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import stern.msapps.com.control_ble_devices.model.dataTypes.PresetSternProduct;
import stern.msapps.com.control_ble_devices.model.enums.SternTypes;


// Data Access Object -  Room DB.
@Dao
public interface PresetSternProductDao {


    @Insert
    long insert(PresetSternProduct presetSternProduct);

    @Query("SELECT * FROM presets_stern_product")
    List<PresetSternProduct> getAll();

    @Query("DELETE FROM presets_stern_product WHERE presetID = :PresetId")
    void deleteByPresetId(int PresetId);



    @Query("SELECT * FROM presets_stern_product WHERE type = :types")
    List<PresetSternProduct> getPresetByType(SternTypes types);

}
