package stern.msapps.com.control_ble_devices.model.roomDataBase.database;


import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;

import stern.msapps.com.control_ble_devices.model.dataTypes.PresetSternProduct;
import stern.msapps.com.control_ble_devices.model.dataTypes.SternProduct;
import stern.msapps.com.control_ble_devices.model.roomDataBase.dao.OperatePresetDao;
import stern.msapps.com.control_ble_devices.model.roomDataBase.dao.PresetSternProductDao;
import stern.msapps.com.control_ble_devices.model.roomDataBase.dao.SternProductDao;
import stern.msapps.com.control_ble_devices.model.dataTypes.OperatePreset;
import stern.msapps.com.control_ble_devices.model.roomDataBase.database.converters.RangesArrayListTypeConverter;
import stern.msapps.com.control_ble_devices.model.roomDataBase.database.converters.ScheduledDayConverter;
import stern.msapps.com.control_ble_devices.model.roomDataBase.database.converters.SternTypeConverter;


// Room Data Base creation.
@Database(entities = {SternProduct.class, PresetSternProduct.class, OperatePreset.class}, version = 1, exportSchema = false)
@TypeConverters({SternTypeConverter.class, RangesArrayListTypeConverter.class, ScheduledDayConverter.class})
public abstract class SternRoomDatabase extends RoomDatabase {

    public abstract SternProductDao sternProductDao();

    public abstract PresetSternProductDao presetSternProductDao();

    public abstract OperatePresetDao operatePresetDao();

    private static volatile SternRoomDatabase INSTANCE;
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {

        }
    };

    public static SternRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (SternRoomDatabase.class) {
                if (INSTANCE == null) {


                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            SternRoomDatabase.class, "stern_database").allowMainThreadQueries().addMigrations(MIGRATION_1_2)
                            .build();


                }
            }
        }
        return INSTANCE;
    }
}
