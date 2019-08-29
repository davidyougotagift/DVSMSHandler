package test.david.com.myapplication;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SMSDeliveredResponseReciever extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        switch (getResultCode()) {
            case Activity.RESULT_OK:
                break;
            case Activity.RESULT_CANCELED:
                break;
        }
    }
}
