package stern.msapps.com.control_ble_devices.view.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import stern.msapps.com.control_ble_devices.presenter.operateScreenPresenter.OperateContract;
import stern.msapps.com.control_ble_devices.presenter.operateScreenPresenter.OperatePresenter;
import stern.msapps.com.control_ble_devices.R;
import stern.msapps.com.control_ble_devices.model.dataTypes.User;


/**
 * Created by Rafael on 7/7/19.
 */

public class OperateFragment extends android.support.v4.app.Fragment implements OperateContract.View, SeekBar.OnSeekBarChangeListener {

    // Variables Declaration ButterKnife library .
    @Nullable
    @BindView(R.id.operate_fragment_title_TV)
    TextView title;
    @Nullable
    @BindView(R.id.operate_fragment_open_valve_TV_value)
    TextView operate_fragment_open_valve_TV_value;
    @Nullable
    @BindView(R.id.operate_fragment_valve_seek_bar)
    SeekBar operate_fragment_valve_seek_bar;
    @Nullable
    @BindView(R.id.operate_fragment_open_valve_BTN)
    TextView operate_fragment_open_valve_BTN;
    @Nullable
    @BindView(R.id.title_upper_turn_on)
    TextView operate_fragment_open_time_valve;
    @Nullable
    @BindView(R.id.scheduled_open_close_constraint_L)
    ConstraintLayout constraintLayout;
    @Nullable
    @BindView(R.id.operate_fragment_scheduled_hygiene_flush)
    View operate_fragment_scheduled_hygiene_flush;
    @Nullable
    @BindView(R.id.operate_fragment_scheduled_standby_flush)
    View operate_fragment_scheduled_standby_flush;
    @BindView(R.id.operate_fragment_show_statistics_button_tv)
    TextView operate_fragment_show_statictics_button_tv;
    @Nullable
    @BindView(R.id.operate_fragment_triple_buttons)
    View operate_fragment_triple_buttons;

    @Nullable
    @BindView(R.id.operate_fragment_main)
    ConstraintLayout operate_fragment_main;


    private final String HYGIENE_TAG = "hygiene_tag";
    private final String STANDBY_TAG = "standby_tag";
    private Context context;
    private View mainView;
    private OperatePresenter operatePresenter;
    private RecyclerView hygiene_days_of_week_recyclerView, hygiene_scheduled_date_recyclerView, standby_days_of_week_recyclerView, standby_scheduled_date_recyclerView;
    private SeekBar hygiene_operate_fragment_upper_seek_bar, hygiene_scheduled_last_activation_seek_bar, hygiene_duration_seek_bar, standby_operate_fragment_upper_seek_bar,
            standby_scheduled_last_activation_seek_bar, standby_duration_seek_bar, hygiene_operate_fragment_from_lastActivation_duration_seekBar,
            standby_operate_fragment_from_lastActivation_duration_seekBar, cleaner_hygiene_seek_bar, cleaner_seek_standBy_bar;

    private TextView hygiene_title_upper_turn_on, hygiene_scheduled_turn_on_now_BTN, hygiene_operate_fragment_upper_duration_title_TV, hygiene_operate_fragment_upper_duration_value_TV,
            hygiene_scheduled_TV, hygiene_scheduled_from_last_activation_value_TV, hygiene_choose_calendar_daty_BTN, hygiene_date_duration_value_TV, standby_title_upper_turn_on,
            standby_scheduled_turn_on_now_BTN, standby_operate_fragment_upper_duration_title_TV, standbyhygiene_operate_fragment_upper_duration_value_TV, standby_scheduled_TV,
            standby_scheduled_from_last_activation_value_TV, standby_choose_calendar_daty_BTN, standby_date_duration_value_TV, hygiene_date_duration_TV, standBy_date_duration_TV,
            hygiene_operate_scheduled_duration_units, hygiene_operate_fragment_duration_units, standby_operate_scheduled_duration_units, standby_operate_fragment_duration_units,
            standby_operate_fragment_scheduled_BTN, hygiene_operate_fragment_scheduled_BTN, hygiene_operate_fragment_from_lastActivation_duration_TV,
            standby_operate_fragment_from_lastActivation_duration_TV, hygiene_operate_fragment_from_lastActivation_duration_VALUE,
            standby_operate_fragment_from_lastActivation_duration_VALUE, standby_scheduled_from_last_activation_TV, standby_scheduled_hours, standby_operate_fragment_from_lastActivation_duration_units,
            standby_operate_fragment_set_time_BTN, hygiene_operate_fragment_set_time_BTN, saveTXT, loadTXT, newTXT, standby_scheduled_from_last_activation_title, cleaner_scheduled_turn_on_now_BTN,
            cleaner_scheduled_hygiene_turn_on_now_BTN, cleaner_title_upper_hygiene_turn_on, cleaner_duration_hygiene_value_TV, cleaner_scheduled_standBy_turn_on_now_BTN, cleaner_duration_standBy_value_TV, cleaner_title,
            cleaner_duration_standBy_units, cleaner_operate_fragment_from_lastActivation_duration_TV;


