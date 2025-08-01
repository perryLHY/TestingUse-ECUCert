/*
 * COPYRIGHT (C) 2019 MITSUBISHI ELECTRIC CORPORATION
 * ALL RIGHTS RESERVED
 */

package com.mitsubishielectric.ahu.efw.ecucertservice;

import static com.mitsubishielectric.ahu.efw.ecucertservice.Common.ECU_ALIAS;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.BACKUP_KEY_STORE_NOT_DELETED;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.CERT_CHAIN_NOT_STORED;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.CERT_CHAIN_STORED;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.CERT_CHAIN_VERIFICATION_ERROR;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.CERT_NOT_FOUND;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.CERT_READ_EXCEPTION;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.CERT_READ_WRONG_BYTES_AMOUNT;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.CN_INFO;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.CSR_GENERATE_FAIL;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.CSR_GENERATE_SUCCESS;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.DTC_NOT_HEALED;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.DTC_NOT_RAISED;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.ECU_CERT_SERVICE_FUNCTION_ID;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.ECU_INSTALLATION_STATUS;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.EMPTY_CERT;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.EMPTY_CHAIN;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.GEN_CERT_EXCEPTION;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.GET_CERTIFICATE_EXCEPTION;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.GET_KEY_ALIAS;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.INSTALLATION_STATUS_NOT_UPDATED;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.KEYSTORE_NOT_COPIED_TO_OEM;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.RESULT_ERROR_CODE;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.RSA_KEYS_COPY_FAIL;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.RSA_KEYS_COPY_SUCCESS;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.RSA_KEYS_NOT_RESTORED;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.SERIAL_NUMBER;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.TOTAL_RETRIAL_IS_MAX_SUCCESS;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.UNPACK_EXCEPTION;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.WRONG_ID;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.WRONG_SERIAL_NUMBER;

import android.content.Context;
import com.mitsubishielectric.ahu.efw.lib.ecucertmgr.CSRType;
import com.mitsubishielectric.ahu.efw.lib.ecucertmgr.CertChainInstallStatus;
import com.mitsubishielectric.ahu.efw.lib.ecucertmgr.CertType;
import com.mitsubishielectric.ahu.efw.lib.ecucertmgr.CertificateChain;
import com.mitsubishielectric.ahu.efw.lib.ecucertmgr.DetailErrorCode;
import com.mitsubishielectric.ahu.efw.lib.logdogcommonclasslib.MLog;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;

final class ECUCertImpl {
    private IAndroidKeyStore mAndroidKeyStoreWrapper;
    private IECUConnector mECUCertConnector;
    private ICSRGenerator mCSRGenerator;
    private IMelcoCertProvider mCertProvider;
    private IInstallationStatusManager mStatusManager;
    private IDTCManager mDtcManager;
    private ISerialNumber mSerialNumber;
    private Context mContext;

    private static final String INTERMEDIATE_CERT_1 = "ecuCaOneCert.pem";
    private static final String INTERMEDIATE_CERT_2 = "ecuCaTwoCert.pem";

    ECUCertImpl(
            IAndroidKeyStore keyStore,
            IECUConnector connector,
            ICSRGenerator csrGenetator,
            IMelcoCertProvider certProvider,
            IInstallationStatusManager statusManager,
            IDTCManager dtcManager,
            ISerialNumber serialNumber,
            Context context) {
        mAndroidKeyStoreWrapper = keyStore;
        mECUCertConnector = connector;
        mCSRGenerator = csrGenetator;
        mCertProvider = certProvider;
        mStatusManager = statusManager;
        mDtcManager = dtcManager;
        mSerialNumber = serialNumber;
        mContext = context;
    }

    byte[] exportCSR(CSRType type, int managerUID) {
        byte[] csr = new byte[0];
        KeyPair rsaKeyPair = mAndroidKeyStoreWrapper.getRSAKeyPair(ECU_ALIAS);
        if (null == rsaKeyPair) {
            if (mECUCertConnector.restoreKey(managerUID, ECU_ALIAS)) {
                rsaKeyPair = mAndroidKeyStoreWrapper.getRSAKeyPair(ECU_ALIAS);
            } else {
                MLog.e(ECU_CERT_SERVICE_FUNCTION_ID, RSA_KEYS_NOT_RESTORED);
            }
        }
        String subjectName = mSerialNumber.getSubjectName();
        if (null != rsaKeyPair && !subjectName.isEmpty()) {
            csr = mCSRGenerator.generateCSR(type, rsaKeyPair, subjectName);
            MLog.i(ECU_CERT_SERVICE_FUNCTION_ID, CSR_GENERATE_SUCCESS);
        } else {
            MLog.e(ECU_CERT_SERVICE_FUNCTION_ID, CSR_GENERATE_FAIL);
        }
        return csr;
    }

