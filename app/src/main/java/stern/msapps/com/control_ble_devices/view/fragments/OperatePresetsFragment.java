package stern.msapps.com.control_ble_devices.view.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.support.v7.widget.SearchView;
import android.widget.TextView;

import stern.msapps.com.control_ble_devices.presenter.operatePresetsPresenter.OperatePresetsContract;
import stern.msapps.com.control_ble_devices.presenter.operatePresetsPresenter.OperatePresetsPresenter;
import stern.msapps.com.control_ble_devices.R;


/**
 * Created by Rafael on 7/7/19.
 */
public class OperatePresetsFragment extends android.support.v4.app.DialogFragment implements OperatePresetsContract.View {



    // Variables Declaration ButterKnife .
    private Context context;
    private View mainInflatedView;
    private RecyclerView operatePresetsMainRecyclerView;
    private TextView title_tv, done_tv;
    private OperatePresetsPresenter operatePresetsPresenter;
    private ImageView operate_presets_fragment_search_icon_IMG;
    private SearchView searchView;


    public OperatePresetsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeAndAttachePresenter();
        operatePresetsPresenter.onCreate();

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
    public void onStart() {
        super.onStart();

        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.dimAmount = 0.9f;
        setDialogPosition();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mainInflatedView = inflater.inflate(R.layout.fragment_presets_operate, container, false);
        viewInit();
        recyclerViewInit();

        onEventInit();
        return mainInflatedView;
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


    @Override
    public void viewInit() {


        done_tv = (TextView) mainInflatedView.findViewById(R.id.operate_presets_fragment_done_tv);
        operate_presets_fragment_search_icon_IMG = (ImageView) mainInflatedView.findViewById(R.id.operate_presets_fragment_search_icon_IMG);
        operate_presets_fragment_search_icon_IMG.setOnClickListener(operatePresetsPresenter);
        searchView = (SearchView) mainInflatedView.findViewById(R.id.presets_fragment_searchView);
        operatePresetsPresenter.presetsSearchEngine(searchView);

    }

    @Override
    public void initializeAndAttachePresenter() {

        operatePresetsPresenter = new OperatePresetsPresenter();
        operatePresetsPresenter.attach(this);



    }

    @Override
    public void recyclerViewInit() {
        operatePresetsMainRecyclerView = (RecyclerView) mainInflatedView.findViewById(R.id.operate_presets_fragment_RecyclerView);
        operatePresetsPresenter.presetsRecyclerViewCreation(operatePresetsMainRecyclerView);
    }

    @Override
    public void hideFragment() {
        this.dismiss();
    }

    @Override
    public void onEventInit() {
        done_tv.setOnClickListener(operatePresetsPresenter);
    }

    @Override
    public android.support.v7.widget.SearchView getSearchView() {
        return searchView;
    }

    @Override
    public Context getActivityContext() {
        return this.context;
    }

    @Nullable
    @Override
    public Context getContext() {
        return context;
    }


}
