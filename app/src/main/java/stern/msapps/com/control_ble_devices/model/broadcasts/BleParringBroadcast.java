package stern.msapps.com.control_ble_devices.model.broadcasts;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


/**
 * This broadcast is for inserting a parring key
 */
public class BleParringBroadcast extends BroadcastReceiver {

    private String TAG = BleParringBroadcast.this.getClass().getSimpleName();
    private static String parringKey;
    //TODO... Save the parring key in SharePref


    public static void setParringKey(String parringKey) {
        BleParringBroadcast.parringKey = parringKey;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG, ".........onReceive()");

        if (intent.getAction().equals("android.bluetooth.device.action.PAIRING_REQUEST")) {
            try {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                int pin = intent.getIntExtra("android.bluetooth.device.extra.PAIRING_KEY", 0);

                //the pin in case you need to accept for an specific pin
                Log.d(TAG, " " + intent.getIntExtra("android.bluetooth.device.extra.PAIRING_KEY", 0) + pin);

                //maybe you look for a name or address
                Log.d(TAG, device.getName());
                byte[] pinBytes;
                //TODO... must get pin code from Shared Pref
                pinBytes = ("" + parringKey).getBytes("UTF-8");
                boolean isSet = device.setPin(pinBytes);

                Log.d(TAG, "The key was added = " + isSet);
                //setPairing confirmation if neeeded
                boolean isParred = device.setPairingConfirmation(true);

                Log.d(TAG, "isPerred = " + isParred);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}


