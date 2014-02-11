
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
     * Umeng analytics event: turn switch on
     */
    public static final String UMENG_SWITCH_ON = "SWITCH_ON";

    /**
     * Umeng analytics event: turn switch off
     */
    public static final String UMENG_SWITCH_OFF = "SWITCH_OFF";

    /**
     * Umeng analytics event: turn mobile data delay time before screen off
     */
    public static final String UMENG_DELAY_TIME = "DELAY_TIME";

    /**
     * Umeng analytics event: auto run service when system boot completed
     */
    public static final String UMENG_AUTO_RUN = "AUTO_RUN";

    /**
     * Umeng analystic event: ad clicked
     */
    public static final String UMENG_AD_CLICKED = "AD_CLICKED";

    /**
     * Get the mobile data status
     * 
     * @param context
     * @return If the mobile data enabled
     */
    private static boolean getMobileDataEnabled(ConnectivityManager cm) {
        boolean mobileDataEnabled = false;
        try {
            Method method = cm.getClass().getMethod("getMobileDataEnabled");
            mobileDataEnabled = (Boolean)method.invoke(cm);
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
