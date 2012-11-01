
package com.lsyiverson.smc;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class FluxCtrlService extends Service {
    private static final String LOG_TAG = "FluxCtrlService";

    private volatile boolean mIsScreenOn;

    private IntentFilter mFilter;

    private Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        mContext = FluxCtrlService.this;

        mFilter = new IntentFilter();
        mFilter.addAction(Intent.ACTION_SCREEN_ON);
        mFilter.addAction(Intent.ACTION_SCREEN_OFF);

        mIsScreenOn = true;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mScreenStateReceiver);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        registerReceiver(mScreenStateReceiver, mFilter);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    private BroadcastReceiver mScreenStateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
                mIsScreenOn = true;
                Log.d(LOG_TAG, "Screen ON");
                Utils.setMobileDataEnabled(true, mContext);
            } else if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
                mIsScreenOn = false;
                Log.d(LOG_TAG, "Screen OFF");
                Utils.setMobileDataEnabled(false, mContext);
            }

        }

    };

}
