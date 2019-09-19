package test.david.com.myapplication;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.BaseColumns;
import android.provider.Telephony;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SMSReaderService extends IntentService {

    public static final String TAG = SMSReaderService.class.getSimpleName();

    private List<Rule> mRules;


    public SMSReaderService() {
        super(TAG);
        mRules = new ArrayList<>();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        mRules.clear();
        Cursor rulesCursor = getContentResolver().query(Constants.CONTENT_URI_RULES, null, null, null, null);
        rulesCursor.moveToFirst();
        while (!rulesCursor.isAfterLast()) {
            String from = rulesCursor.getString(rulesCursor.getColumnIndex(Constants.RulesColumns.COLUMN_FROM_NUMBER));
            String to = rulesCursor.getString(rulesCursor.getColumnIndex(Constants.RulesColumns.COLUMN_TO_NUMBER));
            String messageFormat = rulesCursor.getString(rulesCursor.getColumnIndex(Constants.RulesColumns.COLUMN_MESSAGE_FORMAT));
            rulesCursor.moveToNext();
            mRules.add(new Rule(from, to, messageFormat));
        }
        if (!mRules.isEmpty()) {
            if (Utils.checkForUsagePermission(this)) {
                cleanUpOutbox();
                String[] projection = new String[]{Constants.SMSInboxColumns.COLUMN_ID, Telephony.TextBasedSmsColumns.ADDRESS, Telephony.TextBasedSmsColumns.BODY, Telephony.TextBasedSmsColumns.DATE};
                String selection = Telephony.TextBasedSmsColumns.DATE + " > " + PreferenceData.getLastUpdatedDate(this); /*+ " AND "
                        + Telephony.TextBasedSmsColumns.ADDRESS + " = " + "\""
                        + PreferenceData.getFromNumber(this) + "\""
                        + " AND " + Telephony.TextBasedSmsColumns.BODY + " LIKE "
                        + "\"" + PreferenceData.getMessageFormat(this) + "\"";*/
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
            if (lastAttemptedDate + (1000 * 60 * 2) <= currentTime) {
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


    private void insertToInbox(Cursor cursor) {
        for (Rule rule : mRules) {
            long id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
            String fromAddress = cursor.getString(cursor.getColumnIndex(Telephony.TextBasedSmsColumns.ADDRESS));
            String body = cursor.getString(cursor.getColumnIndex(Telephony.TextBasedSmsColumns.BODY));
            if (fromAddress.equals(rule.mFrom) && like(body, rule.mFormat)) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(Constants.SMSInboxColumns.COLUMN_ID_SMS_PROVIDER, id);
                contentValues.put(Constants.SMSInboxColumns.COLUMN_ADDRESS, fromAddress);
                contentValues.put(Constants.SMSInboxColumns.COLUMN_BODY, body);
                contentValues.put(Constants.SMSInboxColumns.COLUMN_RECIEVED_DATE, cursor.getString(cursor.getColumnIndex(Telephony.TextBasedSmsColumns.DATE)));
                int insertCount = getContentResolver().update(Constants.CONTENT_URI_INBOX, contentValues, Constants.SMSInboxColumns.COLUMN_ID_SMS_PROVIDER + " = ? ", new String[]{id + ""});
                if (insertCount <= 0) {
                    Uri insertUri = getContentResolver().insert(Constants.CONTENT_URI_INBOX, contentValues);
                    contentValues = new ContentValues();
                    contentValues.put(Constants.SMSOutboxColumns.COLUMN_ID_INBOX, insertUri.getLastPathSegment());
                    contentValues.put(Constants.SMSOutboxColumns.COLUMN_ADDRESS_FROM, fromAddress);
                    contentValues.put(Constants.SMSOutboxColumns.COLUMN_ADDRESS_TO, rule.mTo);
                    contentValues.put(Constants.SMSOutboxColumns.COLUMN_BODY, body);
                    contentValues.put(Constants.SMSOutboxColumns.COLUMN_DATE_RECIEVED, cursor.getString(cursor.getColumnIndex(Telephony.TextBasedSmsColumns.DATE)));
                    contentValues.put(Constants.SMSOutboxColumns.COLUMN_IS_SEND, 0);
                    contentValues.put(Constants.SMSOutboxColumns.COLUMN_IS_REQUEST_IN_FLIGHT, 0);
                    contentValues.put(Constants.SMSOutboxColumns.COLUMN_IS_FAILED, 0);
                    contentValues.put(Constants.SMSOutboxColumns.COLUMN_ID_SMS_PROVIDER, id);
                    getContentResolver().insert(Constants.CONTENT_URI_OUTBOX, contentValues);
                }

            }
        }
    }

    private void startSendService() {
        Intent intent = new Intent(this, SMSSendService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }

    public static boolean like(String str, String expr) {
        str = str.replace("\n","");
        expr = expr.toLowerCase();
        expr = expr.replace("\n","");
        expr = expr.replace(".", "\\.");
        expr = expr.replace("?", ".");
        expr = expr.replace("%", ".*");
        str = str.toLowerCase();
        return str.matches(expr);
    }


    private static class Rule {

        private String mFrom;
        private String mTo;
        private String mFormat;

        Rule(String from, String to, String format) {
            mFrom = from;
            mTo = to;
            mFormat = format;
        }

    }

}