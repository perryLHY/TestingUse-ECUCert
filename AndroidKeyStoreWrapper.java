/*
 * COPYRIGHT (C) 2019 MITSUBISHI ELECTRIC CORPORATION
 * ALL RIGHTS RESERVED
 */

package com.mitsubishielectric.ahu.efw.ecucertservice;

import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.ECU_CERT_SERVICE_FUNCTION_ID;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.GENERATE_RSA_KEY_PAIR_EXCEPTION;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.GET_CERTIFICATE_EXCEPTION;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.GET_CERT_CHAIN_EXCEPTION;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.GET_CERT_EXCEPTION;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.GET_ENTRY_EXCEPTION;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.IS_KEY_ENTRY_EXCEPTION;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.NOT_INSTANCE_OF_PRIVATE_KEY_ENTRY;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.STORE_CERT_CHAIN_EXCEPTION;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.VALIDATE_CERT_EXCEPTION;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import com.mitsubishielectric.ahu.efw.lib.logdogcommonclasslib.MLog;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

class AndroidKeyStoreWrapper implements IAndroidKeyStore {

    private KeyStore mKeyStore;

    private static final int RSA_KEY_SIZE = 2048;

    AndroidKeyStoreWrapper()
            throws CertificateException, NoSuchAlgorithmException, IOException, KeyStoreException {
        mKeyStore = KeyStore.getInstance("AndroidKeyStore");
        mKeyStore.load(null);
    }

    @Override
    public boolean generateRSAKeyPair(String alias) {
        boolean result = false;
        try {
            KeyPairGenerator kpg =
                    KeyPairGenerator.getInstance(
                            KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore");
            kpg.initialize(
                    new KeyGenParameterSpec.Builder(
                                    alias,
                                    KeyProperties.PURPOSE_SIGN
                                            | KeyProperties.PURPOSE_VERIFY
                                            | KeyProperties.PURPOSE_ENCRYPT
                                            | KeyProperties.PURPOSE_DECRYPT)
                            .setKeySize(RSA_KEY_SIZE)
                            .setDigests(
                                    KeyProperties.DIGEST_SHA256,
                                    KeyProperties.DIGEST_SHA512,
                                    KeyProperties.DIGEST_SHA384,
                                    KeyProperties.DIGEST_NONE)
                            .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
                            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                            .build());
            kpg.generateKeyPair();
            result = true;
        } catch (InvalidAlgorithmParameterException
                | NoSuchAlgorithmException
                | NoSuchProviderException e) {
            printStackTrace(GENERATE_RSA_KEY_PAIR_EXCEPTION, e);
        }
        return result;
    }

    @Override
    public KeyPair getRSAKeyPair(String alias) {
        boolean result = true;
        PrivateKey privateKey = null;
        PublicKey publicKey = null;
        KeyPair keyPair = null;

        KeyStore.Entry entry = null;
        try {
            entry = mKeyStore.getEntry(alias, null);
        } catch (Exception e) {
            printStackTrace(GET_ENTRY_EXCEPTION, e);
            result = false;
        }

        if (result) {
            if (!(entry instanceof KeyStore.PrivateKeyEntry)) {
                MLog.w(ECU_CERT_SERVICE_FUNCTION_ID, NOT_INSTANCE_OF_PRIVATE_KEY_ENTRY);
                result = false;
            }
        }

        if (result) {
            privateKey = ((KeyStore.PrivateKeyEntry) entry).getPrivateKey();
            try {
                publicKey = mKeyStore.getCertificate(alias).getPublicKey();
            } catch (KeyStoreException e) {
                printStackTrace(GET_CERT_EXCEPTION, e);
                result = false;
            }
        }

        if (result) {
            keyPair = new KeyPair(publicKey, privateKey);
        }

        return keyPair;
    }

    @Override
    public boolean isKeyEntry(String alias) {
        boolean result = false;
        try {
            result = mKeyStore.isKeyEntry(alias);
        } catch (KeyStoreException e) {
            printStackTrace(IS_KEY_ENTRY_EXCEPTION, e);
        }
        return result;
    }

    @Override
    public boolean storeCertChain(String alias, Certificate[] chain) {
        boolean result = true;
        try {
            mKeyStore.setKeyEntry(alias, mKeyStore.getKey(alias, null), null, chain);
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            printStackTrace(STORE_CERT_CHAIN_EXCEPTION, e);
            result = false;
        }
        return result;
    }

    public boolean validateCertificateAgainstPrivateKey(String alias, Certificate cert) {
        boolean result = false;
        try {
            byte[] challenge = "this is a string to be signed".getBytes(StandardCharsets.UTF_8);

            Signature sig = Signature.getInstance("SHA256withRSA");
            KeyPair rsaKeyPair = getRSAKeyPair(alias);
            if (null != rsaKeyPair) {
                sig.initSign(rsaKeyPair.getPrivate());
                sig.update(challenge);
                byte[] signature = sig.sign();

                sig.initVerify(cert.getPublicKey());
                sig.update(challenge);
                result = sig.verify(signature);
            }
        } catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException e) {
            printStackTrace(VALIDATE_CERT_EXCEPTION, e);
        }
        return result;
    }

    @Override
    public List<byte[]> getCertificateChain(String alias) {
        List<byte[]> certChain = new ArrayList();
        try {
            Certificate[] chain = mKeyStore.getCertificateChain(alias);
            for (int i = 0; i < chain.length; i++) {
                certChain.add(chain[i].getEncoded());
            }
        } catch (Exception e) {
            printStackTrace(GET_CERT_CHAIN_EXCEPTION, e);
        }
        return certChain;
    }

    @Override
    public Certificate exportCertificate(String alias) {
        Certificate certificate = null;
        try {
            certificate = mKeyStore.getCertificate(alias);
        } catch (KeyStoreException e) {
            MLog.w(ECU_CERT_SERVICE_FUNCTION_ID, GET_CERTIFICATE_EXCEPTION, e.toString());
        }
        return certificate;
    }

    void printStackTrace(int logId, Exception e) {
        MLog.w(ECU_CERT_SERVICE_FUNCTION_ID, logId, e.toString());
        for (StackTraceElement ste : e.getStackTrace()) {
            MLog.w(ECU_CERT_SERVICE_FUNCTION_ID, logId, ste.toString());
        }
    }
}
