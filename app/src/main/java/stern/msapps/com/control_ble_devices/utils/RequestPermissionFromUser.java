package stern.msapps.com.control_ble_devices.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

public class RequestPermissionFromUser {


    private static final int ASK_PERMISSIONS =100 ;
      Activity activity;

    public RequestPermissionFromUser(Activity activity) {
        this.activity = activity;
    }


    public void requestPermission  () {

        try {


            int hasGpsPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                    & ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);

            if (hasGpsPermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity,
                        new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                        }, ASK_PERMISSIONS);
                return;
            }



        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void  onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case ASK_PERMISSIONS:
                // Check if the only required permission has been granted
                if (grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {


                    Toast.makeText(activity,"Permission is  granted",Toast.LENGTH_LONG).show();


                } else {

                    Toast.makeText(activity,"Permission is not granted",Toast.LENGTH_LONG).show();
                  requestPermission();
                }
                break;
        }
    }

}