    private CheckBox hygiene_operate_fragment_activate_radio_BTN, stanBy_operate_fragment_activate_radio_BTN;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDetach() {
        operatePresenter.detach();
        super.onDetach();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

          // UI initialization according to user type.

        if (User.getUserInstance().getUserType() == User.UserType.TECHNICIAN) { // User type technician .
            mainView = inflater.inflate(R.layout.fragment_technician_operate, container, false);
            ButterKnife.bind(this, mainView);
            technicianViewInit();
            hygieneViewInit();
            standByViewInit();
            setHygieneText();
            setStandByText();
            hideStandByLastActivation();
            setPresenter();
            setAdapter();
            technicianViewsSetListeners();
            setDefaultValueForViews();


        } else { // User type cleaner .
            mainView = inflater.inflate(R.layout.cleaner_operate_screen, container, false);

            setPresenter();
            cleanerViewInit();
            cleanerViewsSetListeners();

        }


        return mainView;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        operatePresenter.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        operatePresenter.onResume();
    }


    @Override
    public void technicianViewsSetListeners() {


        // Set event listeners.
        operate_fragment_valve_seek_bar.setOnSeekBarChangeListener(this);
        hygiene_operate_fragment_upper_seek_bar.setOnSeekBarChangeListener(this);
        standby_operate_fragment_upper_seek_bar.setOnSeekBarChangeListener(this);

        hygiene_scheduled_last_activation_seek_bar.setOnSeekBarChangeListener(this);
        standby_scheduled_last_activation_seek_bar.setOnSeekBarChangeListener(this);

        hygiene_scheduled_last_activation_seek_bar.setOnSeekBarChangeListener(this);
        standby_scheduled_last_activation_seek_bar.setOnSeekBarChangeListener(this);

        hygiene_duration_seek_bar.setOnSeekBarChangeListener(this);
        standby_duration_seek_bar.setOnSeekBarChangeListener(this);

        hygiene_operate_fragment_from_lastActivation_duration_seekBar.setOnSeekBarChangeListener(this);
        standby_operate_fragment_from_lastActivation_duration_seekBar.setOnSeekBarChangeListener(this);

        hygiene_duration_seek_bar.setOnClickListener(operatePresenter);


        hygiene_choose_calendar_daty_BTN.setOnClickListener(operatePresenter);
        standby_choose_calendar_daty_BTN.setOnClickListener(operatePresenter);


        operate_fragment_open_valve_BTN.setOnClickListener(operatePresenter);
        hygiene_scheduled_turn_on_now_BTN.setOnClickListener(operatePresenter);
        standby_scheduled_turn_on_now_BTN.setOnClickListener(operatePresenter);

        hygiene_operate_fragment_activate_radio_BTN.setOnClickListener(operatePresenter);
        stanBy_operate_fragment_activate_radio_BTN.setOnClickListener(operatePresenter);

        hygiene_operate_fragment_scheduled_BTN.setOnClickListener(operatePresenter);
        standby_operate_fragment_scheduled_BTN.setOnClickListener(operatePresenter);

        hygiene_operate_fragment_set_time_BTN.setOnClickListener(operatePresenter);
        standby_operate_fragment_set_time_BTN.setOnClickListener(operatePresenter);

        newTXT.setOnClickListener(operatePresenter);
        loadTXT.setOnClickListener(operatePresenter);
        saveTXT.setOnClickListener(operatePresenter);


    }

    @Override
    public void cleanerViewsSetListeners() {

        // Set event listeners
        operate_fragment_show_statictics_button_tv.setOnClickListener(operatePresenter);
        cleaner_scheduled_hygiene_turn_on_now_BTN.setOnClickListener(operatePresenter);
        cleaner_scheduled_standBy_turn_on_now_BTN.setOnClickListener(operatePresenter);

        cleaner_hygiene_seek_bar.setOnSeekBarChangeListener(this);
        cleaner_seek_standBy_bar.setOnSeekBarChangeListener(this);
    }


