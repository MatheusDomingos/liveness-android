package com.acesso.acessobiosample.sdktest;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class LivenessX {

    iLivenessX iLivenessX;
    private String apikey, authToken;
    private Activity activity;

    public static int REQUEST_LIVENESS = 7280;
    public static String RESULT_OK = "RESULT";

    public LivenessX(Activity context)
    {
        this.iLivenessX = (iLivenessX) context;
        this.activity = (Activity) this.iLivenessX;
    }

    public LivenessX(Fragment context)
    {
        this.iLivenessX = (iLivenessX) context;
        this.activity = (Activity) context.getActivity();
    }

    public void authenticate(String apikey, String authToken) {
        this.apikey = apikey;
        this.authToken = authToken;
    }

    public void openLivenessX(Boolean instructions) {
        if(hasPermission()) {
//            Intent intent = new Intent(this.activity, SelfieActivity.class);
//            activity.startActivityForResult(intent, REQUEST_LIVENESS);
        }else{
            this.iLivenessX.onError("Permissões de câmera não concedidas. É necessário a implementação para prosseguir.");
        }
    }

    private boolean hasPermission(){
        return ContextCompat.checkSelfPermission(this.activity, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }



}
