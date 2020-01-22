package stern.msapps.com.control_ble_devices.view.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import stern.msapps.com.control_ble_devices.R;
import stern.msapps.com.control_ble_devices.model.dataTypes.OperatePreset;


public class OperatePresetAdapter extends RecyclerView.Adapter<OperatePresetAdapter.ViewHolder> {
    private Context context;
    private View mainView;
    private ArrayList<OperatePreset> presetSternProducts;


    public OperatePresetAdapter(Context context, ArrayList<OperatePreset> operatePresets) {

        this.context = context;

        this.presetSternProducts = operatePresets;
    }

    @NonNull
    @Override
    public OperatePresetAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        mainView = LayoutInflater.from(context).inflate(R.layout.single_item_presets_recycler_view, parent, false);


        return new OperatePresetAdapter.ViewHolder(mainView);
    }

    @Override
    public void onBindViewHolder(@NonNull OperatePresetAdapter.ViewHolder holder, int position) {


        holder.bindView(presetSternProducts.get(position));


    }

    public void setSearchData(ArrayList<OperatePreset> op){
        this.presetSternProducts.clear();
        this.presetSternProducts.addAll(op);
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return presetSternProducts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView productImage;
        private TextView presetName, productType, setButton, deleteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.presets_recycler_view_item_iv);
            presetName = itemView.findViewById(R.id.preset_recycler_view_item_product_type_tv);
            productType = itemView.findViewById(R.id.presets_recycler_view_preset_name_tv);
            setButton = itemView.findViewById(R.id.presets_recycler_view_item_set_button_tv);
            deleteButton = itemView.findViewById(R.id.presets_recycler_view_item_delete_button_tv);
        }

        public void bindView(OperatePreset presetSternProduct) {
            Glide.with(context).load(presetSternProduct.getSternType().getImagePath()).into(productImage);
            presetName.setText(presetSternProduct.getPresetName());
            productType.setText(presetSternProduct.getSternType().name());
        }

    }
}
