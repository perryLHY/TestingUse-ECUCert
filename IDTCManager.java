/*
 * COPYRIGHT (C) 2020 MITSUBISHI ELECTRIC CORPORATION
 * ALL RIGHTS RESERVED
 */

package com.mitsubishielectric.ahu.efw.ecucertservice;

interface IDTCManager {

    /*
     * Heal DTC
     *
     * @return Operation result
     */
    boolean healDTC();

    /*
     * Raise DTC
     *
     * @return Operation result
     */
    boolean raiseDTC();
}
