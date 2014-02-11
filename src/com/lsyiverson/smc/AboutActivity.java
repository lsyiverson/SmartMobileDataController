
package com.lsyiverson.smc;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.widget.TextView;
import cn.domob.android.ads.DomobUpdater;

import com.umeng.analytics.MobclickAgent;

public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        init();
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            final ActionBar actionBar = getActionBar();
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE
                    | ActionBar.DISPLAY_SHOW_HOME);
        }
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
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.back_window_forward, R.anim.push_right_out);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

}
