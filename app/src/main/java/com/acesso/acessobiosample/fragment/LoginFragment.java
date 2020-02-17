package com.acesso.acessobiosample.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.acesso.acessobiosample.R;
import com.acesso.acessobiosample.activity.SimpleViewActivity;
import com.acesso.acessobiosample.dto.GetAuthTokenResponse;
import com.acesso.acessobiosample.services.BioService;
import com.acesso.acessobiosample.services.ServiceGenerator;
import com.acesso.acessobiosample.utils.dialog.SweetAlertDialog;
import com.acesso.acessobiosample.utils.enumetators.SharedKey;
import com.orhanobut.hawk.Hawk;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by matheusdomingos on 06/05/17.
 */
public class LoginFragment extends CustomFragment {

    private EditText etEmail;
    private EditText etPassword;
    private Button btEnter;
    private Button btInstance;
    private EditText etInstance;

    String urlInstance;

    private SweetAlertDialog dialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, null);

        Hawk.put(SharedKey.LIVENESS, true);
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

        etInstance = (EditText) view.findViewById(R.id.etInstance);
        btInstance = (Button) view.findViewById(R.id.btInstace);


        btInstance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogInstances();
            }
        });

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


    private void showDialogInstances() {

        final String[] options = { "TREINAMENTO", "PRODUÇÃO"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Selecione a instância");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                etInstance.setText(options[which]);
                if(which == 0) {
                    urlInstance = "https://crediariohomolog.acesso.io/treinamento/services/v2/credService.svc/";
                    Hawk.put(SharedKey.INSTANCE, "https://crediariohomolog.acesso.io/treinamento/services/v2/credService.svc/");
                }else {
                    urlInstance = "https://www2.acesso.io/seres/services/v2/credService.svc/";
                    Hawk.put(SharedKey.INSTANCE, "https://www2.acesso.io/seres/services/v2/credService.svc/");
                }
            }
        });

        builder.show();

    }

    private void login(String user, String password) {


        dialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        dialog.getProgressHelper().setBarColor(Color.parseColor("#2980ff"));
        dialog.setCancelable(false);
        dialog.setTitleText("Autenticando...");
        dialog.show();


        Hawk.put(SharedKey.NAME, user);
        Hawk.put(SharedKey.PASSWORD, password);


        ServiceGenerator
                .createService(BioService.class, true, false,  urlInstance)
                .getAuthToken()
                .enqueue(new Callback<GetAuthTokenResponse>() {

                    @Override
                    public void onResponse(Call<GetAuthTokenResponse> call, Response<GetAuthTokenResponse> response) {


                        dialog.dismiss();
                        GetAuthTokenResponse body = response.body();

                        if (body != null && body.isValid()) {


                            Hawk.put(SharedKey.AUTH_TOKEN, body.getGetAuthTokenResult().getAuthToken());


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