    int getECUCertStatus() {
        CertChainInstallStatus status = mStatusManager.getInstallationStatus(true);
        return status.ordinal();
    }

    String getKeyAlias(int appUID, int managerUID) {
        String keyAlias = "";
        if (-1 != appUID && -1 != managerUID) {
            if (mStatusManager.getInstallationStatus(false)
                    == CertChainInstallStatus.CERT_1_DOWNLOADED_AND_INSTALLED) {
                MLog.d(ECU_CERT_SERVICE_FUNCTION_ID, GET_KEY_ALIAS);
                if (mECUCertConnector.copyKey(appUID, managerUID, ECU_ALIAS)) {
                    MLog.i(ECU_CERT_SERVICE_FUNCTION_ID, RSA_KEYS_COPY_SUCCESS);
                    keyAlias = ECU_ALIAS;
                } else {
                    MLog.w(ECU_CERT_SERVICE_FUNCTION_ID, RSA_KEYS_COPY_FAIL);
                }
            }
        } else {
            MLog.w(ECU_CERT_SERVICE_FUNCTION_ID, WRONG_ID);
        }
        return keyAlias;
    }

    DetailErrorCode importCertChain(
            CertificateChain certChain, int managerUID, boolean installCert) {
        boolean result = true;
        String serialNumber = "";
        DetailErrorCode resultErrorCode = DetailErrorCode.INTERNAL_ERROR;
        if (-1 == managerUID) {
            MLog.e(ECU_CERT_SERVICE_FUNCTION_ID, WRONG_ID);
            resultErrorCode = DetailErrorCode.INTERNAL_ERROR;
            result = false;
        }
        if (result) {
            if (0 == certChain.getData().size()) {
                MLog.w(ECU_CERT_SERVICE_FUNCTION_ID, EMPTY_CHAIN);
                resultErrorCode = DetailErrorCode.NOT_VALID_CERTIFICATE;
                result = false;
            }
        }
        if (result) {
            serialNumber = mSerialNumber.getSerialNumber();
            if (serialNumber.isEmpty()) {
                MLog.e(ECU_CERT_SERVICE_FUNCTION_ID, WRONG_SERIAL_NUMBER);
                resultErrorCode = DetailErrorCode.INTERNAL_ERROR;
                result = false;
            }
        }
        /* tmp solution: retrieve only leaf certificate from chain */
        if (result) {
            try {
                List<byte[]> data = certChain.getData();
                for (int i = 0; i < data.size(); i++) {
                    byte[] certRaw = data.get(i);
                    Certificate cert = generateCertificate(certRaw);
                    String cn = getCommonName((X509Certificate) cert);
                    MLog.d(ECU_CERT_SERVICE_FUNCTION_ID, CN_INFO, cn, cn.length());
                    if (0 == cn.compareTo(serialNumber)) {
                        certChain = new CertificateChain();
                        certChain.addCerificate(certRaw);
                        break;
                    }
                }
            } catch (CertificateException e) {
                MLog.w(ECU_CERT_SERVICE_FUNCTION_ID, GEN_CERT_EXCEPTION, e.toString());
                resultErrorCode = DetailErrorCode.NOT_VALID_CERTIFICATE;
                result = false;
            }
        }
        if (result) {
            for (String crtName : new String[] {INTERMEDIATE_CERT_1, INTERMEDIATE_CERT_2}) {
                byte[] crtData = readCert(crtName);
                if (crtData.length != 0) {
                    certChain.addCerificate(crtData);
                } else {
                    MLog.w(ECU_CERT_SERVICE_FUNCTION_ID, EMPTY_CERT, crtName);
                    result = false;
                }
            }
        }
        if (result) {
            resultErrorCode =
                    mCertProvider.verifyCertChain(
                            "(^|.*, )CN = " + serialNumber + "(, .*|$)", certChain);
            if (resultErrorCode != DetailErrorCode.CERTIFICATE_VALIDATION_SUCCESS) {
                MLog.w(
                        ECU_CERT_SERVICE_FUNCTION_ID,
                        CERT_CHAIN_VERIFICATION_ERROR,
                        resultErrorCode.ordinal());
                result = false;
            }
        }
        if (result) {
            try {
                if (!mAndroidKeyStoreWrapper.validateCertificateAgainstPrivateKey(
                        ECU_ALIAS, generateCertificate(certChain.getData().get(0)))) {
                    MLog.w(ECU_CERT_SERVICE_FUNCTION_ID, CERT_NOT_FOUND);
                    result = false;
                    resultErrorCode = DetailErrorCode.WRONG_PUBLIC_KEY;
                }
            } catch (CertificateException e) {
                MLog.w(ECU_CERT_SERVICE_FUNCTION_ID, GEN_CERT_EXCEPTION, e.toString());
                resultErrorCode = DetailErrorCode.NOT_VALID_CERTIFICATE;
                result = false;
            }
        }
        if (installCert == true) {
            if (result) {
                try {
                    if (!mAndroidKeyStoreWrapper.storeCertChain(ECU_ALIAS, unpack(certChain))) {
                        MLog.e(ECU_CERT_SERVICE_FUNCTION_ID, CERT_CHAIN_NOT_STORED);
                        resultErrorCode = DetailErrorCode.INTERNAL_ERROR;
                        result = false;
                    }
                } catch (CertificateException e) {
                    resultErrorCode = DetailErrorCode.NOT_VALID_CERTIFICATE;
                    MLog.w(ECU_CERT_SERVICE_FUNCTION_ID, UNPACK_EXCEPTION, e.toString());
                    result = false;
                }
            }
            if (result) {
                if (!mECUCertConnector.storeKey(managerUID, ECU_ALIAS)) {
                    MLog.e(ECU_CERT_SERVICE_FUNCTION_ID, KEYSTORE_NOT_COPIED_TO_OEM);
                    resultErrorCode = DetailErrorCode.INTERNAL_ERROR;
                    result = false;
                }
            }
            if (!updateDTCAndInstallStatus(result)) {
                resultErrorCode = DetailErrorCode.INTERNAL_ERROR;
            }
            if (result) {
                MLog.i(ECU_CERT_SERVICE_FUNCTION_ID, CERT_CHAIN_STORED);
            } else {
                MLog.w(ECU_CERT_SERVICE_FUNCTION_ID, CERT_CHAIN_NOT_STORED);
            }
        }
        MLog.i(ECU_CERT_SERVICE_FUNCTION_ID, RESULT_ERROR_CODE, resultErrorCode.ordinal());
        return resultErrorCode;
    }

