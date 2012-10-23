
package com.lsyiverson.smc;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;

public class SwitchActivity extends Activity {
    private static String LOG_TAG = "SwitchActivity";

    private Switch mStartSwitch;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch);
        setupView();
    }

    @Override
    protected void onResume() {
        mStartSwitch.setChecked(isServiceRuning());
        mStartSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.d(LOG_TAG, "switch turn on");
                } else {
                    Log.d(LOG_TAG, "switch turn off");
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

    private boolean isServiceRuning() {
        return true;
    }
}
