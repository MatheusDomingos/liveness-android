package com.acesso.acessobiosample.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.acesso.acessobiosample.R;
import com.acesso.acessobiosample.activity.SelfieActivity;
import com.acesso.acessobiosample.dto.CreateProcessRequest;
import com.acesso.acessobiosample.dto.CreateProcessResponse;
import com.acesso.acessobiosample.dto.Subject;
import com.acesso.acessobiosample.services.BioService;
import com.acesso.acessobiosample.services.ServiceGenerator;
import com.acesso.acessobiosample.support.Validators;
import com.acesso.acessobiosample.utils.dialog.SweetAlertDialog;
import com.acesso.acessobiosample.utils.enumetators.SharedKey;
import com.orhanobut.hawk.Hawk;

import org.json.JSONObject;

import br.com.jansenfelipe.androidmask.MaskEditTextChangedListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by matheusdomingos on 06/05/17.
 */
public class NewProcessFragment extends CustomFragment {

    private EditText etNome, etCPF, etSexo;
    private Button btSexo, btCreate;
    private String gender;

    protected static final int REQUEST_CAMERA_PERMISSION = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_new_process, null);

//        // Taking permissions
//        SupportPermissions permissions = new SupportPermissions();
//        permissions.requestForPermission(getActivity(),
//                Manifest.permission.CAMERA,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.READ_EXTERNAL_STORAGE);
//
//
        etNome = (EditText) view.findViewById(R.id.etNome);
        etCPF = (EditText) view.findViewById(R.id.etCPF);
        etSexo = (EditText) view.findViewById(R.id.etSexo);
        btSexo = (Button) view.findViewById(R.id.btSexo);
        btCreate = (Button) view.findViewById(R.id.btCreate);



        MaskEditTextChangedListener maskCPF = new MaskEditTextChangedListener("###.###.###-##", etCPF);
        etCPF.addTextChangedListener(maskCPF);


        etNome.setText("Matheus Domingos");
        etCPF.setText("098.703.609-20");
        etSexo.setText("Masculino");

        btSexo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogGenders();
            }
        });

        btCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (validateForm()) {

                    String cpf = etCPF.getText().toString();
                    cpf = cpf.replaceAll("\\.", "");
                    cpf = cpf.replaceAll("-", "");

                    CreateProcessRequest request = new CreateProcessRequest();
                    Subject subject = new Subject();
                    subject.setCode(cpf);
                    subject.setGender(gender);
                    subject.setName(etNome.getText().toString());

                    request.setSubject(subject);

                    // Create Process
                    ServiceGenerator
                            .createService(BioService.class)
                            .createProcess("1", request)
                            .enqueue(new Callback<CreateProcessResponse>() {

                                @Override
                                public void onResponse(Call<CreateProcessResponse> call, Response<CreateProcessResponse> response) {
                                    CreateProcessResponse body = response.body();

                                    if (body != null && body.isValid()) {

                                        // define o processo
                                        String processId = body.getCreateProcessResult().getProcess().getId();
                                        Hawk.put(SharedKey.PROCESS, processId);

                                        Intent intent = new Intent(getActivity(), SelfieActivity.class);
                                        startActivity(intent);

                                    } else {

                                        try {
                                            JSONObject j = new JSONObject(response.errorBody().string());
                                            JSONObject error = j.getJSONObject("Error");
                                            String message = error.getString("Description");

                                            new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                                                    .setTitleText("Ops!")
                                                    .setContentText(message)
                                                    .setConfirmText("Entendi")
                                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sDialog) {

                                                    sDialog.dismissWithAnimation();

                                                }
                                            }).show();

                                        } catch (Exception ex) {

                                            if (!showSnackbarError(response)) {
                                                showSnackbarError(body != null ? body.getMessageError() : "Erro ao criar registro");
                                            }

                                            Log.d(TAG, ex.getMessage());
                                        }


                                    }
                                }

                                @Override
                                public void onFailure(Call<CreateProcessResponse> call, Throwable t) {
                                    Log.d(TAG, t.getMessage());
                                    showSnackbarError(t.getMessage());
                                }
                            });
                }

            }
        });


        etCPF.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:

                            showDialogGenders();
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



    @Override
    public void onResume() {
        super.onResume();

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(getActivity(), new String[] {
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, REQUEST_CAMERA_PERMISSION);
            return;
        }
    }



    public void showDialogGenders() {

        final String[] genders = {"Masculino", "Feminino"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Selecione o sexo");
        builder.setItems(genders, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    etSexo.setText(genders[which]);
                    if(which == 0) {
                        gender = "M";
                    }else {
                        gender = "F";
                    }
            }
        });

        builder.show();

    }


    public boolean validateForm() {
        boolean valid = true;
        View focusView = null;

        String cpf = etCPF.getText().toString();
        cpf = cpf.replaceAll("\\.", "");
        cpf = cpf.replaceAll("-", "");
        String name = etNome.getText().toString();


        if (TextUtils.isEmpty(cpf)) {
            etCPF.setError(getString(R.string.error_field_required));
            focusView = etCPF;
            valid = false;
        }
        else if (!isCpfValid(cpf)) {
            etCPF.setError(getString(R.string.error_invalid_cpf));
            focusView = etCPF;
            valid = false;
        }
        else if (TextUtils.isEmpty(name)) {
            etNome.setError(getString(R.string.error_field_required));
            focusView = etNome;
            valid = false;
        }


        if (!valid) {
            focusView.requestFocus();
        }

        return valid;
    }

    public boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    private boolean isCpfValid(String cpf) {
        return Validators.CPF.isValid(cpf);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }


}
