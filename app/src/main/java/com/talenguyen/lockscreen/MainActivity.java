package com.talenguyen.lockscreen;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.*;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

import com.talenguyen.lockscreen.services.LockScreenService;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends Activity {

    private BroadcastReceiver mHomeButtonReceiver = new BroadcastReceiver()
    {
        public void onReceive(Context paramContext, Intent paramIntent)
        {
            if (paramIntent.getCategories().contains("android.intent.category.HOME"))
            {
                android.os.Process.killProcess(paramIntent.resolveActivityInfo(MainActivity.this.getPackageManager(), 0).applicationInfo.uid);
                MainActivity.this.closeContextMenu();
                MainActivity.this.closeOptionsMenu();
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        Log.d("Focus debug", "Focus changed !");

        if(!hasFocus) {
            Log.d("Focus debug", "Lost focus !");
            Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            sendBroadcast(closeDialog);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter homeButtonFilter = new IntentFilter();
        homeButtonFilter.addAction(Intent.ACTION_MAIN);
        homeButtonFilter.addCategory(Intent.CATEGORY_HOME);
        registerReceiver(mHomeButtonReceiver, homeButtonFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mHomeButtonReceiver);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return false;
    }

//    @Override
//    public void onAttachedToWindow() {
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
//                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
//        super.onAttachedToWindow();
//    }

    @OnClick(R.id.ivLock)
    public void unlock() {
        startService(new Intent(this, LockScreenService.class));
        finish();
    }
}
