/*
 * COPYRIGHT (C) 2020 MITSUBISHI ELECTRIC CORPORATION
 * ALL RIGHTS RESERVED
 */

package com.mitsubishielectric.ahu.efw.ecucertservice;

import com.mitsubishielectric.ahu.efw.lib.ecucertmgr.CertChainInstallStatus;

interface IInstallationStatusManager {

    /*
     * Get certificate chain installation status
     *
     * @param verifyCertificate verify leaf certificate
     *
     * @return installation status
     */
    CertChainInstallStatus getInstallationStatus(boolean verifyCertificate);

    /*
     * Update certificate chain installation status
     *
     * @param installStatus - certificate chain installation status
     *
     * @return Operation result
     */
    boolean updateInstallationStatus(CertChainInstallStatus installStatus);
}