    private void hygieneViewInit() {

        // Hygiene plush event views initialization.
        hygiene_title_upper_turn_on = ButterKnife.findById(operate_fragment_scheduled_hygiene_flush, R.id.title_upper_turn_on);
        hygiene_title_upper_turn_on.setTag(operate_fragment_scheduled_hygiene_flush);

        hygiene_scheduled_turn_on_now_BTN = ButterKnife.findById(operate_fragment_scheduled_hygiene_flush, R.id.scheduled_turn_on_now_BTN);
        hygiene_scheduled_turn_on_now_BTN.setTag(operate_fragment_scheduled_hygiene_flush);

        hygiene_operate_fragment_upper_duration_title_TV = ButterKnife.findById(operate_fragment_scheduled_hygiene_flush, R.id.operate_fragment_upper_duration_title_TV);
        hygiene_operate_fragment_upper_duration_title_TV.setTag(operate_fragment_scheduled_hygiene_flush);

        hygiene_operate_fragment_upper_duration_value_TV = ButterKnife.findById(operate_fragment_scheduled_hygiene_flush, R.id.duration_value_TV);
        hygiene_operate_fragment_upper_duration_value_TV.setTag(operate_fragment_scheduled_hygiene_flush);

        hygiene_operate_fragment_upper_seek_bar = ButterKnife.findById(operate_fragment_scheduled_hygiene_flush, R.id.seek_bar);
        hygiene_operate_fragment_upper_seek_bar.setTag(operate_fragment_scheduled_hygiene_flush);
        hygiene_operate_fragment_upper_seek_bar.setMax(255);
        hygiene_operate_fragment_upper_seek_bar.setProgress(30);


        hygiene_scheduled_TV = ButterKnife.findById(operate_fragment_scheduled_hygiene_flush, R.id.scheduled_TV);
        hygiene_scheduled_TV.setTag(operate_fragment_scheduled_hygiene_flush);

        hygiene_scheduled_from_last_activation_value_TV = ButterKnife.findById(operate_fragment_scheduled_hygiene_flush, R.id.scheduled_from_last_activation_value_TV);
        hygiene_scheduled_from_last_activation_value_TV.setTag(operate_fragment_scheduled_hygiene_flush);

        hygiene_scheduled_last_activation_seek_bar = ButterKnife.findById(operate_fragment_scheduled_hygiene_flush, R.id.scheduled_last_activation_seek_bar);
        hygiene_scheduled_last_activation_seek_bar.setTag(operate_fragment_scheduled_hygiene_flush);
        hygiene_scheduled_last_activation_seek_bar.setMax(255);
        hygiene_scheduled_last_activation_seek_bar.setProgress(3);


        hygiene_days_of_week_recyclerView = ButterKnife.findById(operate_fragment_scheduled_hygiene_flush, R.id.days_of_week_recyclerView);
        hygiene_days_of_week_recyclerView.setTag(operate_fragment_scheduled_hygiene_flush);

        hygiene_choose_calendar_daty_BTN = ButterKnife.findById(operate_fragment_scheduled_hygiene_flush, R.id.choose_calendar_day_BTN);
        hygiene_choose_calendar_daty_BTN.setTag(operate_fragment_scheduled_hygiene_flush);

        hygiene_scheduled_date_recyclerView = ButterKnife.findById(operate_fragment_scheduled_hygiene_flush, R.id.scheduled_date_recyclerView);
        hygiene_scheduled_date_recyclerView.setTag(operate_fragment_scheduled_hygiene_flush);

        hygiene_date_duration_TV = ButterKnife.findById(operate_fragment_scheduled_hygiene_flush, R.id.date_duration_TV);
        hygiene_date_duration_TV.setTag(operate_fragment_scheduled_hygiene_flush);

        hygiene_date_duration_value_TV = ButterKnife.findById(operate_fragment_scheduled_hygiene_flush, R.id.date_duration_value_TV);
        hygiene_date_duration_value_TV.setTag(operate_fragment_scheduled_hygiene_flush);

        hygiene_duration_seek_bar = ButterKnife.findById(operate_fragment_scheduled_hygiene_flush, R.id.days_duration_seek_bar);
        hygiene_duration_seek_bar.setTag(operate_fragment_scheduled_hygiene_flush);

        hygiene_operate_fragment_activate_radio_BTN = (CheckBox) ButterKnife.findById(operate_fragment_scheduled_hygiene_flush, R.id.operate_fragment_activate_radio_BTN);
        hygiene_operate_fragment_activate_radio_BTN.setTag(operate_fragment_scheduled_hygiene_flush);

        hygiene_operate_scheduled_duration_units = (TextView) ButterKnife.findById(operate_fragment_scheduled_hygiene_flush, R.id.operate_scheduled_duration_units);
        hygiene_operate_scheduled_duration_units.setTag(operate_fragment_scheduled_hygiene_flush);

        hygiene_operate_fragment_duration_units = (TextView) ButterKnife.findById(operate_fragment_scheduled_hygiene_flush, R.id.duration_units);
        hygiene_operate_fragment_duration_units.setTag(operate_fragment_scheduled_hygiene_flush);

        hygiene_operate_fragment_scheduled_BTN = (TextView) ButterKnife.findById(operate_fragment_scheduled_hygiene_flush, R.id.operate_fragment_scheduled_BTN);
        hygiene_operate_fragment_scheduled_BTN.setTag(operate_fragment_scheduled_hygiene_flush);

        hygiene_operate_fragment_from_lastActivation_duration_TV = (TextView) ButterKnife.findById(operate_fragment_scheduled_hygiene_flush, R.id.operate_fragment_from_lastActivation_duration_TV);
        hygiene_operate_fragment_from_lastActivation_duration_TV.setTag(operate_fragment_scheduled_hygiene_flush);

        hygiene_operate_fragment_from_lastActivation_duration_VALUE = (TextView) ButterKnife.findById(operate_fragment_scheduled_hygiene_flush, R.id.operate_fragment_from_lastActivation_duration_VALUE);
        hygiene_operate_fragment_from_lastActivation_duration_VALUE.setTag(operate_fragment_scheduled_hygiene_flush);

        hygiene_operate_fragment_from_lastActivation_duration_seekBar = (SeekBar) ButterKnife.findById(operate_fragment_scheduled_hygiene_flush, R.id.operate_fragment_from_lastActivation_duration_seekBar);
        hygiene_operate_fragment_from_lastActivation_duration_seekBar.setTag(operate_fragment_scheduled_hygiene_flush);
        hygiene_operate_fragment_from_lastActivation_duration_seekBar.setMax(255);

        hygiene_operate_fragment_set_time_BTN = ButterKnife.findById(operate_fragment_scheduled_hygiene_flush, R.id.operate_fragment_set_time_BTN);
        hygiene_operate_fragment_set_time_BTN.setTag(operate_fragment_scheduled_hygiene_flush);
    }

