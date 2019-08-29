package test.david.com.myapplication;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;

public class SMSSendResponseReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        long outboxId = intent.getLongExtra(Constants.INTENT_DATA_OUTBOX_ID, -1);
        ContentValues contentValues = new ContentValues();
        switch (getResultCode()) {
            case Activity.RESULT_OK:
                contentValues.put(Constants.SMSOutboxColumns.COLUMN_IS_SEND, 1);
                contentValues.put(Constants.SMSOutboxColumns.COLUMN_IS_FAILED, 0);
                contentValues.put(Constants.SMSOutboxColumns.COLUMN_DATE_SENT, System.currentTimeMillis());
                contentValues.put(Constants.SMSOutboxColumns.COLUMN_IS_REQUEST_IN_FLIGHT, 0);
                context.getContentResolver().update(Constants.CONTENT_URI_OUTBOX,
                        contentValues,
                        Constants.SMSOutboxColumns.COLUMN_ID + " = " + outboxId, null);
                break;
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
            case SmsManager.RESULT_ERROR_NO_SERVICE:
            case SmsManager.RESULT_ERROR_NULL_PDU:
            case SmsManager.RESULT_ERROR_RADIO_OFF:
                contentValues.put(Constants.SMSOutboxColumns.COLUMN_IS_SEND, 0);
                contentValues.put(Constants.SMSOutboxColumns.COLUMN_IS_FAILED, 1);
                contentValues.put(Constants.SMSOutboxColumns.COLUMN_DATE_LAST_FAILED, System.currentTimeMillis());
                contentValues.put(Constants.SMSOutboxColumns.COLUMN_IS_REQUEST_IN_FLIGHT, 0);
                context.getContentResolver().update(Constants.CONTENT_URI_OUTBOX,
                        contentValues,
                        Constants.SMSOutboxColumns.COLUMN_ID + " = " + outboxId, null);
                break;
        }
    }
}
