package co.froogal.froogal;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.AlertDialog;
import android.content.DialogInterface;
import co.froogal.froogal.util.basic_utils;
import co.froogal.froogal.services.registration_intent_service;

public class SplashScreensActivity extends Activity {

    // Tag
    private static final String TAG = "SplashScreensActivity";

	// UI variables
	ImageView mLogo;
    private TextView welcomeText;

    // Boolean variables
    private Boolean check_all_proceed = false;
    private Boolean timer_stop = false;

    // Basic_utils object
    basic_utils bu;

    // GCM variables
    public BroadcastReceiver gcm_registration_broadcast_receiver;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

        // Remove ActionBar
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash_screen);

       	mLogo = (ImageView) findViewById(R.id.logo);
		welcomeText = (TextView) findViewById(R.id.welcome_text);

        // basic utils object
        bu = new basic_utils(getApplicationContext());

        // Gcm broadcast reciever
        gcm_registration_broadcast_receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(!bu.get_defaults("gcm_sent_to_server").equals("true"))
                {
                    show_alert_dialog_gcm(SplashScreensActivity.this,"GCM registration failed","Please try again later !");
                }
                else
                {
                    check_all_proceed = true;
                    if(timer_stop)
                    {
                        Log.d(TAG,"Starting activity : LoginRegisterActivity");
                        Intent openMainActivity = new Intent(SplashScreensActivity.this, LoginRegisterActivity.class);
                        startActivity(openMainActivity);
                        finish();
                    }
                }
            }
        };

        start_timer();
        check_location_internet_playservices();
        setAnimation();

	}

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(gcm_registration_broadcast_receiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(gcm_registration_broadcast_receiver);
        super.onPause();
    }

    private boolean check_location_internet_playservices()
    {
        if(bu.internet_check())
        {
            if(bu.location_check())
            {
                if(bu.check_playservices(SplashScreensActivity.this))
                {
                    // GCM Registration

                    Intent intent = new Intent(this, registration_intent_service.class);
                    startService(intent);
                }
                else
                {
                    show_alert_dialog_playservices(SplashScreensActivity.this,"No Google Play Services","Please install play services");
                }

            }
            else
            {
                show_alert_dialog_location(SplashScreensActivity.this, "GPS is not enabled", "Please enable GPS to access !");
            }
        }
        else
        {
            show_alert_dialog_internet(SplashScreensActivity.this, "No Internet Connection", "You don't have internet connection !");
        }
        return true;
    }

    private void start_timer()
    {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(check_all_proceed)
                {
                    timer_stop = true;
                    Log.d(TAG,"Starting activity : LoginRegisterActivity");
                    Intent openMainActivity = new Intent(SplashScreensActivity.this, LoginRegisterActivity.class);
                    startActivity(openMainActivity);
                    finish();
                }
                else
                {
                    timer_stop = true;
                }
            }
        }, 3000);
    }
	
	// Entire Animation code

	private void setAnimation() {
			animation2();
            animation3();

	}

    private void animation2() {
        ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(mLogo, "alpha", 0.0F, 1.0F);
        alphaAnimation.setStartDelay(1000);
        alphaAnimation.setDuration(500);
        alphaAnimation.start();
    }

    private void animation3() {
        ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(welcomeText, "alpha", 0.0F, 1.0F);
        alphaAnimation.setStartDelay(1700);
        alphaAnimation.setDuration(500);
        alphaAnimation.start();
    }


    // All alert dialogs

    @SuppressWarnings("deprecation")
    public void show_alert_dialog_internet(Context context, String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alertDialog.show();
    }

    @SuppressWarnings("deprecation")
    public void show_alert_dialog_location(Context context, String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialog.setButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alertDialog.setButton2("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });
        alertDialog.show();
    }

    @SuppressWarnings("deprecation")
    public void show_alert_dialog_playservices(Context context, String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alertDialog.show();
    }

    @SuppressWarnings("deprecation")
    public void show_alert_dialog_gcm(Context context, String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alertDialog.show();
    }

}
