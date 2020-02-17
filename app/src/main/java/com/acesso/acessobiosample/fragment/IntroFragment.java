package com.acesso.acessobiosample.fragment;

import android.app.Activity;
import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.acesso.acessobiosample.R;


import com.acesso.acessobiosample.activity.SimpleViewActivity;
import com.acesso.acessobiosample.activity.homolog.SelfieActivityHomolog;


import com.acesso.acessobiosample.services.AuthenticationInterceptor;
import com.acesso.acessobiosample.services.BioService;
import com.acesso.acessobiosample.services.ServiceGenerator;
import com.acesso.acessobiosample.support.LivenessXHomolog;
import com.acesso.acessobiosample.support.iLivenessXHomolog;
import com.acesso.acessobiosample.utils.enumetators.SharedKey;
import com.orhanobut.hawk.Hawk;

import java.util.HashMap;
import java.util.Objects;


public class IntroFragment extends CustomFragment implements iLivenessXHomolog {


    protected static final int REQUEST_CAMERA_PERMISSION = 1;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_intro, null);

        Hawk.init(getActivity()).build();

        Button btTakeIntro = (Button) v.findViewById(R.id.btTakeIntro);
        ImageButton btBackIntro = (ImageButton) v.findViewById(R.id.btBackIntro);

        btBackIntro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Objects.requireNonNull(getActivity()).finish();
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            }
        });

        btTakeIntro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {

                    requestPermissions(new String[] {
                            Manifest.permission.CAMERA
                    }, REQUEST_CAMERA_PERMISSION);

                }
                else {
                    LivenessXHomolog livenessX = new LivenessXHomolog(IntroFragment.this, ServiceGenerator.API_BASE_URL_PRD, "f968978f-1417-4d11-8dc4-59477deb3d36" , Hawk.get(SharedKey.AUTH_TOKEN));
                    livenessX.openLivenessX(false);
                }

            }
        });

        return v;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Intent intent = new Intent(getActivity(), SelfieActivityHomolog.class);
                    startActivity(intent);
                }
                break;
            }
        }

    }


    private void exibirMensagemEdt(String titulo, String texto){

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        final EditText titleBox = new EditText(getActivity());
        titleBox.setHint("Nome");
        layout.addView(titleBox); // Notice this is an add method
        final EditText descriptionBox = new EditText(getActivity());
        descriptionBox.setHint("CPF");
        layout.addView(descriptionBox); // Another add method

        AlertDialog.Builder mensagem = new AlertDialog.Builder(getActivity());
        mensagem.setTitle(titulo);
        mensagem.setMessage(null);
        mensagem.setView(layout);
        mensagem.setNeutralButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

            }

        });

        mensagem.show();

    }

    @Override
    public void onResultLiveness(HashMap result) {

    }

    @Override
    public void onError(String error) {
        Log.d("IntroFragment", error);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == LivenessXHomolog.REQUEST_LIVENESS) {
            if (resultCode == Activity.RESULT_OK) {
                assert data != null;
                Intent intent = data;
                HashMap<String, String> result = (HashMap<String, String>)intent.getSerializableExtra(LivenessXHomolog.RESULT_OK);

                byte[] decodedString = Base64.decode(result.get("base64"), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                byte[] decodedStringClose = Base64.decode(result.get("base64Close"), Base64.DEFAULT);
                Bitmap decodedByteClose = BitmapFactory.decodeByteArray(decodedStringClose, 0, decodedStringClose.length);

                Bitmap bitClose = Bitmap.createScaledBitmap(decodedByteClose, 200, 280, false);
                Bitmap bitAfar = Bitmap.createScaledBitmap(decodedByte, 200, 280, false);

                Intent intentResult = new Intent(getActivity(), SimpleViewActivity.class);
                intentResult.putExtra(CustomFragment.FRAGMENT, ResultFragment.class);
                intentResult.putExtra("isLiveness", result.get("isLiveness"));
                intentResult.putExtra("bitmapClose", bitClose);
                intentResult.putExtra("bitmapAfar", bitAfar);
                startActivity(intentResult);


                // TODO Update your TextView.
            }
        }

    }
}
