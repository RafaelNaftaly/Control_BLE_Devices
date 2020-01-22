package stern.msapps.com.control_ble_devices.view.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import stern.msapps.com.control_ble_devices.model.enums.DosageButtons;
import stern.msapps.com.control_ble_devices.presenter.settingPresenter.SettingPresenter;
import stern.msapps.com.control_ble_devices.R;

public class DosageButtonsAdapter extends RecyclerView.Adapter<DosageButtonsAdapter.ViewHolder> {

    private SettingPresenter presenter;
    private ViewHolder selectedHolder = null;
    private int chosenDosage = -1;

    public DosageButtonsAdapter(SettingPresenter presenter) {
        this.presenter = presenter;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(presenter.getmView().getActivityContext()).inflate(R.layout.single_dosage_buttons, parent, false);


        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.onBind(DosageButtons.values()[position]);


        holder.itemView.setOnClickListener(v -> {
            presenter.onDosageButtonClicked(DosageButtons.values()[position]);

            holder.title.setSelected(!holder.title.isSelected());
            if (selectedHolder != null) {
                selectedHolder.title.setSelected(false);

            }
            selectedHolder = holder;
            chosenDosage = (position + 1);
        });

    }

    @Override
    public int getItemCount() {
        return DosageButtons.values().length;
    }

    public int getChosenDosage() {
        return chosenDosage;
    }

    public void setChosenDosage(int chosenDosage) {
        this.chosenDosage = chosenDosage;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;

        public ViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.simple_dosage_button_title_TV);
        }

        public void onBind(DosageButtons dosageButton) {

            title.setText(dosageButton.getNAme());
        }
    }
}
