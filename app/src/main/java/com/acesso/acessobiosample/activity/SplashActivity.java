package com.acesso.acessobiosample.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;
import com.acesso.acessobiosample.R;
import com.acesso.acessobiosample.fragment.CustomFragment;
import com.acesso.acessobiosample.fragment.HomeFragment;
import com.acesso.acessobiosample.fragment.LoginFragment;
import com.acesso.acessobiosample.utils.enumetators.SharedKey;
import com.orhanobut.hawk.Hawk;
import io.fabric.sdk.android.Fabric;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_splash);
        Handler handle = new Handler();

        Hawk.init(this).build();

        Hawk.put(SharedKey.LIVENESS, true);
        Hawk.put(SharedKey.AUTOCAPTURE, true);
        Hawk.put(SharedKey.COUNT_REGRESSIVE, true);

        final int SPLASH_DISPLAY_LENGTH = 1000;
        handle.postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent;

                if(Hawk.contains(SharedKey.AUTH_TOKEN)){

//                    intent = new Intent(SplashActivity.this, SimpleViewActivity.class);
//                    intent.putExtra(CustomFragment.FRAGMENT, HomeFragment.class);
                     intent = new Intent(SplashActivity.this, SelfieActivity.class);
                    startActivity(intent);
                }else{

//                    intent = new Intent(SplashActivity.this, AuthenticationActivity.class);
//                    intent.putExtra(CustomFragment.FRAGMENT, LoginFragment.class);

                     intent = new Intent(SplashActivity.this, SelfieActivity.class);
                    startActivity(intent);

                }
                startActivity(intent);

            }
        }, SPLASH_DISPLAY_LENGTH);




    }

}
