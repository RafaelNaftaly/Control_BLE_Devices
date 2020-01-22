package stern.msapps.com.control_ble_devices.view.fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;


import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import stern.msapps.com.control_ble_devices.model.dataTypes.SternProduct;
import stern.msapps.com.control_ble_devices.presenter.informationScreenPresenter.InformationContract;
import stern.msapps.com.control_ble_devices.presenter.informationScreenPresenter.InformationPresenter;
import stern.msapps.com.control_ble_devices.utils.Constants;
import stern.msapps.com.control_ble_devices.view.activities.MainActivity;
import stern.msapps.com.control_ble_devices.R;


/**
 * Created by Rafael on 8/7/19.
 */
public class ProductInformationFragment extends android.support.v4.app.Fragment implements InformationContract.View, TextView.OnEditorActionListener, View.OnFocusChangeListener {


    // Variables Declaration ButterKnife .
    @BindView(R.id.scanned_product_information_fragment_title_TV)
    TextView title_TV;
    @BindView(R.id.product_information_product_name_value_TV_)
    EditText productName_TV;
    @BindView(R.id.product_information_product_type_value_TV)
    TextView productType_TV;
    @BindView(R.id.product_information_serial_number_value_TV)
    TextView productSerialNumber_TV;
    @BindView(R.id.product_information_serial_software_version_value_TV)
    TextView productSW_TV;
    @BindView(R.id.product_information_manufacturing_date_value_TV)
    TextView productManifactureDate_TV;
    @BindView(R.id.product_information_product_dateOfUnit_value_TV)
    TextView productDateOfUnit_TV;
    @BindView(R.id.product_information_time_of_unit_value_TV)
    TextView productTimeOfUnit_TV;
    @BindView(R.id.product_information_initial_pairing_configuration_date_value_TV)
    TextView productInitialDate_TV;
    @BindView(R.id.scanned_product_information_fragment_shimmerConteiner)
    ShimmerFrameLayout shimerConteiner;
    @BindView(R.id.product_information_location_name_value)
    TextView product_information_value;


    private final String TAG = ProductInformationFragment.class.getSimpleName();

    private Context context;
    //private static SternProduct sternProduct;
    private InformationPresenter presenter;
    private View mainView;
    Pattern regex = Pattern.compile("[$&+,:;=\\\\?@#|/'<>.^*()%!-]");
    Pattern hebrewRegex = Pattern.compile("\\p{InHebrew}");



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context = context;

        ((MainActivity) context).setToolbarVisibility(true);

    }

    public ProductInformationFragment() {
        // Required empty public constructor
    }


    public static void init(SternProduct sternProduct) {
        //  ProductInformationFragment.sternProduct = sternProduct;
    }

    public static ProductInformationFragment getInstance() {
        ProductInformationFragment fragment = new ProductInformationFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mainView = inflater.inflate(R.layout.fragment_product_information, container, false);

        if (getArguments().getString(Constants.SHARED_DONT_LOAD_DATA) != null) {
            presenter = new InformationPresenter(context, false);
            presenter.attach(this);
        } else {
            presenter = new InformationPresenter(context, true);
            presenter.attach(this);
        }


        ButterKnife.bind(this, mainView);

        //shimerConteiner.startShimmerAnimation();

        productName_TV.setOnEditorActionListener(this);
        productName_TV.setOnFocusChangeListener(this);
        // Inflate the layout for this fragment
        return mainView;


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.onStart();

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }


    @Override
    public Context getActivityContext() {
        return context;
    }


    @Override
    public void setNameText(String name) {
        if (!name.isEmpty()) {
            productName_TV.setText(name);
            productName_TV.setTextColor(context.getResources().getColor(R.color.password_screen_gray_color, null));
        } else {
            productName_TV.setTextColor(context.getResources().getColor(R.color.red_color, null));
            productName_TV.setText(getString(R.string.product_information_no_name_error));
        }


    }

    @Override
    public void setSerialNumber(String sNumber) {
        productSerialNumber_TV.setText(sNumber);
    }

    @Override
    public void setProductionType(String str) {
        title_TV.setText(str);
        productType_TV.setText(str);
    }


    @Override
    public void setSoftwareVersion(String version) {
        productSW_TV.setText(version);
    }

    @Override
    public void setManufacturyDate(String date) {
        productManifactureDate_TV.setText(date);
        // shimerConteiner.stopShimmerAnimation();
    }

    @Override
    public void setDateOfUnit(String date) {
        productDateOfUnit_TV.setText(date);
    }

    @Override
    public void setTimeOfUnit(String time) {
        productTimeOfUnit_TV.setText(time);
    }

    @Override
    public void setInitialParringDate(String date) {

        productInitialDate_TV.setText(date);
        shimerConteiner.stopShimmerAnimation();
    }



    @Override
    public void setLocationNAme(String name) {
        product_information_value.setText(name);
    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {


        if (actionId == EditorInfo.IME_ACTION_DONE) {

            if (productName_TV.getText().length() > 22) {
                productName_TV.setError(context.getString(R.string.product_information_name_error_to_long));
                return false;
            } else if (regex.matcher(productName_TV.getText().toString()).find() || hebrewRegex.matcher(productName_TV.getText().toString()).find()) {
                productName_TV.setError(context.getString(R.string.product_information_name_error_wrong_char));
            } else {

                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mainView.getWindowToken(), 0);

                presenter.sendName(productName_TV.getText().toString().trim());
            }

            return true;
        }

        return false;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (presenter != null) {
            presenter.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.onDestroy();
        }
    }

    @Nullable
    @Override
    public Context getContext() {
        return context;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.product_information_product_name_value_TV_:

                if (hasFocus && (productName_TV.getCurrentTextColor() == context.getColor(R.color.red_color))) {
                    productName_TV.setText("");
                    productName_TV.setTextColor(context.getColor(R.color.app_text_black_color));
                }

                return;

        }
    }
}
