package test.david.com.myapplication;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class AddRuleFragment extends Fragment implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {


    public static final String TAG = AddRuleFragment.class.getSimpleName();
    public static final String KEY_ROW_ID = "rule_row_id";
    public static final int ID_RULE_ROW_QUERY = 123;

    private int mRowId;
    private EditText mFromMob;
    private EditText mToMob;
    private EditText mFormat;
    private Button mSave;


    public static AddRuleFragment getInstance(int id) {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_ROW_ID, id);
        AddRuleFragment addRuleFragment = new AddRuleFragment();
        addRuleFragment.setArguments(bundle);
        return addRuleFragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRowId = getArguments().getInt(KEY_ROW_ID);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        mFormat = view.findViewById(R.id.edit_format);
        mFromMob = view.findViewById(R.id.edit_from);
        mToMob = view.findViewById(R.id.edit_to);
        mSave = view.findViewById(R.id.btn_save);
        mSave.setOnClickListener(this);
        if (mRowId > 0) {
            queryRow(mRowId);
        }
    }

    private void queryRow(int id) {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_ROW_ID, id);
        getLoaderManager().initLoader(ID_RULE_ROW_QUERY, bundle, this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_save:
                final String fromNumber = mFromMob.getText().toString();
                final String toNUmber = mToMob.getText().toString();
                final String format = mFormat.getText().toString();
                if (TextUtils.isEmpty(fromNumber)) {
                    Utils.showErrorDialog(getActivity(), "Invalid \"From\" Number", "Error");
                } else if (TextUtils.isEmpty(toNUmber)) {
                    Utils.showErrorDialog(getActivity(), "Invalid \"To\" Number", "Error");
                } else if (TextUtils.isEmpty(format)) {
                    Utils.showErrorDialog(getActivity(), "Message format cannot be empty", "Error");
                } else {
                    Utils.showOKCancelDialog(getActivity(), "Are you sure?", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Utils.showToast(getActivity(), "Saved Rule");
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(Constants.RulesColumns.COLUMN_FROM_NUMBER, fromNumber);
                            contentValues.put(Constants.RulesColumns.COLUMN_TO_NUMBER, toNUmber);
                            contentValues.put(Constants.RulesColumns.COLUMN_MESSAGE_FORMAT, format);
                            if (mRowId > 0) {
                                getActivity().getContentResolver().update(Constants.CONTENT_URI_RULES, contentValues
                                        , Constants.RulesColumns.COLUMN_ID + " = ?", new String[]{Integer.toString(mRowId)});
                            } else {
                                getActivity().getContentResolver().insert(Constants.CONTENT_URI_RULES, contentValues);
                            }
                            dialog.dismiss();
                            if(PreferenceData.getSavedTimeStamp(getActivity()) <= 0){
                                PreferenceData.setSavedTimeStamp(getActivity(), System.currentTimeMillis());
                            }
                            getActivity().finish();
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                }
                break;
        }
    }

    private void fillItems(Cursor cursor) {
        cursor.moveToFirst();
        mFromMob.setText(cursor.getString(cursor.getColumnIndex(Constants.RulesColumns.COLUMN_FROM_NUMBER)));
        mToMob.setText(cursor.getString(cursor.getColumnIndex(Constants.RulesColumns.COLUMN_TO_NUMBER)));
        mFormat.setText(cursor.getString(cursor.getColumnIndex(Constants.RulesColumns.COLUMN_MESSAGE_FORMAT)));
    }


    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        return new CursorLoader(getActivity(), Constants.CONTENT_URI_RULES, null, Constants.RulesColumns.COLUMN_ID + " = ?"
                , new String[]{Integer.toString(bundle.getInt(KEY_ROW_ID))}, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor o) {
        fillItems(o);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}
