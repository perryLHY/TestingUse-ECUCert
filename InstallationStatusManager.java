/*
 * COPYRIGHT (C) 2020 MITSUBISHI ELECTRIC CORPORATION
 * ALL RIGHTS RESERVED
 */

package com.mitsubishielectric.ahu.efw.ecucertservice;

import static com.mitsubishielectric.ahu.efw.ecucertservice.Common.ECU_ALIAS;
import static com.mitsubishielectric.ahu.efw.ecucertservice.Common.INSTALLATION_STATUS;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.ECU_CERT_SERVICE_FUNCTION_ID;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.INSTALLATION_STATUS_IS;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.INSTALLATION_STATUS_NOT_UPDATED;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.INSTALLATION_STATUS_UPDATED;
import static com.mitsubishielectric.ahu.efw.lib.ecucertmgr.CertChainInstallStatus.CERT_0_NEVER_DOWNLOADED;
import static com.mitsubishielectric.ahu.efw.lib.ecucertmgr.CertChainInstallStatus.CERT_1_DOWNLOADED_AND_INSTALLED;
import static com.mitsubishielectric.ahu.efw.lib.ecucertmgr.CertChainInstallStatus.CERT_2_INSTALLATION_ERROR;
import static com.mitsubishielectric.ahu.efw.lib.ecucertmgr.CertChainInstallStatus.CERT_4_INVALID;
import static com.mitsubishielectric.ahu.efw.lib.ecucertmgr.CertChainInstallStatus.KEY_3_INVALID;
import static com.mitsubishielectric.ahu.efw.lib.ecucertmgr.DetailErrorCode.CERTIFICATE_VALIDATION_SUCCESS;

import com.mitsubishielectric.ahu.efw.lib.ecucertmgr.CertChainInstallStatus;
import com.mitsubishielectric.ahu.efw.lib.ecucertmgr.CertificateChain;
import com.mitsubishielectric.ahu.efw.lib.logdogcommonclasslib.MLog;
import java.io.File;

final class InstallationStatusManager implements IInstallationStatusManager {

    private IFileWrapper mFileWrapper;
    private IAndroidKeyStore mKeyStore;
    private IMelcoCertProvider mCertProvider;
    private ISerialNumber mSerialNumber;

    InstallationStatusManager(
            IFileWrapper fileWrapper,
            IAndroidKeyStore keyStore,
            IMelcoCertProvider certProvider,
            ISerialNumber serialNumber) {
        mFileWrapper = fileWrapper;
        mKeyStore = keyStore;
        mCertProvider = certProvider;
        mSerialNumber = serialNumber;
    }

    @Override
    public CertChainInstallStatus getInstallationStatus(boolean verifyCertificate) {
        File file = new File(INSTALLATION_STATUS);
        CertChainInstallStatus installStatus =
                CertChainInstallStatus.fromInt(mFileWrapper.read(file));
        if (verifyCertificate) {
            if (!mKeyStore.isKeyEntry(ECU_ALIAS)) {
                installStatus = KEY_3_INVALID;
            }
            if (CERT_1_DOWNLOADED_AND_INSTALLED == installStatus) {
                CertificateChain certChain =
                        new CertificateChain(mKeyStore.getCertificateChain(ECU_ALIAS));
                if (certChain.getData().size() == 0
                        || mCertProvider.verifyCertChain(
                                        "(^|.*, )CN = "
                                                + mSerialNumber.getSerialNumber()
                                                + "(, .*|$)",
                                        certChain)
                                != CERTIFICATE_VALIDATION_SUCCESS) {
                    installStatus = CERT_4_INVALID;
                } else if (null == mKeyStore.getRSAKeyPair(ECU_ALIAS)) {
                    installStatus = KEY_3_INVALID;
                }
            }
            if (CERT_2_INSTALLATION_ERROR == installStatus) {
                installStatus = CERT_0_NEVER_DOWNLOADED;
            }
        }
        MLog.d(ECU_CERT_SERVICE_FUNCTION_ID, INSTALLATION_STATUS_IS, installStatus.ordinal());
        return installStatus;
    }

    @Override
    public boolean updateInstallationStatus(CertChainInstallStatus installStatus) {
        File file = new File(INSTALLATION_STATUS);
        boolean res = mFileWrapper.write(file, installStatus.ordinal());
        if (res) {
            MLog.i(
                    ECU_CERT_SERVICE_FUNCTION_ID,
                    INSTALLATION_STATUS_UPDATED,
                    installStatus.ordinal());
        } else {
            MLog.w(ECU_CERT_SERVICE_FUNCTION_ID, INSTALLATION_STATUS_NOT_UPDATED);
        }
        return res;
    }
}
