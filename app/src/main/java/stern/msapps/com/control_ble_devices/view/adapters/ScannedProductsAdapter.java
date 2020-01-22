package stern.msapps.com.control_ble_devices.view.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import stern.msapps.com.control_ble_devices.model.dataTypes.SternProduct;
import stern.msapps.com.control_ble_devices.presenter.scanProductsPresenter.ScannedProductPresenter;
import stern.msapps.com.control_ble_devices.R;

public class ScannedProductsAdapter extends RecyclerView.Adapter<ScannedProductsAdapter.ScannedProductsViewHolder> implements android.support.v7.widget.SearchView.OnQueryTextListener {


    public ArrayList<SternProduct> getSternProducts() {
        return sternProducts;
    }

    public void setSternProducts(ArrayList<SternProduct> sternProducts) {
        this.sternProducts = sternProducts;
    }

    private ArrayList<SternProduct> sternProducts, sternProductsFromDB, rawSternProductsArr;
    private Context context;
    private ScannedProductPresenter presenter;
    private NearByFilter nearByFilter;
    private int stringLengthCounter;
    private SternProduct sternProduct;

    public SternProduct getSternProductFromDB() {
        return sternProductFromDB;
    }

    public void setSternProductFromDB(SternProduct sternProductFromDB) {
        this.sternProductFromDB = sternProductFromDB;
    }

    private SternProduct sternProductFromDB = null;


    // Provide a suitable constructor (depends on the kind of dataset)
    public ScannedProductsAdapter(ArrayList<SternProduct> myDataset, Context context, ScannedProductPresenter productPresenter, ArrayList<SternProduct> sternProductsFromDB, NearByFilter nearByFilter) {
        sternProducts = new ArrayList<>();
        sternProducts.addAll(myDataset);
        this.context = context;
        this.presenter = productPresenter;
        this.sternProductsFromDB = sternProductsFromDB;
        this.nearByFilter = nearByFilter;
        this.rawSternProductsArr = new ArrayList<>();
        rawSternProductsArr.addAll(myDataset);


    }

    // Create new views (invoked by the layout manager)
    public ScannedProductsViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.single_scanned_product_adapter, viewGroup, false);

        return new ScannedProductsViewHolder(itemView);
    }


    public void setNearByFilter(NearByFilter filter) {

        this.nearByFilter = filter;


        switch (nearByFilter) {
            case NEW:
                setNewNearByFilter();
                break;

            case NEAR_BY:
                sternProducts.clear();
                sternProducts.addAll(rawSternProductsArr);
                break;

            case FROM_DB:
                sternProducts.clear();
                sternProducts.addAll(sternProductsFromDB);
                break;
        }


        notifyDataSetChanged();

    }

    private void setNewNearByFilter() {

        sternProducts.clear();
        sternProducts.addAll(rawSternProductsArr);

        for (int i = sternProducts.size() - 1; i >= 0; i--) {
            SternProduct stp = sternProducts.get(i);

            if (stp.isPreviouslyConnected()) {
                sternProducts.remove(i);
            }
        }


    }


    /*
     *
     * Method displays filtered list on Recycler View
     *
     * This method declaration will be transferred to presenter layer.
     *
     * */
    public void setFitteredList(ArrayList<SternProduct> products) {

        this.sternProducts = products;

        notifyDataSetChanged();
    }

    public void setNewData(ArrayList<SternProduct> products) {
        this.sternProducts.clear();
        this.sternProducts.addAll(products);
        this.rawSternProductsArr.clear();
        this.rawSternProductsArr.addAll(products);

        notifyDataSetChanged();

    }


    public void onBindViewHolder(ScannedProductsViewHolder viewHolder, final int position) {

        SternProduct sternProduct = sternProducts.get(position);

        switch (nearByFilter) {
            case NEW:
                if (!sternProduct.isPreviouslyConnected()) {
                    viewHolder.bindView(sternProduct);
                    viewHolder.set.setEnabled(true);
                    viewHolder.set.setAlpha(1);
                }
                break;
            case NEAR_BY:
                viewHolder.bindView(sternProduct);
                viewHolder.set.setEnabled(true);
                viewHolder.set.setAlpha(1);
                break;

            case FROM_DB:
                viewHolder.bindView(sternProduct);

                viewHolder.set.setEnabled(false);
                viewHolder.set.setAlpha(.3f);
                sternProductFromDB = sternProduct;
                break;


        }


        onClickListenerSet(viewHolder.set, position);

    }


    @Override
    public int getItemCount() {
        return sternProducts.size();

    }

    private void onClickListenerSet(TextView set, int position) {


        set.setOnClickListener(v -> presenter.connectToBle(sternProducts.get(position)));
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {


        if (newText.isEmpty()) {
            sternProducts.clear();
            sternProducts.addAll(rawSternProductsArr);
            notifyDataSetChanged();
            return true;
        }
        int strL = newText.length();
        stringLengthCounter++;
        ArrayList<SternProduct> arr = new ArrayList<>();

        if (stringLengthCounter > strL) {
            sternProducts.clear();
            sternProducts.addAll(rawSternProductsArr);
            stringLengthCounter = strL;
        }
        for (int i = 0; i < sternProducts.size(); i++) {
            sternProduct = sternProducts.get(i);
            if (sternProducts.get(i).getName().toLowerCase() != null && sternProduct.getName().toLowerCase().contains(newText.toLowerCase()) && !sternProducts.get(i).getName().isEmpty()) {
                arr.add(sternProduct);
            }
        }

        if (arr.isEmpty()) {
            sternProducts.clear();
            notifyDataSetChanged();
            return true;
        }
        if (stringLengthCounter == strL) {
            sternProducts.clear();
            sternProducts.addAll(arr);
        }

        notifyDataSetChanged();

        return true;
    }


    public class ScannedProductsViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView imageView;
        TextView last_connected;
        TextView last_updated;
        TextView set;
        boolean isFromDB;

        public ScannedProductsViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
            imageView = (ImageView) itemView.findViewById(R.id.recycle_view_user_devices_product_IV);
            last_connected = (TextView) itemView.findViewById(R.id.recycle_view_user_devices_last_connected_value_CTV);
            last_updated = (TextView) itemView.findViewById(R.id.recycle_view_user_devices_last_updated_value_CTV);
            set = (TextView) itemView.findViewById(R.id.recycle_view_user_devices_set_TV);


        }

        /**
         * @param product item from array
         */
        private void bindView(SternProduct product) {

            Glide.with(context).load(product.getImage()).into(imageView);

            name.setText(product.getName().isEmpty() ? product.getType().getName() : product.getName());
            last_connected.setText(product.getLastConnected());
            last_updated.setText(product.getLastUpdate());

            isFromDB = product.isPreviouslyConnected();

        }


    }


    public enum NearByFilter {
        NEAR_BY,
        NEW,
        FROM_DB;

    }

    public NearByFilter getNearByFilter() {
        return this.nearByFilter;
    }


}