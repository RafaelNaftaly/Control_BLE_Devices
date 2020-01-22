package stern.msapps.com.control_ble_devices.view.activities;


import android.annotation.SuppressLint;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


import java.lang.reflect.Field;

import androidx.navigation.Navigation;
import butterknife.BindView;
import butterknife.ButterKnife;
import stern.msapps.com.control_ble_devices.model.dataTypes.SternProduct;
import stern.msapps.com.control_ble_devices.model.dataTypes.User;
import stern.msapps.com.control_ble_devices.utils.Constants;
import stern.msapps.com.control_ble_devices.utils.DialogHelper;
import stern.msapps.com.control_ble_devices.R;


/**
 * Created by Rafael on 3/7/19.
 */
public class MainActivity extends AppCompatActivity {


    // Variables declaration ButterKnife library.
    @BindView(R.id.bottom_navigation_main_cleaner_activity)
    BottomNavigationView bottomNavigationViewMainActivity;

    private final String TAG = MainActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        bottomNavigationViewCleanerMainActivity();
        setToolbarVisibility(false);
        disableShiftMode(bottomNavigationViewMainActivity);

        //Disable Setting screen for Cleaner
        if (User.getUserInstance().getUserType().equals(User.UserType.CLEANER)) {
            bottomNavigationViewMainActivity.getMenu().getItem(2).setEnabled(false);
        }


    }

    @Override
    public void onBackPressed() {


        if (Navigation.findNavController(this, R.id.main_activity_host_fragment).getCurrentDestination().getLabel().equals("fragment_scanned_products")) {

            DialogHelper.getInstance().dialogCloseTheAppOnBackPress(this).show();

        } else {


            try {
                Navigation.findNavController(this, R.id.main_activity_host_fragment).navigate(R.id.ScannedProductsFragment);
                bottomNavigationViewMainActivity.getMenu().getItem(0).setChecked(true);
            } catch (Exception ex) {
                ex.printStackTrace();

            }


        }

        setToolbarVisibility(false);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.bind(this).unbind();
    }


    private void bottomNavigationViewCleanerMainActivity() {


        bottomNavigationViewMainActivity.setOnNavigationItemSelectedListener(
                item -> {

                    switch (item.getItemId()) {
                        case R.id.action_information:


                            if (!Navigation.findNavController(this, R.id.main_activity_host_fragment).getCurrentDestination().getLabel().equals("fragment_product_information")) {
                                Bundle data = new Bundle();
                                data.putString(Constants.SHARED_DONT_LOAD_DATA, Constants.SHARED_DONT_LOAD_DATA);
                                Navigation.findNavController(this, R.id.main_activity_host_fragment).navigate(R.id.productInformationFragment, data);
                            }
                            break;
                        case R.id.action_operate:


                            if (!Navigation.findNavController(this, R.id.main_activity_host_fragment).getCurrentDestination().getLabel().equals("fragment_technician_operate")) {
                                Navigation.findNavController(this, R.id.main_activity_host_fragment).navigate(R.id.operateFragment);
                            }
                            break;
                        case R.id.action_settings:

                            if (!Navigation.findNavController(this, R.id.main_activity_host_fragment).getCurrentDestination().getLabel().equals("fragment_settings")) {
                                Navigation.findNavController(this, R.id.main_activity_host_fragment).navigate(R.id.settingsFragment);
                            }
                            break;
                    }
                    return true;
                });
    }

    public void setToolbarVisibility(boolean isVisible) {
        if (bottomNavigationViewMainActivity != null) {

            if (isVisible) {
                bottomNavigationViewMainActivity.setVisibility(View.VISIBLE);
            } else {
                bottomNavigationViewMainActivity.setVisibility(View.GONE);
            }
        }
    }


    @SuppressLint("RestrictedApi")
    public void disableShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                //noinspection RestrictedApi
                item.setShiftingMode(false);
                // set once again checked value, so view will be updated
                //noinspection RestrictedApi
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
            Log.e("BNVHelper", "Unable to get shift mode field", e);
        } catch (IllegalAccessException e) {
            Log.e("BNVHelper", "Unable to change value of shift mode", e);
        }
    }

    public BottomNavigationView getBottomNavigationView() {
        return this.bottomNavigationViewMainActivity;
    }


}
