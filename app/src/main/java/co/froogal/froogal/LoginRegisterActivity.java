package co.froogal.froogal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import co.froogal.froogal.font.RobotoTextView;
import co.froogal.froogal.view.kbv.KenBurnsView;

/**
 * Created by akhil on 22/5/15.
 */
public class LoginRegisterActivity extends Activity{

    RobotoTextView loginButton;
    RobotoTextView registerButton;
    private KenBurnsView mKenBurns;
    SharedPreferences sharedpreferences;

    @Override
    protected void onResume() {
        sharedpreferences=getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        if (sharedpreferences.contains("email"))
        {
            if(sharedpreferences.contains("password")){
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        }
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login_register);


        mKenBurns = (KenBurnsView) findViewById(R.id.ken_burns_image_background);
        loginButton = (RobotoTextView) findViewById(R.id.LoginButton);
        registerButton = (RobotoTextView) findViewById(R.id.RegisterButton);
        mKenBurns.setImageResource(R.drawable.splash_screen_background);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login = new Intent(getApplicationContext(), LoginActivity.class);

                startActivity(login);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent register = new Intent(getApplicationContext(), SignUpActivity.class);

                startActivity(register);
            }
        });
    }
}
