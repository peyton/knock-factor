package com.knockfactor;

import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

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
        boolean knockDetected = intent.getBooleanExtra(STATUS, false);

        if (knockDetected) {
            if (AuthenticatorActivity.mConnected == null) {
                Log.w("Knock Factor", "connecting");

                String mac = AuthenticatorActivity.getMAC(this);

                if (mac != null && mac.length() > 0) {

                    BluetoothDevice device = AuthenticatorActivity.getPairedDevice(mBTAdapter, mac);

                    if (device != null) {
                        if (mConnectThread != null) {
                            mConnectThread.cancel();
                        }

                        mConnectThread = new AuthenticatorActivity.ConnectThread(getApplicationContext(), mBTAdapter,
                                device,
                                new Handler(), mUsers);
                        mConnectThread.start();
                    }
                }
            } else {
                Log.w("Knock Factor", "writing");

                AuthenticatorActivity.mConnected.write("knocked".getBytes());
            }
        }
    }
}
