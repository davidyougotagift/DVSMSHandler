package test.david.com.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class OutBoxListAdapter extends RecyclerView.Adapter<OutBoxListAdapter.OutBoxHolder> {

    private Context mContext;
    private Cursor mCursor;

    public OutBoxListAdapter(Context context) {
        mContext = context;
    }

    public void setCursor(Cursor mCursor) {
        this.mCursor = mCursor;
    }

    @NonNull
    @Override
    public OutBoxHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new OutBoxHolder(LayoutInflater.from(mContext).inflate(R.layout.outbox_item, null));
    }

    @Override
    public void onBindViewHolder(@NonNull OutBoxHolder draftsViewHolder, int i) {
        if (mCursor != null && !mCursor.isClosed()) {
            mCursor.moveToPosition(i);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
            String date = simpleDateFormat.format(new Date(mCursor.getLong(mCursor.getColumnIndex(Constants.SMSOutboxColumns.COLUMN_DATE_RECIEVED))));
            String source = mCursor.getString(mCursor.getColumnIndex(Constants.SMSOutboxColumns.COLUMN_ADDRESS_FROM));
            String message = mCursor.getString(mCursor.getColumnIndex(Constants.SMSOutboxColumns.COLUMN_BODY));
            String destination = PreferenceData.getToNumber(mContext);
            String sendDate = simpleDateFormat.format(new Date(mCursor.getLong(mCursor.getColumnIndex(Constants.SMSOutboxColumns.COLUMN_DATE_SENT))));
            draftsViewHolder.bind(destination, source, date, message, sendDate);
        }

    }

    @Override
    public int getItemCount() {
        return (mCursor != null && !mCursor.isClosed()) ? mCursor.getCount() : 0;
    }


    public static class OutBoxHolder extends RecyclerView.ViewHolder {

        private View mView;
        private TextView mFrom;
        private TextView mTo;
        private TextView mRecivedDate;
        private TextView mSendDate;
        private TextView mMessage;


        public OutBoxHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            mFrom = mView.findViewById(R.id.source);
            mTo = mView.findViewById(R.id.destination);
            mRecivedDate = mView.findViewById(R.id.time_recived);
            mSendDate = mView.findViewById(R.id.time_sent);
            mMessage = mView.findViewById(R.id.message);
        }

        public void bind(String to, String from, String reciveddate, String message, String sentDate) {
            mFrom.setText("From :" + from);
            mTo.setText("To :" + to);
            mRecivedDate.setText("Recieved :" + reciveddate);
            mMessage.setText(message);
            mSendDate.setText("Sent :" + sentDate);
        }



    }
}
