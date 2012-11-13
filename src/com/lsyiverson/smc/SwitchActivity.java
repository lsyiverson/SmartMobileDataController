
package com.lsyiverson.smc;

import java.util.ArrayList;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RelativeLayout;
import cn.domob.android.ads.DomobAdEventListener;
import cn.domob.android.ads.DomobAdView;

public class SwitchActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {
    private static final String LOG_TAG = "SwitchActivity";

    private Intent mFluxCtrlIntent;

    private SharedPreferences mSmartSettings;

    private PreferenceCategory mOptionsCategory;

    private CheckBoxPreference mAutoRunPref;

    private ListPreference mDelayTimePref;

    private RelativeLayout mAdContainer;

    private DomobAdView mAdView320x50;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        setContentView(R.layout.activity_switch);
        mSmartSettings = PreferenceManager.getDefaultSharedPreferences(this);
        mFluxCtrlIntent = new Intent(SwitchActivity.this, FluxCtrlService.class);
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
    }

    private void setupAdView() {
        mAdContainer = (RelativeLayout)findViewById(R.id.ad_container);

        mAdView320x50 = new DomobAdView(this, getResources().getString(R.string.publisher_id),
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
            public void onDomobAdFailed(DomobAdView arg0) {
                // TODO Auto-generated method stub
            }
        });

        mAdContainer.addView(mAdView320x50);
    }

    private void init() {
        mSmartSettings.registerOnSharedPreferenceChangeListener(this);
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
            startService(mFluxCtrlIntent);
        } else {
            Log.d(LOG_TAG, "switch turn off");
            stopService(mFluxCtrlIntent);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        boolean fluxCtrl;
        if (getResources().getString(R.string.key_mobile_data).equals(key)) {
            fluxCtrl = sharedPreferences.getBoolean(key, false);
            ctrlServiceByPreference(fluxCtrl);
        }
    }
}
