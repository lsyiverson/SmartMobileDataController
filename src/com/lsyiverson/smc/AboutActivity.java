
package com.lsyiverson.smc;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.widget.TextView;
import cn.domob.android.ads.DomobUpdater;

public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        init();
    }

    private void init() {
        final ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE
                | ActionBar.DISPLAY_SHOW_HOME);

        try {
            PackageManager pm = getPackageManager();
            PackageInfo pkgInfo;
            pkgInfo = pm.getPackageInfo(getPackageName(), PackageManager.GET_CONFIGURATIONS);
            String version = pkgInfo.versionName;
            String displayVersion = getResources().getString(R.string.app_version);
            displayVersion = String.format(displayVersion, version);
            TextView tvVersion = (TextView)findViewById(R.id.version);
            tvVersion.setText(displayVersion);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        TextView tvDescription = (TextView)findViewById(R.id.description);
        tvDescription.setText(Html.fromHtml(getResources().getString(R.string.app_description)));

        // Use Domob SDK to check update
        DomobUpdater.checkUpdate(AboutActivity.this, getResources()
                .getString(R.string.publisher_id));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = new Intent(this, SwitchActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
