package stern.msapps.com.control_ble_devices.utils;


import android.os.Handler;
import android.util.Log;

public class BleDataWatchDog {


    private static BleDataWatchDog bleDataWatchDog;
    private long time = -1;
    private Handler handler;
    private Runnable runnable;


    public static BleDataWatchDog getBleDataWatchDogInstance() {
        if (bleDataWatchDog == null) {
            bleDataWatchDog = new BleDataWatchDog();
        }

        return bleDataWatchDog;
    }


    public BleDataWatchDog setTime(long time) {

        this.time = time;

        return this;
    }


    public BleDataWatchDog start() {
        if (this.time == -1) {
            throw new IllegalStateException();
        } else {
            handler = new Handler();
            runnable = () -> {
                Log.d("WachDog", ".........****************DONE");
            };
            handler.postDelayed(runnable, time);

        }


        return this;
    }


}
