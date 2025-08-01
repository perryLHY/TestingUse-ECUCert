/*
 * COPYRIGHT (C) 2020 MITSUBISHI ELECTRIC CORPORATION
 * ALL RIGHTS RESERVED
 */

package com.mitsubishielectric.ahu.efw.ecucertservice;

import java.util.HashMap;
import java.util.Map;

public final class ECUCertServiceLogMsg {
    public static final int ECU_CERT_SERVICE_FUNCTION_ID = 1174;

    public static final int EXPORT_CSR = 1;
    public static final int GET_INSTALLATION_STATUS = 2;
    public static final int GET_KEY_ALIAS = 3;
    public static final int IMPORT_CERT_CHAIN = 4;
    public static final int TOTAL_RETRIAL_MAX = 5;
    public static final int GET_ECU_IDENTITY_NUMBER = 6;
    public static final int ON_BIND = 7;
    public static final int ON_CREATE = 8;
    public static final int ECU_MATERIAL_GENERATED_SUCCESS = 9;
    public static final int ON_START_COMMAND = 10;
    public static final int ON_DESTROY = 11;
    public static final int NOT_CONNECTED_TO_ESM = 12;
    public static final int CONNECTED_TO_ESM = 13;
    public static final int CONFIG_READER_NULL = 14;
    public static final int DISCONNECTED_FROM_ESM = 15;
    public static final int ON_CREATE_EXCEPTION = 16;
    public static final int GET_APP_UID_EXCEPTION = 17;
    public static final int NOT_INSTANCE_OF_PRIVATE_KEY_ENTRY = 18;
    public static final int CERT_CHAIN_EMPTY = 19;
    public static final int CN_INFO = 20;
    public static final int GENERATE_RSA_KEY_PAIR_EXCEPTION = 21;
    public static final int GET_ENTRY_EXCEPTION = 22;
    public static final int GET_CERT_EXCEPTION = 23;
    public static final int IS_KEY_ENTRY_EXCEPTION = 24;
    public static final int STORE_CERT_CHAIN_EXCEPTION = 25;
    public static final int VALIDATE_CERT_CHAIN_EXCEPTION = 26;
    public static final int VALIDATE_CERT_EXCEPTION = 27;
    public static final int GENERATE_CSR_EXCEPTION = 28;
    public static final int DTC_HEAL = 29;
    public static final int DTC_RAISE = 30;
    public static final int CSR_GENERATE_SUCCESS = 31;
    public static final int CSR_GENERATE_FAIL = 32;
    public static final int RSA_KEYS_COPY_SUCCESS = 33;
    public static final int RSA_KEYS_COPY_FAIL = 34;
    public static final int WRONG_ID = 35;
    public static final int CERT_CHAIN_VERIFICATION_ERROR = 36;
    public static final int CERT_NOT_FOUND = 37;
    public static final int CERT_CHAIN_NOT_STORED = 38;
    public static final int KEYSTORE_NOT_COPIED_TO_OEM = 39;
    public static final int CERT_CHAIN_STORED = 40;
    public static final int DTC_NOT_HEALED = 41;
    public static final int INSTALLATION_STATUS_NOT_UPDATED = 42;
    public static final int ECU_INSTALLATION_STATUS = 43;
    public static final int TOTAL_RETRIAL_IS_MAX_SUCCESS = 44;
    public static final int SERIAL_NUMBER = 45;
    public static final int DTC_NOT_RAISED = 46;
    public static final int RSA_KEYS_NOT_FOUND = 47;
    public static final int RSA_KEYS_NOT_RESTORED = 48;
    public static final int RSA_KEYS_NOT_GENERATED = 49;
    public static final int RSA_KEYS_NOT_BACKED_UP = 50;
    public static final int RSA_KEYS_GENERATED = 51;
    public static final int RSA_KEYS_RESTORED = 52;
    public static final int RSA_KEYS_FOUND = 53;
    public static final int WRONG_SERVICE_UID = 54;
    public static final int WRITE_EXCEPTION = 55;
    public static final int READ_EXCEPTION = 56;
    public static final int INSTALLATION_STATUS_IS = 57;
    public static final int INSTALLATION_STATUS_UPDATED = 58;
    public static final int GET_OUTPUT_STREAM_EXCEPTION = 59;
    public static final int COUNTRY_NAME = 60;
    public static final int COUNTRY_NOT_FOUND = 61;
    public static final int VERIFY_CERT_CHAIN_EXCEPTION = 62;
    public static final int RESULT_ERROR_CODE = 63;
    public static final int WRONG_SERIAL_NUMBER = 64;
    public static final int CHANGE_ORDER_EXCEPTION = 65;
    public static final int GEN_CERT_EXCEPTION = 66;
    public static final int UNPACK_EXCEPTION = 67;
    public static final int EMPTY_CHAIN = 68;
    public static final int SUBJECT_NAME = 69;
    public static final int DAEMON_STOPPED_WAIT_TIMEOUT = 70;
    public static final int DAEMON_STARTED_WAIT_TIMEOUT = 71;
    public static final int CERTIFICATE_NOT_INSTALLED = 72;
    public static final int BACKUP_KEY_STORE_NOT_DELETED = 73;
    public static final int RESET_INSTALL_STATE = 74;
    public static final int REMOVE_BACKUP_KEY_STORE = 75;
    public static final int REMOVE_APPLICATION_KEYSTORE = 76;
    public static final int ON_APP_START = 77;
    public static final int ON_APP_RESTART = 78;
    public static final int ON_APP_STOP = 79;
    public static final int ON_APP_RESUME = 80;
    public static final int ON_APP_START_ERROR = 81;
    public static final int ON_APP_STOP_ERROR = 82;
    public static final int ON_APP_RESTART_ERROR = 83;
    public static final int ON_APP_RESUME_ERROR = 84;
    public static final int SUBSCRIBE_TO_VPS_EXCEPTION = 85;
    public static final int CERT_READ_WRONG_BYTES_AMOUNT = 86;
    public static final int CERT_READ_EXCEPTION = 87;
    public static final int EMPTY_CERT = 88;
    public static final int GET_CERTIFICATE_EXCEPTION = 89;
    public static final int IMPORT_CERTIFICATE = 90;
    public static final int EXPORT_CERTIFICATE = 91;
    public static final int EXPORT_CERTIFICATE_TO_USB = 92;
    public static final int FILE_NOT_FOUND = 93;
    public static final int CREATE_FILE_EXCEPTION = 94;
    public static final int IO_EXCEPTION = 95;
    public static final int IMPORT_CERTIFICATE_USB = 96;
    public static final int DIRECTORY_FOUND = 97;
    public static final int FILE_FOUND = 98;
    public static final int MEDIA_TO_EXPORT = 99;
    public static final int EXTERNAL_MEDIA_NOT_FOUND = 100;
    public static final int MEDIA_TO_IMPORT = 101;
    public static final int OPERATION_REJECTED = 102;
    public static final int CONNECTED_TO_ONLINE_SERVICE = 103;
    public static final int DISCONNECTED_FROM_ONLINE_SERVICE = 104;
    public static final int NO_CONNECTED_TO_ONLINE_SERVICE = 105;
    public static final int DOWNLOAD_CERT = 106;
    public static final int REMOTE_EXCEPTION = 107;
    public static final int NO_INTERNET_CONNECTION = 108;
    public static final int CERT_DOWNLOAD_ERROR = 109;
    public static final int START_ONLINE_SERVICE = 110;
    public static final int STOP_ONLINE_SERVICE = 111;
    public static final int CONNECT_TO_NETWORK = 112;
    public static final int NETWORK_LOST = 113;
    public static final int EXEC_REGISTER_SERVICE = 114;
    public static final int ALREADY_CONNECTED = 115;
    public static final int GET_CERT_CHAIN_EXCEPTION = 116;

