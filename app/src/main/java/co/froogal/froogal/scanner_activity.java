package co.froogal.froogal;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import co.froogal.froogal.R;

public class scanner_activity extends ActionBarActivity {

    public static String res_latitude = "";
    public static String res_longitude = "";
    public static String res_ID = "";
    private String TAG="scanner activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_activity);
        Intent i = getIntent();
        res_latitude = i.getStringExtra("res_latitude");
        res_longitude = i.getStringExtra("res_longitude");
        res_ID = i.getStringExtra("res_ID");
        Log.d(TAG,res_latitude);
        Log.d(TAG,res_longitude);
        Log.d(TAG,res_ID);
    }


}
