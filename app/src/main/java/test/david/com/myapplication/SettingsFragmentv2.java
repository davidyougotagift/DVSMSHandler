package test.david.com.myapplication;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
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

public class SettingsFragmentv2 extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, RuleItemClickListener {

    public static final String TAG = SettingsFragmentv2.class.getSimpleName();
    private static final int ID_RULES_LOADER = 111;

    public void addRule() {
        AddRuleActivity.start(getActivity(), -1);
    }

    private RecyclerView mRecyclerView;
    private SettingsAdapter mSettingsAdapter;

    public static SettingsFragmentv2 getInstance() {
        SettingsFragmentv2 settingsFragment = new SettingsFragmentv2();
        return settingsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSettingsAdapter = new SettingsAdapter(getActivity(), this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings_v2, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initLoader();
    }

    private void initLoader() {
        getLoaderManager().initLoader(ID_RULES_LOADER, null, this);
    }

    private void initView(View view) {
        mRecyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mSettingsAdapter);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        CursorLoader cursorLoader = null;
        switch (i) {
            case ID_RULES_LOADER:
                cursorLoader = new CursorLoader(getActivity(), Constants.CONTENT_URI_RULES, null, null, null, null);
                break;
        }
        return cursorLoader;
    }


    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null) {
            mSettingsAdapter.setCursor(cursor);
            mSettingsAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mSettingsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDelete(final int rowId) {
        Utils.showOKCancelDialog(getActivity(), "This will delete this rule. Are you sure?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Utils.showToast(getActivity(), "Deleted Rule");
                getActivity().getContentResolver().delete(Constants.CONTENT_URI_RULES
                        , Constants.RulesColumns.COLUMN_ID + " = ?"
                        , new String[]{Integer.toString(rowId)});
                dialog.dismiss();
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onRowSelected(final int rowID) {
        Utils.showOKCancelDialog(getActivity(), "Want to edit this rule?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AddRuleActivity.start(getActivity(), rowID);
                dialog.dismiss();
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }
}
