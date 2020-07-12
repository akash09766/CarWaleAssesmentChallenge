package com.carwale.android.carewale_assessment_challenge.app.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.DialogFragment;

import com.carwale.android.carewale_assessment_challenge.R;

public class AppSettingsDialog extends DialogFragment implements View.OnClickListener {

    private String TAG = AppSettingsDialog.class.getSimpleName();
    private TextView mNotNowBtn, mAppSettingBtn;
    private static AppSettingsDialog frag;

    public static AppSettingsDialog newInstance() {

        if (frag != null) {
            return frag;
        }

        frag = new AppSettingsDialog();
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.app_settings_dialog_layout, container);

        setIdsToViews(view);
        setListenersToViews();

        return view;
    }

    private void setIdsToViews(View view) {
        mNotNowBtn = view.findViewById(R.id.qr_code_not_now_btn_id);
        mAppSettingBtn = view.findViewById(R.id.qr_code_app_setting_btn_id);
    }

    private void setListenersToViews() {
        mNotNowBtn.setOnClickListener(this);
        mAppSettingBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (mNotNowBtn == view) {
            dismissDialog();
        } else if (mAppSettingBtn == view) {
            launchAppSettingScreen();
            dismissDialog();
        }
    }


    private void launchAppSettingScreen() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    private void dismissDialog() {
        getDialog().dismiss();
    }
}