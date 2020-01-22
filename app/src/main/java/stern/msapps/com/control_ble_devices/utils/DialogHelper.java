package stern.msapps.com.control_ble_devices.utils;

import android.app.Activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;


import android.app.AlertDialog;
import android.view.View;

import stern.msapps.com.control_ble_devices.R;
import stern.msapps.com.control_ble_devices.view.activities.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;


// Singleton Class

public class DialogHelper {

    private final String TAG = DialogHelper.class.getSimpleName();

    private int mYear, mMonth, mDay, mHour, mMinute;
    private AlertDialog dialog;

    private static DialogHelper dialogHelper;


    private DialogHelper() {

    }

    public static DialogHelper getInstance() {


        if (dialogHelper == null) {
            dialogHelper = new DialogHelper();
        }

        return dialogHelper;
    }


    public DialogHelper displayDialogEnableBluetooth(Context context) {


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getString(R.string.bluetooth_is_disabled))
                .setPositiveButton(context.getString(R.string.open_bluetooth_settings), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        context.startActivity(new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS));
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        dialog = builder.create();


        // Create the AlertDialog object and return it
        return this;


    }


    public DialogHelper displayLoaderProgressDialog(Context context, String message) {


        dialog = new ProgressDialog(context);
        //TODO remove the comment below
        dialog.setCancelable(false);
        dialog.setMessage(message);

        return this;
    }

    public DialogHelper DisplayMessageDialog(Context context, String message, String positiveBTN, String negativeBTN, OnMessageDialogButtonsClickEvents listener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage(message)
                .setPositiveButton(positiveBTN, (dialogInterface, i) -> listener.onPositiveClicked())
                .setNegativeButton(negativeBTN, (dialogInterface, i) -> {
                    listener.onNegativeClicked();
                    dialog.dismiss();
                });

        dialog = builder.create();

        return this;

    }

    public DialogHelper displayOneButtonDialog(Context context, String message, String btnMessage) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);

        builder.setMessage(message)
                .setPositiveButton(btnMessage, (dialogInterface, i) -> dialog.dismiss());


        dialog = builder.create();

        return this;

    }


    public DialogHelper BlueToothConnectionState(Context context, String message) {


        dialog = null;


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);


        dialog = builder.create();


        // Create the AlertDialog object and return it
        return this;

    }

    public DialogHelper dialogCloseTheAppOnBackPress(Context context) {

        synchronized (this) {


            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            builder.setMessage(context.getString(R.string.on_app_exit_message))
                    .setPositiveButton(context.getString(R.string.app_exit_dialog_cancel), (dialog, id) -> dialog.dismiss())
                    .setNegativeButton(context.getString(R.string.app_exit), (dialog, id) -> ((Activity) context).finish());

            dialog = builder.create();


            // Create the AlertDialog object and return it
            return this;
        }
    }


    public DialogHelper dialogCurrentDateAndTime(Context context, OnDateTimePikerListener onDateTimePikerListener) {

        synchronized (this) {
            if (dialog.isShowing()) {
                dismiss();
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogCustom);

            builder.setCancelable(false);


            final Calendar c = Calendar.getInstance();

            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, yyyy HH:mma"); //Date and time
            String currentDate = sdf.format(c.getTime());
            builder.setMessage(context.getString(R.string.validation_date_message) + "\n " + currentDate).setTitle(context.getString(R.string.validation_date_title))
                    .setPositiveButton(context.getString(R.string.YES), (dialog, id) -> {
                        dialog.dismiss();


                        if (onDateTimePikerListener != null) {
                            onDateTimePikerListener.onDateTimeReceived(c.get(Calendar.YEAR), c.get(Calendar.MONTH),
                                    c.get(Calendar.DAY_OF_MONTH),
                                    c.get(Calendar.HOUR_OF_DAY),
                                    c.get(Calendar.MINUTE), c.get(Calendar.SECOND), true);
                        }
                    })
                    .setNegativeButton(context.getString(R.string.NO), (dialog, id) -> {

                        mYear = c.get(Calendar.YEAR);
                        mMonth = c.get(Calendar.MONTH);
                        mDay = c.get(Calendar.DAY_OF_MONTH);

                        DatePickerDialog datePickerDialog = new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {

                            TimePickerDialog timePickerDialog = new TimePickerDialog(context, (view1, hourOfDay, minute) -> {
                                if (onDateTimePikerListener != null) {
                                    onDateTimePikerListener.onDateTimeReceived(year, month,
                                            dayOfMonth,
                                            hourOfDay,
                                            minute, c.get(Calendar.SECOND), false);
                                }

                            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true);
                            timePickerDialog.show();


                        }, mYear, mMonth, mDay);

                        datePickerDialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
                        datePickerDialog.show();
                    });
            dialog = builder.create();

            return this;
        }
    }

    public DialogHelper dateTimeDialog(Context context, OnDateTimePikerListener onDateTimePikerListener) {

        DatePickerDialog datePickerDialog = new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, dayOfMonth);
            DialogHelper.this.timePicker(context, onDateTimePikerListener, calendar);
        },
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
        datePickerDialog.show();



        return this;
    }

    public void timePicker(Context context, OnDateTimePikerListener onDateTimePikerListener, Calendar calendar) {

        Calendar c = null;
        if (calendar != null) {
            c = calendar;
        } else {
            c = Calendar.getInstance();
        }

        Calendar finalC = c;
        TimePickerDialog timePickerDialog = new TimePickerDialog(context, (view1, hourOfDay, minute) -> {
            if (onDateTimePikerListener != null) {
                onDateTimePikerListener.onDateTimeReceived(finalC.get(Calendar.YEAR), finalC.get(Calendar.MONTH) + 1, finalC.get(Calendar.DAY_OF_MONTH),
                        hourOfDay,
                        minute,
                        Calendar.getInstance().get(Calendar.SECOND), false);
            }

        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true);
        timePickerDialog.show();

    }

    public DialogHelper displayCustomLayoutDialog(Context context, View layout) {


        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogCustom);

        builder.setView(layout);
        builder.setCancelable(true);

        dialog = builder.create();

        return this;
    }

    //  public DialogHelper displayCustomDialogWithCounDown(Context context) {

