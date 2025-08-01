/*
 * COPYRIGHT (C) 2020 MITSUBISHI ELECTRIC CORPORATION
 * ALL RIGHTS RESERVED
 */

package com.mitsubishielectric.ahu.efw.ecucertservice;

import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.CONNECT_TO_NETWORK;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.ECU_CERT_SERVICE_FUNCTION_ID;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.NETWORK_LOST;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import com.mitsubishielectric.ahu.efw.lib.ecucertmgr.CertChainInstallStatus;
import com.mitsubishielectric.ahu.efw.lib.logdogcommonclasslib.MLog;

final class NetworkReceiver extends ConnectivityManager.NetworkCallback {

    private IInstallationStatusManager mStatusManager;
    private static boolean isNetworkReconnect = false;
    private Context mContext;

    NetworkReceiver(IInstallationStatusManager statusManager, Context context) {
        mStatusManager = statusManager;
        mContext = context;
    }

    @Override
    public void onAvailable(Network network) {
        MLog.i(ECU_CERT_SERVICE_FUNCTION_ID, CONNECT_TO_NETWORK);
        CertChainInstallStatus certInstallState = mStatusManager.getInstallationStatus(false);
        if (CertChainInstallStatus.CERT_0_NEVER_DOWNLOADED == certInstallState
                || CertChainInstallStatus.CERT_2_INSTALLATION_ERROR == certInstallState) {
            if (Common.isOnline(mContext)) {
                Common.startOnlineService(mContext, isNetworkReconnect);
                isNetworkReconnect = true;
            }
        }
    }

    @Override
    public void onLost(Network network) {
        MLog.i(ECU_CERT_SERVICE_FUNCTION_ID, NETWORK_LOST);
        Common.stopOnlineService(mContext);
    }
}
