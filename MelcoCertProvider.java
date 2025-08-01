/*
 * COPYRIGHT (C) 2020 MITSUBISHI ELECTRIC CORPORATION
 * ALL RIGHTS RESERVED
 */

package com.mitsubishielectric.ahu.efw.ecucertservice;

import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.ECU_CERT_SERVICE_FUNCTION_ID;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.NOT_CONNECTED_TO_ESM;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.VERIFY_CERT_CHAIN_EXCEPTION;
import static com.mitsubishielectric.ahu.efw.lib.common.Const.CERTPROVIDER_SERVICE;

import android.os.IBinder;
import android.os.RemoteException;
import com.mitsubishielectric.ahu.efw.lib.ecucertmgr.CertificateChain;
import com.mitsubishielectric.ahu.efw.lib.ecucertmgr.DetailErrorCode;
import com.mitsubishielectric.ahu.efw.lib.extendedservicemanager.ExtSrvManager;
import com.mitsubishielectric.ahu.efw.lib.logdogcommonclasslib.MLog;
import com.mitsubishielectric.ahu.efw.lib.melcocertprovider.libcertproviderservice.CertProviderServiceManager;
import com.mitsubishielectric.ahu.efw.lib.melcocertprovider.libcertproviderservice.CertificateVerificationStatus;
import com.mitsubishielectric.ahu.efw.lib.melcocertprovider.libcertproviderservice.ConfigType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class MelcoCertProvider implements IMelcoCertProvider {
    private static final Map<CertificateVerificationStatus, DetailErrorCode> mErrorMap =
            new HashMap<>();

    static {
        mErrorMap.put(
                CertificateVerificationStatus.OK, DetailErrorCode.CERTIFICATE_VALIDATION_SUCCESS);

        mErrorMap.put(
                CertificateVerificationStatus.ERR_UNSPECIFIED, DetailErrorCode.INTERNAL_ERROR);
        mErrorMap.put(
                CertificateVerificationStatus.ERR_UNABLE_TO_GET_ISSUER_CERT,
                DetailErrorCode.UNKNOWN_ISSUER);
        mErrorMap.put(
                CertificateVerificationStatus.ERR_UNABLE_TO_GET_CRL,
                DetailErrorCode.MISSING_CDP); // / Not really but close
        mErrorMap.put(
                CertificateVerificationStatus.ERR_UNABLE_TO_DECRYPT_CERT_SIGNATURE,
                DetailErrorCode.SIGNATURE_VALIDATION_ERROR);
        mErrorMap.put(
                CertificateVerificationStatus.ERR_UNABLE_TO_DECRYPT_CRL_SIGNATURE,
                DetailErrorCode.CRL_SIGNATURE_VALIDATION_ERROR);
        mErrorMap.put(
                CertificateVerificationStatus.ERR_UNABLE_TO_DECODE_ISSUER_PUBLIC_KEY,
                DetailErrorCode.UNKNOWN_ISSUER); // / Not really but close
        mErrorMap.put(
                CertificateVerificationStatus.ERR_CERT_SIGNATURE_FAILURE,
                DetailErrorCode.SIGNATURE_VALIDATION_ERROR);
        mErrorMap.put(
                CertificateVerificationStatus.ERR_CRL_SIGNATURE_FAILURE,
                DetailErrorCode.CRL_SIGNATURE_VALIDATION_ERROR);
        mErrorMap.put(
                CertificateVerificationStatus.ERR_CERT_NOT_YET_VALID,
                DetailErrorCode.OUT_OF_CERTIFICATE_VALIDITY_PERIOD);
        mErrorMap.put(
                CertificateVerificationStatus.ERR_CERT_HAS_EXPIRED,
                DetailErrorCode.OUT_OF_CERTIFICATE_VALIDITY_PERIOD);
        mErrorMap.put(
                CertificateVerificationStatus.ERR_CRL_NOT_YET_VALID,
                DetailErrorCode.OUT_OF_CRL_VALIDITY_PERIOD);
        mErrorMap.put(
                CertificateVerificationStatus.ERR_CRL_HAS_EXPIRED,
                DetailErrorCode.OUT_OF_CRL_VALIDITY_PERIOD);
        mErrorMap.put(
                CertificateVerificationStatus.ERR_ERROR_IN_CERT_NOT_BEFORE_FIELD,
                DetailErrorCode.NOT_VALID_CERTIFICATE);
        mErrorMap.put(
                CertificateVerificationStatus.ERR_ERROR_IN_CERT_NOT_AFTER_FIELD,
                DetailErrorCode.NOT_VALID_CERTIFICATE);
        mErrorMap.put(
                CertificateVerificationStatus.ERR_ERROR_IN_CRL_LAST_UPDATE_FIELD,
                DetailErrorCode.INTERNAL_ERROR); // / not valid CRL
        mErrorMap.put(
                CertificateVerificationStatus.ERR_ERROR_IN_CRL_NEXT_UPDATE_FIELD,
                DetailErrorCode.INTERNAL_ERROR); // / not valid CRL
        mErrorMap.put(CertificateVerificationStatus.ERR_OUT_OF_MEM, DetailErrorCode.INTERNAL_ERROR);
        mErrorMap.put(
                CertificateVerificationStatus.ERR_DEPTH_ZERO_SELF_SIGNED_CERT,
                DetailErrorCode.NOT_VALID_CERTIFICATE);
        mErrorMap.put(
                CertificateVerificationStatus.ERR_SELF_SIGNED_CERT_IN_CHAIN,
                DetailErrorCode.INTERNAL_ERROR); // / not valid chain
        mErrorMap.put(
                CertificateVerificationStatus.ERR_UNABLE_TO_GET_ISSUER_CERT_LOCALLY,
                DetailErrorCode.INTERNAL_ERROR);
        mErrorMap.put(
                CertificateVerificationStatus.ERR_UNABLE_TO_VERIFY_LEAF_SIGNATURE,
                DetailErrorCode.SIGNATURE_VALIDATION_ERROR);
        mErrorMap.put(
                CertificateVerificationStatus.ERR_CERT_CHAIN_TOO_LONG,
                DetailErrorCode.INTERNAL_ERROR); // / not valid chain
        mErrorMap.put(
                CertificateVerificationStatus.ERR_CERT_REVOKED,
                DetailErrorCode.REVOKED_CERTIFICATE);
        mErrorMap.put(
                CertificateVerificationStatus.ERR_INVALID_CA,
                DetailErrorCode.INTERNAL_ERROR); // / CA related
        mErrorMap.put(
                CertificateVerificationStatus.ERR_PATH_LENGTH_EXCEEDED,
                DetailErrorCode.INTERNAL_ERROR);
        mErrorMap.put(
                CertificateVerificationStatus.ERR_INVALID_PURPOSE,
                DetailErrorCode.INTERNAL_ERROR); // / CA related
        mErrorMap.put(
                CertificateVerificationStatus.ERR_CERT_UNTRUSTED,
                DetailErrorCode.INTERNAL_ERROR); // / CA related
        mErrorMap.put(
                CertificateVerificationStatus.ERR_CERT_REJECTED,
                DetailErrorCode.INTERNAL_ERROR); // / CA related
        mErrorMap.put(
                CertificateVerificationStatus.ERR_SUBJECT_ISSUER_MISMATCH,
                DetailErrorCode.INTERNAL_ERROR); // / not error - debug status
        mErrorMap.put(
                CertificateVerificationStatus.ERR_AKID_SKID_MISMATCH,
                DetailErrorCode.INTERNAL_ERROR); // / not error - debug status
        mErrorMap.put(
                CertificateVerificationStatus.ERR_AKID_ISSUER_SERIAL_MISMATCH,
                DetailErrorCode.INTERNAL_ERROR); // / not error - debug status
        mErrorMap.put(
                CertificateVerificationStatus.ERR_KEYUSAGE_NO_CERTSIGN,
                DetailErrorCode.INTERNAL_ERROR); // / not error - debug status
        mErrorMap.put(
                CertificateVerificationStatus.ERR_UNABLE_TO_GET_CRL_ISSUER,
                DetailErrorCode.NOT_VALID_CERTIFICATE);
        mErrorMap.put(
                CertificateVerificationStatus.ERR_UNHANDLED_CRITICAL_EXTENSION,
                DetailErrorCode.NOT_VALID_CERTIFICATE);
        mErrorMap.put(
                CertificateVerificationStatus.ERR_KEYUSAGE_NO_CRL_SIGN,
                DetailErrorCode.INTERNAL_ERROR); // / CA related
        mErrorMap.put(
                CertificateVerificationStatus.ERR_UNHANDLED_CRITICAL_CRL_EXTENSION,
                DetailErrorCode.INTERNAL_ERROR); // / CA related
        mErrorMap.put(
                CertificateVerificationStatus.ERR_INVALID_NON_CA,
                DetailErrorCode.INTERNAL_ERROR); // / CA related
        mErrorMap.put(
                CertificateVerificationStatus.ERR_PROXY_PATH_LENGTH_EXCEEDED,
                DetailErrorCode.INTERNAL_ERROR);
        mErrorMap.put(
                CertificateVerificationStatus.ERR_KEYUSAGE_NO_DIGITAL_SIGNATURE,
                DetailErrorCode.INTERNAL_ERROR); // / CA related
        mErrorMap.put(
                CertificateVerificationStatus.ERR_PROXY_CERTIFICATES_NOT_ALLOWED,
                DetailErrorCode.INTERNAL_ERROR);
        mErrorMap.put(
                CertificateVerificationStatus.ERR_INVALID_EXTENSION,
                DetailErrorCode.NOT_VALID_CERTIFICATE);
        mErrorMap.put(
                CertificateVerificationStatus.ERR_INVALID_POLICY_EXTENSION,
                DetailErrorCode.NOT_VALID_CERTIFICATE);
        mErrorMap.put(
                CertificateVerificationStatus.ERR_NO_EXPLICIT_POLICY,
                DetailErrorCode.NOT_VALID_CERTIFICATE);
        mErrorMap.put(
                CertificateVerificationStatus.ERR_DIFFERENT_CRL_SCOPE,
                DetailErrorCode.INTERNAL_ERROR);
        mErrorMap.put(
                CertificateVerificationStatus.ERR_UNSUPPORTED_EXTENSION_FEATURE,
                DetailErrorCode.NOT_VALID_CERTIFICATE);
        mErrorMap.put(
                CertificateVerificationStatus.ERR_UNNESTED_RESOURCE,
                DetailErrorCode.NOT_VALID_CERTIFICATE);
        mErrorMap.put(
                CertificateVerificationStatus.ERR_PERMITTED_VIOLATION,
                DetailErrorCode.INTERNAL_ERROR); // / CA related
        mErrorMap.put(
                CertificateVerificationStatus.ERR_EXCLUDED_VIOLATION,
                DetailErrorCode.INTERNAL_ERROR); // / CA related
        mErrorMap.put(
                CertificateVerificationStatus.ERR_SUBTREE_MINMAX,
                DetailErrorCode.INTERNAL_ERROR); // / CA related
        mErrorMap.put(
                CertificateVerificationStatus.ERR_APPLICATION_VERIFICATION,
                DetailErrorCode.INTERNAL_ERROR); // / CA related
        mErrorMap.put(
                CertificateVerificationStatus.ERR_UNSUPPORTED_CONSTRAINT_TYPE,
                DetailErrorCode.INTERNAL_ERROR); // / CA related
        mErrorMap.put(
                CertificateVerificationStatus.ERR_UNSUPPORTED_CONSTRAINT_SYNTAX,
                DetailErrorCode.INTERNAL_ERROR); // / CA related
        mErrorMap.put(
                CertificateVerificationStatus.ERR_UNSUPPORTED_NAME_SYNTAX,
                DetailErrorCode.INTERNAL_ERROR); // / CA related
        mErrorMap.put(
                CertificateVerificationStatus.ERR_CRL_PATH_VALIDATION_ERROR,
                DetailErrorCode.INTERNAL_ERROR); // / CA related

        mErrorMap.put(CertificateVerificationStatus.INTERNAL_ERROR, DetailErrorCode.INTERNAL_ERROR);
        mErrorMap.put(
                CertificateVerificationStatus.SUBJECT_NAME_INVALID,
                DetailErrorCode.WRONG_COMMON_NAME);
        mErrorMap.put(
                CertificateVerificationStatus.ISSUER_NAME_INVALID, DetailErrorCode.UNKNOWN_ISSUER);
    }

    @Override
    public DetailErrorCode verifyCertChain(String subjectName, CertificateChain certChain) {
        DetailErrorCode errorCode = DetailErrorCode.INTERNAL_ERROR;
        ExtSrvManager extSrvManager = ExtSrvManager.getInstance();
        if (extSrvManager != null) {
            IBinder binder = extSrvManager.getService(CERTPROVIDER_SERVICE);
            if (binder != null) {
                CertProviderServiceManager.setBinder(binder);
                try {
                    CertProviderServiceManager certProvider = new CertProviderServiceManager();
                    certProvider.init(ConfigType.ECU);
                    certProvider.setSubjectName(subjectName);
                    List<byte[]> certChainList = certChain.getData();
                    for (int i = 1; i < certChainList.size(); i++) {
                        certProvider.addCertToChain(certChainList.get(i));
                    }
                    CertificateVerificationStatus verificationStatus =
                            CertificateVerificationStatus.fromInt(
                                    certProvider.verifyCertificate(certChainList.get(0)).ordinal());
                    errorCode = mErrorMap.get(verificationStatus);
                } catch (RemoteException | IllegalStateException e) {
                    MLog.w(ECU_CERT_SERVICE_FUNCTION_ID, VERIFY_CERT_CHAIN_EXCEPTION, e.toString());
                }
            }
        } else {
            MLog.e(ECU_CERT_SERVICE_FUNCTION_ID, NOT_CONNECTED_TO_ESM);
        }
        return errorCode;
    }
}
