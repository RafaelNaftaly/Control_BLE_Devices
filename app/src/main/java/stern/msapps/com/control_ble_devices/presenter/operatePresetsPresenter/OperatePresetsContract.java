package stern.msapps.com.control_ble_devices.presenter.operatePresetsPresenter;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;

import stern.msapps.com.control_ble_devices.presenter.BaseMvpPresenter;
import stern.msapps.com.control_ble_devices.model.dataTypes.OperatePreset;
import stern.msapps.com.control_ble_devices.view.BaseView;

public interface OperatePresetsContract {


    interface Presenter extends BaseMvpPresenter<View> {


        void presetsRecyclerViewCreation(RecyclerView presetRecyclerView);

        void presetsSearchEngine(SearchView searchView);


        void onCreate();

        void onStart();

        void onResume();

        void onPause();

        void onStop();

        void searchViewOnclick();

        void startOperatePresetEventBusEvents(OperatePreset operatePreset);

    }


    interface View extends BaseView {


        void viewInit();

        void initializeAndAttachePresenter();

        void recyclerViewInit();

        void hideFragment();

        void onEventInit();

        android.support.v7.widget.SearchView getSearchView();


    }
}
