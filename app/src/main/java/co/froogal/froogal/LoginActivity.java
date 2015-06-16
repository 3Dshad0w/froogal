package co.froogal.froogal;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;
import java.util.Locale;

import co.froogal.froogal.library.UserFunctions;
import co.froogal.froogal.util.basic_utils;
import co.froogal.froogal.view.FloatLabeledEditText;


/**
 * Created by akhil on 10/3/15.
 */

public class LoginActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{


    //tag
    public static String TAG = "LoginActivity";

    // Google+ Sign In Variables
    private static final int RC_SIGN_IN = 0;
    private GoogleApiClient google_api_client;
    private boolean mIntentInProgress;
    private boolean sign_in_clicked;
    private com.google.android.gms.common.SignInButton google_sign_in;
    protected Location currentLocation;
    String latitude = "";
    String longitude = "";
    protected LocationRequest locationrequest;
    Person.Name person_name;
    Person.Image person_image;
    String first_name="";
    String last_name="";
    String image_url="";
    String email="";
    String ip_address="";
    String imei="";
    String registered_at = "";
    String registered_through = "g";
    String birthday = "";
    JSONObject json;
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 2000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    // Facebook Sign In Variables
    CallbackManager callbackManager;
    private LoginButton loginButton;
    AccessToken token;
    JSONObject picture_object;
    JSONObject picture_data_object;
    private ProgressDialog fbDialog;
    String id = "";


    // User Function Object
    UserFunctions uf;

    // Basci utils Object
    basic_utils bu;
    String gcm_token;

    TextView btnLogin;
    TextView passreset;
    FloatLabeledEditText inputEmail;
    FloatLabeledEditText inputPassword;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPreferences" ;

    private TextView loginErrorMsg;
    /**
     * Called when the activity is first created.
     */
    private static String KEY_SUCCESS = "success";
    private static String KEY_UID = "uid";
    private static String KEY_MOBILE = "mobile";
    private static String KEY_FIRSTNAME = "fname";
    private static String KEY_LASTNAME = "lname";
    private static String KEY_EMAIL = "email";
    private static String KEY_CREATED_AT = "created_at";


    // flag for Internet connection status
    Boolean isInternetPresent = false;
    // Connection detector class




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        bu = new basic_utils(this);

        //Taking location from shared Preferences
        latitude = bu.get_defaults("latitude");
        longitude = bu.get_defaults("longitude");

