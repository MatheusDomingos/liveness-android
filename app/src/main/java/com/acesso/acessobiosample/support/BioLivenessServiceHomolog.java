package com.acesso.acessobiosample.support;

import android.annotation.SuppressLint;
import android.util.Log;

import com.acesso.acessobiosample.dto.LivenessRequest;
import com.acesso.acessobiosample.dto.LivenessResponse;
import com.acesso.acessobiosample.services.BioService;
import com.acesso.acessobiosample.services.ServiceGenerator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BioLivenessServiceHomolog {

    protected static final String TAG = "BioLivenessServiceHomolog.java";

    public void sendLiveness (LivenessRequest livenessRequest) {

        // /app/liveness  ----------------------

        ServiceGenerator
                .createService(BioService.class)
                .liveness("9ACD012B-BFBD-413E-9B4D-C626D0C6086F", livenessRequest)
                .enqueue(new Callback<LivenessResponse>() {

                    @Override
                    public void onResponse(Call<LivenessResponse> call, Response<LivenessResponse> response) {
                        LivenessResponse body = response.body();


                        if (body != null && body.isValid()) {

                            // work here

                        }
                        else {

                            String message = getErrorMessage(response);
                            if (message != null) {
                                Log.d("log error", message);
                            }else{
                                Log.d("log error", "Face não autenticada");

                            }

                        }
                    }

                    @SuppressLint("LongLogTag")
                    @Override
                    public void onFailure(Call<LivenessResponse> call, Throwable t) {
                        Log.d("ENVIO PARA O SERVER FALHOU: ", "ERRO: " + t.toString());
                    }
                });


    }

    private String getErrorMessage(Response response) {
        try {
            JSONObject j = new JSONObject(response.errorBody().string());
            JSONObject error = j.getJSONObject("Error");
            String message = error.getString("Description");
            Integer code = error.getInt("Code");

            if(code == 511) {
                message = "Liveness falhou";
            }

            return message;

        } catch (JSONException ex) {
            Log.d(TAG, ex.toString());
            return null;
        } catch (IOException ex) {
            Log.d(TAG, ex.toString());
            return null;
        }
    }


}
