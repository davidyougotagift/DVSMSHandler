package test.david.com.myapplication;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.Telephony;
import android.support.annotation.Nullable;

public class SMSReaderService extends IntentService {

    public static final String TAG = SMSReaderService.class.getSimpleName();

    public SMSReaderService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (PreferenceData.isPreferenceSet(this)) {
            String[] projection = new String[]{Constants.SMSInboxColumns.COLUMN_ID, Telephony.TextBasedSmsColumns.ADDRESS, Telephony.TextBasedSmsColumns.BODY, Telephony.TextBasedSmsColumns.DATE};
            String selection = Telephony.TextBasedSmsColumns.DATE + " > " + PreferenceData.getLastUpdatedDate(this) + " AND "
                    + Telephony.TextBasedSmsColumns.ADDRESS + " = " + "\"" + PreferenceData.getFromNumber(this) + "\"";
            long lastUpdatedTime = System.currentTimeMillis();
            Cursor cursor = getContentResolver().query(Telephony.Sms.Inbox.CONTENT_URI,
                    projection,
                    selection,
                    null,
                    Telephony.Sms.Inbox.DEFAULT_SORT_ORDER);
            cursor.moveToFirst();
            if (cursor != null && cursor.getCount() > 0) {
                PreferenceData.setLastUpdatedDate(this, lastUpdatedTime);
            }
            while (!cursor.isAfterLast()) {
                insertToInbox(cursor);
                cursor.moveToNext();
            }
        }

    }

    private void insertToInbox(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.SMSInboxColumns.COLUMN_ID_SMS_PROVIDER, cursor.getLong(cursor.getColumnIndex(BaseColumns._ID)));
        contentValues.put(Constants.SMSInboxColumns.COLUMN_ADDRESS, cursor.getString(cursor.getColumnIndex(Telephony.TextBasedSmsColumns.ADDRESS)));
        contentValues.put(Constants.SMSInboxColumns.COLUMN_BODY, cursor.getString(cursor.getColumnIndex(Telephony.TextBasedSmsColumns.BODY)));
        contentValues.put(Constants.SMSInboxColumns.COLUMN_RECIEVED_DATE, cursor.getString(cursor.getColumnIndex(Telephony.TextBasedSmsColumns.DATE)));
        int insertCount = getContentResolver().update(Constants.CONTENT_URI_INBOX, contentValues, Constants.SMSInboxColumns.COLUMN_ID_SMS_PROVIDER + " = ? ", new String[]{id + ""});
        if (insertCount <= 0) {
            Uri insertUri = getContentResolver().insert(Constants.CONTENT_URI_INBOX, contentValues);
            contentValues = new ContentValues();
            contentValues.put(Constants.SMSOutboxColumns.COLUMN_ID_INBOX, insertUri.getLastPathSegment());
            contentValues.put(Constants.SMSOutboxColumns.COLUMN_ADDRESS_FROM, cursor.getString(cursor.getColumnIndex(Telephony.TextBasedSmsColumns.ADDRESS)));
            contentValues.put(Constants.SMSOutboxColumns.COLUMN_ADDRESS_TO, PreferenceData.getToNumber(this));
            contentValues.put(Constants.SMSOutboxColumns.COLUMN_BODY, cursor.getString(cursor.getColumnIndex(Telephony.TextBasedSmsColumns.BODY)));
            contentValues.put(Constants.SMSOutboxColumns.COLUMN_DATE_RECIEVED, cursor.getString(cursor.getColumnIndex(Telephony.TextBasedSmsColumns.DATE)));
            contentValues.put(Constants.SMSOutboxColumns.COLUMN_IS_SEND, 0);
            contentValues.put(Constants.SMSOutboxColumns.COLUMN_IS_REQUEST_IN_FLIGHT, 0);
            contentValues.put(Constants.SMSOutboxColumns.COLUMN_IS_FAILED, 0);
            getContentResolver().insert(Constants.CONTENT_URI_OUTBOX, contentValues);
            startSendService();
        }
    }

    private void startSendService() {
        Intent intent = new Intent(this, SMSSendService.class);
        startService(intent);
    }

}