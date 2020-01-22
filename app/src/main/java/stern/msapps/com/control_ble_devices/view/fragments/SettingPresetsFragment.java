package stern.msapps.com.control_ble_devices.view.fragments;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import stern.msapps.com.control_ble_devices.model.dataTypes.SternProduct;
import stern.msapps.com.control_ble_devices.presenter.settingPresetPresenter.SettingPresetPresenter;
import stern.msapps.com.control_ble_devices.presenter.settingPresetPresenter.SettingPresetsContract;
import stern.msapps.com.control_ble_devices.*;


/**
 * Created by Rafael on 8/7/19.
 */
public class SettingPresetsFragment extends android.support.v4.app.DialogFragment implements SettingPresetsContract.View {


    // Variables Declaration.
    private Context context;
    private View mainInflatedView;
    private RecyclerView presetsRecyclerView;
    private SearchView searchSearchView;
    private ImageView searchIconImageView;
    private SettingPresetPresenter presetPresenter;
    private TextView presets_fragment_done_tv;
    //TODO.... Remove statics using EventBus
    public static SternProduct sternProduct;


    public static SettingPresetsFragment newInstance(SternProduct sternProduct) {
        SettingPresetsFragment fragment = new SettingPresetsFragment();

        SettingPresetsFragment.sternProduct = sternProduct;

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presetPresenter = new SettingPresetPresenter(context, sternProduct);
        presetPresenter.onCreate();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainInflatedView = inflater.inflate(R.layout.fragment_presets_setting, container, false);

        presetPresenter.attach(this);
        viewInit();
        recyclerViewInit();


        return mainInflatedView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;


    }

    @Override
    public void onDetach() {
        super.onDetach();


    }


    @Override
    public void onResume() {
        super.onResume();
    }

    private void setDialogPosition() {


        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);

            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        }
    }


    private int dpToPx(int dp) {
        DisplayMetrics metrics = getActivity().getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
    }


    @Override
    public void onStart() {
        super.onStart();


        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.dimAmount = 0.9f;
        setDialogPosition();
        presetPresenter.onStart();
    }

    @Nullable
    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public void viewInit() {

        searchSearchView = mainInflatedView.findViewById(R.id.presets_fragment_searchView);
        searchIconImageView = mainInflatedView.findViewById(R.id.presets_fragment_search_icon_iv1);
        presetsRecyclerView = mainInflatedView.findViewById(R.id.presets_fragment_RecyclerView1);
        presets_fragment_done_tv = mainInflatedView.findViewById(R.id.presets_fragment_done_tv);
        presets_fragment_done_tv.setOnClickListener(presetPresenter);
        searchIconImageView.setOnClickListener(presetPresenter);
        presetPresenter.presetsSearchEngine(searchSearchView);
    }

    @Override
    public void recyclerViewInit() {
        presetPresenter.presetsRecyclerViewCreation(presetsRecyclerView);


    }

    @Override
    public void hideFragment() {
        this.dismiss();
    }

    @Override
    public Context getActivityContext() {
        return context;
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
