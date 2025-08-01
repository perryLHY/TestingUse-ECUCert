/*
 * COPYRIGHT (C) 2020 MITSUBISHI ELECTRIC CORPORATION
 * ALL RIGHTS RESERVED
 */

package com.mitsubishielectric.ahu.efw.ecucertservice;

import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.DTC_HEAL;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.DTC_RAISE;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.ECU_CERT_SERVICE_FUNCTION_ID;
import static com.mitsubishielectric.ahu.efw.lib.common.Const.CPU_COM_SERVICE;

import android.os.IBinder;
import com.mitsubishielectric.ahu.efw.lib.cpucomservice.CpuComManager;
import com.mitsubishielectric.ahu.efw.lib.cpucomservice.CpuCommand;
import com.mitsubishielectric.ahu.efw.lib.extendedservicemanager.ExtSrvManager;
import com.mitsubishielectric.ahu.efw.lib.logdogcommonclasslib.MLog;

final class DTCManager implements IDTCManager {

    private static final int DTC_NOTIFICATION_CMD = 0xD2;
    private static final int DTC_NOTIFICATION_SUB_CMD = 0x85;

    // TODO: update DTC code and status
    // data (3 byte) + status (1 byte)
    private static final byte[] DTC_OCCUR =
            new byte[] {(byte) 0xf0, (byte) 0x33, (byte) 0x00, (byte) 0x01};
    // data (3 byte) + status (1 byte)
    private static final byte[] DTC_RESTORE =
            new byte[] {(byte) 0xf0, (byte) 0x33, (byte) 0x00, (byte) 0x02};

    @Override
    public boolean healDTC() {
        MLog.i(ECU_CERT_SERVICE_FUNCTION_ID, DTC_HEAL);
        return sendDtcCmd(DTC_RESTORE);
    }

    @Override
    public boolean raiseDTC() {
        MLog.i(ECU_CERT_SERVICE_FUNCTION_ID, DTC_RAISE);
        return sendDtcCmd(DTC_OCCUR);
    }

    private boolean sendDtcCmd(byte[] data) {
        boolean result = false;
        IBinder cpucomBinder = ExtSrvManager.getInstance().getService(CPU_COM_SERVICE);
        if (cpucomBinder != null) {
            CpuComManager.setBinder(cpucomBinder);
            CpuComManager cpuComManager = CpuComManager.getInstance();
            CpuCommand mCpuCommand = new CpuCommand();
            mCpuCommand.cmd = DTC_NOTIFICATION_CMD;
            mCpuCommand.subCmd = DTC_NOTIFICATION_SUB_CMD;
            mCpuCommand.data = data;
            cpuComManager.sendCmd(mCpuCommand);
            result = true;
        }
        return result;
    }
}
