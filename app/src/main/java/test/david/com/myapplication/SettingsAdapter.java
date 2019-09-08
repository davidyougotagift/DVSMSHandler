package test.david.com.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.SettingsViewHolder> implements RuleItemClickListener{

    static final String TO = "To : ";
    static final String FROM = "From : ";
    static final String FORMAT = "Message Format : ";

    private Context mContext;
    private Cursor mCursor;
    private RuleItemClickListener mRuleItemClickListener;

    public SettingsAdapter(Context context, RuleItemClickListener ruleItemClickListener) {
        mContext = context;
        mRuleItemClickListener = ruleItemClickListener;
    }

    public void setCursor(Cursor mCursor) {
        this.mCursor = mCursor;
    }

    @NonNull
    @Override
    public SettingsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new SettingsViewHolder(LayoutInflater.from(mContext).inflate(R.layout.rules_item, null), this);
    }

    @Override
    public void onBindViewHolder(@NonNull SettingsViewHolder settingsViewHolder, int i) {
        if (mCursor != null && !mCursor.isClosed()) {
            mCursor.moveToPosition(i);
            String from = FROM + mCursor.getString(mCursor.getColumnIndex(Constants.RulesColumns.COLUMN_FROM_NUMBER));
            String to = TO + mCursor.getString(mCursor.getColumnIndex(Constants.RulesColumns.COLUMN_TO_NUMBER));
            String messageFormat = FORMAT + mCursor.getString(mCursor.getColumnIndex(Constants.RulesColumns.COLUMN_MESSAGE_FORMAT));
            int roID = mCursor.getInt(mCursor.getColumnIndex(Constants.RulesColumns.COLUMN_ID));
            settingsViewHolder.bind(to, from, messageFormat, roID);
        }
    }

    @Override
    public int getItemCount() {
        return (mCursor != null && !mCursor.isClosed()) ? mCursor.getCount() : 0;
    }

    @Override
    public void onDelete(int rowId) {
        if(mRuleItemClickListener != null){
            mRuleItemClickListener.onDelete(rowId);
        }
    }

    @Override
    public void onRowSelected(int rowID) {
        if(mRuleItemClickListener != null){
            mRuleItemClickListener.onRowSelected(rowID);
        }
    }

    public static class SettingsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mFromMob;
        private TextView mToMob;
        private TextView mMessageFormat;
        private Button mBtnDelete;
        private RuleItemClickListener mRuleItemClickListener;
        private int mRowID;

        public SettingsViewHolder(@NonNull View itemView, RuleItemClickListener ruleItemClickListener) {
            super(itemView);
            mRuleItemClickListener = ruleItemClickListener;
            mFromMob = itemView.findViewById(R.id.txt_from);
            mToMob = itemView.findViewById(R.id.txt_to);
            mMessageFormat = itemView.findViewById(R.id.txt_message_format);
            mBtnDelete = itemView.findViewById(R.id.btn_delete);
        }

        public void bind(String to, String from, String messageFormat, int rowID) {
            mRowID = rowID;
            mFromMob.setText(from);
            mToMob.setText(to);
            mMessageFormat.setText(messageFormat);
            mBtnDelete.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.btn_delete:
                    if(mRuleItemClickListener != null){
                        mRuleItemClickListener.onDelete(mRowID);
                    }
                    break;
                case R.id.rule:
                    if(mRuleItemClickListener != null){
                        mRuleItemClickListener.onRowSelected(mRowID);
                    }
                    break;
            }
        }
    }


}
