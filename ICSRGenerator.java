/*
 * COPYRIGHT (C) 2019 MITSUBISHI ELECTRIC CORPORATION
 * ALL RIGHTS RESERVED
 */

package com.mitsubishielectric.ahu.efw.ecucertservice;

import com.mitsubishielectric.ahu.efw.lib.ecucertmgr.CSRType;
import java.security.KeyPair;

interface ICSRGenerator {

    /*
     * Generate certificate signing request
     *
     * @param csrType - certificate signing request type
     * @param keyPair - RSA key pair
     * @param subjectName - subject name
     *
     * @return certificate signing request
     */
    byte[] generateCSR(CSRType csrType, KeyPair keyPair, String subjectName);
}
