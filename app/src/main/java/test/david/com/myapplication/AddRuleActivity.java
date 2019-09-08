package test.david.com.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class AddRuleActivity extends AppCompatActivity {

    public static final String ROW_ID = "row_id";
    private int mRowId;


    public static final void start(Context context, int id) {
        Intent intent = new Intent(context, AddRuleActivity.class);
        intent.putExtra(ROW_ID, id);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_rule);
        mRowId = getIntent().getIntExtra(ROW_ID, -1);
        addRuleFragment();
    }

    private void addRuleFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, AddRuleFragment.getInstance(mRowId), SettingsFragmentv2.TAG).commit();
    }

}