    private boolean updateDTCAndInstallStatus(boolean operationStatus) {
        boolean result = true;
        if (operationStatus) {
            if (CertChainInstallStatus.CERT_2_INSTALLATION_ERROR
                    == mStatusManager.getInstallationStatus(false)) {
                if (!mDtcManager.healDTC()) {
                    MLog.e(ECU_CERT_SERVICE_FUNCTION_ID, DTC_NOT_HEALED);
                    result = false;
                }
            }

            if (result) {
                if (!mStatusManager.updateInstallationStatus(
                        CertChainInstallStatus.CERT_1_DOWNLOADED_AND_INSTALLED)) {
                    MLog.e(ECU_CERT_SERVICE_FUNCTION_ID, INSTALLATION_STATUS_NOT_UPDATED);
                    result = false;
                }
            }
        } else {
            if (CertChainInstallStatus.CERT_0_NEVER_DOWNLOADED
                    == mStatusManager.getInstallationStatus(false)) {
                if (!mDtcManager.raiseDTC()) {
                    MLog.e(ECU_CERT_SERVICE_FUNCTION_ID, DTC_NOT_RAISED);
                    result = false;
                }

                if (result) {
                    if (!mStatusManager.updateInstallationStatus(
                            CertChainInstallStatus.CERT_2_INSTALLATION_ERROR)) {
                        MLog.e(ECU_CERT_SERVICE_FUNCTION_ID, INSTALLATION_STATUS_NOT_UPDATED);
                        result = false;
                    }
                }
            }
        }
        return result;
    }

    public boolean resetInstallState() {
        boolean result = true;
        if (!mStatusManager.updateInstallationStatus(
                CertChainInstallStatus.CERT_0_NEVER_DOWNLOADED)) {
            MLog.e(ECU_CERT_SERVICE_FUNCTION_ID, INSTALLATION_STATUS_NOT_UPDATED);
            result = false;
        }
        return result;
    }

    public boolean removeKeyStore(int managerUID, boolean removeBackupRequired) {
        boolean result = true;
        if (!mECUCertConnector.removeKey(managerUID, ECU_ALIAS, removeBackupRequired)) {
            MLog.e(ECU_CERT_SERVICE_FUNCTION_ID, BACKUP_KEY_STORE_NOT_DELETED);
            result = false;
        }
        return result;
    }