        // Facebook button and click
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.facebook_login);
        loginButton.setReadPermissions("public_profile");
        loginButton.setReadPermissions("user_friends");
        loginButton.setReadPermissions("email");
        loginButton.setReadPermissions("user_birthday");

        // TODO Publish permissions to be taken

        // Facebook Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                // Showing progress bar
                fbDialog = new ProgressDialog(LoginActivity.this);
                fbDialog.setTitle("Contacting Servers");
                fbDialog.setMessage("Logging in ...");
                fbDialog.setIndeterminate(false);
                fbDialog.setCancelable(false);
                fbDialog.show();

                token = AccessToken.getCurrentAccessToken();
                // Retrieving user data

                GraphRequest request = GraphRequest.newMeRequest(
                        token,
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                try {
                                    Log.d(TAG,response.toString());

                                    // Modifying progressbar
                                    fbDialog.setMessage("Loading User Space");
                                    fbDialog.setTitle("Getting Data");

                                    if(object.has("first_name")) {
                                        first_name = object.getString("first_name");
                                    }
                                    if(object.has("last_name"))
                                    {
                                        last_name = object.getString("last_name");
                                    }
                                    if(object.has("picture")) {
                                        picture_object = object.getJSONObject("picture");
                                        if(picture_object.has("data"))
                                        {
                                            picture_data_object = picture_object.getJSONObject("data");
                                            if(picture_data_object.has("url"))
                                            {
                                                image_url = picture_data_object.getString("url");
                                            }
                                        }
                                    }
                                    if(object.has("email")) {
                                        email = object.getString("email");
                                    }
                                    if(object.has("id"))
                                    {
                                        id = object.getString("id");
                                    }
                                    ip_address = bu.get_defaults("ip_address");
                                    TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                                    imei = telephonyManager.getDeviceId();
                                    if(latitude != null && longitude !="") {

                                        // To get city name
                                        try {
                                            Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
                                            List<Address> addresses = gcd.getFromLocation(Double.valueOf(latitude), Double.valueOf(longitude), 1);
                                            if (addresses.size() > 0) {
                                                registered_at = addresses.get(0).getLocality();
                                            }
                                        } catch (Exception e) {
                                            Log.d(TAG, "City Name Call Failed");
                                        }
                                    }
                                    registered_through = "f";
                                    if(object.has("birthday"))
                                    {
                                        birthday = object.getString("birthday");
                                    }

                                    // TODO firends list store after someone uses
                                    new process_login_facebook().execute();
                                }
                                catch (Exception e)
                                {
                                    Log.d(TAG,e.toString());
                                    show_alert_dialog(LoginActivity.this, "Server Error", "Please try again later!");
                                }
                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,first_name,last_name,friends,picture,email");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {
                Log.d(TAG,"Cancelled");
                show_alert_dialog(LoginActivity.this, "Server Error", "Please try again later!");
            }

            @Override
            public void onError(FacebookException exception) {
                show_alert_dialog(LoginActivity.this, "Server Error", "Please try again later!");
                Log.d(TAG,exception.toString());
            }
        });

        SpannableString s = new SpannableString("Login");
        Typeface myfont = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");

        s.setSpan(myfont, 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        getSupportActionBar().setTitle(s);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Google+ Sign In Code
        google_api_client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Plus.API)
                .addScope(new Scope("profile"))
                .build();
        createLocationRequest();

        // User Function object registration
        uf = new UserFunctions();

        // Basic utils object
        bu = new basic_utils(getApplicationContext());

        // UI registrations
        google_sign_in = (com.google.android.gms.common.SignInButton)findViewById(R.id.google_login);
        inputEmail = (FloatLabeledEditText) findViewById(R.id.email);
        inputPassword = (FloatLabeledEditText) findViewById(R.id.pword);
        btnLogin = (TextView) findViewById(R.id.login);
        passreset = (TextView)findViewById(R.id.passres);

        // Google+ Sign In onClick
        google_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.google_login && !google_api_client.isConnecting()) {
                    sign_in_clicked = true;
                    google_api_client.connect();
                }
            }
        });


        passreset.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), ForgetPasswordActivity.class);
                startActivityForResult(myIntent, 0);
                finish();
            }});




        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                clearErrors();

                // Store values at the time of the login attempt.
                String email = inputEmail.getText().toString();
                String password = inputPassword.getText().toString();

                boolean cancel = false;
                View focusView = null;

                // Check for a valid password.
                if (TextUtils.isEmpty(password)) {
                    inputPassword.setError(getString(R.string.error_field_required));
                    focusView = inputPassword;
                    cancel = true;
                } else if (password.length() < 4) {
                    inputPassword.setError(getString(R.string.error_invalid_password));
                    focusView = inputPassword;
                    cancel = true;
                }

                // Check for a valid email address.
                if (TextUtils.isEmpty(email)) {
                    inputEmail.setError(getString(R.string.error_field_required));
                    focusView = inputEmail;
                    cancel = true;
                }

                if (cancel) {
                    // There was an error; don't attempt login and focus the first
                    // form field with an error.
                    focusView.requestFocus();
                } else {
                    // perform the user login attempt.
                    NetAsync(view);
                }


            }
        });
    }


    protected void createLocationRequest() {

        locationrequest = new LocationRequest();
        locationrequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        locationrequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        locationrequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                // Respond to the action bar's Up/Home button
                case android.R.id.home:
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
    protected void onStop() {
        super.onStop();


        if (google_api_client.isConnected()) {
            google_api_client.disconnect();
        }
    }

    private void clearErrors(){
       inputEmail.setError(null);
       inputPassword.setError(null);
    }

    @Override
    public void onConnected(Bundle bundle) {

        if (currentLocation == null) {
            currentLocation = LocationServices.FusedLocationApi.getLastLocation(google_api_client);
        }
        if(currentLocation != null) {

            latitude = String.valueOf(currentLocation.getLatitude());
            longitude = String.valueOf(currentLocation.getLongitude());

            // Upating Shared preferences

            bu.set_defaults("latitude",latitude);
            bu.set_defaults("longitude",longitude);

        }
        if(sign_in_clicked) {

            // Retrieving additional information

            if (Plus.PeopleApi.getCurrentPerson(google_api_client) != null) {
                Person currentPerson = Plus.PeopleApi.getCurrentPerson(google_api_client);

                // Code for extracting user data

                if (currentPerson.hasName()) {
                    person_name = currentPerson.getName();
                    if (person_name.hasFamilyName()) {
                        first_name = person_name.getFamilyName();
                    } else {
                        first_name = person_name.getMiddleName();
                    }
                    if (person_name.hasGivenName()) {
                        last_name = person_name.getGivenName();
                    }
                }

                // TODO see birthday
                if (currentPerson.hasBirthday()) {
                    birthday = currentPerson.getBirthday();
                    Log.d(TAG, birthday);
                }
                ip_address = bu.get_defaults("ip_address");
                if (currentPerson.hasImage()) {
                    person_image = currentPerson.getImage();
                    if (person_image.hasUrl()) {
                        image_url = person_image.getUrl();
                    }
                }
                email = Plus.AccountApi.getAccountName(google_api_client);
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                imei = telephonyManager.getDeviceId();

                // To get city name
                try {
                    Geocoder gcd = new Geocoder(this, Locale.getDefault());
                    List<Address> addresses = gcd.getFromLocation(Double.valueOf(latitude), Double.valueOf(longitude), 1);
                    if (addresses.size() > 0) {
                        registered_at = addresses.get(0).getLocality();
                    }
                } catch (Exception e) {
                    Log.d(TAG, "City Name Call Failed");
                }

                registered_at = "g";
                // Store in database
                new process_login_google().execute();

            }
        }

    }

    // Alert Dialogs

    @SuppressWarnings("deprecation")
    public void show_alert_dialog(Context context, String title, String message) {
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


    // For logout

   /* @Override
    public void onClick(View view) {
        if (view.getId() == R.id.sign_out_button) {
            if (mGoogleApiClient.isConnected()) {
                mGoogleApiClient.clearDefaultAccountAndReconnect();
            }
        }
    }*/


    @Override
    public void onConnectionSuspended(int i) {
        google_api_client.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        if (!mIntentInProgress) {
            if (sign_in_clicked && connectionResult.hasResolution()) {
                // The user has already clicked 'sign-in' so we attempt to resolve all
                // errors until the user is signed in, or they cancel.
                try {
                    connectionResult.startResolutionForResult(this, RC_SIGN_IN);
                    mIntentInProgress = true;
                } catch (IntentSender.SendIntentException e) {
                    // The intent was canceled before it was sent.  Return to the default
                    // state and attempt to connect to get an updated ConnectionResult.
                    mIntentInProgress = false;
                    google_api_client.connect();
                }
            }
        }

    }

    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            mIntentInProgress = false;
            Log.d(TAG, "dsafas");
            if (!google_api_client.isConnecting()) {
                google_api_client.connect();
            }
        } else {
            callbackManager.onActivityResult(requestCode,responseCode, intent);
        }
    }

    // Google +  Process login

    private class process_login_google extends AsyncTask<String, String, JSONObject> {

        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setTitle("Contacting Servers");
            pDialog.setMessage("Logging in ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            gcm_token = bu.get_defaults("gcm_token");
            json = uf.save_google_user_data_to_server(gcm_token,first_name,last_name,image_url,email,ip_address,imei,registered_through,latitude,longitude,registered_at,birthday);
            return json;

        }

        @Override
        protected void onPostExecute(JSONObject json) {
            try {

                if (json.getString(KEY_SUCCESS) != null) {

                    String res = json.getString(KEY_SUCCESS);
                    if(Integer.parseInt(res) == 1){

                        pDialog.setMessage("Loading User Space");
                        pDialog.setTitle("Getting Data");

                        bu.set_defaults("email", email);
                        bu.set_defaults("password", "");
                        bu.set_defaults("fname", first_name);
                        bu.set_defaults("lname", last_name);
                        bu.set_defaults("image_url", image_url);
                        bu.set_defaults("ip_address", ip_address);
                        bu.set_defaults("registered_through", registered_through);
                        bu.set_defaults("registered_at", registered_at);
                        bu.set_defaults("mobile", "");
                        bu.set_defaults("birthday", birthday);

                        // TODO done later after akhil singh writes!!

               //         bu.set_defaults("unique_id", json.getJSONObject("user").getString("unique_id"));
               //         bu.set_defaults("uid", json.getJSONObject("user").getString("uid"));

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        pDialog.dismiss();
                        startActivity(intent);
                        finish();
                    }
                    else{

                        pDialog.dismiss();
                        show_alert_dialog(LoginActivity.this, "Server Error", "Please try again later!");
                    }
                }
            } catch (JSONException e) {
                show_alert_dialog(LoginActivity.this, "Server Error", "Please try again later!");
            }
        }
    }

    // Facebook Process login

    private class process_login_facebook extends AsyncTask<String, String, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            gcm_token = bu.get_defaults("gcm_token");
            json = uf.save_facebook_user_data_to_server(id,gcm_token,first_name, last_name, image_url, email, ip_address, imei, registered_through, latitude, longitude, registered_at, birthday);
            return json;

        }

        @Override
        protected void onPostExecute(JSONObject json) {
            try {

                if (json.getString(KEY_SUCCESS) != null) {

                    String res = json.getString(KEY_SUCCESS);
                    if(Integer.parseInt(res) == 1){

                        bu.set_defaults("email", email);
                        bu.set_defaults("password", "");
                        bu.set_defaults("fname", first_name);
                        bu.set_defaults("lname", last_name);
                        bu.set_defaults("image_url", image_url);
                        bu.set_defaults("ip_address", ip_address);
                        bu.set_defaults("registered_through", registered_through);
                        bu.set_defaults("registered_at", registered_at);
                        bu.set_defaults("mobile", "");
                        bu.set_defaults("birthday", birthday);

                        // TODO done later after akhil singh writes!!

                        //         bu.set_defaults("unique_id", json.getJSONObject("user").getString("unique_id"));
                        //         bu.set_defaults("uid", json.getJSONObject("user").getString("uid"));

                        fbDialog.dismiss();

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                    else{

                        show_alert_dialog(LoginActivity.this, "Server Error", "Please try again later!");
                    }
                }
            } catch (JSONException e) {
                show_alert_dialog(LoginActivity.this, "Server Error", "Please try again later!");
            }
        }
    }

    private class ProcessLogin extends AsyncTask<String, String, JSONObject> {


        private ProgressDialog pDialog;

        String email,password;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            inputEmail = (FloatLabeledEditText) findViewById(R.id.email);
            inputPassword = (FloatLabeledEditText) findViewById(R.id.pword);
            email = inputEmail.getText().toString();
            password = inputPassword.getText().toString();
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setTitle("Contacting Servers");
            pDialog.setMessage("Logging in ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();
            Log.d("loginjson", email+" "+password);

            JSONObject json = userFunction.loginUser(gcm_token,email, password);

                Log.d("keysuccess", json.toString());

            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            try {

                if (json.getString(KEY_SUCCESS) != null) {

                    String res = json.getString(KEY_SUCCESS);

                    if(Integer.parseInt(res) == 1){
                        pDialog.setMessage("Loading User Space");
                        pDialog.setTitle("Getting Data");

                        bu.set_defaults("email", email);
                        bu.set_defaults("password", password);
//                        Log.d("fname", json.getString("fname"));
                        bu.set_defaults("fname", json.getJSONObject("user").getString("fname"));
                        bu.set_defaults("lname", json.getJSONObject("user").getString("lname"));
                        bu.set_defaults("mobile", json.getJSONObject("user").getString("mobile"));
                        bu.set_defaults("unique_id", json.getJSONObject("user").getString("unique_id"));
                        bu.set_defaults("uid", json.getJSONObject("user").getString("uid"));


                        Intent upanel = new Intent(getApplicationContext(), MainActivity.class);
                        upanel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        pDialog.dismiss();
                        startActivity(upanel);
                        /**
                         * Close Login Screen
                         **/
                        finish();
                    }else{

                        pDialog.dismiss();
                        showAlertDialog(LoginActivity.this, "Error",
                                "Incorrect username/password.", false);
                        loginErrorMsg.setText("Incorrect username/password");
                    }
                }
            } catch (JSONException e) {
                show_alert_dialog(LoginActivity.this, "Server Error", "Please try again later!");
            }
        }
    }
    public void NetAsync(View view){
        // get Internet status
        //isInternetPresent = cd.isConnectingToInternet();
        isInternetPresent = true;
        // check for Internet status
        if (isInternetPresent) {
            new ProcessLogin().execute();
        }
        else{
            // Internet connection is not present
            // Ask user to connect to Internet
            showAlertDialog(LoginActivity.this, "No Internet Connection",
                    "You don't have internet connection.", false);
        }
    }

    @SuppressWarnings("deprecation")
    public void showAlertDialog(Context context, String title, String message, Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting alert dialog icon
        alertDialog.setIcon(android.R.drawable.ic_dialog_alert);

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

}