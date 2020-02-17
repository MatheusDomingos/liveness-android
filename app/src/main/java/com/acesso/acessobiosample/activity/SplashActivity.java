package com.acesso.acessobiosample.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.acesso.acessobiosample.dto.GetAuthTokenResponse;
import com.acesso.acessobiosample.fragment.WelcomeFragment;
import com.acesso.acessobiosample.services.AuthenticationInterceptor;
import com.acesso.acessobiosample.services.BioService;
import com.acesso.acessobiosample.services.ServiceGenerator;
import com.crashlytics.android.Crashlytics;
import com.acesso.acessobiosample.R;
import com.acesso.acessobiosample.fragment.CustomFragment;
import com.acesso.acessobiosample.fragment.HomeFragment;
import com.acesso.acessobiosample.fragment.LoginFragment;
import com.acesso.acessobiosample.utils.enumetators.SharedKey;
import com.orhanobut.hawk.Hawk;
import io.fabric.sdk.android.Fabric;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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


//                if(Hawk.contains(SharedKey.AUTH_TOKEN)){
//
////                    intent = new Intent(SplashActivity.this, SimpleViewActivity.class);
////                    intent.putExtra(CustomFragment.FRAGMENT, HomeFragment.class);
//                     intent = new Intent(SplashActivity.this, SelfieActivity.class);
//                    startActivity(intent);
//                }else{

                    getAuthToken();
//                  intent = new Intent(SplashActivity.this, SelfieActivity.class);

//                }



            }
        }, SPLASH_DISPLAY_LENGTH);


    }

    private void getAuthToken(){

        Hawk.put(SharedKey.NAME, "ADMIN");
        Hawk.put(SharedKey.PASSWORD, "Ac3ss0#66");

        ServiceGenerator
                .createService(BioService.class, true, false, "https://www2.acesso.io/seres/services/v3/acessoservice.svc/")
                .getAuthToken()
                .enqueue(new Callback<GetAuthTokenResponse>() {

                    @Override
                    public void onResponse(Call<GetAuthTokenResponse> call, Response<GetAuthTokenResponse> response) {

                        GetAuthTokenResponse body = response.body();

                        if (body != null && body.isValid()) {

                            Hawk.put(SharedKey.AUTH_TOKEN, body.getToken());
                            getAuthTokenBlackPanther();


                            Intent intent;
                            intent = new Intent(SplashActivity.this, SimpleViewActivity.class);
                            intent.putExtra(CustomFragment.FRAGMENT, WelcomeFragment.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                        }
                        else {
                            getAuthToken();
                            Log.d("SplashActivity",body != null ? body.getMessageError() : "Erro ao recuperar token");
                        }
                    }

                    @Override
                    public void onFailure(Call<GetAuthTokenResponse> call, Throwable t) {
                        getAuthToken();
                        Log.d("SplashActivity",t.getMessage());
                    }
                });

    }


    private void getAuthTokenBlackPanther(){

        Hawk.put(SharedKey.NAME, "ADMIN");
        Hawk.put(SharedKey.PASSWORD, "Ac3ss0#66");

        ServiceGenerator
                .createService(BioService.class, true, true, "https://crediariohomolog.acesso.io/blackpanther/services/v3/acessoservice.svc/")
                .getAuthToken()
                .enqueue(new Callback<GetAuthTokenResponse>() {

                    @Override
                    public void onResponse(Call<GetAuthTokenResponse> call, Response<GetAuthTokenResponse> response) {

                        GetAuthTokenResponse body = response.body();

                        if (body != null && body.isValid()) {
                            Hawk.put(SharedKey.AUTH_TOKEN_PANTHER, body.getToken());
                        }
                        else {
                            getAuthTokenBlackPanther();
                            Log.d("SplashActivity",body != null ? body.getMessageError() : "Erro ao recuperar token");
                        }
                    }

                    @Override
                    public void onFailure(Call<GetAuthTokenResponse> call, Throwable t) {
                        getAuthTokenBlackPanther();
                        Log.d("SplashActivity",t.getMessage());
                    }
                });

    }

}
