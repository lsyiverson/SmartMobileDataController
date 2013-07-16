
package com.lsyiverson.smc;

import java.util.ArrayList;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import cn.domob.android.ads.DomobAdEventListener;
import cn.domob.android.ads.DomobAdManager.ErrorCode;
import cn.domob.android.ads.DomobAdView;

import com.umeng.analytics.MobclickAgent;

public class SwitchActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {
    private static final String LOG_TAG = "SwitchActivity";

    private Intent mFluxCtrlIntent;

    private SharedPreferences mSmartSettings;

    private SwitchPreference mSwitchPreference;

    private PreferenceCategory mOptionsCategory;

    private CheckBoxPreference mAutoRunPref;

    private ListPreference mDelayTimePref;

    private RelativeLayout mAdContainer;

    private DomobAdView mAdView320x50;

    protected boolean mIsQuit = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MobclickAgent.onError(this);
        addPreferencesFromResource(R.xml.settings);
        setContentView(R.layout.activity_switch);
        mSmartSettings = PreferenceManager.getDefaultSharedPreferences(this);
        mFluxCtrlIntent = new Intent(SwitchActivity.this, FluxCtrlService.class);
        mFluxCtrlIntent.addFlags(Utils.MANUAL_FLAG);
        init();
        setupAdView();
    }

    @Override
    protected void onResume() {
        boolean fluxCtrl = mSmartSettings.getBoolean(
                getResources().getString(R.string.key_mobile_data), false);
        if (fluxCtrl != isServiceRuning(mFluxCtrlIntent.getComponent().getClassName())) {
            ctrlServiceByPreference(fluxCtrl);
        } else {
            setPreferenceState(fluxCtrl);
        }
        super.onResume();
        MobclickAgent.onResume(this);
    }

    private void setupAdView() {
        mAdContainer = (RelativeLayout)findViewById(R.id.ad_container);

        mAdView320x50 = new DomobAdView(this, getResources().getString(R.string.publisher_id), getResources().getString(R.string.InlinePPID),
                DomobAdView.INLINE_SIZE_320X50);
        mAdView320x50.setAdEventListener(new DomobAdEventListener() {

            @Override
            public void onDomobAdReturned(DomobAdView arg0) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onDomobAdOverlayPresented(DomobAdView arg0) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onDomobAdOverlayDismissed(DomobAdView arg0) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onDomobAdClicked(DomobAdView arg0) {
                MobclickAgent.onEvent(SwitchActivity.this, Utils.UMENG_AD_CLICKED);
            }

            @Override
            public void onDomobAdFailed(DomobAdView arg0, ErrorCode arg1) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDomobLeaveApplication(DomobAdView arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public Context onDomobAdRequiresCurrentContext() {
                // TODO Auto-generated method stub
                return null;
            }
        });

        mAdContainer.addView(mAdView320x50);
    }

    private void init() {
        mSmartSettings.registerOnSharedPreferenceChangeListener(this);
        mSwitchPreference = (SwitchPreference)getPreferenceScreen().findPreference(
                getResources().getString(R.string.key_mobile_data));
        mOptionsCategory = (PreferenceCategory)getPreferenceScreen().findPreference(
                getResources().getString(R.string.key_options));
        mAutoRunPref = (CheckBoxPreference)getPreferenceScreen().findPreference(
                getResources().getString(R.string.key_auto_run));
        mDelayTimePref = (ListPreference)getPreferenceScreen().findPreference(
                getResources().getString(R.string.key_delay_time));
    }

    private boolean isServiceRuning(String classname) {
        ActivityManager am = (ActivityManager)this.getSystemService(ACTIVITY_SERVICE);
        ArrayList<RunningServiceInfo> runningServiceInfos = (ArrayList<RunningServiceInfo>)am
                .getRunningServices(30);
        for (int i = 0; i < runningServiceInfos.size(); i++) {
            if (runningServiceInfos.get(i).service.getClassName().equals(classname)) {
                Log.d(LOG_TAG, "Service is running");
                return true;
            }
        }
        Log.d(LOG_TAG, "Service is not running");
        return false;
    }

    private void setPreferenceState(boolean enabled) {
        mOptionsCategory.setEnabled(enabled);
        mAutoRunPref.setSelectable(enabled);
        mDelayTimePref.setSelectable(enabled);
    }

    private void ctrlServiceByPreference(boolean fluxCtrl) {
        setPreferenceState(fluxCtrl);
        if (fluxCtrl) {
            Log.d(LOG_TAG, "switch turn on");
            MobclickAgent.onEvent(this, Utils.UMENG_SWITCH_ON);
            startService(mFluxCtrlIntent);
        } else {
            Log.d(LOG_TAG, "switch turn off");
            MobclickAgent.onEvent(this, Utils.UMENG_SWITCH_OFF);
            stopService(mFluxCtrlIntent);
        }
    }

    @Override
    public void onBackPressed() {
        mIsQuit = true;
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        if (!mIsQuit) {
            overridePendingTransition(R.anim.push_right_in, R.anim.back_window_back);
        }
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        mSmartSettings.unregisterOnSharedPreferenceChangeListener(this);
        Runtime.getRuntime().gc();
        super.onDestroy();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (getResources().getString(R.string.key_mobile_data).equals(key)) {
            final boolean fluxCtrl = sharedPreferences.getBoolean(key, false);
            boolean showTips = PreferenceManager.getDefaultSharedPreferences(SwitchActivity.this)
                    .getBoolean(getResources().getString(R.string.key_showtips), true);
            if (fluxCtrl && showTips) {
                LayoutInflater inflater = LayoutInflater.from(SwitchActivity.this);
                View tipsView = inflater.inflate(R.layout.dialog_tips, null);
                final CheckBox cbShowAgain = (CheckBox)tipsView.findViewById(R.id.show_again);

                Builder builder = new AlertDialog.Builder(SwitchActivity.this);
                builder.setTitle(R.string.tips_title).setView(tipsView)
                .setPositiveButton(R.string.ok, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (cbShowAgain.isChecked()) {
                            PreferenceManager
                            .getDefaultSharedPreferences(SwitchActivity.this)
                            .edit()
                            .putBoolean(
                                    getResources().getString(R.string.key_showtips),
                                    false).commit();
                        }
                        ctrlServiceByPreference(fluxCtrl);
                    }
                }).setNegativeButton(R.string.cancel, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mSwitchPreference.setChecked(false);
                    }
                }).setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface arg0) {
                        mSwitchPreference.setChecked(false);
                    }
                });
                if (!SwitchActivity.this.isFinishing()) {
                    builder.create().show();
                } else {
                    mSwitchPreference.setChecked(false);
                }
            } else {
                ctrlServiceByPreference(fluxCtrl);
            }
        }
    }
}
