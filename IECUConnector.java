/*
 * COPYRIGHT (C) 2019 MITSUBISHI ELECTRIC CORPORATION
 * ALL RIGHTS RESERVED
 */

package com.mitsubishielectric.ahu.efw.ecucertservice;

interface IECUConnector {
    /*
     * Copy Key Store for application
     *
     * @param appUID - application uid
     * @param managerUID - service uid
     * @param alias - alias
     *
     * @return Operation result
     */
    boolean copyKey(int appUID, int managerUID, String alias);

    /*
     * Restore Key Store from persistent storage
     *
     * @param managerUID - service uid
     * @param alias - alias
     *
     * @return Operation result
     */
    boolean restoreKey(int managerUID, String alias);

    /*
     * Store Key Store to persistent storage
     *
     * @param managerUID - service uid
     * @param alias - alias
     *
     * @return Operation result
     */
    boolean storeKey(int managerUID, String alias);

    /*
     * Remove key store file from persistent and data storage
     *
     * @param managerUID - service uid
     * @param alias - alias
     * @param removeBackupRequired - whether backup keystore needs to be removed
     *
     * @return Operation result
     */
    boolean removeKey(int managerUID, String alias, boolean removeBackupRequired);
}
