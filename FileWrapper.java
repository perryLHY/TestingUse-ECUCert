/*
 * COPYRIGHT (C) 2020 MITSUBISHI ELECTRIC CORPORATION
 * ALL RIGHTS RESERVED
 */

package com.mitsubishielectric.ahu.efw.ecucertservice;

import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.ECU_CERT_SERVICE_FUNCTION_ID;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.READ_EXCEPTION;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.WRITE_EXCEPTION;

import com.mitsubishielectric.ahu.efw.lib.logdogcommonclasslib.MLog;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

final class FileWrapper implements IFileWrapper {
    @Override
    public boolean write(File file, int value) {
        boolean result = true;
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            fileOutputStream.write(value);
            fileOutputStream.flush();
            fileOutputStream.getFD().sync();
        } catch (IOException e) {
            MLog.w(ECU_CERT_SERVICE_FUNCTION_ID, WRITE_EXCEPTION, e.toString());
            result = false;
        }
        return result;
    }

    @Override
    public int read(File file) {
        int value;
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            value = fileInputStream.read();
        } catch (IOException e) {
            MLog.w(ECU_CERT_SERVICE_FUNCTION_ID, READ_EXCEPTION, e.toString());
            value = -1;
        }
        return value;
    }
}
