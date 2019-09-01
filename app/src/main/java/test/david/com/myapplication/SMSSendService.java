package test.david.com.myapplication;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;

public class SMSSendService extends IntentService {


    public static final String TAG = SMSSendService.class.getSimpleName();

    public SMSSendService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String selection = Constants.SMSOutboxColumns.COLUMN_IS_SEND + " = " + 0
                + " AND " + Constants.SMSOutboxColumns.COLUMN_IS_FAILED + " = " + 0
                + " AND " + Constants.SMSOutboxColumns.COLUMN_IS_REQUEST_IN_FLIGHT + " = " + 0;
        Cursor cursor = getContentResolver().query(Constants.CONTENT_URI_OUTBOX, null, selection, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            SmsManager sms = SmsManager.getDefault();
            long outboxID = cursor.getLong(cursor.getColumnIndex(Constants.SMSOutboxColumns.COLUMN_ID));
            markAsInFlight(outboxID);
            sms.sendTextMessage(cursor.getString(cursor.getColumnIndex(Constants.SMSOutboxColumns.COLUMN_ADDRESS_TO))
                    , null
                    , cursor.getString(cursor.getColumnIndex(Constants.SMSOutboxColumns.COLUMN_BODY))
                    , getSentPendingIntent(outboxID)
                    , getDeliveryPendingIntent(outboxID)
            );
            cursor.moveToNext();
        }

    }

    private void markAsInFlight(long outboxID) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.SMSOutboxColumns.COLUMN_IS_SEND, 0);
        contentValues.put(Constants.SMSOutboxColumns.COLUMN_IS_FAILED, 0);
        contentValues.put(Constants.SMSOutboxColumns.COLUMN_IS_REQUEST_IN_FLIGHT, 1);
        contentValues.put(Constants.SMSOutboxColumns.COLUMN_DATE_LAST_ATTEMPT_SEND, System.currentTimeMillis());
        getContentResolver().update(Constants.CONTENT_URI_OUTBOX
                , contentValues, Constants.SMSOutboxColumns.COLUMN_ID + " = " + outboxID, null);
    }


    private PendingIntent getSentPendingIntent(long outboxId) {
        Intent intent = new Intent(this, SMSSendResponseReciever.class);
        intent.setAction("test.david.com.INTENT_ACTION_SENT_RESPOSNE");
        intent.putExtra(Constants.INTENT_DATA_OUTBOX_ID, outboxId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (int) outboxId, intent, 0);
        return pendingIntent;
    }

    private PendingIntent getDeliveryPendingIntent(long outboxId) {
        Intent intent = new Intent(this, SMSDeliveredResponseReciever.class);
        intent.setAction("test.david.com.INTENT_ACTION_DELIVERED_RESPOSNE");
        intent.putExtra(Constants.INTENT_DATA_OUTBOX_ID, outboxId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (int) outboxId, intent, 0);
        return pendingIntent;
    }


}