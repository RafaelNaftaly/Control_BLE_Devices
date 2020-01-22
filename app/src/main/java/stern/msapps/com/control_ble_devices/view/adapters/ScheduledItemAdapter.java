package stern.msapps.com.control_ble_devices.view.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import android.widget.TextView;
import android.widget.Toast;


import java.util.Date;

import stern.msapps.com.control_ble_devices.model.dataTypes.Day;
import stern.msapps.com.control_ble_devices.model.dataTypes.ScheduledDay;
import stern.msapps.com.control_ble_devices.model.dataTypes.ScheduledStandByRandomDay;
import stern.msapps.com.control_ble_devices.model.enums.ScheduledDataTypes;
import stern.msapps.com.control_ble_devices.presenter.operateScreenPresenter.OperatePresenter;
import stern.msapps.com.control_ble_devices.R;


import stern.msapps.com.control_ble_devices.model.dataTypes.ScheduledHygieneFlashRandomDay;
import stern.msapps.com.control_ble_devices.utils.dataParser.BleDataParser;


public class ScheduledItemAdapter extends RecyclerView.Adapter<ScheduledItemAdapter.ViewHolder> {

    private Day scheduleDAy;
    private OperatePresenter presenter;
    private ScheduledDataTypes tag;
    private boolean displayCheckBox;

    public ScheduledItemAdapter(OperatePresenter presenter, ScheduledDataTypes tag) {
        this.presenter = presenter;
        this.tag = tag;
    }


    @NonNull
    @Override
    public ScheduledItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_scheduled_date, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduledItemAdapter.ViewHolder holder, int position) {


        Day.ScheduledData scheduledData = scheduleDAy.getScheduledDataArrayList().get(position);

        if (scheduleDAy instanceof ScheduledDay) {

            holder.single_scheduled_reset_BTN.setOnClickListener(v -> {
                presenter.deleteScheduledHygieneFlush(tag, ((ScheduledDay) scheduleDAy).getDayOfWeek(), position);
            });
            holder.bindView(((ScheduledDay) scheduleDAy).getDayOfWeek().toString(),
                    scheduledData.getDuration(),
                    scheduledData.getTime(), displayCheckBox, scheduledData.isWasChosenForPreset());

            holder.single_scheduled_checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                Toast.makeText(presenter.getmView().getContext(), "Checked " + isChecked, Toast.LENGTH_SHORT).show();
                scheduleDAy.getScheduledDataArrayList().get(position).setWasChosenForPreset(isChecked);


            });
        } else if (scheduleDAy instanceof ScheduledHygieneFlashRandomDay || scheduleDAy instanceof ScheduledStandByRandomDay) {
            Day.ScheduledData scheduled = scheduleDAy.getScheduledDataArrayList().get(position);

            holder.single_scheduled_reset_BTN.setOnClickListener(v -> {
                if (scheduleDAy instanceof ScheduledHygieneFlashRandomDay) {
                    presenter.deleteScheduledRandomDay(ScheduledDataTypes.HYGIENE_RANDOM_DAY, position);
                } else {
                    presenter.deleteScheduledRandomDay(ScheduledDataTypes.STANDBY_RANDOM_DAY, position);
                }
            });


            holder.bindView(BleDataParser.getInstance().getDateMillSecforScheduledDay(String.valueOf(scheduled.getTime())),
                    scheduled.getDuration(),
                    scheduled.getTime(), false, false);
        }

    }


    @Override
    public int getItemCount() {
        if (scheduleDAy != null) {

            return scheduleDAy.getScheduledDataArrayList().size();
        }
        return 0;
    }

    public boolean isDisplayCheckBox() {
        return displayCheckBox;
    }

    public void setDisplayCheckBox() {

        this.displayCheckBox = !this.displayCheckBox;

    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView single_scheduled_date_TV, single_scheduled_time_value_TV, single_scheduled_reset_BTN, single_scheduled_duration_TV;
        CheckBox single_scheduled_checkBox;


        public ViewHolder(View itemView) {
            super(itemView);
            single_scheduled_date_TV = (TextView) itemView.findViewById(R.id.single_scheduled_date_TV);
            single_scheduled_time_value_TV = (TextView) itemView.findViewById(R.id.single_scheduled_time_value_TV);
            single_scheduled_reset_BTN = (TextView) itemView.findViewById(R.id.single_scheduled_reset_BTN);
            single_scheduled_duration_TV = (TextView) itemView.findViewById(R.id.single_scheduled_duration_TV);
            single_scheduled_checkBox = (CheckBox) itemView.findViewById(R.id.single_scheduled_checkBox);
        }


        public void bindView(String date, long duration, long time, boolean displayCheckBox, boolean ischeckBoxSelected) {

            Date date1 = new BleDataParser().getDateFromMilisec(String.valueOf(time));


            if (displayCheckBox) {
                single_scheduled_checkBox.setVisibility(View.VISIBLE);

                single_scheduled_checkBox.setChecked(ischeckBoxSelected);

            } else {
                single_scheduled_checkBox.setVisibility(View.GONE);
            }


            single_scheduled_date_TV.setText(date);
            single_scheduled_time_value_TV.setText(new BleDataParser().getHourMillSecforScheduledDay(String.valueOf(time)));
            single_scheduled_duration_TV.setText(String.valueOf(duration + ((tag == ScheduledDataTypes.HYGIENE_FLUSH) ? "s" : "h")));

        }
    }

    public void setDay(Day scheduleDAy) {
        this.scheduleDAy = scheduleDAy;
        notifyDataSetChanged();
    }
}
