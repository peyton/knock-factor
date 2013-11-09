package com.knockfactor;

import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Handler;

import com.knockfactor.testability.DependencyInjector;

public class KnockFactorService extends IntentService {

    public static final String STATUS = "com.knockfactor.knockfactorservice.STATUS";

    private AuthenticatorActivity.ConnectThread mConnectThread;
    private AccountDb mAccountDb;
    private BluetoothAdapter mBTAdapter;
    private AuthenticatorActivity.PinInfo[] mUsers;
    private OtpSource mOtpSource;

    @Override
    public void onCreate() {
        super.onCreate();
        mAccountDb = DependencyInjector.getAccountDb();
        mOtpSource = DependencyInjector.getOtpProvider();
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        mUsers = AuthenticatorActivity.getUsers(mAccountDb, mOtpSource);
    }

    public KnockFactorService () {
        super("KnockFactorService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        boolean knockDetected = intent.getBooleanExtra("STATUS", false);
        if (knockDetected) {
            mConnectThread = new AuthenticatorActivity.ConnectThread(getApplicationContext(), mBTAdapter,
                    AuthenticatorActivity.getPairedDevice(mBTAdapter, AuthenticatorActivity.getMAC(this)),
                    new Handler(), mUsers);
            mConnectThread.start();
        }
    }
}
