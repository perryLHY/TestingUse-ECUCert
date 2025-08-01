/*
 * COPYRIGHT (C) 2019 MITSUBISHI ELECTRIC CORPORATION
 * ALL RIGHTS RESERVED
 */

package com.mitsubishielectric.ahu.efw.ecucertservice;

import static android.os.SystemService.start;
import static android.os.SystemService.stop;
import static android.os.SystemService.waitForState;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.DAEMON_STARTED_WAIT_TIMEOUT;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.DAEMON_STOPPED_WAIT_TIMEOUT;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.ECU_CERT_SERVICE_FUNCTION_ID;

import android.os.SystemService;
import com.mitsubishielectric.ahu.efw.lib.logdogcommonclasslib.MLog;
import java.util.concurrent.TimeoutException;

class ECUCertConnector implements IECUConnector {

    private static final String SERVICE_NAME = "ecucertdaemon";
    private static final long SERVICE_WAIT_STOP_TIMEOUT_MS = 10000;
    private static final long SERVICE_WAIT_START_TIMEOUT_MS = 10000;
    private static final long SERVICE_WAIT_STEP_TIMEOUT_MS = 100;

    static {
        System.loadLibrary("ecucertconnector_jni");
    }

    private static int serviceCallsCounter = 0;

    private void steppedWait(SystemService.State state, long timeoutMillis)
            throws TimeoutException {
        long waitRemainMs = timeoutMillis;
        while (waitRemainMs > 0) {
            // That's not a real wait, because if it blocks, then it's for the whole period
            waitForState(SERVICE_NAME, state, SERVICE_WAIT_STEP_TIMEOUT_MS);
            waitRemainMs -= SERVICE_WAIT_STEP_TIMEOUT_MS;
        }
    }

    private synchronized void safeStartService() {
        // This function is synchronized, so all calls are blocked until the first one starts the
        // daemon
        if (serviceCallsCounter == 0) {
            try {
                steppedWait(SystemService.State.STOPPED, SERVICE_WAIT_STOP_TIMEOUT_MS);
            } catch (TimeoutException e) {
                MLog.w(ECU_CERT_SERVICE_FUNCTION_ID, DAEMON_STOPPED_WAIT_TIMEOUT);
            }
            start(SERVICE_NAME);
            try {
                steppedWait(SystemService.State.RUNNING, SERVICE_WAIT_START_TIMEOUT_MS);
            } catch (TimeoutException e) {
                MLog.w(ECU_CERT_SERVICE_FUNCTION_ID, DAEMON_STARTED_WAIT_TIMEOUT);
            }
        }
        serviceCallsCounter++;
    }

    private synchronized void safeStopService() {
        serviceCallsCounter--;
        if (serviceCallsCounter == 0) {
            stop(SERVICE_NAME);
        }
    }

    @Override
    public boolean copyKey(int appUID, int managerUID, String alias) {
        safeStartService();
        boolean res = nativeCopyKey(appUID, managerUID, alias);
        safeStopService();
        return res;
    }

    @Override
    public boolean restoreKey(int managerUID, String alias) {
        safeStartService();
        boolean res = nativeRestoreKey(managerUID, alias);
        safeStopService();
        return res;
    }

    @Override
    public boolean storeKey(int managerUID, String alias) {
        safeStartService();
        boolean res = nativeStoreKey(managerUID, alias);
        safeStopService();
        return res;
    }

    @Override
    public boolean removeKey(int managerUID, String alias, boolean removeBackupRequired) {
        safeStartService();
        boolean res = nativeRemoveKey(managerUID, alias, removeBackupRequired);
        safeStopService();
        return res;
    }

    private static native boolean nativeCopyKey(int appUID, int managerUID, String alias);

    private static native boolean nativeRestoreKey(int managerUID, String alias);

    private static native boolean nativeStoreKey(int managerUID, String alias);

    private static native boolean nativeRemoveKey(
            int managerUID, String alias, boolean removeBackupRequired);
}
