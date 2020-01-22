package stern.msapps.com.control_ble_devices.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import stern.msapps.com.control_ble_devices.presenter.settingPresenter.SettingPresenter;
import stern.msapps.com.control_ble_devices.presenter.settingPresenter.SettingsContract;
import stern.msapps.com.control_ble_devices.R;


/**
 * Created by Rafael on 10/7/19.
 */
public class SettingsFragment extends android.support.v4.app.Fragment implements SettingsContract.View {


    // Variables Declaration ButterKnife
    @BindView(R.id.settings_fragment_title_TV)
    TextView titleTV;
    @BindView(R.id.settings_fragment_apply_BTN)
    TextView apply_BTN;
    @BindView(R.id.settings_fragment_buttons_RecyclerView)
    RecyclerView buttons_RecyclerView;
    @BindView(R.id.settings_fragment_seek_bar_RecyclerView)
    RecyclerView seek_bar_RecyclerView;
    @BindView(R.id.settings_fragment_triple_buttons)
    View settings_fragment_triple_buttons;
    @BindView(R.id.settings_fragment_dosage_buttons)
    RecyclerView dosage_RecyclerView;
    @BindView(R.id.settings_fragment_main_scrollView)
    ScrollView mainScrollView;


    private SettingPresenter settingPresenter;
    private View mainView;
    private Context context;
    private TextView saveTXT, loadTXT, newTXT;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mainView = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, mainView);
        setPresenter();
        viewInit();
        onEventListeners();

        return mainView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        settingPresenter.onViewCreated();
    }

    @Override
    public void viewInit() {
        saveTXT = ButterKnife.findById(settings_fragment_triple_buttons, R.id.single_item_settings_recycler_triple_button_save_preset_BTN);
        loadTXT = ButterKnife.findById(settings_fragment_triple_buttons, R.id.single_item_settings_recycler_triple_button_load_preset_BTN);
        newTXT = ButterKnife.findById(settings_fragment_triple_buttons, R.id.single_item_settings_recycler_triple_button_new_preset_BTN);


    }

    @Override
    public void setPresenter() {
        settingPresenter = new SettingPresenter(context);
        settingPresenter.attach(this);
        settingPresenter.settButtonsRecyclerView(buttons_RecyclerView);
        settingPresenter.setSeekBarsRecyclerView(seek_bar_RecyclerView);
       // settingPresenter.setDosageRecyclerView(dosage_RecyclerView);

    }

    @Override
    public void setTitle(String title) {
        titleTV.setText(title);
    }

    @Override
    public void onEventListeners() {
        saveTXT.setOnClickListener(settingPresenter);
        loadTXT.setOnClickListener(settingPresenter);
        newTXT.setOnClickListener(settingPresenter);
        apply_BTN.setOnClickListener(settingPresenter);

    }

    @Override
    public void setEnableDisableNewPresetButton() {


        loadTXT.setEnabled(!loadTXT.isEnabled());
        if (loadTXT.isEnabled()) {
            loadTXT.setAlpha(1.0f);
        } else {
            loadTXT.setAlpha(0.3f);
        }


        apply_BTN.setEnabled(!apply_BTN.isEnabled());
        if (apply_BTN.isEnabled()) {
            apply_BTN.setAlpha(1.0f);
        } else {
            apply_BTN.setAlpha(0.3f);
        }

        saveTXT.setEnabled(!saveTXT.isEnabled());
        if(saveTXT.isEnabled()){
            saveTXT.setAlpha(1.0f);
        }else {
            saveTXT.setAlpha(0.3f);
        }



    }

    @Override
    public void setEnableDisableSavePresetButton() {
        loadTXT.setEnabled(true);
        loadTXT.setAlpha(1.0f);

        apply_BTN.setEnabled(true);
        apply_BTN.setAlpha(1.0f);

    }

    @Override
    public ScrollView getMainScrollView() {
        return mainScrollView;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }


    @Override
    public void onDetach() {
        settingPresenter.detach();
        super.onDetach();

    }

    @Override
    public void onStart() {
        settingPresenter.onStart();
        super.onStart();

    }

    @Override
    public void onPause() {
        settingPresenter.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        settingPresenter.onDestroy();
        super.onDestroy();
    }


    @Override
    public Context getActivityContext() {
        return context;
    }

    @Nullable
    @Override
    public View getView() {
        return mainView;
    }
}