    private void standByViewInit() {

        // StandBy event views initialization .
        standby_title_upper_turn_on = ButterKnife.findById(operate_fragment_scheduled_standby_flush, R.id.title_upper_turn_on);
        standby_title_upper_turn_on.setTag(operate_fragment_scheduled_standby_flush);

        standby_scheduled_turn_on_now_BTN = ButterKnife.findById(operate_fragment_scheduled_standby_flush, R.id.scheduled_turn_on_now_BTN);
        standby_scheduled_turn_on_now_BTN.setTag(operate_fragment_scheduled_standby_flush);

        standby_operate_fragment_upper_duration_title_TV = ButterKnife.findById(operate_fragment_scheduled_standby_flush, R.id.operate_fragment_upper_duration_title_TV);
        standby_operate_fragment_upper_duration_title_TV.setTag(operate_fragment_scheduled_standby_flush);

        standbyhygiene_operate_fragment_upper_duration_value_TV = ButterKnife.findById(operate_fragment_scheduled_standby_flush, R.id.duration_value_TV);
        standbyhygiene_operate_fragment_upper_duration_value_TV.setTag(operate_fragment_scheduled_standby_flush);

        standby_operate_fragment_upper_seek_bar = ButterKnife.findById(operate_fragment_scheduled_standby_flush, R.id.seek_bar);
        standby_operate_fragment_upper_seek_bar.setTag(operate_fragment_scheduled_standby_flush);
        standby_operate_fragment_upper_seek_bar.setMax(255);

        standby_scheduled_TV = ButterKnife.findById(operate_fragment_scheduled_standby_flush, R.id.scheduled_TV);
        standby_scheduled_TV.setTag(operate_fragment_scheduled_standby_flush);

        standby_scheduled_from_last_activation_value_TV = ButterKnife.findById(operate_fragment_scheduled_standby_flush, R.id.scheduled_from_last_activation_value_TV);
        standby_scheduled_from_last_activation_value_TV.setTag(operate_fragment_scheduled_standby_flush);

        standby_scheduled_last_activation_seek_bar = ButterKnife.findById(operate_fragment_scheduled_standby_flush, R.id.scheduled_last_activation_seek_bar);
        standby_scheduled_last_activation_seek_bar.setTag(operate_fragment_scheduled_standby_flush);
        standby_scheduled_last_activation_seek_bar.setMax(12);

        standby_days_of_week_recyclerView = ButterKnife.findById(operate_fragment_scheduled_standby_flush, R.id.days_of_week_recyclerView);
        standby_days_of_week_recyclerView.setTag(operate_fragment_scheduled_standby_flush);

        standBy_date_duration_TV = ButterKnife.findById(operate_fragment_scheduled_standby_flush, R.id.date_duration_TV);
        standBy_date_duration_TV.setTag(operate_fragment_scheduled_standby_flush);

        standby_choose_calendar_daty_BTN = ButterKnife.findById(operate_fragment_scheduled_standby_flush, R.id.choose_calendar_day_BTN);
        standby_choose_calendar_daty_BTN.setTag(operate_fragment_scheduled_standby_flush);

        standby_scheduled_date_recyclerView = ButterKnife.findById(operate_fragment_scheduled_standby_flush, R.id.scheduled_date_recyclerView);
        standby_scheduled_date_recyclerView.setTag(operate_fragment_scheduled_standby_flush);

        standby_date_duration_value_TV = ButterKnife.findById(operate_fragment_scheduled_standby_flush, R.id.date_duration_value_TV);
        standby_date_duration_value_TV.setTag(operate_fragment_scheduled_standby_flush);

        standby_duration_seek_bar = ButterKnife.findById(operate_fragment_scheduled_standby_flush, R.id.days_duration_seek_bar);
        standby_duration_seek_bar.setTag(operate_fragment_scheduled_standby_flush);
        standby_duration_seek_bar.setMax(255);


        stanBy_operate_fragment_activate_radio_BTN = (CheckBox) ButterKnife.findById(operate_fragment_scheduled_standby_flush, R.id.operate_fragment_activate_radio_BTN);
        stanBy_operate_fragment_activate_radio_BTN.setTag(operate_fragment_scheduled_standby_flush);

        standby_operate_scheduled_duration_units = (TextView) ButterKnife.findById(operate_fragment_scheduled_standby_flush, R.id.operate_scheduled_duration_units);
        standby_operate_scheduled_duration_units.setTag(operate_fragment_scheduled_standby_flush);

        standby_operate_fragment_duration_units = (TextView) ButterKnife.findById(operate_fragment_scheduled_standby_flush, R.id.duration_units);
        standby_operate_fragment_duration_units.setTag(operate_fragment_scheduled_standby_flush);

        standby_operate_fragment_scheduled_BTN = (TextView) ButterKnife.findById(operate_fragment_scheduled_standby_flush, R.id.operate_fragment_scheduled_BTN);
        standby_operate_fragment_scheduled_BTN.setTag(operate_fragment_scheduled_standby_flush);

        standby_operate_fragment_from_lastActivation_duration_TV = (TextView) ButterKnife.findById(operate_fragment_scheduled_standby_flush, R.id.operate_fragment_from_lastActivation_duration_TV);
        standby_operate_fragment_from_lastActivation_duration_TV.setTag(operate_fragment_scheduled_standby_flush);

        standby_operate_fragment_from_lastActivation_duration_VALUE = (TextView) ButterKnife.findById(operate_fragment_scheduled_standby_flush, R.id.operate_fragment_from_lastActivation_duration_VALUE);
        standby_operate_fragment_from_lastActivation_duration_VALUE.setTag(operate_fragment_scheduled_standby_flush);

        standby_operate_fragment_from_lastActivation_duration_seekBar = (SeekBar) ButterKnife.findById(operate_fragment_scheduled_standby_flush, R.id.operate_fragment_from_lastActivation_duration_seekBar);
        standby_operate_fragment_from_lastActivation_duration_seekBar.setTag(operate_fragment_scheduled_standby_flush);


        standby_scheduled_from_last_activation_TV = ButterKnife.findById(operate_fragment_scheduled_standby_flush, R.id.scheduled_from_last_activation_TV);
        standby_scheduled_from_last_activation_TV.setTag(operate_fragment_scheduled_standby_flush);

        standby_scheduled_hours = ButterKnife.findById(operate_fragment_scheduled_standby_flush, R.id.scheduled_hours);
        standby_scheduled_hours.setTag(operate_fragment_scheduled_standby_flush);

        standby_operate_fragment_from_lastActivation_duration_units = ButterKnife.findById(operate_fragment_scheduled_standby_flush, R.id.operate_fragment_from_lastActivation_duration_units);
        standby_operate_fragment_from_lastActivation_duration_units.setTag(operate_fragment_scheduled_standby_flush);

        standby_operate_fragment_set_time_BTN = ButterKnife.findById(operate_fragment_scheduled_standby_flush, R.id.operate_fragment_set_time_BTN);
        standby_operate_fragment_set_time_BTN.setTag(operate_fragment_scheduled_standby_flush);

        standby_scheduled_from_last_activation_title = ButterKnife.findById(operate_fragment_scheduled_standby_flush, R.id.scheduled_from_last_activation_title);
        standby_scheduled_from_last_activation_title.setVisibility(View.GONE);

    }


