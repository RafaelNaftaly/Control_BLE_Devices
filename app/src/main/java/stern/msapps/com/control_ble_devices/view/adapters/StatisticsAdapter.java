package stern.msapps.com.control_ble_devices.view.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


import stern.msapps.com.control_ble_devices.presenter.statisticsPresenter.StatisticsPresenter;
import stern.msapps.com.control_ble_devices.R;

import stern.msapps.com.control_ble_devices.model.dataTypes.SternProductStatistics;

public class StatisticsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String TAG = StatisticsAdapter.class.getCanonicalName();


    private StatisticsPresenter presenter;
    private ArrayList<SternProductStatistics> dataArr;
    private ArrayAdapter<CharSequence> spinnerAdapter;
    private boolean isMoreThanThreeMonth;

    public StatisticsAdapter(StatisticsPresenter presenter, ArrayList<SternProductStatistics> data) {
        this.presenter = presenter;
        this.dataArr = data;

        spinnerAdapter = ArrayAdapter.createFromResource(presenter.getmView().getContext(), R.array.aeratorSpinnerArray, R.layout.support_simple_spinner_dropdown_item);
        spinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        //TODO... replace with real data!!!!
        // isMoreThanThreeMonth = isMoreThanThreeMonth(1528545600000L);
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View mView;

        switch (viewType) {
            case 0:
                mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_statistics_standart_row, parent, false);
                return new ViewHolderStandart(mView);

            case 1:
                mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_statistics_button_row, parent, false);

                return new ViewHolderButton(mView);
            case 2:
                mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_statistics_spinner_row, parent, false);
                return new ViewHolderSpinner(mView);


            case 3:
                mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_statistics_date_duration_row, parent, false);
                return new ViewHolderDateDuration(mView);


        }

        return null;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType()) {
            case 0:
                ((ViewHolderStandart) holder).bindView(dataArr.get(position));
                break;

            case 1:
                ((ViewHolderButton) holder).bindView(dataArr.get(position));
                if (dataArr.get(position).getType() == SternProductStatistics.StatisticTypes.LAST_FILTER_CLEANED) {
                    if (isMoreThanThreeMonth(dataArr.get(position).getRawValue())) {
                        ((ViewHolderButton) holder).cleanFilter.setVisibility(View.VISIBLE);
                    }
                }

                ((ViewHolderButton) holder).reset.setOnClickListener(v -> presenter.onResetStatisticClicked(dataArr.get(position).getType()));
                break;

            case 2:
                ((ViewHolderSpinner) holder).bindView(dataArr.get(position));
                ((ViewHolderSpinner) holder).aeratorSpinner.setTag(position);
                ((ViewHolderSpinner) holder).aeratorSpinner.setAdapter(spinnerAdapter);
                ((ViewHolderSpinner) holder).aeratorSpinner.setSelection(spinnerAdapter.getCount() - 1);
                SpinnerOnClick(((ViewHolderSpinner) holder).aeratorSpinner, position, ((ViewHolderSpinner) holder));


                break;

            case 3:
                ((ViewHolderDateDuration) holder).bindView(dataArr.get(position));
                break;

        }

    }


    @Override
    public int getItemCount() {
        return dataArr.size();
    }


    @Override
    public int getItemViewType(int position) {
        switch (dataArr.get(position).getType()) {
            case TIME_SINCE_LAST_POWERED:
            case BATTERY_STATE:
            case HALF_FLUSH_PERCENTAGE:
            case FULL_FLUSH_PERCENTAGE:
            case NUMBER_OF_ACTIVATIONS:
                return 0;

            case LAST_FILTER_CLEANED:
            case LAST_SOAP_REFILLED:
                return 1;

            case AVERAGE_OPEN_TIME:
                return 2;

            case LAST_HYGIENE_FLUSH:
            case NEXT_HYGIENE_FLUSH:
            case LAST_STANDBY:
            case NEXT_STANDBY:
                return 3;

            default:
                return 0;

        }
    }


    public class ViewHolderStandart extends RecyclerView.ViewHolder {

        TextView title;
        TextView content;


        public ViewHolderStandart(android.view.View itemView) {
            super(itemView);


            title = (TextView) itemView.findViewById(R.id.single_statistics_standart_row_title);
            content = (TextView) itemView.findViewById(R.id.single_statistics_standart_row_data);


        }

        private void bindView(SternProductStatistics statistics) {

            if (statistics.getParsedStatisticsTypeStringValue().contains("%")) {
                Log.d("", "");
            }

            this.title.setText(statistics.getType().name);
            this.content.setText(statistics.getParsedStatisticsTypeStringValue());

        }
    }

    public class ViewHolderDateDuration extends ViewHolderStandart {

        TextView duration;

        public ViewHolderDateDuration(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.single_statistics_date_duration_row_title);
            content = (TextView) itemView.findViewById(R.id.single_statistics_date_duration_row_date_value);
            duration = (TextView) itemView.findViewById(R.id.single_statistics_date_duration_row_duration_value);


        }


        private void bindView(SternProductStatistics statistics) {

            this.title.setText(statistics.getType().name);
            this.content.setText(statistics.getParsedStatisticsTypeStringValue());

            if (statistics.getType().equals(SternProductStatistics.StatisticTypes.LAST_HYGIENE_FLUSH) ||
                    statistics.getType().equals(SternProductStatistics.StatisticTypes.NEXT_HYGIENE_FLUSH)) {

                this.duration.setText(String.valueOf(statistics.getDuration()) + " " + content.getResources().getString(R.string.statistics_seconds));
            } else {
                this.duration.setText(String.valueOf(statistics.getDuration()) + " " + content.getResources().getString(R.string.statistics_hours));
            }

        }
    }

    public class ViewHolderButton extends ViewHolderStandart {

        TextView reset, cleanFilter;

        public ViewHolderButton(android.view.View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.single_statistics_button_row_title);
            content = (TextView) itemView.findViewById(R.id.single_statistics_button_row_data);
            reset = (TextView) itemView.findViewById(R.id.single_statistics_button_row_reset_BTN);
            cleanFilter = (TextView) itemView.findViewById(R.id.single_statistics_button_row_last_filer_clean);


        }

        private void bindView(SternProductStatistics statistics) {

            this.title.setText(statistics.getType().name);
            this.content.setText(statistics.getParsedStatisticsTypeStringValue());

        }
    }

    public class ViewHolderSpinner extends ViewHolderStandart {

        Spinner aeratorSpinner;
        TextView single_statistics_spinner_row_title_value;

        public ViewHolderSpinner(android.view.View itemView) {
            super(itemView);
            this.title = (TextView) itemView.findViewById(R.id.single_statistics_spinner_row_title);
            this.content = (TextView) itemView.findViewById(R.id.single_statistics_spinner_row_dat);
            this.aeratorSpinner = (Spinner) itemView.findViewById(R.id.single_statistics_spinner_row_spinner);
            this.single_statistics_spinner_row_title_value = (TextView) itemView.findViewById(R.id.single_statistics_spinner_row_title_value);


        }

        private void bindView(SternProductStatistics statistics) {

            this.title.setText(statistics.getType().name);
            // this.content.setText(statistics.getParsedStatisticsTypeStringValue());
            single_statistics_spinner_row_title_value.setText(String.valueOf(statistics.getRawValue()) + " " + presenter.getmView().getContext().getResources().getString(R.string.statistics_seconds));


        }

        private void setAeratorText(String str) {
            this.content.setText(str);
        }
    }

    private void SpinnerOnClick(Spinner spinner, int arrPosition, ViewHolderSpinner holderSpinner) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                Log.d(TAG, "onItemSelected()");

                long rawVal = dataArr.get(arrPosition).getRawValue();

                float aeratorCalculated = 0;

                String aeratorStr = "";

                // int idTag = (int) ((Spinner) view.getParent()).getTag();

                switch (position) {
                    case 0:

                        aeratorCalculated = ((float) ((double) 1.3 / (double) 60) * rawVal);
                        break;
                    case 1:

                        aeratorCalculated = ((float) ((double) 1.89 / (double) 60) * rawVal);
                        break;
                    case 2:

                        aeratorCalculated = ((float) ((double) 3 / (double) 60) * rawVal);
                        break;
                    case 3:

                        aeratorCalculated = ((float) ((double) 4 / (double) 60) * rawVal);
                        break;
                    case 4:

                        aeratorCalculated = ((float) ((double) 6 / (double) 60) * rawVal);
                        break;
                }

                Log.d(TAG, aeratorStr);

                aeratorStr = String.valueOf(new DecimalFormat("##.##").format(aeratorCalculated)) + " " + presenter.getmView().getContext().getResources().getString(R.string.statistics_lpc);

//                if (aeratorCalculated < 60) {
//                    aeratorStr = String.valueOf(new DecimalFormat("##.##").format(aeratorCalculated)) + " " + "Seconds";
//                } else if (aeratorCalculated < 3600) {
//                    if ((aeratorCalculated % 60) == 0) {
//                        aeratorStr = String.valueOf(new DecimalFormat("##.##").format(aeratorCalculated)) + " " + "Minutes";
//                    } else {
//                        double sec = aeratorCalculated % 60;
//
//                        aeratorStr = String.valueOf((int) aeratorCalculated / 60) + " " + "Minutes" + " "
//                                + String.valueOf(sec) + " " + "Seconds";
//                    }
//                } else if (aeratorCalculated < 86400) {
//
//                }


                holderSpinner.setAeratorText(aeratorStr);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private boolean isMoreThanThreeMonth(long data) {

        long threeMonth = 7862400000L;

        Calendar currentDate = Calendar.getInstance();

        Date date = new Date(data);


        Calendar deviceCAlendar = Calendar.getInstance();
        deviceCAlendar.setTime(date);


        if ((currentDate.getTimeInMillis() - deviceCAlendar.getTimeInMillis()) >= threeMonth) {

            return true;
        }


        return false;
    }


}
