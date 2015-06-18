package co.froogal.froogal.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import co.froogal.froogal.LoginActivity;
import co.froogal.froogal.MainActivity;

/**
 * Created by rohitsakala on 7/6/15.
 */
public class basic_utils {

    public Context _context;

    public basic_utils(Context context)
    {
        _context = context;
    }

    // Internet check function
    public boolean internet_check(){
        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }

        }
        return false;
    }

    // Store values into shared preferences
    public void set_defaults(String key, String value) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(_context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();

    }

    public void clear_defaults(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(_context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

    public boolean login_check(){

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(_context);
        if (preferences.contains("email"))
        {
            if(preferences.contains("password")){
                if(preferences.contains("mobile_verify")) {
                    if (this.get_defaults("mobile_verify").toString().equals("true")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    // Get values from shared preferences

    public String get_defaults(String key) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(_context);
        return preferences.getString(key, null);

    }

    // GPS check function
    public boolean location_check()
    {

        final LocationManager manager = (LocationManager) _context.getSystemService(Context.LOCATION_SERVICE);
        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) )
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    // Play Services check fucntion
    private  final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public boolean check_playservices(Activity activity) {

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(_context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode,activity ,PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                return false;
            }
            return false;
        }
        return true;

    }

}
