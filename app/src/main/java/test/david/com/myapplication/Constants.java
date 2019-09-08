package test.david.com.myapplication;

import android.net.Uri;

public class Constants {

    public static final String AUTHORITY = "com.smsreader.provider";

    public static final String TABLE_INBOX = "sms_inbox";
    public static final String TABLE_OUTBOX = "sms_outbox";
    public static final String TABLE_RULES = "rules";

    public static final Uri CONTENT_URI_INBOX = Uri.parse("content://" + AUTHORITY + "/" + TABLE_INBOX);
    public static final Uri CONTENT_URI_OUTBOX = Uri.parse("content://" + AUTHORITY + "/" + TABLE_OUTBOX);
    public static final Uri CONTENT_URI_RULES = Uri.parse("content://" + AUTHORITY + "/" + TABLE_RULES);
    public static final int ID_INBOX = 12;
    public static final int ID_OUTBOX = 13;
    public static final int ID_RULES = 14;

    public static final String INTENT_DATA_OUTBOX_ID = "outbox_id";


    public static final String QUERY_CREATE_TABLE_INBOX = "create table " + TABLE_INBOX + "("
            + SMSInboxColumns.COLUMN_ID + " integer primary key autoincrement, "
            + SMSInboxColumns.COLUMN_ID_SMS_PROVIDER + " integer, "
            + SMSInboxColumns.COLUMN_ADDRESS + " text, "
            + SMSInboxColumns.COLUMN_BODY + " text, "
            + SMSInboxColumns.COLUMN_RECIEVED_DATE + " text )";

    public static final String QUERY_DROP_TABLE_INBOX = "DROP TABLE IF EXISTS " + TABLE_INBOX;


    public static final String QUERY_CREATE_TABLE_OUTBOX = "create table " + TABLE_OUTBOX + "(" + SMSOutboxColumns.COLUMN_ID + " integer primary key autoincrement, "
            + SMSOutboxColumns.COLUMN_ID_SMS_PROVIDER + " integer, "
            + SMSOutboxColumns.COLUMN_ID_INBOX + " integer, "
            + SMSOutboxColumns.COLUMN_ADDRESS_FROM + " text, "
            + SMSOutboxColumns.COLUMN_ADDRESS_TO + " text, "
            + SMSOutboxColumns.COLUMN_BODY + " text, "
            + SMSOutboxColumns.COLUMN_DATE_RECIEVED + " integer, "
            + SMSOutboxColumns.COLUMN_IS_SEND + " integer, "
            + SMSOutboxColumns.COLUMN_DATE_SENT + " integer, "
            + SMSOutboxColumns.COLUMN_RESPONSE_CODE_SEND + " integer, "
            + SMSOutboxColumns.COLUMN_DATE_LAST_ATTEMPT_SEND + " integer, "
            + SMSOutboxColumns.COLUMN_IS_FAILED + " integer, "
            + SMSOutboxColumns.COLUMN_RESPONSE_CODE_FAILURE + " integer, "
            + SMSOutboxColumns.COLUMN_DATE_LAST_FAILED + " integer, "
            + SMSOutboxColumns.COLUMN_IS_REQUEST_IN_FLIGHT + " integer )";

    public static final String QUERY_DROP_TABLE_OUTBOX = "DROP TABLE IF EXISTS " + TABLE_OUTBOX;

    public static final String QUERY_CREATE_TABLE_RULES = "create table " + TABLE_RULES + "(" + RulesColumns.COLUMN_ID + " integer primary key autoincrement, "
            + RulesColumns.COLUMN_FROM_NUMBER + " text, "
            + RulesColumns.COLUMN_MESSAGE_FORMAT + " text, "
            + RulesColumns.COLUMN_TO_NUMBER + " text )";

    public static final String QUERY_DROP_TABLE_RULES = "DROP TABLE IF EXISTS " + TABLE_RULES;


    public static class SMSInboxColumns {
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_ID_SMS_PROVIDER = "column_id_sms_provider";
        public static final String COLUMN_ADDRESS = "address";
        public static final String COLUMN_BODY = "body";
        public static final String COLUMN_RECIEVED_DATE = "date";
    }


    public static class SMSOutboxColumns {
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_ID_SMS_PROVIDER = "column_id_sms_provider";
        public static final String COLUMN_ID_INBOX = "column_id_inbox";
        public static final String COLUMN_ADDRESS_FROM = "from_address";
        public static final String COLUMN_ADDRESS_TO = "to_address";
        public static final String COLUMN_BODY = "body";
        public static final String COLUMN_DATE_RECIEVED = "date_received";
        public static final String COLUMN_IS_SEND = "is_send";
        public static final String COLUMN_DATE_SENT = "date_send";
        public static final String COLUMN_RESPONSE_CODE_SEND = "response_code_send";
        public static final String COLUMN_DATE_LAST_ATTEMPT_SEND = "date_last_sent_attempt";
        public static final String COLUMN_IS_FAILED = "is_failed";
        public static final String COLUMN_RESPONSE_CODE_FAILURE = "response_code_failure";
        public static final String COLUMN_DATE_LAST_FAILED = "date_last_failed";
        public static final String COLUMN_IS_REQUEST_IN_FLIGHT = "is_in_flight";

    }


    public static class RulesColumns {
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_FROM_NUMBER = "from_number";
        public static final String COLUMN_MESSAGE_FORMAT = "message_format";
        public static final String COLUMN_TO_NUMBER = "to_number";
    }


}
