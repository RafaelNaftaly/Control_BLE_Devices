package stern.msapps.com.control_ble_devices.presenter.settingPresetPresenter;

import android.bluetooth.le.ScanResult;
import android.content.Context;
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
import stern.msapps.com.control_ble_devices.presenter.settingPresenter.SettingPresenter;
import stern.msapps.com.control_ble_devices.R;
import stern.msapps.com.control_ble_devices.model.dataTypes.PresetSternProduct;
import stern.msapps.com.control_ble_devices.eventBus.Events;
import stern.msapps.com.control_ble_devices.eventBus.GlobalBus;
import stern.msapps.com.control_ble_devices.model.roomDataBase.database.SternRoomDatabase;
import stern.msapps.com.control_ble_devices.view.adapters.PresetsRecyclerViewAdapter;
import stern.msapps.com.control_ble_devices.view.adapters.SwipeHelper;

public class SettingPresetPresenter extends BasePresenter<SettingPresetsContract.View> implements SettingPresetsContract.Presenter,
        View.OnClickListener {
    private Context context;
    private LinearLayoutManager presetsLinearLayoutManager;
    private RecyclerView presetRecyclerView;
    private SternProduct sternProduct;
    private PresetsRecyclerViewAdapter presetsRecyclerViewAdapter;
    private SearchView presetsSearchView;

    public SettingPresetPresenter(Context context, SternProduct sternProduct) {

        this.sternProduct = sternProduct;
        this.context = context;

    }

    @Subscribe(sticky = true)
    public void getSternProduct(Events.SterProductTransmition sternProduct) {
        this.sternProduct = sternProduct.getSternProduct();

    }



    @Override
    protected void parseOnDataReceived(String dataSTR, String uuid, int requestID) {
        super.parseOnDataReceived(dataSTR, uuid, requestID);
    }

    @Override
    public void onScannedDeviceData(ScanResult scanResults) {

    }


    @Override
    public void presetsRecyclerViewCreation(RecyclerView presetRecyclerView) {


        ArrayList<PresetSternProduct> sternProducts = (ArrayList<PresetSternProduct>) SternRoomDatabase.getDatabase(context).presetSternProductDao().getPresetByType(sternProduct.getType());


        this.presetRecyclerView = presetRecyclerView;
        this.presetRecyclerView.setFocusable(false);
        presetsRecyclerViewAdapter = new PresetsRecyclerViewAdapter(getmView().getContext(), sternProducts);
        presetsLinearLayoutManager = new LinearLayoutManager(getmView().getContext(), LinearLayoutManager.VERTICAL, false);
        this.presetRecyclerView.setLayoutManager(presetsLinearLayoutManager);
        this.presetRecyclerView.setAdapter(presetsRecyclerViewAdapter);
        new SwipeHelper(context, presetRecyclerView) {
            @Override
            public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {
                underlayButtons.add(new SwipeHelper.UnderlayButton(
                        context.getResources().getString(R.string.preset_screen_delete),
                        0,
                        Color.parseColor("#FF3C30"),
                        pos -> {

                            SternRoomDatabase.getDatabase(context).presetSternProductDao().deleteByPresetId(sternProducts.get(pos).getPresetID());
                            sternProducts.remove(pos);
                            presetsRecyclerViewAdapter.notifyDataSetChanged();


                            // TODO: onDelete
                        }
                ));

                underlayButtons.add(new SwipeHelper.UnderlayButton(
                        context.getResources().getString(R.string.preset_screen_set),
                        0,
                        Color.parseColor("#016990"),
                        pos -> {
                            SettingsPresetCommunication settingsPresetCommunication = (SettingsPresetCommunication) SettingPresenter.settingPresenter;
                            settingsPresetCommunication.sendPresetObjectToSettings(sternProducts.get(pos));
                            getmView().hideFragment();


                        }
                ));

            }
        };


    }

    @Override
    public void presetsSearchEngine(SearchView searchView) {

        presetsSearchView = searchView;

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                ArrayList<PresetSternProduct> presetSternProducts = (ArrayList<PresetSternProduct>) SternRoomDatabase.getDatabase(context).presetSternProductDao().getAll();
                ArrayList<PresetSternProduct> presetSternProductsNew = new ArrayList<>();
                for (PresetSternProduct sternProduct : presetSternProducts)

                {
                    if (sternProduct.getPresetName().toLowerCase().contains(newText)) {
                        presetSternProductsNew.add(sternProduct);

                    }


                }
                presetsRecyclerViewAdapter.setSearchData(presetSternProductsNew);


                return true;
            }
        });
    }

    @Override
    public void onDoneClicked() {


    }

    @Override
    public void onCreate() {
        GlobalBus.getBus().register(this);
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }


    @Override
    public void onStop() {

    }

    @Override
    public void searchViewOnclick() {
        if (presetsSearchView.getVisibility() == View.GONE) {
            presetsSearchView.setVisibility(View.VISIBLE);
        } else {
            presetsSearchView.setVisibility(View.GONE);
        }
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.presets_fragment_search_icon_iv1:
                searchViewOnclick();
                break;
            case R.id.presets_fragment_done_tv:
                getmView().hideFragment();
                break;
        }
    }

    public interface SettingsPresetCommunication {

        void sendPresetObjectToSettings(PresetSternProduct presetSternProduct);
    }
}
