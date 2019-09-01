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
            cleanUpOutbox();
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
            startSendService();
        }

    }

    private void cleanUpOutbox() {
        String[] projection = new String[]{Constants.SMSOutboxColumns.COLUMN_ID, Constants.SMSOutboxColumns.COLUMN_DATE_LAST_ATTEMPT_SEND};
        String selection = Constants.SMSOutboxColumns.COLUMN_IS_SEND + " = 0 AND "
                + Constants.SMSOutboxColumns.COLUMN_IS_REQUEST_IN_FLIGHT + " = 1";
        Cursor cursor = getContentResolver().query(Constants.CONTENT_URI_OUTBOX, projection, selection, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            long lastAttemptedDate = cursor.getLong(cursor.getColumnIndex(Constants.SMSOutboxColumns.COLUMN_DATE_LAST_ATTEMPT_SEND));
            long currentTime = System.currentTimeMillis();
            int id = cursor.getInt(cursor.getColumnIndex(Constants.SMSOutboxColumns.COLUMN_ID));
            if(lastAttemptedDate + (1000 * 60 * 2) <= currentTime){
                ContentValues contentValues = new ContentValues();
                contentValues.put(Constants.SMSOutboxColumns.COLUMN_IS_SEND, 0);
                contentValues.put(Constants.SMSOutboxColumns.COLUMN_IS_FAILED, 1);
                contentValues.put(Constants.SMSOutboxColumns.COLUMN_IS_REQUEST_IN_FLIGHT, 0);
                getContentResolver().update(Constants.CONTENT_URI_OUTBOX, contentValues,
                        Constants.SMSOutboxColumns.COLUMN_ID_SMS_PROVIDER + " = " + id, null);
            }
            cursor.moveToNext();
        }
        cursor.close();
    }

    /*private void cleanUpOutbox() {
        List<Integer> inFLightRequestIds = new ArrayList<>();
        String[] projection = new String[]{Constants.SMSOutboxColumns.COLUMN_ID_SMS_PROVIDER};
        String selection = Constants.SMSOutboxColumns.COLUMN_IS_SEND + " = 0 AND "
                + Constants.SMSOutboxColumns.COLUMN_IS_REQUEST_IN_FLIGHT + " = 1";
        Cursor cursor = getContentResolver().query(Constants.CONTENT_URI_OUTBOX, projection, selection, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            inFLightRequestIds.add(cursor.getInt(cursor.getColumnIndex(Constants.SMSOutboxColumns.COLUMN_ID_SMS_PROVIDER)));
            cursor.moveToNext();
        }
        cursor.close();
        //projection = new String[]{Telephony.Sms.STATUS, Telephony.Sms.DATE_SENT};
        selection = BaseColumns._ID + " = ?";
        for (int i = 0; i < inFLightRequestIds.size(); i++) {
            cursor = getContentResolver().query(Telephony.Sms.CONTENT_URI, null, selection, new String[]{Integer.toString(inFLightRequestIds.get(i))}, null);
            cursor.moveToFirst();
            int status = cursor.getInt(cursor.getColumnIndex(Telephony.Sms.STATUS));
            long dateSent = cursor.getLong(cursor.getColumnIndex(Telephony.Sms.DATE_SENT));
            ContentValues contentValues = new ContentValues();
            if (status == Telephony.Sms.STATUS_COMPLETE) {
                contentValues.put(Constants.SMSOutboxColumns.COLUMN_IS_SEND, 1);
                contentValues.put(Constants.SMSOutboxColumns.COLUMN_IS_FAILED, 0);
                contentValues.put(Constants.SMSOutboxColumns.COLUMN_DATE_SENT, dateSent);
                contentValues.put(Constants.SMSOutboxColumns.COLUMN_IS_REQUEST_IN_FLIGHT, 0);
                getContentResolver().update(Constants.CONTENT_URI_OUTBOX, contentValues,
                        Constants.SMSOutboxColumns.COLUMN_ID_SMS_PROVIDER + " = " + inFLightRequestIds.get(i), null);
            } else if (status == Telephony.Sms.STATUS_FAILED) {
                contentValues.put(Constants.SMSOutboxColumns.COLUMN_IS_SEND, 0);
                contentValues.put(Constants.SMSOutboxColumns.COLUMN_IS_FAILED, 1);
                contentValues.put(Constants.SMSOutboxColumns.COLUMN_IS_REQUEST_IN_FLIGHT, 0);
                getContentResolver().update(Constants.CONTENT_URI_OUTBOX, contentValues,
                        Constants.SMSOutboxColumns.COLUMN_ID_SMS_PROVIDER + " = " + inFLightRequestIds.get(i), null);

            } else if (status == Telephony.Sms.STATUS_PENDING) {

            }
            cursor.close();
        }
    }
*/

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
            contentValues.put(Constants.SMSOutboxColumns.COLUMN_ID_SMS_PROVIDER, cursor.getLong(cursor.getColumnIndex(BaseColumns._ID)));
            getContentResolver().insert(Constants.CONTENT_URI_OUTBOX, contentValues);
        }
    }

    private void startSendService() {
        Intent intent = new Intent(this, SMSSendService.class);
        startService(intent);
    }

}