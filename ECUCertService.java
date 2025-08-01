/*
 * COPYRIGHT (C) 2019 MITSUBISHI ELECTRIC CORPORATION
 * ALL RIGHTS RESERVED
 */

package com.mitsubishielectric.ahu.efw.ecucertservice;

import static com.mitsubishielectric.ahu.efw.ecucertservice.Common.ECU_ONLINE_SERVICE_NAME;
import static com.mitsubishielectric.ahu.efw.ecucertservice.Common.ECU_ONLINE_SERVICE_PACKAGE_NAME;
import static com.mitsubishielectric.ahu.efw.ecucertservice.Common.stopOnlineService;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.ALREADY_CONNECTED;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.CERT_DOWNLOAD_ERROR;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.CERT_READ_WRONG_BYTES_AMOUNT;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.CONNECTED_TO_ONLINE_SERVICE;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.DIRECTORY_FOUND;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.DISCONNECTED_FROM_ONLINE_SERVICE;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.DOWNLOAD_CERT;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.ECU_CERT_SERVICE_FUNCTION_ID;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.EXEC_REGISTER_SERVICE;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.EXPORT_CERTIFICATE;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.EXPORT_CERTIFICATE_TO_USB;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.EXPORT_CSR;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.EXTERNAL_MEDIA_NOT_FOUND;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.FILE_FOUND;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.FILE_NOT_FOUND;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.GET_APP_UID_EXCEPTION;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.GET_ECU_IDENTITY_NUMBER;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.GET_INSTALLATION_STATUS;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.IMPORT_CERTIFICATE;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.IMPORT_CERTIFICATE_USB;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.IMPORT_CERT_CHAIN;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.IO_EXCEPTION;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.MEDIA_TO_EXPORT;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.MEDIA_TO_IMPORT;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.MESSAGES;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.NO_CONNECTED_TO_ONLINE_SERVICE;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.NO_INTERNET_CONNECTION;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.ON_APP_RESTART;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.ON_APP_RESTART_ERROR;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.ON_APP_RESUME;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.ON_APP_RESUME_ERROR;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.ON_APP_START;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.ON_APP_START_ERROR;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.ON_APP_STOP_ERROR;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.ON_BIND;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.ON_CREATE;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.ON_CREATE_EXCEPTION;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.ON_DESTROY;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.ON_START_COMMAND;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.OPERATION_REJECTED;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.REMOTE_EXCEPTION;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.REMOVE_APPLICATION_KEYSTORE;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.REMOVE_BACKUP_KEY_STORE;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.RESET_INSTALL_STATE;
import static com.mitsubishielectric.ahu.efw.ecucertservice.ECUCertServiceLogMsg.TOTAL_RETRIAL_MAX;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.IBinder;
import android.os.RemoteException;
import com.mitsubishielectric.ahu.efw.lib.common.Const;
import com.mitsubishielectric.ahu.efw.lib.ecucertmgr.CSRType;
import com.mitsubishielectric.ahu.efw.lib.ecucertmgr.CertChainInstallStatus;
import com.mitsubishielectric.ahu.efw.lib.ecucertmgr.CertType;
import com.mitsubishielectric.ahu.efw.lib.ecucertmgr.CertificateChain;
import com.mitsubishielectric.ahu.efw.lib.ecucertmgr.DetailErrorCode;
import com.mitsubishielectric.ahu.efw.lib.ecucertmgr.ExportDetailErrorCode;
import com.mitsubishielectric.ahu.efw.lib.ecucertmgr.IECUCertService;
import com.mitsubishielectric.ahu.efw.lib.extendedservicemanager.ExtSrvManager;
import com.mitsubishielectric.ahu.efw.lib.logdogcommonclasslib.MLog;
import com.mitsubishielectric.ahu.efw.lib.onlineservicemgr.ECUOnlineServiceManager;
import com.mitsubishielectric.ahu.efw.lib.vehiclepwrmgr.IVehiclePowerServiceListener;
import com.mitsubishielectric.ahu.efw.lib.vehiclepwrmgr.VehiclePowerServiceManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ECUCertService extends Service {
    private ECUCertImpl ecuCert;
    private ECUMaterialCreator ecuMaterialGenerator;
    private NetworkReceiver mReceiver;
    private InstallationStatusManager mStatusManager;
    private ConnectivityManager mConnectivityManager;
    Context mContext;
    private static final String STORAGE = "/storage";
    private static final String NOT_RELEVANT_MEDIA_1 = "self";
    private static final String NOT_RELEVANT_MEDIA_2 = "emulated";
    private static final String CERT_NAME = "ecuLeafCrt.der";

    private static final Lock mLock = new ReentrantLock();
    private ResettableCountDownLatch mCountDownLatch = new ResettableCountDownLatch(1);
    private ServiceConnection mECUOnlineServiceConnection;
    private ServiceConnection mExtServiceConnection;
    private static final int waitServiceConnectionSec = 15;

    private final BroadcastReceiver mEsmBroadcastReceiver =
            new BroadcastReceiver() {

                private void registerToExtendedServiceManager(Intent intent) {
                    IBinder esmBinder = intent.getExtras().getBinder(Const.ESM_SERVICE);
                    ExtSrvManager.setBinder(esmBinder);
                    ExtSrvManager.getInstance().addService(Const.ECUCERT_SERVICE, mBinder);
                }

                @Override
                public void onReceive(Context context, Intent intent) {
                    registerToExtendedServiceManager(intent);
                }
            };

    private final IVehiclePowerServiceListener mVehiclePowerServiceListener =
            new IVehiclePowerServiceListener.Stub() {
                @Override
                public void onAppStart() {
                    MLog.i(ECU_CERT_SERVICE_FUNCTION_ID, ON_APP_START);
                    try {
                        VehiclePowerServiceManager.getInstance()
                                .startComplete(ECUCertService.class.getCanonicalName());
                    } catch (RemoteException e) {
                        MLog.w(ECU_CERT_SERVICE_FUNCTION_ID, ON_APP_START_ERROR, e.toString());
                    }
                }

                @Override
                public void onAppRestart() {
                    MLog.i(ECU_CERT_SERVICE_FUNCTION_ID, ON_APP_RESTART);
                    try {
                        VehiclePowerServiceManager.getInstance()
                                .restartCompleteEfw(ECUCertService.class.getCanonicalName());
                    } catch (RemoteException e) {
                        MLog.w(ECU_CERT_SERVICE_FUNCTION_ID, ON_APP_RESTART_ERROR, e.toString());
                    }
                }

                @Override
                public void onAppStop() {
                    try {
                        VehiclePowerServiceManager.getInstance()
                                .stopCompleteEfw(ECUCertService.class.getCanonicalName());
                    } catch (RemoteException e) {
                        MLog.w(ECU_CERT_SERVICE_FUNCTION_ID, ON_APP_STOP_ERROR, e.toString());
                    }
                }

                @Override
                public void onAppResume() {
                    MLog.i(ECU_CERT_SERVICE_FUNCTION_ID, ON_APP_RESUME);
                    try {
                        VehiclePowerServiceManager.getInstance()
                                .resumeCompleteEfw(ECUCertService.class.getCanonicalName());
                    } catch (RemoteException e) {
                        MLog.w(ECU_CERT_SERVICE_FUNCTION_ID, ON_APP_RESUME_ERROR, e.toString());
                    }
                }
            };

    private final IECUCertService.Stub mBinder =
            new IECUCertService.Stub() {
                @Override
                public byte[] exportCSR(CSRType type) {
                    MLog.d(ECU_CERT_SERVICE_FUNCTION_ID, EXPORT_CSR);
                    return ecuCert.exportCSR(type, getAppUID(mContext.getPackageName()));
                }

                @Override
                public int getECUCertStatus() {
                    MLog.d(ECU_CERT_SERVICE_FUNCTION_ID, GET_INSTALLATION_STATUS);
                    return ecuCert.getECUCertStatus();
                }

                @Override
                public String getKeyAlias(String appId) {
                    return ecuCert.getKeyAlias(
                            getAppUID(appId), getAppUID(mContext.getPackageName()));
                }

                @Override
                public DetailErrorCode importCertChain(CertificateChain certChain) {
                    MLog.d(ECU_CERT_SERVICE_FUNCTION_ID, IMPORT_CERT_CHAIN);
                    DetailErrorCode res;
                    if (mLock.tryLock()) {
                        try {
                            CertChainInstallStatus certInstallState =
                                    mStatusManager.getInstallationStatus(false);
                            if (CertChainInstallStatus.CERT_1_DOWNLOADED_AND_INSTALLED
                                    == certInstallState) {
                                res =
                                        ecuCert.importCertChain(
                                                certChain,
                                                getAppUID(mContext.getPackageName()),
                                                false);
                            } else {
                                res =
                                        ecuCert.importCertChain(
                                                certChain,
                                                getAppUID(mContext.getPackageName()),
                                                true);
                            }

                        } finally {
                            mLock.unlock();
                        }
                    } else {
                        res =
                                ecuCert.importCertChain(
                                        certChain, getAppUID(mContext.getPackageName()), false);
                    }
                    return res;
                }

                @Override
                public String getECUIdentityNumber() {
                    MLog.d(ECU_CERT_SERVICE_FUNCTION_ID, GET_ECU_IDENTITY_NUMBER);
                    return ecuCert.getECUIdentityNumber();
                }

                @Override
                public boolean resetInstallState() {
                    MLog.d(ECU_CERT_SERVICE_FUNCTION_ID, RESET_INSTALL_STATE);
                    return ecuCert.resetInstallState();
                }

                @Override
                public boolean removeKeyStore() {
                    MLog.d(ECU_CERT_SERVICE_FUNCTION_ID, REMOVE_BACKUP_KEY_STORE);
                    boolean removeBackUp = true;
                    return ecuCert.removeKeyStore(
                            getAppUID(mContext.getPackageName()), removeBackUp);
                }

                @Override
                public boolean removeAppKeyStore(String appId) {
                    MLog.d(ECU_CERT_SERVICE_FUNCTION_ID, REMOVE_APPLICATION_KEYSTORE);
                    boolean removeBackUp = false;
                    return ecuCert.removeKeyStore(getAppUID(appId), removeBackUp);
                }

                @Override
                public CertChainInstallStatus getInstallationStatus() {
                    MLog.d(ECU_CERT_SERVICE_FUNCTION_ID, GET_INSTALLATION_STATUS);
                    return ecuCert.getInstallationStatus();
                }

                @Override
                public boolean raiseDTC() {
                    MLog.d(ECU_CERT_SERVICE_FUNCTION_ID, TOTAL_RETRIAL_MAX);
                    return ecuCert.raiseDTC();
                }

                @Override
                public DetailErrorCode importCertificateFromUSB() {
                    MLog.w(ECU_CERT_SERVICE_FUNCTION_ID, IMPORT_CERTIFICATE_USB);
                    DetailErrorCode res = DetailErrorCode.OPERATION_REJECTED;
                    if (mLock.tryLock()) {
                        try {
                            String media = getExternalMedia();
                            if (!media.isEmpty()) {
                                MLog.d(ECU_CERT_SERVICE_FUNCTION_ID, MEDIA_TO_IMPORT, media);
                                File crtFile = new File(media + "/" + CERT_NAME);
                                try (FileInputStream is = new FileInputStream(crtFile)) {
                                    int bytesAvailable = is.available();
                                    byte[] crtData = new byte[bytesAvailable];
                                    int bytesRead = is.read(crtData);
                                    if (bytesRead != bytesAvailable) {
                                        MLog.w(
                                                ECU_CERT_SERVICE_FUNCTION_ID,
                                                CERT_READ_WRONG_BYTES_AMOUNT,
                                                bytesRead,
                                                bytesAvailable);
                                    }
                                    res = importCertificate(crtData);
                                    if (DetailErrorCode.CERTIFICATE_VALIDATION_SUCCESS == res) {
                                        stopOnlineService(mContext);
                                    }
                                } catch (FileNotFoundException e) {
                                    MLog.w(
                                            ECU_CERT_SERVICE_FUNCTION_ID,
                                            FILE_NOT_FOUND,
                                            e.toString());
                                    res = DetailErrorCode.IO_ERROR;
                                } catch (IOException e) {
                                    MLog.w(
                                            ECU_CERT_SERVICE_FUNCTION_ID,
                                            IO_EXCEPTION,
                                            e.toString());
                                    res = DetailErrorCode.IO_ERROR;
                                }
                            } else {
                                MLog.w(ECU_CERT_SERVICE_FUNCTION_ID, EXTERNAL_MEDIA_NOT_FOUND);
                                res = DetailErrorCode.IO_ERROR;
                            }
                        } finally {
                            mLock.unlock();
                        }
                    } else {
                        MLog.w(ECU_CERT_SERVICE_FUNCTION_ID, OPERATION_REJECTED);
                    }
                    return res;
                }

                @Override
                public ExportDetailErrorCode exportCertificateToUSB() {
                    MLog.d(ECU_CERT_SERVICE_FUNCTION_ID, EXPORT_CERTIFICATE_TO_USB);
                    ExportDetailErrorCode res;
                    byte[] crtData = exportCertificate(CertType.DER);
                    if (crtData.length != 0) {
                        String media = getExternalMedia();
                        if (!media.isEmpty()) {
                            MLog.d(ECU_CERT_SERVICE_FUNCTION_ID, MEDIA_TO_EXPORT, media);
                            File exportedCert = new File(media + "/" + CERT_NAME);
                            try (FileOutputStream os = new FileOutputStream(exportedCert)) {
                                os.write(crtData);
                                os.flush();
                                res = ExportDetailErrorCode.CERTIFICATE_EXPORT_SUCCESS;
                            } catch (FileNotFoundException e) {
                                MLog.w(ECU_CERT_SERVICE_FUNCTION_ID, FILE_NOT_FOUND, e.toString());
                                res = ExportDetailErrorCode.IO_ERROR;
                            } catch (IOException e) {
                                MLog.w(ECU_CERT_SERVICE_FUNCTION_ID, IO_EXCEPTION, e.toString());
                                res = ExportDetailErrorCode.IO_ERROR;
                            }
                        } else {
                            MLog.w(ECU_CERT_SERVICE_FUNCTION_ID, EXTERNAL_MEDIA_NOT_FOUND);
                            res = ExportDetailErrorCode.IO_ERROR;
                        }
                    } else {
                        res = ExportDetailErrorCode.CERT_NOT_INSTALLED;
                    }
                    return res;
                }

                @Override
                public DetailErrorCode downloadCertificate() {
                    MLog.i(ECU_CERT_SERVICE_FUNCTION_ID, DOWNLOAD_CERT);
                    DetailErrorCode res = DetailErrorCode.OPERATION_REJECTED;
                    if (mLock.tryLock()) {
                        try {
                            connectToECUOnlineService();
                            if (mCountDownLatch.await(waitServiceConnectionSec, TimeUnit.SECONDS)) {
                                if (Common.isOnline(mContext)) {
                                    ECUOnlineServiceManager onlineServiceManager =
                                            ECUOnlineServiceManager.getInstance();
                                    byte[] cert = onlineServiceManager.downloadCertificate();
                                    if (0 != cert.length) {
                                        res =
                                                ecuCert.importCertificate(
                                                        cert, getAppUID(mContext.getPackageName()));
                                    } else {
                                        MLog.w(ECU_CERT_SERVICE_FUNCTION_ID, CERT_DOWNLOAD_ERROR);
                                        res = DetailErrorCode.CERTIFICATE_DOWNLOAD_FAILED;
                                    }
                                    onlineServiceManager.sendReport(res.ordinal());
                                    if (DetailErrorCode.CERTIFICATE_VALIDATION_SUCCESS == res) {
                                        stopOnlineService(mContext);
                                    }
                                } else {
                                    MLog.w(ECU_CERT_SERVICE_FUNCTION_ID, NO_INTERNET_CONNECTION);
                                    res = DetailErrorCode.NO_INTERNET_CONNECTION;
                                }
                            } else {
                                MLog.w(
                                        ECU_CERT_SERVICE_FUNCTION_ID,
                                        NO_CONNECTED_TO_ONLINE_SERVICE);
                                res = DetailErrorCode.INTERNAL_ERROR;
                            }
                        } catch (InterruptedException | RemoteException e) {
                            MLog.w(ECU_CERT_SERVICE_FUNCTION_ID, REMOTE_EXCEPTION, e.toString());
                            Thread.currentThread().interrupt();
                            res = DetailErrorCode.INTERNAL_ERROR;
                        } finally {
                            mCountDownLatch.reset();
                            if (mECUOnlineServiceConnection != null) {
                                unbindService(mECUOnlineServiceConnection);
                            }
                            mLock.unlock();
                        }
                    } else {
                        MLog.w(ECU_CERT_SERVICE_FUNCTION_ID, OPERATION_REJECTED);
                    }
                    return res;
                }

                @Override
                public byte[] exportCertificate(CertType type) {
                    MLog.d(ECU_CERT_SERVICE_FUNCTION_ID, EXPORT_CERTIFICATE);
                    return ecuCert.exportCertificate(type);
                }

                @Override
                public DetailErrorCode importCertificate(byte[] cert) {
                    MLog.d(ECU_CERT_SERVICE_FUNCTION_ID, IMPORT_CERTIFICATE);
                    DetailErrorCode res = DetailErrorCode.OPERATION_REJECTED;
                    if (mLock.tryLock()) {
                        try {
                            res =
                                    ecuCert.importCertificate(
                                            cert, getAppUID(mContext.getPackageName()));
                            if (DetailErrorCode.CERTIFICATE_VALIDATION_SUCCESS == res) {
                                stopOnlineService(mContext);
                            }
                        } finally {
                            mLock.unlock();
                        }
                    } else {
                        MLog.w(ECU_CERT_SERVICE_FUNCTION_ID, OPERATION_REJECTED);
                    }
                    return res;
                }
            };

    @Override
    public IBinder onBind(Intent intent) {
        MLog.i(ECU_CERT_SERVICE_FUNCTION_ID, ON_BIND);
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter(Const.ESM_REBOOT_ACTION_INTENT);
        registerReceiver(mEsmBroadcastReceiver, intentFilter, Const.ACCESS_MELCO_SERVICES, null);
        try {
            mContext = getApplicationContext();
            AndroidKeyStoreWrapper keyStore = new AndroidKeyStoreWrapper();
            ECUCertConnector connector = new ECUCertConnector();
            BCProviderWrapper mCSRGenerator = new BCProviderWrapper();
            MelcoCertProvider mCertProvider = new MelcoCertProvider();
            FileWrapper mFileWrapper = new FileWrapper();

            DTCManager mDtcManager = new DTCManager();
            SerialNumber mSerialNumber = new SerialNumber();
            mStatusManager =
                    new InstallationStatusManager(
                            mFileWrapper, keyStore, mCertProvider, mSerialNumber);
            ecuMaterialGenerator = new ECUMaterialCreator(connector, keyStore);
            ecuCert =
                    new ECUCertImpl(
                            keyStore,
                            connector,
                            mCSRGenerator,
                            mCertProvider,
                            mStatusManager,
                            mDtcManager,
                            mSerialNumber,
                            mContext);
            ecuMaterialGenerator = new ECUMaterialCreator(connector, keyStore);
            MLog.initialize(ECU_CERT_SERVICE_FUNCTION_ID, MESSAGES);
            MLog.i(ECU_CERT_SERVICE_FUNCTION_ID, ON_CREATE);
        } catch (CertificateException
                | NoSuchAlgorithmException
                | IOException
                | KeyStoreException e) {
            MLog.e(ECU_CERT_SERVICE_FUNCTION_ID, ON_CREATE_EXCEPTION, e.toString());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (intent != null && intent.getExtras() != null) {
            IBinder esmBinder = intent.getExtras().getBinder(Const.ESM_SERVICE);
            registerService(esmBinder);
        } else {
            mExtServiceConnection =
                    new ServiceConnection() {
                        private boolean mRegistered = false;

                        @Override
                        public void onServiceConnected(ComponentName name, IBinder esmBinder) {
                            if (mRegistered) {
                                MLog.w(ECU_CERT_SERVICE_FUNCTION_ID, ALREADY_CONNECTED);
                                return;
                            }
                            registerService(esmBinder);
                            mRegistered = true;
                        }

                        public void onServiceDisconnected(ComponentName name) {
                            // Stub only
                        }
                    };
            Intent esmIntent = new Intent();
            esmIntent.setClassName(Const.ESM_PACKAGE, Const.ESM_SERVICE);
            bindService(esmIntent, mExtServiceConnection, BIND_AUTO_CREATE);
        }

        if (ecuMaterialGenerator.generateECUMaterial(getAppUID(mContext.getPackageName()))) {
            mReceiver = new NetworkReceiver(mStatusManager, mContext);
            mConnectivityManager =
                    (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (mConnectivityManager != null) {
                NetworkRequest.Builder builder = new NetworkRequest.Builder();
                builder.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
                NetworkRequest networkRequest = builder.build();
                mConnectivityManager.requestNetwork(networkRequest, mReceiver);
            }
        }
        MLog.i(ECU_CERT_SERVICE_FUNCTION_ID, ON_START_COMMAND);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mEsmBroadcastReceiver);
        if (mExtServiceConnection != null) {
            unbindService(mExtServiceConnection);
        }
        MLog.i(ECU_CERT_SERVICE_FUNCTION_ID, ON_DESTROY);
        MLog.terminate(ECU_CERT_SERVICE_FUNCTION_ID);
    }

    private int getAppUID(String appPackageName) {
        int uid;

        try {
            uid = getApplicationContext().getPackageManager().getPackageUid(appPackageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            uid = -1;
            MLog.w(ECU_CERT_SERVICE_FUNCTION_ID, GET_APP_UID_EXCEPTION, e.toString());
        }
        return uid;
    }

    private String getExternalMedia() {
        String selectedMedia = "";
        File storage = new File(STORAGE);
        for (File media : storage.listFiles()) {
            String fileName = media.getName();
            if (media.isDirectory()) {
                MLog.d(ECU_CERT_SERVICE_FUNCTION_ID, DIRECTORY_FOUND, fileName);
                if (!fileName.equals(NOT_RELEVANT_MEDIA_1)
                        && !fileName.equals(NOT_RELEVANT_MEDIA_2)) {
                    selectedMedia = media.getAbsolutePath();
                }
            } else {
                MLog.d(ECU_CERT_SERVICE_FUNCTION_ID, FILE_FOUND, fileName);
            }
        }
        return selectedMedia;
    }

    private void connectToECUOnlineService() {
        Intent intent = new Intent();
        intent.setClassName(ECU_ONLINE_SERVICE_PACKAGE_NAME, ECU_ONLINE_SERVICE_NAME);
        mECUOnlineServiceConnection =
                new ServiceConnection() {
                    public void onServiceConnected(ComponentName name, IBinder binder) {
                        if (binder == null) {
                            return;
                        }
                        MLog.i(ECU_CERT_SERVICE_FUNCTION_ID, CONNECTED_TO_ONLINE_SERVICE);
                        ECUOnlineServiceManager.setBinder(binder);
                        mCountDownLatch.countDown();
                    }

                    public void onServiceDisconnected(ComponentName name) {
                        MLog.i(ECU_CERT_SERVICE_FUNCTION_ID, DISCONNECTED_FROM_ONLINE_SERVICE);
                    }
                };
        bindService(intent, mECUOnlineServiceConnection, BIND_AUTO_CREATE);
    }

    private void registerService(IBinder esmBinder) {
        try {
            ExtSrvManager.setBinder(esmBinder);
            ExtSrvManager.getInstance().addService(Const.ECUCERT_SERVICE, mBinder);

            IBinder vpsBinder = ExtSrvManager.getInstance().getService(Const.VEHICLE_POWER_SERVICE);
            VehiclePowerServiceManager.setBinder(vpsBinder);
            VehiclePowerServiceManager.getInstance()
                    .subscribeFWService(
                            mVehiclePowerServiceListener, ECUCertService.class.getCanonicalName());
        } catch (RemoteException rex) {
            MLog.w(ECU_CERT_SERVICE_FUNCTION_ID, EXEC_REGISTER_SERVICE);
        }
    }
}
