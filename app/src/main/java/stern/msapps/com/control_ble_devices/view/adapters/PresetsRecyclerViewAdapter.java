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
import java.util.List;

import stern.msapps.com.control_ble_devices.model.dataTypes.PresetSternProduct;
import stern.msapps.com.control_ble_devices.R;

public class PresetsRecyclerViewAdapter extends RecyclerView.Adapter<PresetsRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private View mainView;
    private List<PresetSternProduct> presetSternProducts;


    public PresetsRecyclerViewAdapter(Context context, ArrayList<PresetSternProduct> presetSternProducts) {

        this.context = context;

        this.presetSternProducts = presetSternProducts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        mainView = LayoutInflater.from(context).inflate(R.layout.single_item_presets_recycler_view, parent, false);


        return new ViewHolder(mainView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        holder.bindView(presetSternProducts.get(position));


    }

    public void setSearchData(ArrayList<PresetSternProduct> op){
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

        public void bindView(PresetSternProduct presetSternProduct) {
            Glide.with(context).load(presetSternProduct.getType().getImagePath()).into(productImage);
            presetName.setText(presetSternProduct.getPresetName());
            productType.setText(presetSternProduct.getType().getName());
        }

    }


}
