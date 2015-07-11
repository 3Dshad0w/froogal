package co.froogal.froogal;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import co.froogal.froogal.library.UserFunctions;
import co.froogal.froogal.util.ImageUtil;
import co.froogal.froogal.util.basic_utils;
import co.froogal.froogal.view.FloatLabeledEditText;

/**
 * Created by akhil on 8/7/15.
 */
public class EditProfileActivity extends ActionBarActivity {

    private String fname;
    private String lname;
    private String email;
    private String mobile;
    private basic_utils bu;

    private TextView fnameTextView;
    private TextView lnameTextView;
    private TextView emailTextView;
    private TextView mobileTextView;
    private TextView changePassword;
    private TextView saveChanges;

    private FloatLabeledEditText fnameEditText;
    private FloatLabeledEditText lnameEditText;
    private FloatLabeledEditText emailEditText;

    private ImageView user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);

        SpannableString s = new SpannableString("Edit Profile");
        Typeface myfont = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");

        s.setSpan(myfont, 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        getSupportActionBar().setTitle(s);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        bu = new basic_utils(this);
        fname = bu.get_defaults("fname");
        lname = bu.get_defaults("lname");
        email = bu.get_defaults("email");
        mobile = bu.get_defaults("mobile");

        user = (ImageView) findViewById(R.id.userImage);

        fnameTextView = (TextView) findViewById(R.id.fnameTextView);
        lnameTextView = (TextView) findViewById(R.id.lnameTextView);
        emailTextView = (TextView) findViewById(R.id.emailTextView);
        mobileTextView = (TextView) findViewById(R.id.mobileTextView);
        changePassword = (TextView) findViewById(R.id.changePassword);
        saveChanges = (TextView) findViewById(R.id.saveChanges);

        fnameEditText = (FloatLabeledEditText) findViewById(R.id.fnameEditText);
        lnameEditText = (FloatLabeledEditText) findViewById(R.id.lnameEditText);
        emailEditText = (FloatLabeledEditText) findViewById(R.id.emailEditText);


        fnameTextView.setText(fname);
        lnameTextView.setText(lname);
        emailTextView.setText(email);
        mobileTextView.setText(mobile);

        fnameEditText.setText(fname);
        lnameEditText.setText(lname);
        emailEditText.setText(email);

        ImageUtil.displayRoundImage(user, bu.get_defaults("image_url"), null);

        fnameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fnameTextView.setVisibility(View.GONE);
                fnameEditText.setVisibility(View.VISIBLE);
            }
        });

        lnameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lnameTextView.setVisibility(View.GONE);
                lnameEditText.setVisibility(View.VISIBLE);
            }
        });

        emailTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailTextView.setVisibility(View.GONE);
                emailEditText.setVisibility(View.VISIBLE);
            }
        });
        mobileTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                * set mobile verified as no
                * start otp activity
                * */

                new ProcessMobile().execute();

            }
        });
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(EditProfileActivity.this, ChangePasswordActivity.class);
                startActivity(myIntent);
                //finish();
            }
        });

        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                clearErrors();

                boolean cancel = false;
                View focusView = null;

                // Store values at the time of the login attempt.
                String mEmail = emailEditText.getText().toString();
                String mFirstName = fnameEditText.getText().toString();
                String mLastName = lnameEditText.getText().toString();



                // Check for a valid email address.
                if (TextUtils.isEmpty(mEmail)) {
                    emailEditText.setError(getString(R.string.error_field_required));
                    focusView = emailEditText;
                    cancel = true;
                } else if (!mEmail.contains("@") || !mEmail.contains(".")) {
                    emailEditText.setError(getString(R.string.error_invalid_email));
                    focusView = emailEditText;
                    cancel = true;
                }


                if (TextUtils.isEmpty(mFirstName)) {
                    fnameEditText.setError(getString(R.string.error_field_required));
                    focusView = fnameEditText;
                    cancel = true;
                }

                if (TextUtils.isEmpty(mLastName)) {
                    lnameEditText.setError(getString(R.string.error_field_required));
                    focusView = lnameEditText;
                    cancel = true;
                }
                /*
                if (TextUtils.isEmpty(mPhoneNumber)) {
                    inputMobile.setError(getString(R.string.error_field_required));
                    focusView = inputMobile;
                    cancel = true;
                }
                else if(mPhoneNumber.length() != 10) {
                    inputMobile.setError("Invalid PhoneNumber");
                    focusView = inputMobile;
                    cancel = true;
                }
                */

                if (cancel) {
                    // There was an error; don't attempt login and focus the first
                    // form field with an error.
                    focusView.requestFocus();
                } else {
                    // Show a progress spinner, and kick off a background task to
                    // perform the user login attempt.
                    NetAsync(view);

                }

            }


        });



    }
    public void NetAsync(View view){
        new NetCheck().execute();
    }

    private class NetCheck extends AsyncTask<String,String,Boolean>
    {
        private ProgressDialog nDialog;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            nDialog = new ProgressDialog(EditProfileActivity.this);
            nDialog.setMessage("Loading..");
            nDialog.setTitle("Checking Network");
            nDialog.setIndeterminate(false);
            nDialog.setCancelable(true);
            nDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... args){


/**
 * Gets current device state and checks for working internet connection by trying Google.
 **/
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                try {
                    URL url = new URL("http://www.google.com");
                    HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                    urlc.setConnectTimeout(3000);
                    urlc.connect();
                    if (urlc.getResponseCode() == 200) {
                        return true;
                    }
                } catch (MalformedURLException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return false;

        }
        @Override
        protected void onPostExecute(Boolean th){

            if(th == true){
                nDialog.dismiss();
                new ProcessSaveChanges().execute();
            }
            else{
                nDialog.dismiss();
                showAlertDialog(EditProfileActivity.this, "No Internet Connection",
                        "You don't have internet connection.", false);
            }
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
    private void clearErrors(){
        emailEditText.setError(null);
        fnameEditText.setError(null);
        lnameEditText.setError(null);

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

    private class ProcessSaveChanges extends AsyncTask<String, String, JSONObject> {

        /**
         * Defining Process dialog
         **/
        private ProgressDialog pDialog;

        String email,fname,lname;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            fname = fnameEditText.getText().toString();
            lname = lnameEditText.getText().toString();
            email = emailEditText.getText().toString();
            pDialog = new ProgressDialog(EditProfileActivity.this);
            pDialog.setTitle("Contacting Servers");
            pDialog.setMessage("Saving Changes ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {


            UserFunctions userFunction = new UserFunctions();
            Log.d("upjson", fname + lname + email);
            JSONObject json = userFunction.updateUser(bu.get_defaults("uid"), fname, lname, email);
            Log.d("json", json.toString());
            return json;


        }
        @Override
        protected void onPostExecute(JSONObject json) {
            /**
             * Checks for success message.
             **/
            try {
                if (json.getString("success") != null) {
                    Log.d("jsonhereinsignup", json.toString());
                    String res = json.getString("success");

                    String red = json.getString("success");

                    if(Integer.parseInt(res) == 1){
                        pDialog.setTitle("Getting Data");
                        pDialog.setMessage("Loading Info");



                        bu.set_defaults("email", email);
                        bu.set_defaults("fname", fname);
                        bu.set_defaults("lname", lname);

                        pDialog.dismiss();


                    }

                    else if (Integer.parseInt(red) ==2){
                        pDialog.dismiss();
                        showAlertDialog(EditProfileActivity.this, "Error",
                                "User already exists.", false);
                    }
                    else if (Integer.parseInt(red) ==3){
                        pDialog.dismiss();
                        showAlertDialog(EditProfileActivity.this, "Error",
                                "Invalid Email id.", false);

                    }

                    else {
                        pDialog.dismiss();
                        showAlertDialog(EditProfileActivity.this, "Error",
                                "Error occured in registration.", false);

                    }
                }


                else{
                    pDialog.dismiss();
                    showAlertDialog(EditProfileActivity.this, "Error",
                            "Error occured in registration.", false);

                }

            } catch (JSONException e) {
                pDialog.dismiss();
                e.printStackTrace();


            }
        }

    }

    private class ProcessMobile extends AsyncTask<String, String, JSONObject> {

        /**
         * Defining Process dialog
         **/
        private ProgressDialog pDialog;

        String email,fname,lname;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditProfileActivity.this);
            pDialog.setTitle("Contacting Servers");
            pDialog.setMessage("Saving Changes ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {


            UserFunctions userFunction = new UserFunctions();
            JSONObject json = userFunction.invalidateMobile(bu.get_defaults("uid"), bu.get_defaults("mobile"));
            Log.d("json", json.toString());
            return json;


        }
        @Override
        protected void onPostExecute(JSONObject json) {
            /**
             * Checks for success message.
             **/
            try {
                if (json.getString("success") != null) {
                    Log.d("jsonhereinsignup", json.toString());
                    String res = json.getString("success");

                    String red = json.getString("success");

                    if(Integer.parseInt(res) == 1){
                        pDialog.setTitle("Getting Data");
                        pDialog.setMessage("Loading Info");



                        bu.set_defaults("mobile", "");
                        bu.set_defaults("mobile_verified","no");

                        Intent myIntent = new Intent(EditProfileActivity.this, otp_activity.class);
                        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(myIntent);
                        finish();



                    }

                    else if (Integer.parseInt(red) ==2){
                        pDialog.dismiss();
                        showAlertDialog(EditProfileActivity.this, "Error",
                                "User already exists.", false);
                    }
                    else if (Integer.parseInt(red) ==3){
                        pDialog.dismiss();
                        showAlertDialog(EditProfileActivity.this, "Error",
                                "Invalid Email id.", false);

                    }

                    else {
                        pDialog.dismiss();
                        showAlertDialog(EditProfileActivity.this, "Error",
                                "Error occured in registration.", false);

                    }
                }


                else{
                    pDialog.dismiss();
                    showAlertDialog(EditProfileActivity.this, "Error",
                            "Error occured in registration.", false);

                }

            } catch (JSONException e) {
                pDialog.dismiss();
                e.printStackTrace();


            }
        }

    }

}
