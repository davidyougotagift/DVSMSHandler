package test.david.com.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DraftsListAdapter extends RecyclerView.Adapter<DraftsListAdapter.DraftsViewHolder> implements OutboxItemClickListener {

    private Context mContext;
    private Cursor mCursor;
    private OutboxItemClickListener mOutboxItemClickListener;

    public DraftsListAdapter(Context context, OutboxItemClickListener clickListener) {
        mContext = context;
        mOutboxItemClickListener = clickListener;
    }

    public void setCursor(Cursor mCursor) {
        this.mCursor = mCursor;
    }

    @NonNull
    @Override
    public DraftsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new DraftsViewHolder(LayoutInflater.from(mContext).inflate(R.layout.drafts_item, null), this);
    }

    @Override
    public void onBindViewHolder(@NonNull DraftsViewHolder draftsViewHolder, int i) {
        if (mCursor != null && !mCursor.isClosed()) {
            mCursor.moveToPosition(i);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
            String date = simpleDateFormat.format(new Date(mCursor.getLong(mCursor.getColumnIndex(Constants.SMSOutboxColumns.COLUMN_DATE_RECIEVED))));
            String source = mCursor.getString(mCursor.getColumnIndex(Constants.SMSOutboxColumns.COLUMN_ADDRESS_FROM));
            String message = mCursor.getString(mCursor.getColumnIndex(Constants.SMSOutboxColumns.COLUMN_BODY));
            String destination = mCursor.getString(mCursor.getColumnIndex(Constants.SMSOutboxColumns.COLUMN_ADDRESS_TO));
            int isFailed = mCursor.getInt(mCursor.getColumnIndex(Constants.SMSOutboxColumns.COLUMN_IS_FAILED));
            long id = mCursor.getLong(mCursor.getColumnIndex(Constants.SMSOutboxColumns.COLUMN_ID));
            draftsViewHolder.bind(destination, source, date, (isFailed == 1) ? true : false, message, id);
        }

    }

    @Override
    public int getItemCount() {
        return (mCursor != null && !mCursor.isClosed()) ? mCursor.getCount() : 0;
    }

    @Override
    public void onRowClick(long id) {
        if (mOutboxItemClickListener != null) {
            mOutboxItemClickListener.onRowClick(id);
        }
    }

    public static class DraftsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private View mView;
        private TextView mFrom;
        private TextView mTo;
        private TextView mRecivedDate;
        private TextView mMessage;
        private TextView mSendStatus;
        private ProgressBar mProgress;
        private Button mResend;
        private OutboxItemClickListener mOutboxItemClickListener;
        private long mId;


        public DraftsViewHolder(@NonNull View itemView, OutboxItemClickListener clickListener) {
            super(itemView);
            mView = itemView;
            mFrom = mView.findViewById(R.id.source);
            mTo = mView.findViewById(R.id.destination);
            mRecivedDate = mView.findViewById(R.id.time);
            mMessage = mView.findViewById(R.id.message);
            mSendStatus = mView.findViewById(R.id.txt_status);
            mProgress = mView.findViewById(R.id.progress);
            mResend = mView.findViewById(R.id.btn_resend);
            mResend.setOnClickListener(this);
            mOutboxItemClickListener = clickListener;
        }

        public void bind(String to, String from, String reciveddate, boolean isFailed, String message, long id) {
            mId = id;
            mFrom.setText("From :" + from);
            mTo.setText("To :" + to);
            mRecivedDate.setText("Recieved :" + reciveddate);
            mMessage.setText(message);
            if (isFailed) {
                mSendStatus.setTextColor(mSendStatus.getContext().getResources().getColor(android.R.color.holo_red_dark));
                mSendStatus.setText("Sending Failed...");
                mResend.setVisibility(View.VISIBLE);
                mProgress.setVisibility(View.GONE);
            } else {
                mSendStatus.setTextColor(mSendStatus.getContext().getResources().getColor(android.R.color.black));
                mSendStatus.setText("Sending in Progress...");
                mResend.setVisibility(View.GONE);
                mProgress.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onClick(View v) {
            if (mOutboxItemClickListener != null) {
                mOutboxItemClickListener.onRowClick(mId);
            }
        }
    }

}
