package com.talenguyen.lockscreen.services;

import android.app.KeyguardManager;
import android.app.Service;
import android.app.UiModeManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.talenguyen.lockscreen.MainActivity;

import java.util.concurrent.locks.Lock;

public class LockScreenService extends Service{

    private static final String TAG = LockScreenService.class.getSimpleName();
    private boolean IsDuringPhoneCall;
    private KeyguardManager.KeyguardLock kl;

    @Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

    // Recommend
	@Override
	public void onCreate() {
		super.onCreate();
		registerReceiver(mScreenOffReceiver, new IntentFilter(
				Intent.ACTION_SCREEN_OFF));
		PhoneStateListener phoneStateListener = new PhoneStateListener() {
			@Override
			public void onCallStateChanged(int state, String number) {
				String currentPhoneState = null;
				switch (state) {
				case TelephonyManager.CALL_STATE_RINGING:
					currentPhoneState = "Device is ringing. Call from "
							+ number + ".\n\n";
					break;
				case TelephonyManager.CALL_STATE_OFFHOOK:
					currentPhoneState = "Device call state is currently Off Hook.\n\n";
					IsDuringPhoneCall = true;
					break;
				case TelephonyManager.CALL_STATE_IDLE:
					currentPhoneState = "Device call state is currently Idle.\n\n";
					IsDuringPhoneCall = false;
					break;
				}
				Log.d(TAG, currentPhoneState);
			}
		};
		final TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		telephonyManager.listen(phoneStateListener,
				PhoneStateListener.LISTEN_CALL_STATE);

        this.kl = ((KeyguardManager)getSystemService(KEYGUARD_SERVICE)).newKeyguardLock(TAG);
        this.kl.disableKeyguard();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mScreenOffReceiver);
        this.kl.reenableKeyguard();
	}

	private BroadcastReceiver mScreenOffReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
				Log.d(TAG, "onReceiver: action user present");
			} else {
				Log.d(TAG, "onReceiver: action screen off");
			}
			if (!IsDuringPhoneCall) {
				Intent i = new Intent(LockScreenService.this,
						MainActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(i);
			}
		}
	};

}
