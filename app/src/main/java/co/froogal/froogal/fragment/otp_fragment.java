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
import android.graphics.Path;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.Layout;
import android.text.LoginFilter;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.nhaarman.listviewanimations.util.AbsListViewWrapper;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UTFDataFormatException;

import co.froogal.froogal.MainActivity;
import co.froogal.froogal.QuickstartPreferences;
import co.froogal.froogal.R;
import co.froogal.froogal.library.UserFunctions;
import co.froogal.froogal.otp_activity;
import co.froogal.froogal.util.basic_utils;
import co.froogal.froogal.view.FloatLabeledEditText;

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
    long duration;
    View view_animate_edittext;
    View view_animate_button;
    View view_animate_text;
    View view_animate_text1;
    AnimatorSet bouncer;
    AnimatorSet bouncer1;
    ObjectAnimator animator_edittext;
    ObjectAnimator animator_otp_edittext;
    ObjectAnimator animator_button;
    ObjectAnimator animator_otp_button;
    ObjectAnimator animator_text;
    ObjectAnimator animator_text1;
    ObjectAnimator animator_otp_text;
    Display display;
    int width;
    int height;

    //UI elements
    private Button submit_button;
    private EditText edit_Text;
    TextView text_otp;

    //otp variables
    String number = "";
    String otp = "";
    int otp_got = 0;
    String name = "";
    String result ="";

    //basic utils object
    basic_utils bu;

    // Receiver
    public BroadcastReceiver sms_otp;

    // contructor
    public otp_fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_otp_fragment, container, false);

        // UI initialization
        view_animate_edittext = v.findViewById(R.id.editText);
        view_animate_button = v.findViewById(R.id.button);
        view_animate_text = v.findViewById(R.id.textView4);
        view_animate_text1 = v.findViewById(R.id.textView5);
        display = getActivity().getWindowManager().getDefaultDisplay();
        bouncer = new AnimatorSet();
        bouncer1 = new AnimatorSet();
        submit_button = (Button) v.findViewById(R.id.button);
        edit_Text = (EditText) v.findViewById(R.id.editText);
        text_otp = (TextView) v.findViewById(R.id.textView5);
        bu = new basic_utils(getActivity());

        // Select interpolator
        Interpolator interpolator_overshoot = new AnimationUtils().loadInterpolator(getActivity(), android.R.interpolator.overshoot);
        Interpolator interpolator_anti_overshoot = new AnimationUtils().loadInterpolator(getActivity(), android.R.interpolator.anticipate_overshoot);

        // Getting height and width
        width = display.getWidth();
        height = display.getHeight();
        Log.d(TAG, "Width " + width);

        // Start animation
        bouncer.play(startAnimation_edittext(interpolator_anti_overshoot)).with(startAnimation_button(interpolator_anti_overshoot)).before(startAnimation_text(interpolator_overshoot)).before(startAnimation_text1(interpolator_overshoot));
        bouncer.start();

        // onclick listener
        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edit_Text.getText().length() >= 10)
                {
                    number = edit_Text.getText().toString();
                    send_otp();
                }
            }
        });
        return v;
    }

    private void send_otp()
    {
        new process_otp().execute();
    }

    public class SMSReceiver extends BroadcastReceiver {

        // Get the object of SmsManager
        final SmsManager sms = SmsManager.getDefault();

        public void onReceive(Context context, Intent intent)
        {


            // Retrieves a map of extended data from the intent.
            final Bundle bundle = intent.getExtras();

            try {

                if (bundle != null) {

                    final Object[] pdusObj = (Object[]) bundle.get("pdus");

                    for (int i = 0; i < pdusObj.length; i++) {

                        SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                        String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                        String senderNum = phoneNumber;
                        String message = currentMessage.getDisplayMessageBody();

                        Log.i("SmsReceiver", "senderNum: "+ senderNum + "; message: " + message);


                        // Show Alert
                        int duration = Toast.LENGTH_LONG;
                        Toast toast = Toast.makeText(context,
                                "senderNum: "+ senderNum + ", message: " + message, duration);
                        toast.show();

                    } // end for loop
                } // bundle is null

            } catch (Exception e) {
                Log.e("SmsReceiver", "Exception smsReceiver" + e);

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

            UserFunctions userFunction = new UserFunctions();
            name = bu.get_defaults("fname");
            if(name == "")
            {
                name = bu.get_defaults("lname");
            }
            JSONObject json = userFunction.send_otp(String.valueOf((int)(Math.random() * 9000)+1000),number,name);
            return json;

        }

        @Override
        protected void onPostExecute(JSONObject json) {


            pDialog.dismiss();
            try {
                if (json.getString("result") != null) {

                    Log.d(TAG,json.toString());
                    String res = json.getString("result"    );

                    if(res.contains("Your message is successfully sent to")){


                        /*// Sending broadcast
                        final String action = "android.provider.Telephony.SMS_RECEIVED";
                        IntentFilter intentFilter = new IntentFilter(action);



                        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(sms_otp, intentFilter);

                        // broadcast reciever
                        sms_otp = new BroadcastReceiver() {

                            @Override
                            public void onReceive(Context context, Intent intent) {

                                final Bundle bundle = intent.getExtras();

                                try {

                                    if (bundle != null) {

                                        final Object[] pdusObj = (Object[]) bundle.get("pdus");

                                        for (int i = 0; i < pdusObj.length; i++) {

                                            SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                                            String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                                            String senderNum = phoneNumber;
                                            String message = currentMessage.getDisplayMessageBody();
                                            if(senderNum.equals("DM-FRUGAL"))
                                            {
                                                if(message.contains("Froogal."))
                                                {
                                                    if(message.contains("OTP"))
                                                    {
                                                        otp_got = Integer.valueOf(message.substring(29,33));
                                                        edit_Text.setText(otp_got);
                                                        Log.d(TAG,"GIt into");

                                                        // TODO
                                                        //LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(sms_otp);

                                                    }
                                                }
                                            }
                                        }
                                    }

                                } catch (Exception e) {
                                    Log.e("SmsReceiver", "Exception smsReceiver" +e);

                                }
                            }
                        };*/

                        text_otp.setText("OTP");
                        edit_Text.setText("");
                        bouncer1.play(startAnimation_otp_edittext()).with(startAnimation_otp_button()).with(startAnimation_otp_text());
                        bouncer1.start();

                    }else{
                        // Start animation for cycle
                        Log.d(TAG,"Cycle animation");

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

    // All animations


    public ObjectAnimator startAnimation_text1(Interpolator interpolator) {

        animator_text1 = ObjectAnimator.ofFloat(view_animate_text1, "alpha",0,1);

        // Set the duration and interpolator for this animation
        animator_text1.setDuration(2000);
        animator_text1.setInterpolator(interpolator);

        return animator_text1;
    }

    public ObjectAnimator startAnimation_otp_edittext() {

        animator_otp_edittext = ObjectAnimator.ofFloat(view_animate_edittext, View.X, width, width/2 -310 );

        // Set the duration and interpolator for this animation
        animator_otp_edittext.setDuration(2000);
        animator_otp_edittext.setInterpolator(new AnimationUtils().loadInterpolator(getActivity(), android.R.interpolator.anticipate_overshoot));

        return animator_otp_edittext;
    }

    public ObjectAnimator startAnimation_otp_button() {

        animator_otp_button = ObjectAnimator.ofFloat(view_animate_button, View.X, width, width/2 - 220 );

        // Set the duration and interpolator for this animation
        animator_otp_button.setDuration(2000);
        animator_otp_button.setInterpolator(new AnimationUtils().loadInterpolator(getActivity(), android.R.interpolator.anticipate_overshoot));

        return animator_otp_button;
    }

    public ObjectAnimator startAnimation_edittext(Interpolator interpolator) {

        animator_edittext = ObjectAnimator.ofFloat(view_animate_edittext, View.TRANSLATION_Y, -100, height/4 - 100);

        // Set the duration and interpolator for this animation
        animator_edittext.setDuration(2000);
        animator_edittext.setInterpolator(interpolator);

        return animator_edittext;
    }

    public ObjectAnimator startAnimation_button(Interpolator interpolator) {

        animator_button = ObjectAnimator.ofFloat(view_animate_button, View.TRANSLATION_Y,height,height/3 + 100);

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

        animator_otp_text = ObjectAnimator.ofFloat(view_animate_text1, View.X,width,width/2 - 80);

        // Set the duration and interpolator for this animation
        animator_otp_text.setDuration(2000);
        animator_otp_text.setInterpolator(new AnimationUtils().loadInterpolator(getActivity(), android.R.interpolator.anticipate_overshoot));

        return animator_otp_text;
    }


}
