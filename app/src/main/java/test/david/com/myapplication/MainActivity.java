package test.david.com.myapplication;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mTitle;
    private static final int REQUEST_CODE_SMS_READ = 11;
    private TextView mAddRule;

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
        mAddRule.setVisibility(View.GONE);
        startCopyService();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, InboxFragment.getInstance(), InboxFragment.TAG).commit();

    }

    private void addOutboxFragment() {
        mAddRule.setVisibility(View.GONE);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, OutboxFragment.getInstance(), OutboxFragment.TAG).commit();

    }

    private void addSettingsFragment() {
        mAddRule.setVisibility(View.VISIBLE);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, SettingsFragmentv2.getInstance(), SettingsFragmentv2.TAG).commit();
    }

    private void addDraftsFragment() {
        mAddRule.setVisibility(View.GONE);
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
        mAddRule = findViewById(R.id.id_add_rule);
        mAddRule.setVisibility(View.GONE);
        mAddRule.setOnClickListener(this);
        setToolbar();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mTitle.setText(R.string.title_home);
        if (hasSMSReadPermission()) {
            addInboxFragment();
        }
        if (!Utils.checkForUsagePermission(this)) {
            Utils.showErrorDialog(this, "Free usage limit is exceeded. Update to Premium version."
                    , "Error"
                    , new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    });
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.id_add_rule:
                Fragment fragment = getSupportFragmentManager().findFragmentByTag(SettingsFragmentv2.TAG);
                if (fragment != null && fragment instanceof SettingsFragmentv2) {
                    ((SettingsFragmentv2) fragment).addRule();
                }
                break;
        }
    }
}
