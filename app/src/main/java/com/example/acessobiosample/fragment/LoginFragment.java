package com.example.acessobiosample.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.Signature;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.acessobiosample.R;
import com.example.acessobiosample.activity.AuthenticationActivity;
import com.example.acessobiosample.activity.SelfieActivity;
import com.example.acessobiosample.activity.SimpleViewActivity;
import com.example.acessobiosample.activity.SplashActivity;
import com.example.acessobiosample.dto.GetAuthTokenResponse;
import com.example.acessobiosample.dto.User;
import com.example.acessobiosample.services.BioService;
import com.example.acessobiosample.services.ServiceGenerator;
import com.example.acessobiosample.utils.SupportPermissions;
import com.example.acessobiosample.utils.dialog.SweetAlertDialog;
import com.example.acessobiosample.utils.enumetators.SharedKey;
import com.orhanobut.hawk.Hawk;


import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.pm.PackageManager.GET_SIGNATURES;

/**
 * Created by matheusdomingos on 06/05/17.
 */
public class LoginFragment extends CustomFragment {

    private EditText etEmail;
    private EditText etPassword;
    private Button btEnter;
    private SweetAlertDialog dialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, null);

        Hawk.put(SharedKey.LIVENESS, false);
        Hawk.put(SharedKey.AUTOCAPTURE, true);
        Hawk.put(SharedKey.COUNT_REGRESSIVE, true);

//        // Taking permissions
//        SupportPermissions permissions = new SupportPermissions();
//        permissions.requestForPermission(getActivity(),
//                Manifest.permission.CAMERA,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.READ_EXTERNAL_STORAGE);
//
//
        etEmail = (EditText) view.findViewById(R.id.etEmail);
        etPassword = (EditText) view.findViewById(R.id.etSenha);
        btEnter = (Button) view.findViewById(R.id.btEnter);

        btEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(etEmail.getText().toString(), etPassword.getText().toString());
            }
        });

        etPassword.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:


                            login(etEmail.getText().toString(), etPassword.getText().toString());


                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });


        return view;
    }


    public void login(String user, String password) {


        dialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        dialog.getProgressHelper().setBarColor(Color.parseColor("#2980ff"));
        dialog.setCancelable(false);
        dialog.setTitleText("Autenticando...");
        dialog.show();


        ServiceGenerator
                .createService(BioService.class, true, user, password)
                .getAuthToken()
                .enqueue(new Callback<GetAuthTokenResponse>() {

                    @Override
                    public void onResponse(Call<GetAuthTokenResponse> call, Response<GetAuthTokenResponse> response) {


                        dialog.dismiss();
                        GetAuthTokenResponse body = response.body();

                        if (body != null && body.isValid()) {
                            Hawk.put(SharedKey.AUTH_TOKEN, body.getGetAuthTokenResult().getAuthToken());
                            Hawk.put(SharedKey.NAME,etEmail.getText().toString());
                            Hawk.put(SharedKey.PASSWORD,etPassword.getText().toString());

                            Intent intent = new Intent(getActivity(), SimpleViewActivity.class);
                            intent.putExtra(CustomFragment.FRAGMENT, HomeFragment.class);
                            startActivity(intent);

                        }
                        else {
                            showSnackbarError(body != null ? body.getMessageError() : "Erro ao recuperar token");
                        }
                    }

                    @Override
                    public void onFailure(Call<GetAuthTokenResponse> call, Throwable t) {
                        dialog.dismiss();
                        showSnackbarError(t.getMessage());
                    }
                });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }




}
