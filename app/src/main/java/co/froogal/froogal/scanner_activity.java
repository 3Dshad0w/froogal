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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scanner_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