    protected static final Map<Integer, String> MESSAGES = new HashMap<>();

    static {
        /* ECUCertService */
        MESSAGES.put(EXPORT_CSR, "export CSR");
        MESSAGES.put(GET_INSTALLATION_STATUS, "getInstallationStatus");
        MESSAGES.put(GET_KEY_ALIAS, "getKeyAlias");
        MESSAGES.put(IMPORT_CERT_CHAIN, "importCertChain");
        MESSAGES.put(TOTAL_RETRIAL_MAX, "totalRetrialIsMax");
        MESSAGES.put(GET_ECU_IDENTITY_NUMBER, "getECUIdentityNumber");
        MESSAGES.put(ON_BIND, "onBind");
        MESSAGES.put(ON_CREATE, "onCreate");
        MESSAGES.put(ECU_MATERIAL_GENERATED_SUCCESS, "ECU key material generated successfully");
        MESSAGES.put(ON_START_COMMAND, "onStartCommand");
        MESSAGES.put(ON_DESTROY, "onDestroy");
        MESSAGES.put(NOT_CONNECTED_TO_ESM, "Couldn't connect to ESM");
        MESSAGES.put(CONNECTED_TO_ESM, "Connected to ESM");
        MESSAGES.put(CONFIG_READER_NULL, "Config reader is null");
        MESSAGES.put(DISCONNECTED_FROM_ESM, "Disconnected from ESM");
        MESSAGES.put(ON_CREATE_EXCEPTION, "onCreate exception: %s");
        MESSAGES.put(GET_APP_UID_EXCEPTION, "getAppUID exception: %s");
        MESSAGES.put(RESET_INSTALL_STATE, "resetInstallState");
        MESSAGES.put(REMOVE_BACKUP_KEY_STORE, "removeBackupKeyStore");
        MESSAGES.put(REMOVE_APPLICATION_KEYSTORE, "removeAppKeyStore");
        MESSAGES.put(ON_APP_START, "onAppStart()");
        MESSAGES.put(ON_APP_RESTART, "onAppRestart()");
        MESSAGES.put(ON_APP_STOP, "onAppStop()");
        MESSAGES.put(ON_APP_RESUME, "onAppResume()");
        MESSAGES.put(ON_APP_START_ERROR, "onAppStart() Error: %s");
        MESSAGES.put(ON_APP_STOP_ERROR, "onAppStop() Error: %s");
        MESSAGES.put(ON_APP_RESTART_ERROR, "onAppRestart() Error: %s");
        MESSAGES.put(ON_APP_RESUME_ERROR, "onAppResume() Error: %s");
        MESSAGES.put(SUBSCRIBE_TO_VPS_EXCEPTION, "Exception: %s");
        MESSAGES.put(IMPORT_CERTIFICATE, "importCertificate");
        MESSAGES.put(EXPORT_CERTIFICATE, "exportCertificate");
        MESSAGES.put(EXPORT_CERTIFICATE_TO_USB, "exportCertificateToUSB");
        MESSAGES.put(FILE_NOT_FOUND, "FileNotFoundException: %s");
        MESSAGES.put(CREATE_FILE_EXCEPTION, "createNewFile(\"%s\") exception: %s");
        MESSAGES.put(IO_EXCEPTION, "IOException: %s");
        MESSAGES.put(IMPORT_CERTIFICATE_USB, "importCertificateFromUSB");
        MESSAGES.put(DIRECTORY_FOUND, "Directory found in /storage: %s");
        MESSAGES.put(FILE_FOUND, "File (not directory) found in /storage: %s");
        MESSAGES.put(MEDIA_TO_EXPORT, "Media selected for export: %s");
        MESSAGES.put(EXTERNAL_MEDIA_NOT_FOUND, "External media not found");
        MESSAGES.put(MEDIA_TO_IMPORT, "Media selected for export: %s");
        MESSAGES.put(OPERATION_REJECTED, "Operation rejected: import is already running");
        MESSAGES.put(CONNECTED_TO_ONLINE_SERVICE, "Connected to OnlineService");
        MESSAGES.put(DISCONNECTED_FROM_ONLINE_SERVICE, "Disconnected from OnlineService");
        MESSAGES.put(NO_CONNECTED_TO_ONLINE_SERVICE, "Couldn't connect to OnlineService");
        MESSAGES.put(DOWNLOAD_CERT, "downloadCertificate()");
        MESSAGES.put(REMOTE_EXCEPTION, "Exception: %s");
        MESSAGES.put(NO_INTERNET_CONNECTION, "No internet connection");
        MESSAGES.put(CERT_DOWNLOAD_ERROR, "Certificate was not download");
        MESSAGES.put(EXEC_REGISTER_SERVICE, "registerService(), Remote exception.");
        MESSAGES.put(ALREADY_CONNECTED, "onServiceConnected: already registered");

        /* AndroidKeyStoreWrapper */
        MESSAGES.put(NOT_INSTANCE_OF_PRIVATE_KEY_ENTRY, "Not an instance of a PrivateKeyEntry");
        MESSAGES.put(CERT_CHAIN_EMPTY, "Certificate chain is empty");
        MESSAGES.put(CN_INFO, "CN: %s (CN len: %d)");
        MESSAGES.put(GENERATE_RSA_KEY_PAIR_EXCEPTION, "generateRSAKeyPair exception: %s");
        MESSAGES.put(GET_ENTRY_EXCEPTION, "getRSAKeyPair: getEntry exception: %s");
        MESSAGES.put(GET_CERT_EXCEPTION, "getRSAKeyPair: getCertificate exception: %s");
        MESSAGES.put(IS_KEY_ENTRY_EXCEPTION, "isKeyEntry exception: %s");
        MESSAGES.put(STORE_CERT_CHAIN_EXCEPTION, "storeCertChain exception: %s");
        MESSAGES.put(
                VALIDATE_CERT_CHAIN_EXCEPTION, "validateCertChainAgainstPrivateKey exception: %s");
        MESSAGES.put(VALIDATE_CERT_EXCEPTION, "validateCertAgainstPrivateKey exception: %s");
        MESSAGES.put(
                GET_CERT_CHAIN_EXCEPTION, "getCertificateChain: getCertificateChain exception: %s");

        /* BCProviderWrapper */
        MESSAGES.put(GENERATE_CSR_EXCEPTION, "generateCSR exception: %s");

        /* DTCManager */
        MESSAGES.put(DTC_HEAL, "DTC was healed");
        MESSAGES.put(DTC_RAISE, "DTC was raised");

        /* ECUCertImpl */
        MESSAGES.put(CSR_GENERATE_SUCCESS, "CSR was generated successfully");
        MESSAGES.put(CSR_GENERATE_FAIL, "CSR was not generated");
        MESSAGES.put(RSA_KEYS_COPY_SUCCESS, "RSA keyPair was copied successfully");
        MESSAGES.put(RSA_KEYS_COPY_FAIL, "RSA keyPair was not copied");
        MESSAGES.put(CERTIFICATE_NOT_INSTALLED, "getKeyAlias failed: certificate not installed");
        MESSAGES.put(WRONG_ID, "Wrong ID");
        MESSAGES.put(
                CERT_CHAIN_VERIFICATION_ERROR,
                "Certificate chain verification error, detail error code: %d");
        MESSAGES.put(CERT_NOT_FOUND, "Certificate with valid public key was not found");
        MESSAGES.put(
                CERT_CHAIN_NOT_STORED,
                "There was an error during chain installation. See logs for more info");
        MESSAGES.put(KEYSTORE_NOT_COPIED_TO_OEM, "ECU KeyStore was not copied to oem partition");
        MESSAGES.put(
                CERT_CHAIN_STORED, "Certificate chain was stored to android keystore successfully");
        MESSAGES.put(DTC_NOT_HEALED, "DTC was not healed");
        MESSAGES.put(INSTALLATION_STATUS_NOT_UPDATED, "Installation status was not updated");
        MESSAGES.put(ECU_INSTALLATION_STATUS, "ECU CertChain installation status: %d");
        MESSAGES.put(
                TOTAL_RETRIAL_IS_MAX_SUCCESS,
                "operation totalRetrialIsMax was finished successfully");
        MESSAGES.put(SERIAL_NUMBER, "SERIAL NUMBER: %s, Len: %d");
        MESSAGES.put(DTC_NOT_RAISED, "DTC was not raised");
        MESSAGES.put(RESULT_ERROR_CODE, "Detail error code: %d");
        MESSAGES.put(WRONG_SERIAL_NUMBER, "Serial number is empty");
        MESSAGES.put(CHANGE_ORDER_EXCEPTION, "Exception: %s");
        MESSAGES.put(GEN_CERT_EXCEPTION, "Exception: %s");
        MESSAGES.put(UNPACK_EXCEPTION, "Exception: %s");
        MESSAGES.put(BACKUP_KEY_STORE_NOT_DELETED, "Backup key store was not deleted");
        MESSAGES.put(CERT_READ_WRONG_BYTES_AMOUNT, "%d bytes read instead of %d available");
        MESSAGES.put(CERT_READ_EXCEPTION, "Certificate \"%s\" read exception: %s");
        MESSAGES.put(EMPTY_CERT, "Empty certificate: \"%s\"");
        MESSAGES.put(EMPTY_CHAIN, "Empty cert chain");
        MESSAGES.put(GET_CERTIFICATE_EXCEPTION, "exportCertificate exception: %s");

        /* ECUMaterialCreator */
        MESSAGES.put(
                RSA_KEYS_NOT_FOUND,
                "RSA key pair was not found in Android keystore, /data/misc/keystore");
        MESSAGES.put(RSA_KEYS_NOT_RESTORED, "RSA key pair was not restored from backup partition");
        MESSAGES.put(RSA_KEYS_NOT_GENERATED, "RSA key pair was not generated by Android keystore");
        MESSAGES.put(RSA_KEYS_NOT_BACKED_UP, "RSA key pair was not backed up");
        MESSAGES.put(RSA_KEYS_GENERATED, "RSA key pair was generated successfully");
        MESSAGES.put(
                RSA_KEYS_RESTORED, "RSA key pair was restored successfully from backup partition");
        MESSAGES.put(RSA_KEYS_FOUND, "RSA key pair was found in Android keystore");
        MESSAGES.put(WRONG_SERVICE_UID, "Wrong service UID");

        /* FileWrapper */
        MESSAGES.put(WRITE_EXCEPTION, "write exception: %s");
        MESSAGES.put(READ_EXCEPTION, "read exception: %s");

        /* InstallationStatusManager */
        MESSAGES.put(INSTALLATION_STATUS_IS, "Installation status: %d");
        MESSAGES.put(
                INSTALLATION_STATUS_UPDATED, "Installation status was updated successfully: %d");

        /* JCESigner */
        MESSAGES.put(GET_OUTPUT_STREAM_EXCEPTION, "getOutputStream exception: %s");

        /* SerialNumber */
        MESSAGES.put(COUNTRY_NAME, "Country name: %s");
        MESSAGES.put(COUNTRY_NOT_FOUND, "Country name was not found");
        MESSAGES.put(SUBJECT_NAME, "Subject name: %s");

        /* MelcoCertProvider */
        MESSAGES.put(VERIFY_CERT_CHAIN_EXCEPTION, "verifyCertChain exception: %s");

        /* ECUCertConnector */
        MESSAGES.put(DAEMON_STOPPED_WAIT_TIMEOUT, "Daemon wait stopped timeout");
        MESSAGES.put(DAEMON_STARTED_WAIT_TIMEOUT, "Daemon wait started timeout");

        /* Common */
        MESSAGES.put(START_ONLINE_SERVICE, "Start online service");
        MESSAGES.put(STOP_ONLINE_SERVICE, "Stop online service");

        /* NetworkReceiver */
        MESSAGES.put(CONNECT_TO_NETWORK, "Connect to network");
        MESSAGES.put(NETWORK_LOST, "Disconnected from network");
    }
}
