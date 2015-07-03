package co.froogal.froogal.fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;

import co.froogal.froogal.LoginActivity;
import co.froogal.froogal.MainActivity;
import co.froogal.froogal.R;
import co.froogal.froogal.library.UserFunctions;
import co.froogal.froogal.otp_activity;
import co.froogal.froogal.util.basic_utils;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link otp_fragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link otp_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class otp_fragment extends Fragment {

    // tag
    public static final String TAG = "otp_fragment";

    // Animation Varibales
    View view_animate_edittext;
    View view_animate_button;
    View view_animate_text;
    View view_animate_text1;
    View view_animate_resend;
    AnimatorSet bouncer;
    AnimatorSet bouncer1;
    AnimatorSet bouncer2;
    AnimatorSet bouncer3;
    ObjectAnimator animator_edittext;
    ObjectAnimator animator_otp_edittext;
    ObjectAnimator animator_button;
    ObjectAnimator animator_otp_button;
    ObjectAnimator animator_text;
    ObjectAnimator animator_text1;
    ObjectAnimator animator_otp_text;
    ObjectAnimator animator_buzz;
    ObjectAnimator animator_otp_resend;
    Display display;
    int width;
    int height;

    //UI elements
    private Button submit_button;
    public EditText edit_Text;
    TextView text_otp;
    TextView resend_otp;

    //otp variables
    String number = "";
    long otp = 0;
    long otp_got = 0;
    String name = "";
    Boolean otp_sent = false;
    Cursor cursor;

    //basic utils object
    basic_utils bu;

    //User Function
    UserFunctions uf;

    // Receiver
    public BroadcastReceiver sms_otp;

    // contructor
    public otp_fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_otp_fragment, container, false);

        // Initialize receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        filter.addCategory("co.froogal.froogal");
        getActivity().registerReceiver(new SMSReceiver(), filter);

        // UI initialization
        view_animate_edittext = v.findViewById(R.id.editText);
        view_animate_button = v.findViewById(R.id.button);
        view_animate_text = v.findViewById(R.id.textView4);
        view_animate_text1 = v.findViewById(R.id.textView5);
        view_animate_resend = v.findViewById(R.id.resend);
        display = getActivity().getWindowManager().getDefaultDisplay();
        bouncer = new AnimatorSet();
        bouncer1 = new AnimatorSet();
        bouncer2 = new AnimatorSet();
        bouncer3 = new AnimatorSet();
        submit_button = (Button) v.findViewById(R.id.button);
        edit_Text = (EditText) v.findViewById(R.id.editText);
        text_otp = (TextView) v.findViewById(R.id.textView5);
        resend_otp = (TextView) v.findViewById(R.id.resend);
        bu = new basic_utils(getActivity());
        uf = new UserFunctions();

        // Take phone number if not available
        number = bu.get_defaults("mobile");

        // Select interpolator
        Interpolator interpolator_overshoot = new AnimationUtils().loadInterpolator(getActivity(), android.R.interpolator.overshoot);
        Interpolator interpolator_anti_overshoot = new AnimationUtils().loadInterpolator(getActivity(), android.R.interpolator.anticipate_overshoot);

        // Getting height and width
        width = display.getWidth();
        height = display.getHeight();
        Log.d(TAG, "Width " + width);

        // Start animation
        if(bu.get_defaults("mobile").length() >= 10)
        {
            text_otp.setText("OTP");
            new process_otp().execute();
            startAnimation_otp_resend().start();
            view_animate_resend.setVisibility(View.VISIBLE);
        }
        bouncer.play(startAnimation_edittext(interpolator_anti_overshoot)).with(startAnimation_button(interpolator_anti_overshoot)).before(startAnimation_text(interpolator_overshoot)).before(startAnimation_text1(interpolator_overshoot));
        bouncer.start();

        // onclick listener
        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bu.get_defaults("otp_sent") == "true")
                {
                    otp_sent = true;
                }
                send_otp();
            }
        });

        // onclick listener
        resend_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new process_otp().execute();
            }
        });
        return v;
    }

    private void send_otp()
    {
        if(!otp_sent)
        {
            if(edit_Text.getText().length() >= 10) {
                number = edit_Text.getText().toString();
                bu.set_defaults("mobile",number);
                new process_otp().execute();
            }
            else
            {
                bouncer2.play(startAnimation_buzz1()).before(startAnimation_buzz2());
                bouncer3.play(bouncer2).before(startAnimation_buzz3());
                bouncer3.start();
                edit_Text.setText("");
            }
        }
        else
        {
            if(edit_Text.getText() != null)
            {
                try {
                    otp_got = Integer.parseInt(edit_Text.getText().toString());
                }
                catch (NumberFormatException e) {
                    Log.d(TAG,"Number Format exception");
                }
                if(otp_got == otp)
                {

                    new process_mobile().execute();
                }
                else
                {
                    bouncer2.play(startAnimation_buzz1()).before(startAnimation_buzz2());
                    bouncer3.play(bouncer2).before(startAnimation_buzz3());
                    bouncer3.start();
                    edit_Text.setText("");
                }
            }
        }
    }

    public class SMSReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent)
        {
            final Bundle bundle = intent.getExtras();
            try {
                if (bundle != null) {
                    final Object[] pdusObj = (Object[]) bundle.get("pdus");
                    for (int i = 0; i < pdusObj.length; i++) {
                        SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                        String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                        String senderNum = phoneNumber;
                        String message = currentMessage.getDisplayMessageBody();
                        if(message.contains("Froogal.")) {
                            if (message.contains("OTP")) {
                                if (message.toString().contains(String.valueOf(otp))) {
                                    edit_Text.setText(String.valueOf(otp));
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("SmsReceiver", "Exception smsReceiver" + e);
            }
        }
    }

    private class process_mobile extends AsyncTask<String, String, JSONObject> {


        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(getActivity());
            pDialog.setTitle("Checking ");
            pDialog.setMessage("Starting ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();
            JSONObject json = userFunction.send_mobile_verification_status(bu.get_defaults("uid"), number, "yes");
            return json;

        }

        @Override
        protected void onPostExecute(JSONObject json) {

            pDialog.dismiss();
            bu.set_defaults("mobile_verified", "yes");
            bu.set_defaults("mobile", number);
            try {
                 if (json.getString("success") != null) {
                    String res = json.getString("success");
                    if(Integer.parseInt(res) == 1){
                        bu.set_defaults("mobile_verified", "yes");
                        bu.set_defaults("mobile", number);
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                        getActivity().finish();

                    }
                    else{
                        bu.clear_defaults();
                        show_alert_dialog(getActivity(), "Server Error", json.getString("message") + " Please try again later!");
                    }
                }
            } catch (JSONException e) {
                bu.clear_defaults();
                show_alert_dialog(getActivity(), "Server Error", "Please try again later!");
            }
        }
    }

    private class process_otp extends AsyncTask<String, String, JSONObject> {


        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(getActivity());
            pDialog.setTitle("Sending OTP");
            pDialog.setMessage("On the way ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new     UserFunctions();
            name = bu.get_defaults("fname");
            if(name == "")
            {
                name = bu.get_defaults("lname");
            }
            otp = (int)(Math.random() * 9000) + 1000;
            bu.set_defaults("otp",String.valueOf(otp));
            JSONObject json = userFunction.send_otp(String.valueOf(otp),number,name);
            return json;

        }

        @Override
        protected void onPostExecute(JSONObject json) {


            pDialog.dismiss();
            try {
                if (json.getString("result") != null) {

                    Log.d(TAG,json.toString());
                    String res = json.getString("result");

                    if(res.contains("Your message is successfully sent to")){

                        otp_sent = true;
                        bu.set_defaults("otp_sent","true");
                        text_otp.setText("OTP");
                        edit_Text.setText("");
                        view_animate_resend.setVisibility(View.VISIBLE);
                        bouncer1.play(startAnimation_otp_edittext()).with(startAnimation_otp_button()).with(startAnimation_otp_text()).before(startAnimation_otp_resend());
                        bouncer1.start();

                    }else{

                        if(bu.get_defaults("mobile") != "") {
                            show_alert_dialog_invalid(getActivity(),"Invalid Mobile Number", "Please check the number you have typed !");
                        }
                        else
                        {
                            bouncer2.play(startAnimation_buzz1()).before(startAnimation_buzz2());
                            bouncer3.play(bouncer2).before(startAnimation_buzz3());
                            bouncer3.start();
                            edit_Text.setText("");

                        }

                    }
                }
            } catch (JSONException e) {
                show_alert_dialog(getActivity(), "Server Error", "Please try again later!");
            }
        }
    }


    // Error Dialog

    @SuppressWarnings("deprecation")
    public void show_alert_dialog(Context context, String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                getActivity().finish();
            }
        });
        alertDialog.show();
    }

    @SuppressWarnings("deprecation")
    public void show_alert_dialog_invalid(Context context, String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        alertDialog.show();
    }

    // All animations


    public ObjectAnimator startAnimation_text1(Interpolator interpolator) {

        animator_text1 = ObjectAnimator.ofFloat(view_animate_text1, "alpha",0,1);

        // Set the duration and interpolator for this animation
        animator_text1.setDuration(2000);
        animator_text1.setInterpolator(interpolator);

        return animator_text1;
    }

    public ObjectAnimator startAnimation_otp_edittext() {

        animator_otp_edittext = ObjectAnimator.ofFloat(view_animate_edittext, View.X, width, width/2 - Math.round(width/3.5) );

        // Set the duration and interpolator for this animation
        animator_otp_edittext.setDuration(1000);
        animator_otp_edittext.setInterpolator(new AnimationUtils().loadInterpolator(getActivity(), android.R.interpolator.anticipate_overshoot));

        return animator_otp_edittext;
    }

    public ObjectAnimator startAnimation_otp_button() {

        animator_otp_button = ObjectAnimator.ofFloat(view_animate_button, View.X, width, width/2 - Math.round(width/5) );

        // Set the duration and interpolator for this animation
        animator_otp_button.setDuration(1000);
        animator_otp_button.setInterpolator(new AnimationUtils().loadInterpolator(getActivity(), android.R.interpolator.anticipate_overshoot));

        return animator_otp_button;
    }

    public ObjectAnimator startAnimation_edittext(Interpolator interpolator) {

        animator_edittext = ObjectAnimator.ofFloat(view_animate_edittext, View.TRANSLATION_Y, -100, height/4 - Math.round(height/19.2));

        // Set the duration and interpolator for this animation
        animator_edittext.setDuration(2000);
        animator_edittext.setInterpolator(interpolator);

        return animator_edittext;
    }

    public ObjectAnimator startAnimation_button(Interpolator interpolator) {

        animator_button = ObjectAnimator.ofFloat(view_animate_button, View.TRANSLATION_Y,height,height/3 + Math.round(height/19.2));

        // Set the duration and interpolator for this animation
        animator_button.setDuration(2000);
        animator_button.setInterpolator(interpolator);

        return animator_button;
    }

    public ObjectAnimator startAnimation_text(Interpolator interpolator) {

        animator_text = ObjectAnimator.ofFloat(view_animate_text, "alpha",0,1);

        // Set the duration and interpolator for this animation
        animator_text.setDuration(2000);
        animator_text.setInterpolator(interpolator);

        return animator_text;
    }

    public ObjectAnimator startAnimation_otp_text() {

        animator_otp_text = ObjectAnimator.ofFloat(view_animate_text1, View.X,width,width/2 - Math.round(width/13.7));

        // Set the duration and interpolator for this animation
        animator_otp_text.setDuration(1000);
        animator_otp_text.setInterpolator(new AnimationUtils().loadInterpolator(getActivity(), android.R.interpolator.anticipate_overshoot));

        return animator_otp_text;
    }

    public ObjectAnimator startAnimation_otp_resend() {

        animator_otp_resend = ObjectAnimator.ofFloat(view_animate_resend, "alpha",0,1);

        // Set the duration and interpolator for this animation
        animator_otp_resend.setDuration(2000);
        animator_otp_resend.setInterpolator(new AnimationUtils().loadInterpolator(getActivity(), android.R.interpolator.anticipate_overshoot));

        return animator_otp_resend;
    }

    public ObjectAnimator startAnimation_buzz1() {

        animator_buzz = ObjectAnimator.ofFloat(view_animate_edittext, View.X,view_animate_edittext.getX(),view_animate_edittext.getX() - 70);

        // Set the duration and interpolator for this animation
        animator_buzz.setDuration(80);
        animator_buzz.setInterpolator(new AnimationUtils().loadInterpolator(getActivity(), android.R.interpolator.linear));

        return animator_buzz;
    }

    public ObjectAnimator startAnimation_buzz2() {

        animator_buzz = ObjectAnimator.ofFloat(view_animate_edittext, View.X,view_animate_edittext.getX() - 70,view_animate_edittext.getX() + 70);

        // Set the duration and interpolator for this animation
        animator_buzz.setDuration(80);
        animator_buzz.setInterpolator(new AnimationUtils().loadInterpolator(getActivity(), android.R.interpolator.linear));

        return animator_buzz;
    }

    public ObjectAnimator startAnimation_buzz3() {

        animator_buzz = ObjectAnimator.ofFloat(view_animate_edittext, View.X,view_animate_edittext.getX() + 70,view_animate_edittext.getX());

        // Set the duration and interpolator for this animation
        animator_buzz.setDuration(80);
        animator_buzz.setInterpolator(new AnimationUtils().loadInterpolator(getActivity(), android.R.interpolator.linear));

        return animator_buzz;
    }


}
