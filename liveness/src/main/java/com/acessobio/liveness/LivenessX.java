package com.acessobio.liveness;

import android.content.Context;
import android.content.Intent;

import com.acessobio.liveness.activity.SelfieActivity;

public class LivenessX {

    private String apikey, authToken;
    Context context;

    public LivenessX(Context context) {
        this.context = context;
    }

    public void authenticate(String apikey, String authToken) {
        this.apikey = apikey;
        this.authToken = authToken;
    }

    public void openLivenessX(Boolean instructions) {
        Intent intent = new Intent(context, SelfieActivity.class);
        context.startActivity(intent);
    }


}
