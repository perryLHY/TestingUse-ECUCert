/*
 * COPYRIGHT (C) 2020 MITSUBISHI ELECTRIC CORPORATION
 * ALL RIGHTS RESERVED
 */

package com.mitsubishielectric.ahu.efw.ecucertservice;

interface ISerialNumber {

    /*
     * Get ECU Serial number
     *
     * @return ECU Serial number
     */
    String getSerialNumber();

    /*
     * Get subject name
     *
     * @return subject name
     */
    String getSubjectName();
}
