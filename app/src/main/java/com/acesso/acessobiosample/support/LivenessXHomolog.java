package com.acesso.acessobiosample.support;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.acesso.acessobiosample.activity.homolog.SelfieActivityHomolog;


public class LivenessXHomolog {

    iLivenessXHomolog iLivenessX;
    private String urlInstance, apikey, authToken;
    private Activity activity;

    public static int REQUEST_LIVENESS = 7280;
    public static String RESULT_OK = "RESULT";

    public LivenessXHomolog(Activity context, String urlIntance, String apikey, String authToken) {
        this.iLivenessX = (iLivenessXHomolog) context;
        this.activity = (Activity) this.iLivenessX;
        this.urlInstance = urlIntance;
        this.apikey = apikey;
        this.authToken = authToken;
    }

    public LivenessXHomolog(Fragment context,String urlIntance, String apikey, String authToken) {
        this.iLivenessX = (iLivenessXHomolog) context;
        this.activity = (Activity) context.getActivity();
        this.urlInstance = urlIntance;
        this.apikey = apikey;
        this.authToken = authToken;
    }

    public void authenticate(String apikey, String authToken) {
        this.apikey = apikey;
        this.authToken = authToken;
    }

    public void openLivenessX(Boolean instructions) {
        if (hasPermission()) {
            Intent intent = new Intent(this.activity, SelfieActivityHomolog.class);
            intent.putExtra("urlInstance", this.urlInstance);
            intent.putExtra("apikey", this.apikey);
            intent.putExtra("authToken", this.authToken);
            activity.startActivityForResult(intent, REQUEST_LIVENESS);
        } else {
            this.iLivenessX.onError("Permissões de câmera não concedidas. É necessário a implementação para prosseguir.");
        }
    }

    private boolean hasPermission() {
        return ContextCompat.checkSelfPermission(this.activity, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

}
