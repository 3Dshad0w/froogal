package co.froogal.froogal;

/**
 * Created by rohitsakala on 19/5/15.
 */

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FetchAddressIntentService extends IntentService {

    private static final String TAG = "fetch-address-intent-service";
    protected ResultReceiver mReceiver;

    public FetchAddressIntentService() {

        super(TAG);

    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String errorMessage = "";
        mReceiver = intent.getParcelableExtra(Constants.RECEIVER);

        if (mReceiver == null) {
            showToast("No receiver received. There is nowhere to send the results.");
            return;
        }
        Location location = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);
        if (location == null) {
            deliverResultToReceiver(Constants.FAILURE_RESULT, "No location data provided to the service!");
            return;
        }

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
           addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    1);
        } catch (IOException ioException) {
            errorMessage = "Service not available";
        } catch (IllegalArgumentException illegalArgumentException) {
            errorMessage = "Invalid longitude and latitude";
        }

        if (addresses == null || addresses.size()  == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = "No address found";
            }
            deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
        } else {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<String>();
            for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }
            deliverResultToReceiver(Constants.SUCCESS_RESULT,
                    TextUtils.join(System.getProperty("line.separator"), addressFragments));
        }
    }

    private void deliverResultToReceiver(int resultCode, String message) {

        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, message);
        mReceiver.send(resultCode, bundle);

    }

    protected void showToast(String text) {

        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();

    }
}
