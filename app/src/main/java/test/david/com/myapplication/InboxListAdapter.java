package test.david.com.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class InboxListAdapter extends RecyclerView.Adapter<InboxListAdapter.InboxViewHolder> {


    private Cursor mCursor;
    private Context mContext;

    public InboxListAdapter(Context context) {
        mContext = context;
    }


    public void setCursor(Cursor cursor) {
        this.mCursor = cursor;
    }

    @NonNull
    @Override
    public InboxViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new InboxViewHolder(LayoutInflater.from(mContext).inflate(R.layout.inbox_item, null));
    }

    @Override
    public void onBindViewHolder(@NonNull InboxViewHolder inboxViewHolder, int i) {
        if (!mCursor.isClosed()) {
            mCursor.moveToPosition(i);
            String source = mCursor.getString(mCursor.getColumnIndex(Telephony.TextBasedSmsColumns.ADDRESS));
            String message = mCursor.getString(mCursor.getColumnIndex(Telephony.TextBasedSmsColumns.BODY));
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
            String date = simpleDateFormat.format(new Date(mCursor.getLong(mCursor.getColumnIndex(Telephony.TextBasedSmsColumns.DATE))));
            inboxViewHolder.setContent(source, date, message);
        }
    }

    @Override
    public int getItemCount() {
        return (mCursor != null && !mCursor.isClosed()) ? mCursor.getCount() : 0;
    }

    public static class InboxViewHolder extends RecyclerView.ViewHolder {

        private TextView mSource;
        private TextView mTime;
        private TextView mMessage;

        public InboxViewHolder(@NonNull View itemView) {
            super(itemView);
            mSource = itemView.findViewById(R.id.source);
            mTime = itemView.findViewById(R.id.time);
            mMessage = itemView.findViewById(R.id.message);
        }

        public void setContent(String source, String time, String message) {
            mSource.setText("From :" + source);
            mTime.setText("Recieved :" + time);
            mMessage.setText(message);
        }

    }
}
