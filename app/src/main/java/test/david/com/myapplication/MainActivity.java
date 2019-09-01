package test.david.com.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private TextView mTitle;
    private static final int REQUEST_CODE_SMS_READ = 11;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.inbox:
                    mTitle.setText(R.string.title_home);
                    addInboxFragment();
                    return true;
                case R.id.outbox:
                    mTitle.setText(R.string.title_dashboard);
                    addOutboxFragment();
                    return true;
                case R.id.dratfs:
                    mTitle.setText(R.string.title_notifications);
                    addDraftsFragment();
                    return true;
                case R.id.settings:
                    mTitle.setText(R.string.title_settings);
                    addSettingsFragment();
                    return true;
            }
            return false;
        }
    };

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void addInboxFragment() {
        startCopyService();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, InboxFragment.getInstance(), InboxFragment.TAG).commit();

    }

    private void addOutboxFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, OutboxFragment.getInstance(), OutboxFragment.TAG).commit();

    }

    private void addSettingsFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, SettingsFragment.getInstance(), SettingsFragment.TAG).commit();
    }

    private void addDraftsFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, DraftsFragment.getInstance(), DraftsFragment.TAG).commit();

    }


    private void startCopyService() {
        Intent intent = new Intent(this, SMSReaderService.class);
        startService(intent);
    }

    private boolean hasSMSReadPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_SMS)
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECEIVE_SMS)
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
                Toast.makeText(this, "Enable Permissions from Device Settings", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS},
                        REQUEST_CODE_SMS_READ);
            }
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_SMS_READ: {
                if (grantResults.length > 2
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    addInboxFragment();
                } else {
                    Toast.makeText(this, "Enable Permission", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTitle = findViewById(R.id.id_txt);
        setToolbar();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mTitle.setText(R.string.title_home);
        if (hasSMSReadPermission()) {
            addInboxFragment();
        }
    }

}
