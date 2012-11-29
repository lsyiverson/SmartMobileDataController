
package com.lsyiverson.smc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.Log;

public class ActionReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = "ActionRecevier";

    @Override
    public void onReceive(Context context, Intent intent) {
        Resources res = context.getResources();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        Intent fluxCtrlIntent = new Intent(context, FluxCtrlService.class);
        fluxCtrlIntent.addFlags(Utils.AUTOMATIC_FLAG);

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            if (sp.getBoolean(res.getString(R.string.key_auto_run), false)
                    && sp.getBoolean(res.getString(R.string.key_mobile_data), false)) {
                Log.d(LOG_TAG, "System boot completed, start FluxCtrlService");
                context.startService(fluxCtrlIntent);
            }
        } else if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {
            String pkgName = intent.getData().getSchemeSpecificPart();
            if (pkgName.equals(context.getPackageName())
                    && sp.getBoolean(res.getString(R.string.key_mobile_data), false)) {
                Log.d(LOG_TAG, "App has replaced, restart FluxCtrlService");
                context.startService(fluxCtrlIntent);
            }
        }
    }

}
