package co.froogal.froogal;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import co.froogal.froogal.font.RobotoTextView;
import co.froogal.froogal.services.location_service;
import co.froogal.froogal.util.basic_utils;
import co.froogal.froogal.services.registration_intent_service;

public class SplashScreensActivity extends Activity implements GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks{

    // Tag
    private static final String TAG = "SplashScreensActivity";

    //Location Variables
    protected GoogleApiClient googleapiclientlocation;
    protected Location currentLocation;
    protected LocationRequest locationrequest;
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 2000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;

	// UI variables
	ImageView mLogo;

    // Boolean variables
    private Boolean check_all_proceed = false;

    // Basic_utils object
    basic_utils bu;


    // GCM variables
    public BroadcastReceiver gcm_registration_broadcast_receiver;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    RobotoTextView loginButton;
    RobotoTextView registerButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

        //Build location
        buildGoogleApiClient();

        // Facebook call
        FacebookSdk.sdkInitialize(getApplicationContext());

        // Remove ActionBar
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash_screen);

       	mLogo = (ImageView) findViewById(R.id.logo);
		loginButton = (RobotoTextView) findViewById(R.id.LoginButton);
        registerButton = (RobotoTextView) findViewById(R.id.RegisterButton);

        setAnimation();
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login = new Intent(getApplicationContext(), LoginActivity.class);

                startActivity(login);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent register = new Intent(getApplicationContext(), SignUpActivity.class);

                startActivity(register);
            }
        });
        // basic utils object
        bu = new basic_utils(getApplicationContext());

        // Gcm broadcast reciever
        gcm_registration_broadcast_receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(bu.get_defaults("gcm_token") == null)
                {
                    show_alert_dialog_gcm(SplashScreensActivity.this,"GCM registration failed","Please try again later !");
                }
                else
                {
                    check_all_proceed = true;
                    if(bu.login_check()) {

                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        finish();
                    }
                    else {
                       animation2();
                       animation3();
                       animation4();
                    }
                }
            }
        };

        check_location_internet_playservices();
        get_ip_address();


	}

    protected synchronized void buildGoogleApiClient() {

        googleapiclientlocation = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();

    }

    protected void createLocationRequest() {

        locationrequest = new LocationRequest();
        locationrequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        locationrequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        locationrequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    @Override
    public void onConnected(Bundle connectionHint) {

        if (currentLocation == null) {
            currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleapiclientlocation);
        }
        if(currentLocation != null) {

            //Updating shared prefecerences
            bu.set_defaults("latitude", String.valueOf(currentLocation.getLatitude()));
            bu.set_defaults("longitude", String.valueOf(currentLocation.getLongitude()));

        }
        else
        {
            buildGoogleApiClient();
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {

        googleapiclientlocation.connect();

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    @Override
    protected void onStart() {

        super.onStart();
        googleapiclientlocation.connect();

    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(gcm_registration_broadcast_receiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (googleapiclientlocation.isConnected()) {
            googleapiclientlocation.disconnect();
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(gcm_registration_broadcast_receiver);
    }

    // Ip Address Function
    private void get_ip_address()
    {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        bu.set_defaults("ip_address", inetAddress.getHostAddress().toString());
                    }
                }
            }
        } catch (SocketException e) {
            bu.set_defaults("ip_address","");
        }
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
	
	// Entire Animation code

	private void setAnimation() {
			animation1();
            //animation3();

	}

    private void animation1() {
        ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(mLogo, "alpha", 0.0F, 1.0F);
        alphaAnimation.setStartDelay(00);
        alphaAnimation.setDuration(1000);
        alphaAnimation.start();
    }


    private void animation2() {
        mLogo.setAlpha(1.0F);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.translate_top_to_center);
        mLogo.startAnimation(anim);
    }

    private void animation3() {
        ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(loginButton, "alpha", 0.0F, 1.0F);
        alphaAnimation.setStartDelay(1000);
        alphaAnimation.setDuration(500);
        alphaAnimation.start();
    }

    private void animation4() {
        ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(registerButton, "alpha", 0.0F, 1.0F);
        alphaAnimation.setStartDelay(1000);
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
                finish();
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
