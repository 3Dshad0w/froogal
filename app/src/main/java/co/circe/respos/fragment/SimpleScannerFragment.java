package co.circe.respos.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;

import co.circe.respos.MenuOrder;
import co.circe.respos.library.UserFunctions;
import co.circe.respos.scanner_activity;
import co.circe.respos.util.basic_utils;
import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

public class SimpleScannerFragment extends Fragment implements ZBarScannerView.ResultHandler {

    //tag
    public static String TAG = "SimpleScannerFragment";

    private ZBarScannerView mScannerView;
    private String value="";
    private String latitude ="";
    private String longitude ="";
    private basic_utils bu;
    private UserFunctions uf;
    private LatLng location;
    private float distanceInMetersOne;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Object initiazation
        bu = new basic_utils(getActivity());
        uf = new UserFunctions();

        //Taking location from shared Preferences
        latitude = bu.get_defaults("latitude");
        longitude = bu.get_defaults("longitude");
        location = new LatLng(Double.valueOf(latitude),Double.valueOf(longitude));


        mScannerView = new ZBarScannerView(getActivity());
        return mScannerView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void handleResult(Result rawResult) {

        value = rawResult.getContents().toString();
        Intent i = getActivity().getIntent();
        Log.d("value of res", value);
        Log.d("intent resid", i.getStringExtra("res_ID"));
        if(value.equals(i.getStringExtra("res_ID")))
        {
            float[] results = new float[1];
            Location.distanceBetween(Double.valueOf(scanner_activity.res_latitude), Double.valueOf(scanner_activity.res_longitude), Double.valueOf(latitude), Double.valueOf(longitude), results);
            if(results[0] > 100.00)
            {
                Intent intent =  new Intent(getActivity(),MenuOrder.class);
                intent.putExtra("res_ID",scanner_activity.res_ID);
                startActivity(intent);
                getActivity().finish();
            }
            else
            {
                show_alert_dialog(getActivity(),"Invalid","You are not in the restarurant !");
            }
        }
        else
        {
            show_alert_dialog(getActivity(),"Invalid","Please check in from the selected restaurant !");
        }

    }

    @SuppressWarnings("deprecation")
    public void show_alert_dialog(Context context, String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //getActivity().finish();
            }
        });
        alertDialog.show();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }
}
