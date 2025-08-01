/*
 * COPYRIGHT (C) 2019 MITSUBISHI ELECTRIC CORPORATION
 * ALL RIGHTS RESERVED
 */

package com.mitsubishielectric.ahu.efw.ecucertservice;

import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.ECU_CERT_SERVICE_FUNCTION_ID;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.GENERATE_CSR_EXCEPTION;

import com.mitsubishielectric.ahu.efw.lib.ecucertmgr.CSRType;
import com.mitsubishielectric.ahu.efw.lib.logdogcommonclasslib.MLog;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;

class BCProviderWrapper implements ICSRGenerator {

    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";

    @Override
    public byte[] generateCSR(CSRType csrType, KeyPair keyPair, String subjectName) {
        byte[] csrByteArr = new byte[0];
        try {
            ContentSigner signer = new JCESigner(keyPair.getPrivate(), SIGNATURE_ALGORITHM);
            PKCS10CertificationRequestBuilder csrBuilder =
                    new JcaPKCS10CertificationRequestBuilder(
                            new X500Name(subjectName), keyPair.getPublic());
            ExtensionsGenerator extensionsGenerator = new ExtensionsGenerator();
            extensionsGenerator.addExtension(
                    Extension.basicConstraints, false, new BasicConstraints(false));
            csrBuilder.addAttribute(
                    PKCSObjectIdentifiers.pkcs_9_at_extensionRequest,
                    extensionsGenerator.generate());
            PKCS10CertificationRequest csr = csrBuilder.build(signer);

            switch (csrType) {
                case PEM:
                    StringWriter pemString = new StringWriter();
                    JcaPEMWriter pemWriter = new JcaPEMWriter(pemString);

                    pemWriter.writeObject(csr);
                    pemWriter.flush();
                    pemWriter.close();

                    csrByteArr = pemString.toString().getBytes(StandardCharsets.UTF_8);
                    break;
                case DER:
                    csrByteArr = csr.getEncoded();
                    break;
                case DER_BASE64:
                    csrByteArr = Base64.getEncoder().encodeToString(csr.getEncoded()).getBytes();
                    break;
                default:
                    break;
            }

        } catch (NoSuchAlgorithmException | InvalidKeyException | IOException e) {
            MLog.w(ECU_CERT_SERVICE_FUNCTION_ID, GENERATE_CSR_EXCEPTION, e.toString());
        }
        return csrByteArr;
    }
}
