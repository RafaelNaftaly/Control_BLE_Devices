package stern.msapps.com.control_ble_devices.view.adapters;


import android.annotation.SuppressLint;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

import stern.msapps.com.control_ble_devices.model.dataTypes.SternProduct;
import stern.msapps.com.control_ble_devices.model.dataTypes.ranges.Ranges;
import stern.msapps.com.control_ble_devices.model.enums.RangeTypes;
import stern.msapps.com.control_ble_devices.model.enums.SternTypes;
import stern.msapps.com.control_ble_devices.presenter.settingPresenter.SettingPresenter;
import stern.msapps.com.control_ble_devices.R;

public class SettingsSeekBarsAdapter extends RecyclerView.Adapter<SettingsSeekBarsAdapter.ViewHolder> {

    private SettingPresenter presenter;
    private boolean displayCheckBox;
    private ArrayList<Ranges> rangesArrayList;
    private ArrayList<Ranges> modifiedRanges = new ArrayList<Ranges>();
    private ArrayList<Ranges> changetRanges;
    private boolean[] selectedRanges;
    private SternProduct sternProduct;


    public SettingsSeekBarsAdapter(SettingPresenter presenter, ArrayList<Ranges> sternProducts, SternProduct sternProduct) {
        this.presenter = presenter;
        this.rangesArrayList = sternProducts;
        this.sternProduct = sternProduct;


        for (Ranges ranges : rangesArrayList) {
            this.modifiedRanges.add(new Ranges(ranges.getRangeType(), ranges.getCurrentValue(), ranges.getMaximumValue(), ranges.getMinimumValue(), ranges.getDefaultValue()));
        }


        selectedRanges = new boolean[modifiedRanges.size()];

    }

    public void setNewData(ArrayList<Ranges> sternProducts) {
        this.rangesArrayList = sternProducts;
        for (Ranges r : sternProducts) {

            if (this.changetRanges != null) {
                this.changetRanges.add(r);
            }
        }
        notifyDataSetChanged();
    }

    public ArrayList<Ranges> getSelectedRages() {
        ArrayList<Ranges> ranges = new ArrayList<>();

        for (int i = 0; i < modifiedRanges.size(); i++) {

            if (selectedRanges[i]) {
                ranges.add(modifiedRanges.get(i));
            }

        }

        return ranges;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_item_recycler_view_settings_seekbar, parent, false);


