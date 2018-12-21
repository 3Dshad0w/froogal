package co.circe.respos;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import co.circe.respos.library.UserFunctions;
import co.circe.respos.util.basic_utils;
import co.circe.respos.view.FloatLabeledEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ChangePasswordActivity extends Activity {

    private static String KEY_SUCCESS = "success";
    private static String KEY_ERROR = "error";



    FloatLabeledEditText newpass;
    FloatLabeledEditText oldpass;
    TextView changePass;

    basic_utils bu;


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_changepassword);



        bu = new basic_utils(ChangePasswordActivity.this);
        newpass = (FloatLabeledEditText) findViewById(R.id.newpword);
        oldpass = (FloatLabeledEditText) findViewById(R.id.oldpword);
        changePass = (TextView) findViewById(R.id.changePass);

        changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                clearErrors();

                boolean cancel = false;
                View focusView = null;

                // Store values at the time of the login attempt.
                String moldpass = oldpass.getText().toString();
                String mnewpass = newpass.getText().toString();

                if (TextUtils.isEmpty(moldpass)) {
                    oldpass.setError(getString(R.string.error_field_required));
                    focusView = oldpass;
                    cancel = true;
                } else if (moldpass.length() < 4) {
                    oldpass.setError(getString(R.string.error_invalid_password));
                    focusView = oldpass;
                    cancel = true;
                }

                if (TextUtils.isEmpty(mnewpass)) {
                    newpass.setError(getString(R.string.error_field_required));
                    focusView = newpass;
                    cancel = true;
                } else if (mnewpass.length() < 4) {
                    newpass.setError(getString(R.string.error_invalid_password));
                    focusView = newpass;
                    cancel = true;
                }



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
        });}

    private void clearErrors() {
        oldpass.setError(null);
        newpass.setError(null);
    }

    private class NetCheck extends AsyncTask<String,String,Boolean>
    {
        private ProgressDialog nDialog;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            nDialog = new ProgressDialog(ChangePasswordActivity.this);
            nDialog.setMessage("Loading..");
            nDialog.setTitle("Checking Network");
            nDialog.setIndeterminate(false);
            nDialog.setCancelable(true);
            nDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... args){
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
                new ProcessChangePassword().execute();
            }
            else{
                nDialog.dismiss();

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    private class ProcessChangePassword extends AsyncTask<String, String, JSONObject> {


        private ProgressDialog pDialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            pDialog = new ProgressDialog(ChangePasswordActivity.this);
            pDialog.setTitle("Contacting Servers");
            pDialog.setMessage("Getting Data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {


            UserFunctions userFunction = new UserFunctions();
            JSONObject json = userFunction.chgPass(oldpass.getTextString(), newpass.getTextString(), bu.get_defaults("uid"));
            Log.d("Button", "Register");
            return json;


        }


        @Override
        protected void onPostExecute(JSONObject json) {

            try {
                if (json.getString(KEY_SUCCESS) != null) {
                    String res = json.getString(KEY_SUCCESS);
                    String red = json.getString(KEY_ERROR);


                    if (Integer.parseInt(res) == 1) {
                        /**
                         * Dismiss the process dialog
                         **/
                        pDialog.dismiss();
                        showAlertDialog(ChangePasswordActivity.this, "Success", "Your Password is successfully changed.", false);


                    } else if (Integer.parseInt(red) == 2) {
                        pDialog.dismiss();
                        showAlertDialog(ChangePasswordActivity.this, "Error", "Invalid old Password.", false);

                    } else if (Integer.parseInt(red) == 1) {
                        pDialog.dismiss();
                        showAlertDialog(ChangePasswordActivity.this, "Success", "Error occured in changing Password.", false);

                    }


                }
            } catch (JSONException e) {
                e.printStackTrace();


            }

        }}
    public void NetAsync(View view){
        new NetCheck().execute();
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






