//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//
//        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
//        View dialogView = inflater.inflate(R.layout.dialog_counter_view, null);
//
//        builder.setView(dialogView);
//
//        TextView okBtn, counter_TV, titleTV;
//        ProgressBar progressBar;
//        titleTV = (TextView) dialogView.findViewById(R.id.dialog_counter_title);
//        titleTV.setText(context.getResources().getString(R.string.scannes_product_fragment_scanning));
//        okBtn = (TextView) dialogView.findViewById(R.id.dialog_counter_btn);
//        counter_TV = (TextView) dialogView.findViewById(R.id.dialog_counter_counter_TV);
//        progressBar = (ProgressBar) dialogView.findViewById(R.id.progressBar);
//
//        okBtn.setOnClickListener(v -> {
//            dialog.dismiss();
//            BLEDeviceConnectionManager.getInstance().setOnScanCounterChangesListener(null);
//        });
//
//        BLEDeviceConnectionManager.getInstance().setOnScanCounterChangesListener(counter -> {
//            counter_TV.setText(String.valueOf(counter));
//            progressBar.setProgress(counter);
//            if(counter == 1){
//                new android.os.Handler().postDelayed(() -> dialog.dismiss(), 1000);
//            }
//        });
//
//
//        dialog = builder.create();
//
//        return this;

    //   }

    public DialogHelper displayDataReceiveError(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);

        builder.setMessage(context.getString(R.string.receiving_data_issue_title))
                .setPositiveButton(context.getString(R.string.receiving_data_issue_button), (dialog, which) -> {
                    ((MainActivity) context).getBottomNavigationView().getMenu().getItem(1).setChecked(true);
                    ((MainActivity) context).onBackPressed();
                    this.dismiss();
                });

        dialog = builder.create();
        return this;
    }


    public Dialog displayCutomDialog(Context context, int layout) {


        Dialog dialog = new Dialog(context);
        dialog.setCancelable(false);
        dialog.setContentView(layout);
        return dialog;
    }


    public boolean isDialogShown() {

        if (dialog != null) {
            return dialog.isShowing();
        } else return false;
    }

    public void show() {
        if (!dialog.isShowing()) {
            dialog.show();
        }


    }

    public void dismiss() {
        if (isDialogShown()) {
            dialog.dismiss();
        }
    }

    public interface OnDateTimePikerListener {
        void onDateTimeReceived(int year, int month, int dayOfMonth, int hour, int minutes, int seconds, boolean isYesClicked);
    }

    public interface OnMessageDialogButtonsClickEvents {
        void onPositiveClicked();

        void onNegativeClicked();
    }


}
