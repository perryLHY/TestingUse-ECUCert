/*
 * COPYRIGHT (C) 2020 MITSUBISHI ELECTRIC CORPORATION
 * ALL RIGHTS RESERVED
 */

package com.mitsubishielectric.ahu.efw.ecucertservice;

import java.io.File;

interface IFileWrapper {

    /*
     * Write integer value to file
     *
     * @param file - file name
     * @param value - integer value
     *
     * @return Operation result
     */
    boolean write(File file, int value);

    /*
     * Read integer value from file
     *
     * @param file - file name
     *
     * @return Integer value
     */
    int read(File file);
}
