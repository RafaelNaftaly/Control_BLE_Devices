package stern.msapps.com.control_ble_devices.model.dataTypes;

import android.content.Context;

import stern.msapps.com.control_ble_devices.utils.AppSharedPref;
import stern.msapps.com.control_ble_devices.utils.Constants;


// Singletone Class.
public class User {

    private static User userInstance;

    protected UserType userType;


    private  User() {
    }


    public static User getUserInstance() {

        if (userInstance == null) {
            return userInstance = new User();
        }
        return userInstance;
    }


    public boolean isUserTypeSet(Context context) {

        if (AppSharedPref.getInstance(context).getPrefString(Constants.SHARED_PREF_USER_PASSWORD, null).isEmpty()) {
            return false;
        } else {
            setUserType(AppSharedPref.getInstance(context).getPrefString(Constants.SHARED_PREF_USER_PASSWORD, null), context);
            return true;
        }


    }

    public User.UserType getUserType(){
        return this.userType;
    }


    public void setUserType(String password, Context context) {

        AppSharedPref.getInstance(context).savePrefString(Constants.SHARED_PREF_USER_PASSWORD, password);

        this.userType = UserType.getType(password);
    }

    public void logOut(Context context) {
        AppSharedPref.getInstance(context).savePrefString(Constants.SHARED_PREF_USER_PASSWORD, null);
        this.userType = UserType.getType("");
    }


    public enum UserType {
        CLEANER,
        TECHNICIAN,
        UNDEFINED;

        public static UserType getType(String password) {
            switch (password) {
                case "4321":
                    return TECHNICIAN;

                case "1234":

                    return CLEANER;

                default:
                    return UNDEFINED;
            }
        }

    }


}
