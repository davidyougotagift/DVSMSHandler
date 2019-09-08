package test.david.com.myapplication;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class SettingsFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = SettingsFragment.class.getSimpleName();

    private EditText mFromMob;
    private EditText mToMob;
    private EditText mFormat;
    private Button mSave;

    public static SettingsFragment getInstance() {
        SettingsFragment settingsFragment = new SettingsFragment();
        return settingsFragment;
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
        mFromMob.setText(PreferenceData.getFromNumber(getActivity()));
        mToMob.setText(PreferenceData.getToNumber(getActivity()));
        mFormat.setText(PreferenceData.getMessageFormat(getActivity()));
        mSave.setOnClickListener(this);
    }

    public void addRule() {

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
                    Utils.showOKCancelDialog(getActivity(), "This will reset the app. Are you sure?", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Utils.showToast(getActivity(), "Saved Details");
                            getActivity().getContentResolver().delete(Constants.CONTENT_URI_OUTBOX, null, null);
                            getActivity().getContentResolver().delete(Constants.CONTENT_URI_INBOX, null, null);
                            PreferenceData.setData(getActivity(), toNUmber, fromNumber, System.currentTimeMillis(), format);
                            dialog.dismiss();
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
}
