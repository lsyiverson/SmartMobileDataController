
package com.lsyiverson.smc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

public class Utils {
    private static final String LOG_TAG = "Utils";

    public static final int MANUAL_FLAG = 0x01;

    public static final int AUTOMATIC_FLAG = 0x02;

    public static final String MOBILE_DATA_CHANGED = "com.lsyiverson.smc.MOBILE_DATA_CHANGED";

    /**
     * Get the mobile data status
     * 
     * @param context
     * @return If the mobile data enabled
     */
    private static boolean getMobileDataEnabled(ConnectivityManager cm) {
        boolean mobileDataEnabled = true;
        try {
            Method method = cm.getClass().getMethod("getMobileDataEnabled", null);
            Object[] arg = null;
            mobileDataEnabled = (Boolean)method.invoke(cm, arg);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return mobileDataEnabled;
    }

    /**
     * Set the mobile data status
     * 
     * @param If the mobile data is enabled
     */
    public static void setMobileDataEnabled(boolean enabled, Context context) {
        ConnectivityManager cm = (ConnectivityManager)context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (enabled != getMobileDataEnabled(cm)) {
            try {
                Method method = cm.getClass().getMethod("setMobileDataEnabled", boolean.class);
                method.invoke(cm, enabled);
                if (enabled) {
                    Log.d(LOG_TAG, "Mobile Data turn on");
                } else {
                    Log.d(LOG_TAG, "Mobile Data turn off");
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        Intent intent = new Intent(MOBILE_DATA_CHANGED);
        intent.putExtra("enabled", enabled);
        context.sendBroadcast(intent);
    }
}