    private void setHygieneText() {
        hygiene_title_upper_turn_on.setText(R.string.operate_screen_open_valve_time);
        hygiene_operate_fragment_upper_duration_title_TV.setText(R.string.operate_flush_duration);
        hygiene_date_duration_TV.setText(R.string.operate_screen_flush_duration);
        hygiene_operate_fragment_upper_duration_title_TV.setText(R.string.operate_screen_duration);
        hygiene_operate_fragment_set_time_BTN.setText(R.string.operate_screen_scheduled_hygiene_flush);


    }

    private void setStandByText() {
        standby_title_upper_turn_on.setText(R.string.operate_standby_mode);
        standby_operate_fragment_upper_duration_title_TV.setText(R.string.operate_hygiene_duration);
        standby_scheduled_TV.setText(R.string.operate_standby_scheduled);
        standby_operate_scheduled_duration_units.setText(R.string.operate_minutes);
        standby_operate_fragment_duration_units.setText(R.string.operate_minutes);
        standBy_date_duration_TV.setText(R.string.operate_duration);
    }


    @Override
    public void technicianViewInit() {

         // User type - Technician views  initialization.
        saveTXT = ButterKnife.findById(operate_fragment_triple_buttons, R.id.single_item_settings_recycler_triple_button_save_preset_BTN);
        loadTXT = ButterKnife.findById(operate_fragment_triple_buttons, R.id.single_item_settings_recycler_triple_button_load_preset_BTN);
        newTXT = ButterKnife.findById(operate_fragment_triple_buttons, R.id.single_item_settings_recycler_triple_button_new_preset_BTN);
    }

