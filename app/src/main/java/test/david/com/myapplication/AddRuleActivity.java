package test.david.com.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class AddRuleActivity extends AppCompatActivity {

    public static final String ROW_ID = "row_id";
    private int mRowId;

    private TextView mTitle;

    public static final void start(Context context, int id) {
        Intent intent = new Intent(context, AddRuleActivity.class);
        intent.putExtra(ROW_ID, id);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_rule);
        mTitle = findViewById(R.id.id_txt);
        mRowId = getIntent().getIntExtra(ROW_ID, -1);
        if(mRowId > 0){
            mTitle.setText(getString(R.string.label_edit_rule));
        }else {
            mTitle.setText(getString(R.string.label_add_rule));
        }
        addRuleFragment();
    }

    private void addRuleFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, AddRuleFragment.getInstance(mRowId), SettingsFragmentv2.TAG).commit();
    }

}
