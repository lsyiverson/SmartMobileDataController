package com.lsyiverson.smc;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class SwitchActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_switch, menu);
        return true;
    }
}
