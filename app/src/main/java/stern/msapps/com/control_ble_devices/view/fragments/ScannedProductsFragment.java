package stern.msapps.com.control_ble_devices.view.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;


import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


import butterknife.BindView;
import butterknife.ButterKnife;
import stern.msapps.com.control_ble_devices.model.dataTypes.SternProduct;
import stern.msapps.com.control_ble_devices.model.enums.SternTypes;
import stern.msapps.com.control_ble_devices.model.roomDataBase.database.SternRoomDatabase;
import stern.msapps.com.control_ble_devices.presenter.scanProductsPresenter.ScannedProductContract;
import stern.msapps.com.control_ble_devices.presenter.scanProductsPresenter.ScannedProductPresenter;
import stern.msapps.com.control_ble_devices.utils.DialogHelper;
import stern.msapps.com.control_ble_devices.view.adapters.FilterAdapter;
import stern.msapps.com.control_ble_devices.view.adapters.RVEmptyObserver;
import stern.msapps.com.control_ble_devices.view.adapters.ScannedProductsAdapter;
import stern.msapps.com.control_ble_devices.view.adapters.SwipeHelperScannProduct;
import stern.msapps.com.control_ble_devices.R;
import stern.msapps.com.control_ble_devices.model.dataTypes.User;
import stern.msapps.com.control_ble_devices.view.activities.PasswordActivity;


/**
 * Created by Rafael on 9/7/19.
 */
public class ScannedProductsFragment extends BaseFragment implements FilterAdapter.OnFilterImageClicked, View.OnClickListener, ScannedProductContract.View {



    // Variables Declaration .
    private final String TAG = ScannedProductsFragment.class.getSimpleName();
    private static ScannedProductsAdapter recyclerViewAdapterMyProducts;
    private RVEmptyObserver adapterObserver;
    private FilterAdapter recyclerViewAdapterImagesFilter;
    private static LinearLayoutManager mLinearLayoutManager, filterLinearLayout;
    public ArrayList<SternProduct> productsArr;
    private View view;
    private Activity context;
    private RecyclerView filterRecycleView;
    private static SternRoomDatabase sternRoomDatabase;


    @BindView(R.id.scanned_product_fragment_recycler_view)
    public RecyclerView mainRecyclerView;
    @BindView(R.id.scanned_product_fragment_searchView)
    android.support.v7.widget.SearchView cleanerProductsSearchView;
    @BindView(R.id.scanned_product_fragment_new_TV)
    TextView scanned_product_fragment_new_TV;
    @BindView(R.id.scanned_product_fragment_near_by_TV)
    TextView scanned_product_fragment_near_by_TV;
    @BindView(R.id.scanned_product_fragment_no_products_fount_TV)
    TextView scanned_product_fragment_no_products_fount_TV;
    @BindView(R.id.scanned_product_fragment_filter_IV)
    ImageView cleaner_product_fragment_filter_IV;
    @BindView(R.id.presets_fragment_search_icon_iv)
    ImageView cleaner_product_fragment_search_icon_IV;
    @BindView(R.id.scanned_product_swipe_to_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayoutForProductsRecyclerView;
    @BindView(R.id.scanned_product_fragment_log_out)
    TextView scanned_product_fragment_log_out;


    private ScannedProductPresenter scannedProductPresenter;

    private ArrayList<SternTypes> sternTypesArr = new ArrayList<>();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = (Activity) context;
        sternRoomDatabase = SternRoomDatabase.getDatabase(context);
    }

    public ScannedProductsFragment() {
        Log.d(TAG, ".....DefaultCon");
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_scanned_products, container, false);

        ButterKnife.bind(this, view);

        Log.d(TAG, "**********onCreateView");

        onClick(scanned_product_fragment_near_by_TV);
        scannedProductPresenter = new ScannedProductPresenter(context);
        scannedProductPresenter.attach((ScannedProductContract.View) this);
        scannedProductPresenter.initBle();

        setListenersToView();
        createMainRecyclerView();
        setSwipeToRefreshListener();


        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();

        scannedProductPresenter.onStart();
        scannedProductPresenter.bleStartScan();