    @Override
    public void cleanerViewInit() {


        // User type - Cleaner views initialization.
        operate_fragment_show_statictics_button_tv = mainView.findViewById(R.id.operate_fragment_show_statistics_button_tv);
        cleaner_scheduled_hygiene_turn_on_now_BTN = mainView.findViewById(R.id.scheduled_hygiene_turn_on_now_BTN);
        cleaner_scheduled_hygiene_turn_on_now_BTN.setTag(HYGIENE_TAG);

        cleaner_hygiene_seek_bar = mainView.findViewById(R.id.hygiene_seek_bar);
        cleaner_hygiene_seek_bar.setTag(HYGIENE_TAG);

        cleaner_duration_hygiene_value_TV = mainView.findViewById(R.id.duration_hygiene_value_TV);


        cleaner_scheduled_standBy_turn_on_now_BTN = mainView.findViewById(R.id.scheduled_standBy_turn_on_now_BTN);
        cleaner_scheduled_standBy_turn_on_now_BTN.setTag(STANDBY_TAG);

        cleaner_seek_standBy_bar = mainView.findViewById(R.id.seek_standBy_bar);
        cleaner_seek_standBy_bar.setTag(STANDBY_TAG);

        cleaner_duration_standBy_value_TV = mainView.findViewById(R.id.duration_standBy_value_TV);
        cleaner_duration_standBy_value_TV.setTag(STANDBY_TAG);

        cleaner_title = mainView.findViewById(R.id.operate_fragment_title_TV);

        cleaner_duration_standBy_units = mainView.findViewById(R.id.duration_standBy_units);
        cleaner_duration_standBy_units.setText(R.string.operate_screen_hour_tag);

        cleaner_operate_fragment_from_lastActivation_duration_TV = mainView.findViewById(R.id.operate_fragment_from_lastActivation_duration_TV);


    }

    @Override
    public void setEnableDisableNewPresetButton() {
        loadTXT.setEnabled(!loadTXT.isEnabled());
        if (loadTXT.isEnabled()) {
            loadTXT.setAlpha(1.0f);
        } else {
            loadTXT.setAlpha(0.3f);
        }


        saveTXT.setEnabled(!saveTXT.isEnabled());
        if (saveTXT.isEnabled()) {
            saveTXT.setAlpha(1.0f);
        } else {
            saveTXT.setAlpha(0.3f);
        }
    }



    @Override
    public void setTitle(String title) {
        if (User.getUserInstance().getUserType().equals(User.UserType.TECHNICIAN)) {
            this.title.setText(title);
        } else {
            this.cleaner_title.setText(title);
        }


    }

    @Override
    public void setPresenter() {
        operatePresenter = new OperatePresenter(context);
        operatePresenter.attach(this);
    }

    @Override
    public void setAdapter() {

        operatePresenter.setHygieneDaysRecyclerView(hygiene_days_of_week_recyclerView);
        operatePresenter.setStandByDaysRecyclerView(standby_days_of_week_recyclerView);
        operatePresenter.setScheduledHygieneRecyclerView(hygiene_scheduled_date_recyclerView);
        operatePresenter.setScheduledStandByRecyclerView(standby_scheduled_date_recyclerView);
    }

