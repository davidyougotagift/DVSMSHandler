package test.david.com.myapplication;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static test.david.com.myapplication.Constants.TABLE_INBOX;
import static test.david.com.myapplication.Constants.TABLE_OUTBOX;

public class SMSCOntentProvider extends ContentProvider {


    private static final UriMatcher URI_MATCHER = new UriMatcher(
            UriMatcher.NO_MATCH);

    static {
        URI_MATCHER.addURI(Constants.AUTHORITY, TABLE_INBOX, Constants.ID_INBOX);
        URI_MATCHER.addURI(Constants.AUTHORITY, TABLE_OUTBOX, Constants.ID_OUTBOX);
    }

    private SMSDatabase mSmsDatabase;
    private Object mOutboxAccessLock = new Object();

    @Override
    public boolean onCreate() {
        mSmsDatabase = new SMSDatabase(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        int matchCode = URI_MATCHER.match(uri);
        Cursor cursor = null;
        switch (matchCode) {
            case Constants.ID_INBOX:
                queryBuilder.setTables(TABLE_INBOX);
                cursor = queryBuilder.query(mSmsDatabase.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case Constants.ID_OUTBOX:
                queryBuilder.setTables(TABLE_OUTBOX);
                synchronized (mOutboxAccessLock){
                    cursor = queryBuilder.query(mSmsDatabase.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
                }
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;
        }

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase sqLiteDatabase = mSmsDatabase.getWritableDatabase();
        Uri resultUri = null;
        long result = -1;
        String table = null;
        int matchCode = URI_MATCHER.match(uri);
        switch (matchCode) {
            case Constants.ID_INBOX:
                table = TABLE_INBOX;
                result = sqLiteDatabase.insert(table, null, values);
                if (result != -1) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                break;
            case Constants.ID_OUTBOX:
                table = TABLE_OUTBOX;
                synchronized (mOutboxAccessLock){
                    result = sqLiteDatabase.insert(table, null, values);
                }
                if (result != -1) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                break;
        }
        if (result != -1) {
            resultUri = Uri.parse("content://" + Constants.AUTHORITY + "/" + table + "/" + result);
        }
        return resultUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase sqLiteDatabase = mSmsDatabase.getWritableDatabase();
        int matchCode = URI_MATCHER.match(uri);
        switch (matchCode) {
            case Constants.ID_INBOX:
                sqLiteDatabase.delete(Constants.TABLE_INBOX, null, null);
                break;
            case Constants.ID_OUTBOX:
                synchronized (mOutboxAccessLock){
                    sqLiteDatabase.delete(Constants.TABLE_OUTBOX, null, null);
                }
                break;
        }
        return 0;
    }


    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase sqLiteDatabase = mSmsDatabase.getWritableDatabase();
        int updateCount = 0;
        int matchCode = URI_MATCHER.match(uri);
        switch (matchCode) {
            case Constants.ID_INBOX:
                updateCount = sqLiteDatabase.update(TABLE_INBOX, values, selection, selectionArgs);
                break;
            case Constants.ID_OUTBOX:
                synchronized (mOutboxAccessLock){
                    updateCount = sqLiteDatabase.update(TABLE_OUTBOX, values, selection, selectionArgs);
                }
                break;
        }
        if (updateCount > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return updateCount;
    }
}
