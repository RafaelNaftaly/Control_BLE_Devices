package stern.msapps.com.control_ble_devices.view.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import stern.msapps.com.control_ble_devices.presenter.statisticsPresenter.StatisticsContract;
import stern.msapps.com.control_ble_devices.presenter.statisticsPresenter.StatisticsPresenter;
import stern.msapps.com.control_ble_devices.R;
import stern.msapps.com.control_ble_devices.utils.DialogHelper;



/**
 * Created by Rafael on 10/7/19.
 */
public class StatisticsFragment extends android.support.v4.app.DialogFragment implements StatisticsContract.View {



    // Variables Declaration ButterKnife
    @BindView(R.id.statistics_fragment_recycler_view)
    RecyclerView statistics_fragment_recycler_view;

    @BindView(R.id.statistics_fragment_close_button_tv)
    TextView statistics_fragment_close_button_tv;

    @BindView(R.id.statistics_fragment_sendReport_BTN)
    FloatingActionButton sendReportBTN;


    private Context context;
    private View mainInflatedView;
    private StatisticsPresenter statisticsPresenter;

    @Override
    public void onStart() {
        super.onStart();
        setPresenter();

        statisticsPresenter.onStart();

        setDialogPosition();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainInflatedView = inflater.inflate(R.layout.fragment_statistics, container, false);

        ButterKnife.bind(this, mainInflatedView);
        sendReportBTN.setEnabled(false);

        return mainInflatedView;


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        new Handler().postDelayed(() -> {
            if (!DialogHelper.getInstance().isDialogShown()) {
                DialogHelper.getInstance().displayLoaderProgressDialog(context, context.getResources().getString(R.string.loading)).show();

            }

        }, 1000);


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;

    }


    @Override
    public void onDetach() {
        super.onDetach();

    }

    private void setDialogPosition() {


        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);

            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        }
    }

    @Override
    public void viewInit() {

    }

    @Override
    public void setPresenter() {

        statisticsPresenter = new StatisticsPresenter(context);
        statisticsPresenter.attach(this);
        statisticsPresenter.setStatisticsRecyclerView(statistics_fragment_recycler_view);

        onEventListeners();


    }


    @Override
    public void hideFragment() {

        this.dismiss();
    }

    @Override
    public void onEventListeners() {
        statistics_fragment_close_button_tv.setOnClickListener(statisticsPresenter);
        sendReportBTN.setOnClickListener(statisticsPresenter);
    }

    @Override
    public void setClickableFloatingButton(boolean enable) {
        sendReportBTN.setEnabled(enable);
    }

    @Nullable
    @Override
    public Context getContext() {
        return context;
    }


    @Override
    public Context getActivityContext() {
        return this.context;
    }
}
