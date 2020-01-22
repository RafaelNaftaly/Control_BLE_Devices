package stern.msapps.com.control_ble_devices.view.activities;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import stern.msapps.com.control_ble_devices.model.dataTypes.User;
import stern.msapps.com.control_ble_devices.utils.AppSharedPref;
import stern.msapps.com.control_ble_devices.utils.Constants;
import stern.msapps.com.control_ble_devices.BuildConfig;
import stern.msapps.com.control_ble_devices.R;


/**
 * Created by Rafael on 3/7/19.
 */
public class SplashActivity extends AppCompatActivity implements View.OnClickListener {

    // Variables declaration ButterKnife library.
    @BindView(R.id.splash_version)
    TextView version_TV;
    @BindView(R.id.splash_powered_by)
    ImageView poweredBy;


    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this, this);





        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Intent startRelevantActivity = null;

            //If the user was already defined
            if (User.getUserInstance().isUserTypeSet(SplashActivity.this)) {
                startRelevantActivity = new Intent(SplashActivity.this, MainActivity.class);
            } else {
                startRelevantActivity = new Intent(SplashActivity.this, PasswordActivity.class);
            }

            startActivity(startRelevantActivity);
            finish();

        }, 1500);


        version_TV.setText("Version: " + BuildConfig.VERSION_NAME);

        poweredBy.setOnClickListener(this);
        makeImageGray();

        //Don't add a shortcut if already added
        if (AppSharedPref.getInstance(this).getPrefString(Constants.SHARED_SHORCUT_TO_MAIN_SCREEN, null).isEmpty()) {
            setShortCut();
            AppSharedPref.getInstance(this).savePrefString(Constants.SHARED_SHORCUT_TO_MAIN_SCREEN, Constants.SHARED_SHORCUT_TO_MAIN_SCREEN);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.splash_powered_by:
                startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://www.msapps.mobi")));
                break;
        }
    }

    private void makeImageGray() {

        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        poweredBy.setColorFilter(filter);
    }


    /**
     * Setting a shortcut to HomePage
     */
    private void setShortCut() {

        if (Build.VERSION.SDK_INT < 26) {


            Intent shortcutintent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
            shortcutintent.putExtra("duplicate", false);
            shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.app_name));
            Parcelable icon = Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.mipmap.ic_launcher);
            shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
            shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(getApplicationContext(), SplashActivity.class));
            sendBroadcast(shortcutintent);
        } else {
            ShortcutManager shortcutManager
                    = getSystemService(ShortcutManager.class);
            if (shortcutManager.isRequestPinShortcutSupported()) {
                Intent intent = new Intent(
                        getApplicationContext(), getClass());
                intent.setAction(Intent.ACTION_MAIN);
                ShortcutInfo pinShortcutInfo = new ShortcutInfo
                        .Builder(this, "pinned-shortcut")
                        .setIcon(
                                Icon.createWithResource(this, R.mipmap.ic_launcher)
                        )
                        .setIntent(intent)
                        .setShortLabel(getString(R.string.app_name))
                        .build();
                Intent pinnedShortcutCallbackIntent = shortcutManager
                        .createShortcutResultIntent(pinShortcutInfo);
                //Get notified when a shortcut is pinned successfully//
                PendingIntent successCallback
                        = PendingIntent.getBroadcast(
                        this, 0
                        , pinnedShortcutCallbackIntent, 0
                );
                shortcutManager.requestPinShortcut(
                        pinShortcutInfo, successCallback.getIntentSender()
                );
            }
        }
    }
}
