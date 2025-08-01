/*
 * COPYRIGHT (C) 2020 MITSUBISHI ELECTRIC CORPORATION
 * ALL RIGHTS RESERVED
 */

package com.mitsubishielectric.ahu.efw.ecucertservice;

import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.CONFIG_READER_NULL;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.COUNTRY_NAME;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.ECU_CERT_SERVICE_FUNCTION_ID;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.NOT_CONNECTED_TO_ESM;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.SERIAL_NUMBER;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.SUBJECT_NAME;

import android.os.IBinder;
import com.mitsubishielectric.ahu.efw.lib.common.Const;
import com.mitsubishielectric.ahu.efw.lib.common.Error;
import com.mitsubishielectric.ahu.efw.lib.extendedservicemanager.ExtSrvManager;
import com.mitsubishielectric.ahu.efw.lib.logdogcommonclasslib.MLog;
import com.mitsubishielectric.ahu.efw.lib.vehicleconfigservice.VCString;
import com.mitsubishielectric.ahu.efw.lib.vehicleconfigservice.VehicleConfigManager;
import java.util.HashMap;
import java.util.Map;

final class SerialNumber implements ISerialNumber {
    private static final int FACTORY_ID = 9;

    private static final String ORGANIZATION_NAME = "FCA NV";

    private static final Map<String, String> country = new HashMap<>();

    static {
        // Japan
        country.put("6", "JP");
        country.put("0", "JP");
        country.put("1", "JP");

        // Mexico
        country.put("X", "MX");
        country.put("Y", "MX");

        // Thailand
        country.put("7", "TH");
        country.put("P", "TH");
        country.put("R", "TH");
        country.put("U", "TH");
    }

    @Override
    public String getSerialNumber() {
        String serialNumber = "";
        ExtSrvManager extSrvManager = ExtSrvManager.getInstance();
        if (extSrvManager != null) {
            IBinder reader = extSrvManager.getService(Const.VEHICLE_CONFIG_READER_SERVICE);
            if (reader != null) {
                VehicleConfigManager.setReaderBinder(reader);
                VehicleConfigManager vehicleConfigManager = VehicleConfigManager.getInstance();
                VCString vcString = new VCString();
                if (vehicleConfigManager.getString("ECUSERIALNUMBER", vcString)
                        != Error.ERR_FAILED) {
                    serialNumber = vcString.value;
                }
            } else {
                MLog.e(ECU_CERT_SERVICE_FUNCTION_ID, CONFIG_READER_NULL);
            }
        } else {
            MLog.e(ECU_CERT_SERVICE_FUNCTION_ID, NOT_CONNECTED_TO_ESM);
        }
        MLog.d(ECU_CERT_SERVICE_FUNCTION_ID, SERIAL_NUMBER, serialNumber, serialNumber.length());
        return serialNumber;
    }

    private String getCountryNameFomSerialNumber(String serialNumber) {
        String countryName = country.get(String.valueOf(serialNumber.charAt(FACTORY_ID)));
        if (null == countryName) {
            countryName = "";
        }
        MLog.d(ECU_CERT_SERVICE_FUNCTION_ID, COUNTRY_NAME, countryName);
        return countryName;
    }

    @Override
    public String getSubjectName() {
        String subjectName = "";
        String serialNumber = getSerialNumber();
        String countryName = getCountryNameFomSerialNumber(serialNumber);
        if (false == serialNumber.isEmpty() && false == countryName.isEmpty()) {
            subjectName =
                    "C="
                            + countryName
                            + ", "
                            + "O="
                            + ORGANIZATION_NAME
                            + ", "
                            + "CN="
                            + serialNumber;
        }
        MLog.d(ECU_CERT_SERVICE_FUNCTION_ID, SUBJECT_NAME, subjectName);
        return subjectName;
    }
}
