package test.david.com.myapplication;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class InboxFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = InboxFragment.class.getSimpleName();

    private static final int ID_SMS_LIST_LOADER = 111;

    public static InboxFragment getInstance() {
        InboxFragment settingsFragment = new InboxFragment();
        return settingsFragment;
    }

    private RecyclerView mRecyclerView;
    private InboxListAdapter mInboxListAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInboxListAdapter = new InboxListAdapter(getActivity());
    }

    private void initLoader() {
        getLoaderManager().initLoader(ID_SMS_LIST_LOADER, null, this);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inbox, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initLoader();
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    private void initView(View view) {
        mRecyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mInboxListAdapter);

    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        CursorLoader cursorLoader = null;
        switch (i) {
            case ID_SMS_LIST_LOADER:
                cursorLoader = new CursorLoader(getActivity(), Constants.CONTENT_URI_INBOX, null, null, null, Telephony.Sms.Inbox.DEFAULT_SORT_ORDER);
                break;
        }
        return cursorLoader;
    }


    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null) {
            int size = cursor.getCount();
            //Utils.showToast(getActivity(), "Size : " + size);
            mInboxListAdapter.setCursor(cursor);
            mInboxListAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mInboxListAdapter.notifyDataSetChanged();
    }
}
