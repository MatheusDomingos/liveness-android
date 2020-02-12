package com.acessobio.liveness;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;

import com.acessobio.liveness.activity.SelfieActivity;

public class LivenessX {

    iLivenessX iLivenessX;
    private String apikey, authToken;
    private Activity activity;

    public LivenessX(iLivenessX context)
    {
        this.iLivenessX = context;
        this.activity = (Activity) this.iLivenessX;
    }

    public void authenticate(String apikey, String authToken) {
        this.apikey = apikey;
        this.authToken = authToken;
    }

    public void openLivenessX(Boolean instructions) {
        if(hasPermission()) {
            Intent intent = new Intent(activity, SelfieActivity.class);
            activity.startActivity(intent);
        }else{
            this.iLivenessX.onError("Permissões de câmera não concedidas. É necessário a implementação para prosseguir.");
        }
    }

    private boolean hasPermission(){
        return ContextCompat.checkSelfPermission(this.activity, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED;
    }


}
