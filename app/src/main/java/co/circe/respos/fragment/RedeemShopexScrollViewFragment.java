package co.circe.respos.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONObject;

import co.circe.respos.R;
import co.circe.respos.font.RobotoTextView;
import co.circe.respos.library.NotifyingScrollView;
import co.circe.respos.library.UserFunctions;
import co.circe.respos.util.basic_utils;
import co.circe.respos.view.FloatLabeledEditText;
import co.circe.respos.view.ScrollViewFragment;

/**
 * Created by akhil on 17/6/15.
 */
public class RedeemShopexScrollViewFragment extends ScrollViewFragment {

    basic_utils bu;
    private FloatLabeledEditText uid;
    private FloatLabeledEditText amount;
    private RobotoTextView submit;
    String amount_text;
    String uid_text;

    public static final String TAG = RedeemShopexScrollViewFragment.class.getSimpleName();

    public static Fragment newInstance(int position) {
        RedeemShopexScrollViewFragment fragment = new RedeemShopexScrollViewFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    public RedeemShopexScrollViewFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mPosition = getArguments().getInt(ARG_POSITION);

        bu = new basic_utils(getActivity());
        View view = inflater.inflate(R.layout.fragment_redeem_shopex_scroll_view, container, false);
        amount = (FloatLabeledEditText) view.findViewById(R.id.amount_edit);
        uid = (FloatLabeledEditText) view.findViewById(R.id.uid_edit);
        submit = (RobotoTextView) view.findViewById(R.id.submit_shopex);

        // Onclick
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clearErrors();

                boolean cancel = false;
                View focusView = null;

                // Store values at the time of the login attempt.
                amount_text = amount.getText().toString();
                uid_text = uid.getText().toString();

                // Check for a valid uid.
                if (TextUtils.isEmpty(uid_text)) {
                    uid.setError("Field is empty");
                    focusView = uid;
                    cancel = true;
                }

                // Check for a valid amount.
                if (TextUtils.isEmpty(amount_text)) {
                    amount.setError("Field is empty");
                    focusView = amount;
                    cancel = true;
                }

                if (cancel) {
                    // There was an error; don't attempt login and focus the first
                    // form field with an error.
                    focusView.requestFocus();
                } else {
                    // Show a progress spinner, and kick off a background task to
                    // perform the user login attempt.
                    new process_shopex().execute();

                }

            }
        });

        mScrollView = (NotifyingScrollView) view.findViewById(R.id.scrollview);
        setScrollViewOnScrollListener();
        return view;
    }

    private class process_shopex extends AsyncTask<String, String, JSONObject> {


        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(getActivity());
            pDialog.setTitle("Contacting Servers");
            pDialog.setMessage("Logging in ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();
            JSONObject json = userFunction.shopex(uid_text,amount_text);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            pDialog.dismiss();
        }
    }

    public void clearErrors() {
        uid.setError(null);
        amount.setError(null);
    }

    @SuppressWarnings("deprecation")
    public void showAlertDialog(Context context, String title, String message, Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
    }
}