    CertChainInstallStatus getInstallationStatus() {
        CertChainInstallStatus status = mStatusManager.getInstallationStatus(false);
        MLog.i(ECU_CERT_SERVICE_FUNCTION_ID, ECU_INSTALLATION_STATUS, status.ordinal());
        return status;
    }

    boolean raiseDTC() {
        boolean result = true;

        if (!mDtcManager.raiseDTC()) {
            MLog.e(ECU_CERT_SERVICE_FUNCTION_ID, DTC_NOT_RAISED);
            result = false;
        }

        if (result) {
            if (!mStatusManager.updateInstallationStatus(
                    CertChainInstallStatus.CERT_2_INSTALLATION_ERROR)) {
                MLog.e(ECU_CERT_SERVICE_FUNCTION_ID, INSTALLATION_STATUS_NOT_UPDATED);
                result = false;
            }
        }

        if (result) {
            MLog.i(ECU_CERT_SERVICE_FUNCTION_ID, TOTAL_RETRIAL_IS_MAX_SUCCESS);
        }

        return result;
    }

    public byte[] exportCertificate(CertType type) {
        byte[] cert = new byte[0];
        CertChainInstallStatus status = mStatusManager.getInstallationStatus(false);
        if (status == CertChainInstallStatus.CERT_1_DOWNLOADED_AND_INSTALLED) {
            Certificate certificate = mAndroidKeyStoreWrapper.exportCertificate(ECU_ALIAS);
            if (certificate != null) {
                try {
                    switch (type) {
                        case PEM:
                            StringWriter pemString = new StringWriter();
                            JcaPEMWriter pemWriter = new JcaPEMWriter(pemString);
                            pemWriter.writeObject(certificate);
                            pemWriter.flush();
                            pemWriter.close();

                            cert = pemString.toString().getBytes(StandardCharsets.UTF_8);
                            break;
                        case DER:
                            cert = certificate.getEncoded();
                            break;
                        case DER_BASE64:
                            cert =
                                    Base64.getEncoder()
                                            .encodeToString(certificate.getEncoded())
                                            .getBytes();
                            break;
                        default:
                            break;
                    }
                } catch (IOException | CertificateEncodingException e) {
                    MLog.e(ECU_CERT_SERVICE_FUNCTION_ID, GET_CERTIFICATE_EXCEPTION, e.toString());
                }
            }
        } else {
            MLog.e(ECU_CERT_SERVICE_FUNCTION_ID, ECU_INSTALLATION_STATUS, status.toString());
        }
        return cert;
    }

    String getECUIdentityNumber() {
        String serialNumber = mSerialNumber.getSerialNumber();
        MLog.i(ECU_CERT_SERVICE_FUNCTION_ID, SERIAL_NUMBER, serialNumber, serialNumber.length());
        return serialNumber;
    }

    private Certificate generateCertificate(byte[] certRawData) throws CertificateException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        Certificate certificate = cf.generateCertificate(new ByteArrayInputStream(certRawData));
        return certificate;
    }

    private String getCommonName(X509Certificate cert) throws CertificateEncodingException {
        X500Name x500name = new JcaX509CertificateHolder(cert).getSubject();
        RDN cn = x500name.getRDNs(BCStyle.CN)[0];
        return IETFUtils.valueToString(cn.getFirst().getValue());
    }

    private Certificate[] unpack(CertificateChain cert) throws CertificateException {
        List<byte[]> certList = cert.getData();
        Certificate[] chain = new Certificate[certList.size()];
        for (int i = 0; i < certList.size(); i++) {
            chain[i] = generateCertificate(certList.get(i));
        }
        return chain;
    }

    private byte[] readCert(String certName) {
        byte[] res;
        try (InputStream is = mContext.getAssets().open(certName)) {
            int bytesAvailable = is.available();
            res = new byte[bytesAvailable];
            int bytesRead = is.read(res);
            if (bytesRead != bytesAvailable) {
                MLog.w(
                        ECU_CERT_SERVICE_FUNCTION_ID,
                        CERT_READ_WRONG_BYTES_AMOUNT,
                        bytesRead,
                        bytesAvailable);
            }
        } catch (IOException e) {
            MLog.w(ECU_CERT_SERVICE_FUNCTION_ID, CERT_READ_EXCEPTION, certName, e.toString());
            res = new byte[0];
        }

        return res;
    }

    public DetailErrorCode importCertificate(byte[] cert, int managerUID) {
        CertificateChain certChain = new CertificateChain(Arrays.asList(cert));
        return importCertChain(certChain, managerUID, true);
    }
}
