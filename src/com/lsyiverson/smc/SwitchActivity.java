
package com.lsyiverson.smc;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;

public class SwitchActivity extends Activity {
    private static final String LOG_TAG = "SwitchActivity";

    private Intent mFluxCtrlIntent;

    private Switch mStartSwitch;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch);
        setupView();
        mFluxCtrlIntent = new Intent(SwitchActivity.this, FluxCtrlService.class);
    }

    @Override
    protected void onResume() {
        mStartSwitch.setChecked(isServiceRuning(mFluxCtrlIntent.getComponent().getClassName()));
        mStartSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.d(LOG_TAG, "switch turn on");
                    startService(mFluxCtrlIntent);
                } else {
                    Log.d(LOG_TAG, "switch turn off");
                    stopService(mFluxCtrlIntent);
                }
            }
        });
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_switch, menu);
        return true;
    }

    private void setupView() {
        mStartSwitch = (Switch)findViewById(R.id.start_switch);
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
}
