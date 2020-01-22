package stern.msapps.com.control_ble_devices.view.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.OnTextChanged;
import stern.msapps.com.control_ble_devices.model.dataTypes.User;
import stern.msapps.com.control_ble_devices.utils.Constants;
import stern.msapps.com.control_ble_devices.utils.RequestPermissionFromUser;
import stern.msapps.com.control_ble_devices.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;



/**
 * Created by Rafael on 3/7/19.
 */

public class PasswordActivity extends AppCompatActivity {


    /*
     * Variables declaration ButterKnife library.
     * */
    @BindView(R.id.password_activity_enter_password_ET)
    EditText enterPassword_EDTX;
    @BindView(R.id.password_activity_connect_TV)
    TextView password_fargment_connect_BTN;

    private RequestPermissionFromUser requestPermisionFromUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);


        ButterKnife.bind(this);

        // ACCESS_FINE_LOCATION , ACCESS_COARSE_LOCATION.
        requestPermisionFromUser = new RequestPermissionFromUser(this);
        requestPermisionFromUser.requestPermission();


    }

        // Set event listener ButterKnife .
    @OnTextChanged(R.id.password_activity_enter_password_ET)
    void passwordActivityConnectTXVEfected() {

        if (enterPassword_EDTX.getText().toString().length() == 4) {
            password_fargment_connect_BTN.setEnabled(true);
            password_fargment_connect_BTN.setClickable(true);
        } else {
            password_fargment_connect_BTN.setEnabled(false);
            password_fargment_connect_BTN.setClickable(false);
        }

        if (enterPassword_EDTX.getText().toString().length() > 4) {
            enterPassword_EDTX.setError("The password must be 4 digits");
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.bind(this).unbind();
    }

    /*
     * Method , checks if entered password is correct.
     *
     * @param:
     * @return:
     * @throws:
     * */

    @OnClick(R.id.password_activity_connect_TV)
    void checkPassword() {


        //If the password is wrong
        if (!enterPassword_EDTX.getText().toString().equals(Constants.SHARED_PREF_CLEANER_PASSWORD) && !enterPassword_EDTX.getText().toString().equals(Constants.SHARED_PREF_TECHNICIAN_PASSWORD)) {
            enterPassword_EDTX.setError("The password is wrong ");
        } else { //If the password is OK
            String password = enterPassword_EDTX.getText().toString();
            //Creating a user with password
            User.getUserInstance().setUserType(password, this);

            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

    }


    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {


        requestPermisionFromUser.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

}
