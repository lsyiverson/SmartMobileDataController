
package com.lsyiverson.smc;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

public class FluxCtrlService extends Service {
    private static final String LOG_TAG = "FluxCtrlService";

    private volatile boolean mIsScreenOn;

    private IntentFilter mFilter;

    private Context mContext;

    private DelayDisableFluxTask mTask;

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
        Log.d(LOG_TAG, "Stop FluxCtrlService");
        unregisterReceiver(mScreenStateReceiver);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "Start FluxCtrlService");
        registerReceiver(mScreenStateReceiver, mFilter);
        if (intent != null) {
            switch (intent.getFlags()) {
                case Utils.MANUAL_FLAG:
                    Utils.setMobileDataEnabled(true, mContext);
                    break;
                case Utils.AUTOMATIC_FLAG:
                    break;
            }
        }
        return START_STICKY;
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
                if (mTask != null && mTask.getStatus() == Status.RUNNING) {
                    mTask.cancel(true);
                    mTask = null;
                }
                Utils.setMobileDataEnabled(true, mContext);
            } else if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
                mIsScreenOn = false;
                Log.d(LOG_TAG, "Screen OFF");
                String delayTime = PreferenceManager.getDefaultSharedPreferences(
                        FluxCtrlService.this).getString(
                        getResources().getString(R.string.key_delay_time), "0");
                mTask = new DelayDisableFluxTask();
                mTask.execute(Integer.valueOf(delayTime).intValue());
            }

        }

    };

    private class DelayDisableFluxTask extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... params) {
            int time = params[0].intValue() * 1000;
            if (time > 0) {
                try {
                    Thread.sleep(time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (!mIsScreenOn) {
                Utils.setMobileDataEnabled(false, mContext);
            }
            super.onPostExecute(result);
        }

    }

}