    @Override
    public long getFlashScheduledDuration() {
        return Long.parseLong(hygiene_date_duration_value_TV.getText().toString());
    }

    @Override
    public long getStandByScheduledDuration() {
        return Long.parseLong(standby_date_duration_value_TV.getText().toString());

    }

    @Override
    public long getOpenCloseValveDuration() {
        if (User.getUserInstance().getUserType().equals(User.UserType.TECHNICIAN)) {
            return Long.parseLong(hygiene_operate_fragment_upper_duration_value_TV.getText().toString());
        } else {
            return cleaner_hygiene_seek_bar.getProgress();
        }
    }

    @Override
    public int getHygieneFlushDuration() {

        if (User.getUserInstance().getUserType().equals(User.UserType.TECHNICIAN)) {
            return Integer.parseInt(hygiene_operate_fragment_upper_duration_value_TV.getText().toString());
        } else {
            return cleaner_hygiene_seek_bar.getProgress();
        }


    }

    @Override
    public int getStandByoPenCloseDuration() {
        if (User.getUserInstance().getUserType().equals(User.UserType.TECHNICIAN)) {
            return standby_operate_fragment_upper_seek_bar.getProgress();
        } else {
            return cleaner_seek_standBy_bar.getProgress();
        }
    }

    @Override
    public TextView getHygieneFlushFromLastActivationTimeTV() {
        return hygiene_scheduled_from_last_activation_value_TV;
    }

    @Override
    public TextView getHygieneFlushFromLastActivationDurationTV() {
        return hygiene_operate_fragment_from_lastActivation_duration_VALUE;
    }

    @Override
    public TextView getStandByFromLastActivationDurationTV() {
        return standby_scheduled_from_last_activation_value_TV;
    }

    @Override
    public void setHygieneFlushFromLastActivationTimeTV(String val) {
        hygiene_scheduled_from_last_activation_value_TV.setText(val);
    }

    @Override
    public void setHygieneFlushFromLastActivationDurationTV(String val) {
        hygiene_operate_fragment_from_lastActivation_duration_VALUE.setText(val);
    }

    @Override
    public void setStandByFromLastActivationDurationTV(String val) {
        standby_operate_fragment_from_lastActivation_duration_VALUE.setText(val);
    }

    @Override
    public void setHygieneFlushFromLastActivationSeekBarValue(int val) {
        hygiene_scheduled_last_activation_seek_bar.setProgress(val);
    }

    @Override
    public void setHygieneFlushFromLastActivationDurationSeekBarValue(int val) {
        hygiene_operate_fragment_from_lastActivation_duration_seekBar.setProgress(val);
    }

    @Override
    public void setStandByFromLastActivationDurationSeekBarValue(int val) {
        standby_operate_fragment_from_lastActivation_duration_seekBar.setProgress(val);
    }

    @Override
    public void setStanByFromLastActivationDurationTV(String val) {
        standby_scheduled_from_last_activation_value_TV.setText(val);
    }

    @Override
    public void setStandByFromLastActivationSeekBarValue(int val) {
        standby_scheduled_last_activation_seek_bar.setProgress(val);
    }

    @Override
    public void setHygieneFlushFromLastActivationRadioButton(boolean set) {
        hygiene_operate_fragment_activate_radio_BTN.setChecked(set);
    }

    @Override
    public void setStandByromLastActivationRadioButton(boolean set) {
        stanBy_operate_fragment_activate_radio_BTN.setChecked(set);
    }

    @Override
    public void hideStandByLastActivation() {
        standby_scheduled_from_last_activation_TV.setVisibility(View.GONE);
        standby_scheduled_from_last_activation_value_TV.setVisibility(View.GONE);
        standby_scheduled_hours.setVisibility(View.GONE);
        standby_operate_fragment_from_lastActivation_duration_TV.setVisibility(View.GONE);
        standby_operate_fragment_from_lastActivation_duration_VALUE.setVisibility(View.GONE);
        standby_operate_fragment_from_lastActivation_duration_units.setVisibility(View.GONE);
        standby_operate_fragment_from_lastActivation_duration_seekBar.setVisibility(View.GONE);
        standby_scheduled_last_activation_seek_bar.setVisibility(View.GONE);
        stanBy_operate_fragment_activate_radio_BTN.setVisibility(View.GONE);
    }


    @Override
    public TextView getOpenCloseValveButton() {
        if (User.getUserInstance().getUserType().equals(User.UserType.TECHNICIAN)) {
            return hygiene_scheduled_turn_on_now_BTN;
        } else {
            return cleaner_scheduled_hygiene_turn_on_now_BTN;
        }
    }



