package stern.msapps.com.control_ble_devices.model.roomDataBase.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;


import java.util.List;

import stern.msapps.com.control_ble_devices.model.dataTypes.SternProduct;


// Data Access Object -  Room DB.

@Dao
public interface SternProductDao {


    @Insert
    long insert(SternProduct sternProduct);

    @Query("SELECT * FROM stern_product")
    List<SternProduct> getAll();

    @Update
    void updateSternProduct(SternProduct sternProduct);

    @Query("DELETE FROM stern_product WHERE id = :sternProductId")
    void deleteByPresetId(int sternProductId);


    @Query("DELETE FROM stern_product WHERE mac_address = :macAddress")
    void deleteByMacAddress(String macAddress);

    @Query("SELECT * FROM stern_product WHERE mac_address = :macAddress")
    SternProduct getProductFromDBbyMacAdress(String macAddress);

    @Delete
    void deleteAll(SternProduct... sternProducts);

    @Query("UPDATE stern_product SET name = :name WHERE mac_address = :mac_address")
    void updateName(String name, String mac_address);


    @Query("UPDATE stern_product SET last_connected = :last_connected WHERE mac_address = :mac_address")
    void updateLastConnected(String last_connected, String mac_address);

    @Query("UPDATE stern_product SET last_updated = :last_updated WHERE mac_address = :mac_address")
    void updateLastUpdated(String last_updated, String mac_address);
}



