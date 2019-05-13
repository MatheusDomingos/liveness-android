package com.example.acessobiosample.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.acessobiosample.R;
import com.example.acessobiosample.fragment.CustomFragment;
import com.example.acessobiosample.fragment.HomeFragment;
import com.example.acessobiosample.fragment.LoginFragment;
import com.example.acessobiosample.utils.enumetators.SharedKey;
import com.google.firebase.FirebaseApp;
import com.orhanobut.hawk.Hawk;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        Handler handle = new Handler();

        Hawk.init(this).build();

        final int SPLASH_DISPLAY_LENGTH = 1000;
        handle.postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent;

                if(Hawk.contains(SharedKey.NAME)){

                    intent = new Intent(SplashActivity.this, SimpleViewActivity.class);
                    intent.putExtra(CustomFragment.FRAGMENT, HomeFragment.class);

                }else{

                    intent = new Intent(SplashActivity.this, AuthenticationActivity.class);
                    intent.putExtra(CustomFragment.FRAGMENT, LoginFragment.class);

                }
                startActivity(intent);

            }
        }, SPLASH_DISPLAY_LENGTH);




    }

}
