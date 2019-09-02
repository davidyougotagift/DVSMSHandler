package test.david.com.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

public class Utils {

    public static final void showErrorDialog(Context context, String error, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(error);
        builder.setCancelable(false);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    public static final void showErrorDialog(Context context, String error, String title, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(error);
        builder.setCancelable(false);
        builder.setPositiveButton("Ok", listener);
        builder.create().show();
    }


    public static final void showOKCancelDialog(Context context, String message, DialogInterface.OnClickListener okListenr, DialogInterface.OnClickListener cancelListenr) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Alert");
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("Ok", okListenr);
        builder.setNegativeButton("Cancel", cancelListenr);
        builder.create().show();
    }


    public static final void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static boolean checkForUsagePermission(Context context) {
        Cursor cursor = context.getContentResolver().query(Constants.CONTENT_URI_INBOX, new String[]{Constants.SMSInboxColumns.COLUMN_ID}
                , null, null, null);
        boolean allow = (cursor != null && cursor.getCount() > 30) ? false : true;
        cursor.close();
        return allow;
    }

}
