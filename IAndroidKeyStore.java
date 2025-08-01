/*
 * COPYRIGHT (C) 2019 MITSUBISHI ELECTRIC CORPORATION
 * ALL RIGHTS RESERVED
 */

package com.mitsubishielectric.ahu.efw.ecucertservice;

import java.security.KeyPair;
import java.security.cert.Certificate;
import java.util.List;

interface IAndroidKeyStore {

    /*
     * Generate RSA key pair in Android Key Store
     *
     * @param alias - Key Store alias
     *
     * @return Operation result
     */
    boolean generateRSAKeyPair(String alias);

    /*
     * Get RSA Key pair from Android Key Store
     *
     * @param alias - Key Store alias
     *
     * @return Certificate chain
     */
    KeyPair getRSAKeyPair(String alias);

    /*
     * Is Key Entry
     *
     * @param alias - Key Store alias
     *
     * @return true if the entry identified by the given alias is a key-related entry,
     *         false otherwise.
     */
    boolean isKeyEntry(String alias);

    /*
     * Store certificate chain to Android Key Store
     *
     * @param alias - Key Store alias
     * @param chain - Certificate chain
     *
     * @return Operation result
     */
    boolean storeCertChain(String alias, Certificate[] chain);

    /*
     * Validate certificate against private key
     *
     * @param alias - Key Store alias
     * @param cert - Certificate
     *
     * @return Operation result
     */
    boolean validateCertificateAgainstPrivateKey(String alias, Certificate cert);

    List<byte[]> getCertificateChain(String alias);

    /*
     * Export certificate
     *
     * @param alias - Key Store alias
     *
     * @return certificate
     */
    Certificate exportCertificate(String alias);
}
