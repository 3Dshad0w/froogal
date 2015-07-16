package co.froogal.froogal;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;

import java.io.IOException;
import java.net.URL;

import com.google.android.gms.plus.PlusShare;
import com.google.android.gms.plus.model.people.Person;
import java.io.IOException;
import java.net.URL;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import io.fabric.sdk.android.Fabric;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

/**
 * Created by akhil on 8/7/15.
 */
public class InviteActivity extends ActionBarActivity {

    //Animation variables
    ObjectAnimator bottom_animator;
    Display display;
    int width;
    int height;
    LinearLayout bottom;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invite);
        SpannableString s = new SpannableString("Invite");
        Typeface myfont = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
        s.setSpan(myfont, 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(s);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TwitterAuthConfig authConfig =  new TwitterAuthConfig("pbwuKrP21OYnntNGa8KCFU2zj", "pejM8Ah6KacttWm4JsZU104pXuC34kWUHbkr158Di5cD5Q9kYS");
        Fabric.with(this, new TwitterCore(authConfig), new TweetComposer());

        // Content for sharing
        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                .setContentTitle("Froogal")
                .setContentDescription(
                        "Froogal gives you rewards !")
                .setContentUrl(Uri.parse("http://froogal.in"))
                .build();

        // fb share button
        ShareButton shareButton = (ShareButton) findViewById(R.id.fb_share);
        shareButton.setShareContent(linkContent);

        Button share_button = (Button) findViewById(R.id.share_button_t);
        share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TweetComposer.Builder builder = new TweetComposer.Builder(InviteActivity.this)
                        .text("Froogal gives you rewards !.");
                builder.show();
            }
        });

        // google+ share button
        Button share_g = (Button) findViewById(R.id.share_button);
        share_g.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch the Google+ share dialog with attribution to your app.
                Intent shareIntent = new PlusShare.Builder(InviteActivity.this)
                        .setType("text/plain")
                        .setText("Froogal gives you rewards !")
                        .setContentUrl(Uri.parse("https://froogal.in"))
                        .getIntent();

                startActivityForResult(shareIntent, 0);
            }
        });

        //TODO UI after jeevan photoshop


        // Getting height and width
        display = getWindowManager().getDefaultDisplay();
        width = display.getWidth();
        height = display.getHeight();
        bottom = (LinearLayout) findViewById(R.id.down_invite);

        startAnimation_bottom().start();
        // TODO Akhil Singh Referral code to text view
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public ObjectAnimator startAnimation_bottom() {

        bottom_animator = ObjectAnimator.ofFloat(bottom, "translationY", 2000, 0);

        // Set the duration and interpolator for this animation
        bottom_animator.setDuration(1000);
        bottom_animator.setInterpolator(new AnimationUtils().loadInterpolator(this, android.R.interpolator.accelerate_quad));

        return bottom_animator;
    }

}