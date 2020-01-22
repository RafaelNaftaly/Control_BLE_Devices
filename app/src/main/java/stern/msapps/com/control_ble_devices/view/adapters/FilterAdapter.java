package stern.msapps.com.control_ble_devices.view.adapters;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.bumptech.glide.Glide;

import stern.msapps.com.control_ble_devices.model.enums.SternTypes;
import stern.msapps.com.control_ble_devices.R;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.ViewHolder> {


    private Context context;
    private OnFilterImageClicked onFilterImageClicked;
    private int lastPosition = -1;
    private ViewGroup viewGroup;


    // Provide a suitable constructor (depends on the kind of dataset)
    public FilterAdapter(Context context, OnFilterImageClicked onFilterImageClicked) {

        this.onFilterImageClicked = onFilterImageClicked;

        this.context = context;


    }

    // Create new views (invoked by the layout manager)
    public FilterAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View itemView = LayoutInflater.from(context).inflate(R.layout.single_filter_item, viewGroup, false);


        this.viewGroup = viewGroup;

        ViewHolder viewHolder = new ViewHolder(itemView);


        return viewHolder;
    }


    public void onBindViewHolder(FilterAdapter.ViewHolder viewHolder, final int position) {


        viewHolder.bindView(SternTypes.values()[position]);

        viewHolder.itemView.setOnClickListener(v -> {

            viewHolder.constraintLayout.setSelected(!viewHolder.constraintLayout.isSelected());


            if (onFilterImageClicked != null) {
                onFilterImageClicked.onFilterClicked(SternTypes.values()[position], viewHolder.constraintLayout.isSelected());
            }
        });

    }

    @Override
    public int getItemCount() {
        return SternTypes.values().length;

    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private ConstraintLayout constraintLayout;


        public ViewHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.single_filter_adapter_IMG);
            constraintLayout = (ConstraintLayout) itemView.findViewById(R.id.single_filter_adapter_boarder);
        }

        /**
         * @param type item from array
         */
        private void bindView(SternTypes type) {

            Glide.with(context).load(type.getImagePath()).into(imageView);


        }

    }

    public interface OnFilterImageClicked {
        void onFilterClicked(SternTypes type, boolean isSelected);

    }


}


