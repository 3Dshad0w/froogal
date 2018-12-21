package co.circe.respos;

import android.app.Application;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;

public class MainApplication extends Application {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "JkJusWRKDiSjqTAC24SIh92kz";
    private static final String TWITTER_SECRET = "R94aX3RjdDfCNert8l4OLo0fgE8V7LLmBXdIQTUNr9dE4pVq5C";


	@Override
	public void onCreate() {
		super.onCreate();
		TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
		Fabric.with(this, new Twitter(authConfig));
		ImageLoader.getInstance()
				.init(ImageLoaderConfiguration.createDefault(this));
	}
}
