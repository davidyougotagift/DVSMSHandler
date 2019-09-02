package test.david.com.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

public class PreferenceData {

    private static final String PREF_NAME = "to_from_numbers";
    private static final String KEY_TO_MOBILE = "to_mobile";
    private static final String KEY_FROM_MOBILE = "from_mobile";
    private static final String KEY_MESSAGE_FORMAT = "message_format";
    private static final String KEY_TIME = "time_stamp";
    private static final String KEY_LAST_UPDATED_DATE = "last_updated_time_stamp";


    public static final void setData(Context context, String toMobile, String fromMobile, long time, String messageFormat) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(KEY_TO_MOBILE, toMobile);
        editor.putString(KEY_FROM_MOBILE, fromMobile);
        editor.putLong(KEY_TIME, time);
        editor.putString(KEY_MESSAGE_FORMAT, messageFormat);
        editor.commit();
    }


    public static long getLastUpdatedDate(Context context) {
        return getPreferences(context).getLong(KEY_LAST_UPDATED_DATE, getSavedTimeStamp(context));
    }

    public static boolean isPreferenceSet(Context context) {
        return TextUtils.isEmpty(getFromNumber(context)) ? false : true;
    }

    public static String getMessageFormat(Context context) {
        return getPreferences(context).getString(KEY_MESSAGE_FORMAT, "");
    }

    public static void setLastUpdatedDate(Context context, long timeStamp) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putLong(KEY_LAST_UPDATED_DATE, timeStamp);
        editor.commit();
    }

    public static String getToNumber(Context context) {
        return getPreferences(context).getString(KEY_TO_MOBILE, "");
    }


    public static String getFromNumber(Context context) {
        return getPreferences(context).getString(KEY_FROM_MOBILE, "");
    }

    public static long getSavedTimeStamp(Context context) {
        return getPreferences(context).getLong(KEY_TIME, 0);
    }


    private static SharedPreferences getPreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences;
    }

}
