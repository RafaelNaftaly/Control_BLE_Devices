package stern.msapps.com.control_ble_devices.view.adapters;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import stern.msapps.com.control_ble_devices.model.enums.SettingButtons;
import stern.msapps.com.control_ble_devices.presenter.settingPresenter.SettingPresenter;
import stern.msapps.com.control_ble_devices.R;

public class SettingsButtonsAdapter extends RecyclerView.Adapter<SettingsButtonsAdapter.ViewHolder> {


    private SettingPresenter presenter;

    public SettingsButtonsAdapter(SettingPresenter presenter) {
        this.presenter = presenter;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_item_settings_recycler_button, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.bindView(SettingButtons.values()[position]);

        holder.itemView.setOnClickListener(v -> presenter.onButtonClicked(SettingButtons.values()[position]));
    }

    @Override
    public int getItemCount() {
        return SettingButtons.values().length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView text;


        public ViewHolder(View itemView) {
            super(itemView);

            text = (TextView) itemView.findViewById(R.id.settings_item_text);
        }

        private void bindView(SettingButtons button) {
            text.setText(button.getName(button));
        }
    }


}