        return new ViewHolder(itemView);
    }


    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.setIsRecyclable(false);


        int title = rangesArrayList.get(position).getRangeType().getName();

        //If Stern product is FOAM_SOAP_DISPENSER change SECURITY_TIME name to foam soap discharge
        if (sternProduct.getType() == SternTypes.FOAM_SOAP_DISPENSER && rangesArrayList.get(position).getRangeType() == RangeTypes.SECURITY_TIME) {
            title = R.string.range_type_foam_soap_discharge;
        }

        holder.onBind(rangesArrayList.get(position).getRangeType(),
                title,
                rangesArrayList.get(position).getMinimumValue(),
                rangesArrayList.get(position).getCurrentValue(),
                rangesArrayList.get(position).getMaximumValue(),
                rangesArrayList.get(position).getDefaultValue());

        getModifiedRanges().get(position).setMaximumValue(rangesArrayList.get(position).getMaximumValue());
        getModifiedRanges().get(position).setMinimumValue(rangesArrayList.get(position).getMinimumValue());
        holder.getSeekBar().setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                holder.value_TV.setText(String.valueOf(i));

                getModifiedRanges().get(position).setCurrentValue(i);

                //Setting minimum value
                if (i < rangesArrayList.get(position).getMinimumValue()) {
                    seekBar.setProgress(rangesArrayList.get(position).getMinimumValue());
                }


                setChangetRanges(getModifiedRanges().get(position));

                if (getModifiedRanges().get(position).getRangeType().equals(RangeTypes.AIR_MOTOR) || getModifiedRanges().get(position).getRangeType().equals(RangeTypes.SOAP_MOTOR)) {
                    setChangetRanges(getSoapAirRange(getModifiedRanges().get(position), getModifiedRanges()));
                }



            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        holder.selectRangeChkBox.setOnCheckedChangeListener((compoundButton, b) -> {

            if (b) {
                selectedRanges[position] = true;
            } else {
                selectedRanges[position] = false;
            }
        });


    }


    @Override
    public int getItemCount() {
        return rangesArrayList.size();
    }

    public boolean isDisplayCheckBox() {
        return displayCheckBox;
    }

    public void setDisplayCheckBox() {

        this.displayCheckBox = !this.displayCheckBox;
        notifyDataSetChanged();
    }

    public ArrayList<Ranges> getModifiedRanges() {
        return modifiedRanges;
    }

    public void setModifiedRanges(ArrayList<Ranges> modifiedRanges) {
        this.modifiedRanges = modifiedRanges;
    }

    public ArrayList<Ranges> getChangetRanges() {
        return changetRanges;
    }

    public void removeFromChanged(Ranges ranges) {

        if (changetRanges != null && !changetRanges.isEmpty()) {
            for (int i = 0; i < changetRanges.size(); i++) {
                Ranges cRange = changetRanges.get(i);
                if (cRange.getRangeType().equals(ranges.getRangeType())) {
                    changetRanges.remove(cRange);
                }

            }

        }
    }

    public void setChangetRanges(Ranges ranges) {
        if (changetRanges == null) {
            changetRanges = new ArrayList<>();
        }

        Ranges cRange = null;

        //Checking if changetRanges already has current range
        if (changetRanges.size() > 0) {


            for (int i = 0; i < changetRanges.size(); i++) {
                Ranges cRangeIn = changetRanges.get(i);
                if (cRangeIn.getRangeType().equals(ranges.getRangeType())) {
                    if (cRangeIn.getCurrentValue() != ranges.getCurrentValue()) {

                        cRangeIn.setCurrentValue(ranges.getCurrentValue());

                    } else {
                        changetRanges.remove(cRange);
                    }

                    cRange = cRangeIn;

                }

            }

        }

        //If changetRanges does't has the new Range, add it and return
        if (cRange == null) {
            Ranges nRange = new Ranges();
            nRange.setNewRange(ranges);
            changetRanges.add(nRange);

        }


    }

    //The method must receive either AIR_MOTOR or SOAP_MOTOR and return the opposite of them
    private Ranges getSoapAirRange(Ranges ranges, ArrayList<Ranges> rangesArr) {

        for (Ranges range : rangesArr) {
            if (ranges.getRangeType().equals(RangeTypes.AIR_MOTOR)) {
                if (range.getRangeType().equals(RangeTypes.SOAP_MOTOR)) {
                    if (range.getCurrentValue() == 0) {
                        return getSoapAirRange(ranges, rangesArrayList);

                    }
                    return range;
                }
            } else {
                if (range.getRangeType().equals(RangeTypes.AIR_MOTOR)) {

                    if (range.getCurrentValue() == 0) {
                        return getSoapAirRange(ranges, rangesArrayList);

                    }
                    return range;
                }
            }
        }
        return null;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private SeekBar seekBar;
        TextView title_TV, value_TV, duration_units;
        private RangeTypes type;
        private int defaultValue;
        public CheckBox selectRangeChkBox;


        public ViewHolder(View itemView) {
            super(itemView);

            seekBar = (SeekBar) itemView.findViewById(R.id.seek_bar_settings);
            title_TV = (TextView) itemView.findViewById(R.id.title_TV);
            value_TV = (TextView) itemView.findViewById(R.id.duration_value_TV);
            selectRangeChkBox = (CheckBox) itemView.findViewById(R.id.settings_seek_bar_adapter_selected_cb);
            duration_units = (TextView) itemView.findViewById(R.id.duration_units);

        }


        private void onBind(RangeTypes type, int titleStr, int minVal, int currentValue, int maxVal, int defaultValue) {

            if (displayCheckBox) {
                selectRangeChkBox.setVisibility(View.VISIBLE);
            } else {
                selectRangeChkBox.setVisibility(View.GONE);
            }

            if (type == RangeTypes.DETECTION_RANGE) {
                duration_units.setText(R.string.settings_screen_steps);
            } else {
                duration_units.setText(R.string.seconds);
            }

            if (type == RangeTypes.AIR_MOTOR || type == RangeTypes.FOAM_SOAP || type == RangeTypes.SOAP_MOTOR) {
                duration_units.setText(R.string.settings_screen_percentage);
            }

            if (type == RangeTypes.SOAP_DOSAGE) {
                duration_units.setText(R.string.settings_screen_dosage);
            }

            this.setType(type);
            this.defaultValue = defaultValue;


            //For lower versions we define the minimum val in onProgressChanged() above.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                seekBar.setMin(minVal);
            }

            seekBar.setMax(maxVal);
            seekBar.setProgress(currentValue);


            title_TV.setText(titleStr);
            value_TV.setText(String.valueOf(currentValue));

        }

        public CheckBox getSelectRangeChkBox() {
            return selectRangeChkBox;
        }

        public SeekBar getSeekBar() {
            return seekBar;
        }

        public void setSeekBar(SeekBar seekBar) {
            this.seekBar = seekBar;
        }

        public RangeTypes getType() {
            return type;
        }

        public void setType(RangeTypes type) {
            this.type = type;
        }

        public int getDefaultValue() {
            return defaultValue;
        }

        public void setDefaultValue(int defaultValue) {
            this.defaultValue = defaultValue;
        }
    }


}
