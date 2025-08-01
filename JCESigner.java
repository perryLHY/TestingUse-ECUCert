/*
 * COPYRIGHT (C) 2019 MITSUBISHI ELECTRIC CORPORATION
 * ALL RIGHTS RESERVED
 */

package com.mitsubishielectric.ahu.efw.ecucertservice;

import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.ECU_CERT_SERVICE_FUNCTION_ID;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.GET_OUTPUT_STREAM_EXCEPTION;

import com.mitsubishielectric.ahu.efw.lib.logdogcommonclasslib.MLog;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.ContentSigner;

final class JCESigner implements ContentSigner {
    private static Map<String, AlgorithmIdentifier> ALGOS = new HashMap<>();

    private String mAlgorithm;
    private Signature mSignature;
    private ByteArrayOutputStream mOutputStream;

    static {
        ALGOS.put(
                "SHA256withRSA".toLowerCase(Locale.getDefault()),
                new AlgorithmIdentifier(new ASN1ObjectIdentifier("1.2.840.113549.1.1.11")));
        ALGOS.put(
                "SHA1withRSA".toLowerCase(Locale.getDefault()),
                new AlgorithmIdentifier(new ASN1ObjectIdentifier("1.2.840.113549.1.1.5")));
    }

    public JCESigner(PrivateKey privateKey, String sigAlgorithm)
            throws NoSuchAlgorithmException, InvalidKeyException {
        mAlgorithm = sigAlgorithm.toLowerCase(Locale.getDefault());
        this.mOutputStream = new ByteArrayOutputStream();
        this.mSignature = Signature.getInstance(sigAlgorithm);
        this.mSignature.initSign(privateKey);
    }

    @Override
    public AlgorithmIdentifier getAlgorithmIdentifier() {
        AlgorithmIdentifier id = ALGOS.get(mAlgorithm);
        if (id == null) {
            throw new IllegalArgumentException("Does not support algorithm: " + mAlgorithm);
        }
        return id;
    }

    @Override
    public OutputStream getOutputStream() {
        return mOutputStream;
    }

    @Override
    public byte[] getSignature() {
        byte[] sign = new byte[0];
        try {
            mSignature.update(mOutputStream.toByteArray());
            sign = mSignature.sign();
        } catch (SignatureException e) {
            MLog.w(ECU_CERT_SERVICE_FUNCTION_ID, GET_OUTPUT_STREAM_EXCEPTION, e.toString());
        }
        return sign;
    }
}
