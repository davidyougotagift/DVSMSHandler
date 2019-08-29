package test.david.com.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SMSBroadcastReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, SMSReaderService.class);
        context.startService(serviceIntent);
    }
}
