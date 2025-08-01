/*
 * COPYRIGHT (C) 2019 MITSUBISHI ELECTRIC CORPORATION
 * ALL RIGHTS RESERVED
 */

package com.mitsubishielectric.ahu.efw.ecucertservice;

import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.ECU_CERT_SERVICE_FUNCTION_ID;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.START_ONLINE_SERVICE;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.STOP_ONLINE_SERVICE;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.mitsubishielectric.ahu.efw.lib.logdogcommonclasslib.MLog;

final class Common {
    public static final String ECU_ALIAS = "ecu";
    public static final String INSTALLATION_STATUS = "/mnt/vendor/oemkeys/ecu/state/ecucertstatus";

    public static final String ECU_ONLINE_SERVICE_PACKAGE_NAME =
            "com.mitsubishielectric.ahu.efw.ecuonlineservice";
    public static final String ECU_ONLINE_SERVICE_NAME =
            "com.mitsubishielectric.ahu.efw.ecuonlineservice.ECUOnlineService";

    public static void startOnlineService(Context context, boolean isNetworkReconnect) {
        Intent serviceIntent = new Intent();
        serviceIntent.setClassName(ECU_ONLINE_SERVICE_PACKAGE_NAME, ECU_ONLINE_SERVICE_NAME);
        serviceIntent.putExtra("isNetworkReconnect", isNetworkReconnect);
        context.startService(serviceIntent);
        MLog.i(ECU_CERT_SERVICE_FUNCTION_ID, START_ONLINE_SERVICE);
    }

    public static void stopOnlineService(Context context) {
        Intent serviceIntent = new Intent();
        serviceIntent.setClassName(ECU_ONLINE_SERVICE_PACKAGE_NAME, ECU_ONLINE_SERVICE_NAME);
        context.stopService(serviceIntent);
        MLog.i(ECU_CERT_SERVICE_FUNCTION_ID, STOP_ONLINE_SERVICE);
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
}