        // Navigation.findNavController((Activity) context, R.id.main_activity_host_fragment).navigate(R.id.productInformationFragment);

    }


    /**
     * Initialize and handle the Swipe to refresh Listener
     */
    private void setSwipeToRefreshListener() {


        swipeRefreshLayoutForProductsRecyclerView.setOnRefreshListener(() -> {


            if (recyclerViewAdapterMyProducts.getNearByFilter() != ScannedProductsAdapter.NearByFilter.FROM_DB) {

                if (productsArr != null) {
                    productsArr.clear();
                }
                cleanerProductsSearchView.setVisibility(View.GONE);


                //If the filter set to display data from database, disable swipe to refresh
                if (!scanned_product_fragment_new_TV.isSelected() && !scanned_product_fragment_near_by_TV.isSelected()) {

                    swipeRefreshLayoutForProductsRecyclerView.setRefreshing(false);
                }


                scannedProductPresenter.bleStartScan();

                adapterObserver.onChanged();

                Log.d(TAG, "...............StartScanning........");

                Handler watchDogHandler = new Handler();

                //Check if there is no data, stop the loader...
                watchDogHandler.postDelayed(() -> {
                    if (swipeRefreshLayoutForProductsRecyclerView.isRefreshing()) {
                        swipeRefreshLayoutForProductsRecyclerView.setRefreshing(false);
                    }
                }, 1500);

            } else {
                if (swipeRefreshLayoutForProductsRecyclerView.isRefreshing()) {
                    swipeRefreshLayoutForProductsRecyclerView.setRefreshing(false);
                }
            }
        });


    }


    private void setListenersToView() {


        cleaner_product_fragment_filter_IV.setOnClickListener(this);

        scanned_product_fragment_new_TV.setOnClickListener(this);


        scanned_product_fragment_near_by_TV.setOnClickListener(this);


        cleaner_product_fragment_search_icon_IV.setOnClickListener(this);

        scanned_product_fragment_log_out.setOnClickListener(this);


        swipeRefreshLayoutForProductsRecyclerView.setColorSchemeResources(R.color.app_blue_color, R.color.colorAccent, R.color.password_btn_pressed_blue_color);


    }


    /**
     * Method displays Search View and hide a Recycler View.
     *
     * @param:
     * @return:
     * @throw:
     */
    private void displaySearchView() {


        if (filterRecycleView != null) {
            displayRecyclerViewFilter(false);
            cleanerProductsSearchView.setVisibility(View.VISIBLE);

        } else {
            if (cleanerProductsSearchView.getVisibility() == View.VISIBLE) {
                cleanerProductsSearchView.setVisibility(View.GONE);
            } else {
                cleanerProductsSearchView.setVisibility(View.VISIBLE);
            }

        }

        if (cleanerProductsSearchView.getVisibility() == View.VISIBLE) {

            cleanerProductsSearchView.setOnQueryTextListener(recyclerViewAdapterMyProducts);
        }


    }

    /**
     * Method displays RecyclerView by creation linearLayoutManager  and RecyclerView adapter.
     *
     * @param:
     * @return:
     * @throw:
     */
    private void createMainRecyclerView() {

        mLinearLayoutManager = new LinearLayoutManager(context);
        mainRecyclerView.setLayoutManager(mLinearLayoutManager);


        productsArr = new ArrayList<>();
        recyclerViewAdapterMyProducts = new ScannedProductsAdapter(productsArr, context, scannedProductPresenter, scannedProductPresenter.getSternProductsFromDB(), ScannedProductsAdapter.NearByFilter.NEAR_BY);

        mainRecyclerView.setAdapter(recyclerViewAdapterMyProducts);
        addSwipeHelperToRecyclerView(mainRecyclerView);


        adapterObserver = new RVEmptyObserver(mainRecyclerView, scanned_product_fragment_no_products_fount_TV);
        recyclerViewAdapterMyProducts.registerAdapterDataObserver(adapterObserver);


        //setQueryListenerToSearchView(cleanerProductsSearchView);


    }


    /**
     * Method displays RecyclerView and hide SearchView.
     *
     * @param:
     * @return:
     * @throw:
     */
    private void displayRecyclerViewFilter(boolean display) {


        if (display) {
            if (cleanerProductsSearchView.getVisibility() == View.VISIBLE) {
                cleanerProductsSearchView.setVisibility(View.GONE);
            }

            filterLinearLayout = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);

            if (filterRecycleView == null) {
                filterRecycleView = (RecyclerView) view.findViewById(R.id.presets_fragment_RecyclerView);
            }
            filterRecycleView.setLayoutManager(filterLinearLayout);
            recyclerViewAdapterImagesFilter = new FilterAdapter(context, ScannedProductsFragment.this);
            filterRecycleView.setAdapter(recyclerViewAdapterImagesFilter);
            filterRecycleView.setVisibility(View.VISIBLE);
            recyclerViewAdapterMyProducts.setFitteredList(productsArr);
            sternTypesArr.clear();
        } else {
            filterRecycleView.setVisibility(View.GONE);

            filterRecycleView = null;
            filterLinearLayout = null;
            recyclerViewAdapterImagesFilter = null;


        }

    }


    //Creating a list of Stern Objects FOR TESTING
    private ArrayList<SternProduct> getProductsArr() {

        productsArr = new ArrayList<>();

        for (int i = 0; i < 10; i++)

        {

            SternProduct sternProduct = null;

            switch (i) {
                case 0:
                    sternProduct = new SternProduct(SternTypes.URINAL);
                    break;
                case 1:
                    sternProduct = new SternProduct(SternTypes.WC);
                    break;
                case 2:
                    sternProduct = new SternProduct(SternTypes.SOAP_DISPENSER);
                    break;
                case 3:
                    sternProduct = new SternProduct(SternTypes.FAUCET);

                default:
                    sternProduct = new SternProduct(SternTypes.FAUCET);
            }


            sternProduct.setName("Toilet Electra1");
            sternProduct.setLastConnected("Last updated Las 2018");
            sternProduct.setLastUpdate(" Last updated  2018  10:32");
            productsArr.add(sternProduct);
        }

        return productsArr;
    }


    @Override


    /**
     *
     * Overridden Method of Interface FilterAdapter.OnFilterImageClicked
     * displays filtered list of SternProducts on recyclerView .
     * @param: enum SternTypes type
     * @return:
     * throws:
     *
     * */
    public void onFilterClicked(SternTypes type, boolean isSelected) {
        Toast.makeText(context, type.name() + " " + isSelected, Toast.LENGTH_SHORT).show();

        ArrayList<SternProduct> sternProducts = new ArrayList<>();


        if (isSelected) {
            sternTypesArr.add(type);
        } else {
            sternTypesArr.remove(type);
        }

        for (SternProduct product : productsArr) {
            for (int i = 0; i < sternTypesArr.size(); i++) {
                if (product.getType() == sternTypesArr.get(i)) {
                    sternProducts.add(product);
                }
            }
        }

        if (sternTypesArr.isEmpty()) {
            recyclerViewAdapterMyProducts.setFitteredList(productsArr);
        } else {
            recyclerViewAdapterMyProducts.setFitteredList(sternProducts);
        }


    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.scanned_product_fragment_log_out) {
            logOut();

            return;
        }

        if (scannedProductPresenter != null && scannedProductPresenter.isScanning() && recyclerViewAdapterMyProducts.getNearByFilter() != ScannedProductsAdapter.NearByFilter.FROM_DB) {
            DialogHelper.getInstance().displayOneButtonDialog(context, context.getResources().getString(R.string.scannes_product_fragment_scanning),
                    context.getResources().getString(R.string.name_passkey_ok)).show();
        } else {

            switch (v.getId()) {
                case R.id.scanned_product_fragment_filter_IV:
                    displayFilter();
                    break;
                case R.id.scanned_product_fragment_new_TV:
                    onNearByNewFilterClicked(v);
                    break;

                case R.id.scanned_product_fragment_near_by_TV:
                    onNearByNewFilterClicked(v);
                    break;

                case R.id.presets_fragment_search_icon_iv:
                    displaySearchView();
                    break;
            }
        }


    }


    /**
     * Displaying or Hiding the Filter Layout
     */
    private void displayFilter() {

        if (filterRecycleView == null) {
            displayRecyclerViewFilter(true);
        } else {
            displayRecyclerViewFilter(false);
        }
    }


    @Override
    public void bleScanningResult(SternProduct product) {

        if (!checkIfContains(product.getMacAddress())) {

            switch (recyclerViewAdapterMyProducts.getNearByFilter()) {
                case NEAR_BY:
                    this.productsArr.add(product);

                    break;
                case NEW:
                    if (!product.isPreviouslyConnected()) {
                        this.productsArr.add(product);

                    }
                    break;

            }

            recyclerViewAdapterMyProducts.setNewData(this.productsArr);
            recyclerViewAdapterMyProducts.notifyDataSetChanged();


        }


    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onNearByNewFilterClicked(android.view.View v) {

        v.setSelected(!v.isSelected());
        switch (v.getId()) {
            case R.id.scanned_product_fragment_new_TV:
                if (v.isSelected()) {
                    scanned_product_fragment_near_by_TV.setTextColor(context.getResources().getColor(R.color.white_color, null));
                    ((TextView) v).setTextColor(context.getResources().getColor(R.color.password_btn_pressed_blue_color, null));
                    scanned_product_fragment_near_by_TV.setSelected(false);
                    if (recyclerViewAdapterMyProducts != null) {
                        recyclerViewAdapterMyProducts.setNearByFilter(ScannedProductsAdapter.NearByFilter.NEW);
                    }
                } else {
                    ((TextView) v).setTextColor(context.getResources().getColor(R.color.white_color, null));
                }

                break;

            case R.id.scanned_product_fragment_near_by_TV:
                if (v.isSelected()) {
                    scanned_product_fragment_new_TV.setSelected(false);
                    scanned_product_fragment_new_TV.setTextColor(context.getResources().getColor(R.color.white_color, null));
                    ((TextView) v).setTextColor(context.getResources().getColor(R.color.password_btn_pressed_blue_color, null));
                    if (recyclerViewAdapterMyProducts != null) {
                        recyclerViewAdapterMyProducts.setNearByFilter(ScannedProductsAdapter.NearByFilter.NEAR_BY);
                    }
                } else {
                    ((TextView) v).setTextColor(context.getResources().getColor(R.color.white_color, null));
                }
                break;

        }


        if (!scanned_product_fragment_new_TV.isSelected() && !scanned_product_fragment_near_by_TV.isSelected()) {
            if (recyclerViewAdapterMyProducts != null) {
                recyclerViewAdapterMyProducts.setNearByFilter(ScannedProductsAdapter.NearByFilter.FROM_DB);

            }
        }

    }


    /**
     * Checking if the Products Collection contains the received MAC address
     *
     * @param macAddress
     * @return if the ProductArr contains the received MAC address or not
     */
    public boolean checkIfContains(String macAddress) {
        swipeRefreshLayoutForProductsRecyclerView.setRefreshing(false);
        for (SternProduct product : productsArr) {
            if (product.getMacAddress().equals(macAddress)) {
                return true;
            }
        }
        return false;

    }

    @Override
    public void addSwipeHelperToRecyclerView(RecyclerView recyclerView) {


        new SwipeHelperScannProduct(context, recyclerView) {
            @Override
            public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {
                underlayButtons.add(new SwipeHelperScannProduct.UnderlayButton(
                        context.getResources().getString(R.string.preset_screen_delete),
                        0,
                        Color.parseColor("#FF3C30"),
                        (int pos) -> {

                            Log.d("DeleteTest", "DeleteTest1");

                            sternRoomDatabase.sternProductDao().deleteByMacAddress(recyclerViewAdapterMyProducts.getSternProducts().get(pos).getMacAddress());

                            scannedProductPresenter.refreshDBforScannedProduct();
                            String macAddress = recyclerViewAdapterMyProducts.getSternProducts().get(pos).getMacAddress();
                            scannedProductPresenter.deleteSharedPrefScheduledEvents(macAddress);


                            recyclerViewAdapterMyProducts.getSternProducts().remove(pos);
                            recyclerViewAdapterMyProducts.notifyDataSetChanged();


                            // TODO: onDelete
                        }
                ));
            }
        };

    }


    @Override
    public void logOut() {
        User.getUserInstance().logOut(context);

        context.startActivity(new Intent(context, PasswordActivity.class));

        ((Activity) context).finish();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        scannedProductPresenter.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        scannedProductPresenter.onPause();
    }

    @Nullable
    @Override
    public Activity getContext() {
        return context;
    }


}
