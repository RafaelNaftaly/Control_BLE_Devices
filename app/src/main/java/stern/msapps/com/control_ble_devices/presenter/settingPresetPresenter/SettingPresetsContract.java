package stern.msapps.com.control_ble_devices.presenter.settingPresetPresenter;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;

import stern.msapps.com.control_ble_devices.presenter.BaseMvpPresenter;
import stern.msapps.com.control_ble_devices.view.BaseView;

public interface SettingPresetsContract {


    interface Presenter extends BaseMvpPresenter<View> {


        void presetsRecyclerViewCreation(RecyclerView presetRecyclerView);
        void presetsSearchEngine(SearchView searchView);
        void onDoneClicked();
        void onCreate();
        void onStart();
        void onResume();
        void onPause();
        void onStop();
        void searchViewOnclick();

    }


    interface View extends BaseView {


        void viewInit();

        void recyclerViewInit();

        void hideFragment();


    }


}
