
package com.lsyiverson.smc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.Log;

public class BootRecevier extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            Resources res = context.getResources();
            if (sp.getBoolean(res.getString(R.string.key_auto_run), false)
                    && sp.getBoolean(res.getString(R.string.key_mobile_data), false)) {
                Log.d("debug", "System boot completed, start FluxCtrlService");
                context.startService(new Intent(context, FluxCtrlService.class));
            }
        }
    }

}
