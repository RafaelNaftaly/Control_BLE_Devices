package stern.msapps.com.control_ble_devices.view.fragments;

import android.support.v4.app.Fragment;
import android.content.Context;

import stern.msapps.com.control_ble_devices.view.BaseView;


/**
 * Created by Rafael on 5/7/19.
 */
public class BaseFragment extends Fragment implements BaseView {

    private Context context;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }


    @Override
    public Context getActivityContext() {
        return context;


    }
}
