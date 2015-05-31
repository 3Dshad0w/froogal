package co.froogal.froogal;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class SplashScreensActivity extends Activity {

	//private FontelloTextView mLogo;
	ImageView mLogo;
    private TextView welcomeText;

    // flag for Internet connection status
    Boolean isInternetPresent = false;
    // Connection detector class
    ConnectionDetector cd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE); //Removing ActionBar
		setContentView(R.layout.activity_splash_screen);
		
		//mKenBurns = (KenBurnsView) findViewById(R.id.ken_burns_images);
		mLogo = (ImageView) findViewById(R.id.logo);
		welcomeText = (TextView) findViewById(R.id.welcome_text);
//		mKenBurns.setImageResource(R.drawable.splash_screen_background);
        cd = new ConnectionDetector(getApplicationContext());
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // get Internet status
                isInternetPresent = cd.isConnectingToInternet();
                // check for Internet status
                if (isInternetPresent) {
                Intent openMainActivity =  new Intent(SplashScreensActivity.this, LoginRegisterActivity.class);
                startActivity(openMainActivity);
                finish();
                }
                else{
                    // Internet connection is not present
                    // Ask user to connect to Internet
                    showAlertDialog(SplashScreensActivity.this, "No Internet Connection",
                            "You don't have internet connection.", false);
                }

            }
        }, 3000);
        setAnimation();
	}
	
	/** Animation depends on category.
	 * */
	private void setAnimation() {
			animation2();
            animation3();

	}

    private void animation2() {
        ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(mLogo, "alpha", 0.0F, 1.0F);
        alphaAnimation.setStartDelay(1000);
        alphaAnimation.setDuration(500);
        alphaAnimation.start();
    }

    private void animation3() {
        ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(welcomeText, "alpha", 0.0F, 1.0F);
        alphaAnimation.setStartDelay(1700);
        alphaAnimation.setDuration(500);
        alphaAnimation.start();
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
