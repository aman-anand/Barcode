package android.rn.com.barcode.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.rn.com.barcode.R;
import android.support.v7.app.AlertDialog;




/**
 * Created by DELL on 9/25/2017.
 */

public class AlertBuilder {
    private static AlertBuilder alertBuilder;

    private AlertBuilder() {
    }

    public static AlertBuilder getInstance() {
        if (alertBuilder == null) {
            alertBuilder = new AlertBuilder();
        }
        return alertBuilder;
    }


    /**
     * displays an alert dialog
     *
     * @param context - context of the calling activity
     * @param message - message to be displayed
     * @param title   - 0 for success, 1 for failure
     */
    public void getDialog(final Activity context, String message, int title) {
        context.setProgressBarVisibility(false);
        AlertDialog alertDialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        switch (title) {
            case 0:
                builder.setTitle(context.getString(R.string.alert_success));
                break;
            case 1:
                builder.setTitle(context.getString(R.string.alert_stop));
                break;
            default:
                builder.setTitle(context.getString(R.string.alert));
                break;
        }

        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(context.getResources().getString(R.string.alert_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.show();


    }

    /**
     * @param context- of the calling activity
     * @param message- message to be shown
     * @param title-   0 for success and 1 for failure
     * @param action   - various actions to be performed
     */
    public void getDialog(final Activity context, String message, int title, int action) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        switch (title) {
            case 0:
                builder.setTitle(context.getString(R.string.alert_success));
                break;
            case 1:
                builder.setTitle(context.getString(R.string.alert_stop));
                break;
            default:
                builder.setTitle(context.getString(R.string.alert));
                break;
        }

        builder.setMessage(message);


        switch (action) {
            case 1:

                builder.setPositiveButton(context.getString(R.string.alert_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        context.finish();
                    }
                });
                break;
            case 3:
                builder.setPositiveButton(context.getString(R.string.alert_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
            case 2:
                builder.setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.setNeutralButton(context.getString(R.string.No), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        context.finish();
                    }
                });
            default:
                break;
        }
        builder.show();


    }

}