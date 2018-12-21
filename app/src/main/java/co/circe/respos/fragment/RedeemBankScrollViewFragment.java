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
public class RedeemBankScrollViewFragment extends ScrollViewFragment {

    basic_utils bu;
    private FloatLabeledEditText account_name;
    private FloatLabeledEditText bank_name;
    private FloatLabeledEditText account_number;
    private FloatLabeledEditText ifsc_code;
    private FloatLabeledEditText amount;
    private FloatLabeledEditText number;
    private RobotoTextView submit;
    String number_text;
    String amount_text;
    String account_name_text;
    String bank_name_text;
    String account_number_text;
    String ifsc_code_text;

    public static final String TAG = RedeemBankScrollViewFragment.class.getSimpleName();

    public static Fragment newInstance(int position) {
        RedeemBankScrollViewFragment fragment = new RedeemBankScrollViewFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    public RedeemBankScrollViewFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mPosition = getArguments().getInt(ARG_POSITION);

        bu = new basic_utils(getActivity());
        View view = inflater.inflate(R.layout.fragment_redeem_bank_scroll_view, container, false);
        account_name = (FloatLabeledEditText) view.findViewById(R.id.account_name_edit);
        bank_name = (FloatLabeledEditText) view.findViewById(R.id.bank_name_edit);
        account_number = (FloatLabeledEditText) view.findViewById(R.id.account_number_edit);
        ifsc_code = (FloatLabeledEditText) view.findViewById(R.id.ifsc_code_edit);
        amount = (FloatLabeledEditText) view.findViewById(R.id.amount_edit);
        number = (FloatLabeledEditText) view.findViewById(R.id.number_edit);
        submit = (RobotoTextView) view.findViewById(R.id.submit_bank);

        // Onclick
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clearErrors();

                boolean cancel = false;
                View focusView = null;

                // Store values at the time of the login attempt.
                account_name_text = account_name.getText().toString();
                bank_name_text = bank_name.getText().toString();
                account_number_text = account_number.getText().toString();
                ifsc_code_text = ifsc_code.getText().toString();
                amount_text = amount.getText().toString();
                number_text = number.getText().toString();

                // Check for a valid account name
                if(TextUtils.isEmpty(account_name_text))
                {
                    account_name.setError("Field is empty");
                    focusView = account_name;
                    cancel = true;
                }

                // Check for a valid bank name
                if(TextUtils.isEmpty(bank_name_text))
                {
                    bank_name.setError("Field is empty");
                    focusView = bank_name;
                    cancel = true;
                }

                //Check for a valid account number
                if(TextUtils.isEmpty(account_number_text))
                {
                    account_number.setError("Field is required");
                    focusView = account_number;
                    cancel = true;
                }

                // Check for a valid ifsc code
                if (TextUtils.isEmpty(ifsc_code_text)) {
                    ifsc_code.setError("Field is empty");
                    focusView = ifsc_code;
                    cancel = true;
                }

                //Check for a valid amount
                if (TextUtils.isEmpty(amount_text)) {
                    amount.setError("Field is empty");
                    focusView = amount;
                    cancel = true;
                }
                if( Integer.valueOf(amount_text) < 250)
                {
                    amount.setError("Amount should be greater than 250");
                    focusView = number;
                    cancel = true;
                }

                //Check for a valid number
                if(TextUtils.isEmpty(number_text))
                {
                    number.setError("Field is empty");
                    focusView = number;
                    cancel = true;
                }
                if(number_text.length() > 10)
                {
                    number.setError("Invalid number");
                    focusView = number;
                    cancel = true;
                }

                if (cancel) {
                    // There was an error; don't attempt login and focus the first
                    // form field with an error.
                    focusView.requestFocus();
                } else {
                    // Show a progress spinner, and kick off a background task to
                    // perform the user login attempt.
                    new process_bank().execute();

                }

            }
        });

        mScrollView = (NotifyingScrollView) view.findViewById(R.id.scrollview);
        setScrollViewOnScrollListener();
        return view;
    }

    private class process_bank extends AsyncTask<String, String, JSONObject> {


        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(getActivity());
            pDialog.setTitle("Contacting Servers");
            pDialog.setMessage("Fueling your account ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();
            JSONObject json = userFunction.bank(account_name_text,account_number_text,bank_name_text,ifsc_code_text,number_text,amount_text);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            pDialog.dismiss();
        }
    }

    public void clearErrors() {
        number.setError(null);
        amount.setError(null);
        account_name.setError(null);
        account_number.setError(null);
        bank_name.setError(null);
        ifsc_code.setError(null);
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
