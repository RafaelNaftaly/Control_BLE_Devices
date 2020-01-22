package stern.msapps.com.control_ble_devices.presenter.operatePresetsPresenter;

import android.bluetooth.le.ScanResult;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;

import android.view.View;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import stern.msapps.com.control_ble_devices.model.dataTypes.SternProduct;
import stern.msapps.com.control_ble_devices.presenter.BasePresenter;
import stern.msapps.com.control_ble_devices.R;
import stern.msapps.com.control_ble_devices.model.dataTypes.OperatePreset;
import stern.msapps.com.control_ble_devices.eventBus.Events;
import stern.msapps.com.control_ble_devices.eventBus.GlobalBus;
import stern.msapps.com.control_ble_devices.model.roomDataBase.database.SternRoomDatabase;

import stern.msapps.com.control_ble_devices.view.adapters.OperatePresetAdapter;
import stern.msapps.com.control_ble_devices.view.adapters.SwipeHelper;

public class OperatePresetsPresenter extends BasePresenter<OperatePresetsContract.View> implements OperatePresetsContract.Presenter,
        View.OnClickListener {


    private LinearLayoutManager presetsLinearLayoutManager;
    private RecyclerView presetRecyclerView;
    private SternProduct sternProduct;
    private OperatePresetAdapter operatePresetsRecyclerViewAdapter;
    private ArrayList<OperatePreset> operatePresetArrayList;
    private ArrayList<OperatePreset> operatePresets;


    @Subscribe(sticky = true)
    public void getSternProduct(Events.SterProductTransmition sternProduct) {

        this.sternProduct = sternProduct.getSternProduct();


    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.operate_presets_fragment_done_tv:
                getmView().hideFragment();
                break;

            case R.id.operate_presets_fragment_search_icon_IMG:
                searchViewOnclick();
                break;

        }
    }




    @Override
    public void onScannedDeviceData(ScanResult scanResults) {

    }

    @Override
    public void presetsRecyclerViewCreation(RecyclerView presetRecyclerView) {

        this.presetRecyclerView = presetRecyclerView;
        this.presetRecyclerView.setFocusable(false);
        this.presetsLinearLayoutManager = new LinearLayoutManager(getmView().getContext(), LinearLayoutManager.VERTICAL, false);
        this.presetRecyclerView.setLayoutManager(this.presetsLinearLayoutManager);


        operatePresets = new ArrayList<>(SternRoomDatabase.getDatabase(getmView().getContext()).operatePresetDao().getPresetByType(this.sternProduct.getType()));

        this.operatePresetsRecyclerViewAdapter = new OperatePresetAdapter(getmView().getContext(), operatePresets);
        this.presetRecyclerView.setAdapter(operatePresetsRecyclerViewAdapter);

        new SwipeHelper(getmView().getContext(), presetRecyclerView) {
            @Override
            public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {
                underlayButtons.add(new SwipeHelper.UnderlayButton(
                        getmView().getContext().getResources().getString(R.string.preset_screen_delete),
                        0,
                        Color.parseColor("#FF3C30"),
                        pos -> {

                            SternRoomDatabase.getDatabase(getmView().getContext()).operatePresetDao().deletById(operatePresets.get(pos).getPreset_ID());
                            operatePresets.remove(pos);
                            operatePresetsRecyclerViewAdapter.notifyDataSetChanged();


                            // TODO: onDelete
                        }
                ));

                underlayButtons.add(new SwipeHelper.UnderlayButton(
                        getmView().getContext().getResources().getString(R.string.preset_screen_set),
                        0,
                        Color.parseColor("#016990"),
                        pos -> {
                            startOperatePresetEventBusEvents(operatePresets.get(pos));
                            getmView().hideFragment();
                        }
                ));

            }
        };


    }

    @Override
    public void presetsSearchEngine(SearchView searchView) {


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                ArrayList<OperatePreset> presetSternProducts = (ArrayList<OperatePreset>) SternRoomDatabase.getDatabase(getmView().getContext()).operatePresetDao().getAll();
                ArrayList<OperatePreset> presetSternProductsNew = new ArrayList<>();
                for (OperatePreset sternProduct : presetSternProducts)

                {
                    if (sternProduct.getPresetName().toLowerCase().contains(newText)) {
                        presetSternProductsNew.add(sternProduct);

                    }


                }

                operatePresetsRecyclerViewAdapter.setSearchData(presetSternProductsNew);

                return true;
            }
        });

    }



    @Override
    public void onCreate() {
        GlobalBus.getBus().register(this);
    }

    @Override
    public void onStart() {
        // GlobalBus.getBus().register(this);

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {
        GlobalBus.getBus().unregister(this);
    }

    @Override
    public void onStop() {

    }

    @Override
    public void searchViewOnclick() {

        if (getmView().getSearchView().getVisibility() == View.GONE) {
            getmView().getSearchView().setVisibility(View.VISIBLE);
        } else {
            getmView().getSearchView().setVisibility(View.GONE);
        }
    }

    @Override
    public void startOperatePresetEventBusEvents(OperatePreset operatePreset) {

        Events.OperatePresetTransmission operatePresetTransmission = new Events.OperatePresetTransmission(operatePreset);

        GlobalBus.getBus().post(operatePresetTransmission);

    }


}
