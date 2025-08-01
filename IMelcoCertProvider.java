/*
 * COPYRIGHT (C) 2020 MITSUBISHI ELECTRIC CORPORATION
 * ALL RIGHTS RESERVED
 */

package com.mitsubishielectric.ahu.efw.ecucertservice;

import com.mitsubishielectric.ahu.efw.lib.ecucertmgr.CertificateChain;
import com.mitsubishielectric.ahu.efw.lib.ecucertmgr.DetailErrorCode;

interface IMelcoCertProvider {

    /*
     * Verify certificate chain
     *
     * @param certChain - Certificate chain
     * @param subjectName -subject name
     *
     * @return Detail error code
     */
    DetailErrorCode verifyCertChain(String subjectName, CertificateChain certChain);
}