    @Override
    public android.view.View getHygieneView() {
        return operate_fragment_scheduled_hygiene_flush;
    }

    @Override
    public String getHygieneViewTag() {
        return HYGIENE_TAG;
    }



    @Override
    public void setDefaultValueForViews() {

        hygiene_operate_fragment_upper_seek_bar.setProgress(30);
        hygiene_scheduled_last_activation_seek_bar.setProgress(3);
        hygiene_scheduled_from_last_activation_value_TV.setText("3");
        hygiene_operate_fragment_upper_duration_value_TV.setText("30");
        hygiene_scheduled_from_last_activation_value_TV.setText("3");
        hygiene_operate_fragment_from_lastActivation_duration_seekBar.setProgress(30);
        standby_duration_seek_bar.setProgress(3);
        //standby_duration_seek_bar.setMin(3);
        standby_date_duration_value_TV.setText("3");
        standby_scheduled_last_activation_seek_bar.setProgress(3);
        standby_scheduled_from_last_activation_value_TV.setText("3");
        hygiene_duration_seek_bar.setProgress(30);
        hygiene_date_duration_value_TV.setText("30");
        standby_scheduled_from_last_activation_value_TV.setText("1");
        standby_operate_fragment_upper_seek_bar.setProgress(1);
        standby_scheduled_last_activation_seek_bar.setProgress(3);
        operate_fragment_show_statictics_button_tv.setOnClickListener(operatePresenter);

    }

    @Override
    public void changeOpenValveTextViewTextByObjectType(int str) {
        if (User.getUserInstance().getUserType().equals(User.UserType.TECHNICIAN)) {
            operate_fragment_open_valve_TV_value.setText(str);
        } else {

        }
    }

    @Override
    public int setMinimumValueToSeekBar(SeekBar seekBar, int minimumValue, int currentValue) {
        if (currentValue < minimumValue) {
            seekBar.setProgress(minimumValue);
            return minimumValue;
        }

        return currentValue;
    }


    @Nullable
    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public Context getActivityContext() {
        return context;
    }

    @Override
    public void onPause() {
        operatePresenter.onPause();
        super.onPause();

    }

    @Override
    public void onDestroy() {
        operatePresenter.onDestroy();
        super.onDestroy();

    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.operate_fragment_valve_seek_bar:
                operate_fragment_open_valve_TV_value.setText(String.valueOf(progress));
                break;

            case R.id.seek_bar:
                if (seekBar.getTag() == operate_fragment_scheduled_hygiene_flush) {
                    hygiene_operate_fragment_upper_duration_value_TV.setText(String.valueOf(progress));
                } else {
                    standbyhygiene_operate_fragment_upper_duration_value_TV.setText(String.valueOf(setMinimumValueToSeekBar(standby_operate_fragment_upper_seek_bar, 1, progress)));

                }
                break;

            case R.id.scheduled_last_activation_seek_bar:
                if (seekBar.getTag() == operate_fragment_scheduled_hygiene_flush) {
                    hygiene_scheduled_from_last_activation_value_TV.setText(String.valueOf(setMinimumValueToSeekBar(standby_scheduled_last_activation_seek_bar, 3, progress)));

                } else {
                    standby_scheduled_from_last_activation_value_TV.setText(String.valueOf(progress));
                }
                break;

            case R.id.days_duration_seek_bar:
                if (seekBar.getTag() == operate_fragment_scheduled_hygiene_flush) {
                    hygiene_date_duration_value_TV.setText(String.valueOf(progress));
                } else {
                    standby_date_duration_value_TV.setText(String.valueOf(setMinimumValueToSeekBar(standby_duration_seek_bar, 3, progress)));

                }
                break;
            case R.id.operate_fragment_from_lastActivation_duration_seekBar:
                if (seekBar.getTag() == operate_fragment_scheduled_hygiene_flush) {
                    hygiene_operate_fragment_from_lastActivation_duration_VALUE.setText(String.valueOf(progress));
                } else {
                    standby_operate_fragment_from_lastActivation_duration_VALUE.setText(String.valueOf(progress));
                }
                break;
            case R.id.hygiene_seek_bar:
                cleaner_duration_hygiene_value_TV.setText(String.valueOf(cleaner_hygiene_seek_bar.getProgress()));
                break;

            case R.id.seek_standBy_bar:
                cleaner_duration_standBy_value_TV.setText(String.valueOf(cleaner_seek_standBy_bar.getProgress()));
                break;


        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Nullable
    @Override
    public View getView() {
        return mainView;
    }


    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }


}
