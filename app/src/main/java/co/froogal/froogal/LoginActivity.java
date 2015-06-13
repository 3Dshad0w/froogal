package co.froogal.froogal;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import co.froogal.froogal.font.RobotoTextView;
import co.froogal.froogal.library.UserFunctions;
import co.froogal.froogal.services.location_service;
import co.froogal.froogal.util.basic_utils;
import co.froogal.froogal.view.FloatLabeledEditText;


/**
 * Created by akhil on 10/3/15.
 */
public class LoginActivity extends Activity {

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
    protected void onResume() {
        sharedpreferences=getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        if (sharedpreferences.contains("email"))
        {
            if(sharedpreferences.contains("password")){
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        }
        super.onResume();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);


        inputEmail = (FloatLabeledEditText) findViewById(R.id.email);
        inputPassword = (FloatLabeledEditText) findViewById(R.id.pword);
        btnLogin = (TextView) findViewById(R.id.login);
        passreset = (TextView)findViewById(R.id.passres);
        loginErrorMsg = (TextView) findViewById(R.id.loginErrorMsg);


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




    private void clearErrors(){
       inputEmail.setError(null);
       inputPassword.setError(null);
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
            JSONObject json = userFunction.loginUser(email, password);

                Log.d("keysuccess", json.toString());

            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            try {

                if (json.getString(KEY_SUCCESS) != null) {

                    Log.d("loginjsondownhere", json.toString());
                    String res = json.getString(KEY_SUCCESS);

                    if(Integer.parseInt(res) == 1){
                        pDialog.setMessage("Loading User Space");
                        pDialog.setTitle("Getting Data");
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("email", email);
                        editor.putString("password", password);
//                        Log.d("fname", json.getString("fname"));
                        editor.putString("fname", json.getJSONObject("user").getString("fname"));
                        editor.putString("lname", json.getJSONObject("user").getString("lname"));
                        editor.putString("mobile", json.getJSONObject("user").getString("mobile"));
                        editor.putString("unique_id", json.getJSONObject("user").getString("unique_id"));
                        editor.putString("uid", json.getJSONObject("user").getString("uid"));
                        basic_utils bf = new basic_utils(getApplicationContext());
                        bf.set_defaults("uid",json.getJSONObject("user").getString("uid"));



                        editor.commit();
                        Log.d("preferenceUID", sharedpreferences.getString("uid", "xxx"));
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
                        loginErrorMsg.setText("Incorrect username/password");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
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