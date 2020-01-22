package stern.msapps.com.control_ble_devices.view.adapters;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import stern.msapps.com.control_ble_devices.model.enums.DaysOfWeek;
import stern.msapps.com.control_ble_devices.model.enums.ScheduledDataTypes;
import stern.msapps.com.control_ble_devices.presenter.operateScreenPresenter.OperatePresenter;
import stern.msapps.com.control_ble_devices.R;

public class DaysAdapter extends RecyclerView.Adapter<DaysAdapter.ViewHolder> {

    private OperatePresenter presenter;
    private ScheduledDataTypes recyclerType;
    private short lastSelectedPosition = -1;



    public DaysAdapter(OperatePresenter presenter, ScheduledDataTypes recyclerType) {
        this.presenter = presenter;
        this.recyclerType = recyclerType;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_day_item, parent, false);

        int height = parent.getMeasuredHeight();
        int width = parent.getMeasuredWidth() / getItemCount();


        return new ViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        holder.bindView(DaysOfWeek.values()[position]);

        if (getLastSelectedPosition() == position) {
            holder.day_of_week.setSelected(true);
        } else {
            holder.day_of_week.setSelected(false);
        }


        holder.day_of_week.setOnClickListener(v -> {


            holder.day_of_week.setSelected(!holder.day_of_week.isSelected());

            if (getLastSelectedPosition() >= 0) {
                notifyItemChanged(getLastSelectedPosition());
                lastSelectedPosition = (short) holder.getAdapterPosition();
                notifyItemChanged(getLastSelectedPosition());
            }

            presenter.onDaySelected(DaysOfWeek.values()[position], this.recyclerType);


            this.lastSelectedPosition = (short) position;

            notifyDataSetChanged();
        });

    }

    @Override
    public int getItemCount() {
        return DaysOfWeek.values().length;
    }

    public short getLastSelectedPosition() {
        return lastSelectedPosition;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView day_of_week;

        public ViewHolder(View itemView) {
            super(itemView);

            day_of_week = itemView.findViewById(R.id.day_of_week);
        }

        private void bindView(DaysOfWeek day) {

            day_of_week.setText(day.getName(presenter.getmView().getContext()));


        }

        public void unSelect() {
            day_of_week.setSelected(false);

        }


    }


}
