/*
 * COPYRIGHT (C) 2019 MITSUBISHI ELECTRIC CORPORATION
 * ALL RIGHTS RESERVED
 */

package com.mitsubishielectric.ahu.efw.ecucertservice;

import static com.mitsubishielectric.ahu.efw.ecucertservice.Common.ECU_ALIAS;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.ECU_CERT_SERVICE_FUNCTION_ID;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.RSA_KEYS_FOUND;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.RSA_KEYS_GENERATED;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.RSA_KEYS_NOT_BACKED_UP;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.RSA_KEYS_NOT_FOUND;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.RSA_KEYS_NOT_GENERATED;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.RSA_KEYS_NOT_RESTORED;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.RSA_KEYS_RESTORED;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.WRONG_SERVICE_UID;

import com.mitsubishielectric.ahu.efw.lib.logdogcommonclasslib.MLog;

final class ECUMaterialCreator {
    private IAndroidKeyStore mAndroidKeyStoreWrapper;
    private IECUConnector mECUCertConnector;

    ECUMaterialCreator(IECUConnector ecuConnector, IAndroidKeyStore androidKeyStore) {
        mAndroidKeyStoreWrapper = androidKeyStore;
        mECUCertConnector = ecuConnector;
    }

    public boolean generateECUMaterial(int serviceUid) {
        boolean result = true;
        if (-1 != serviceUid) {
            if (!mAndroidKeyStoreWrapper.isKeyEntry(ECU_ALIAS)) {
                MLog.i(ECU_CERT_SERVICE_FUNCTION_ID, RSA_KEYS_NOT_FOUND);
                if (!mECUCertConnector.restoreKey(serviceUid, ECU_ALIAS)) {
                    MLog.i(ECU_CERT_SERVICE_FUNCTION_ID, RSA_KEYS_NOT_RESTORED);
                    if (!mAndroidKeyStoreWrapper.generateRSAKeyPair(ECU_ALIAS)) {
                        MLog.e(ECU_CERT_SERVICE_FUNCTION_ID, RSA_KEYS_NOT_GENERATED);
                        result = false;
                    } else {
                        if (!mECUCertConnector.storeKey(serviceUid, ECU_ALIAS)) {
                            MLog.e(ECU_CERT_SERVICE_FUNCTION_ID, RSA_KEYS_NOT_BACKED_UP);
                            result = false;
                        }
                        MLog.i(ECU_CERT_SERVICE_FUNCTION_ID, RSA_KEYS_GENERATED);
                    }
                } else {
                    MLog.i(ECU_CERT_SERVICE_FUNCTION_ID, RSA_KEYS_RESTORED);
                }
            } else {
                MLog.i(ECU_CERT_SERVICE_FUNCTION_ID, RSA_KEYS_FOUND);
            }
        } else {
            MLog.e(ECU_CERT_SERVICE_FUNCTION_ID, WRONG_SERVICE_UID);
            result = false;
        }
        return result;
    }
}
