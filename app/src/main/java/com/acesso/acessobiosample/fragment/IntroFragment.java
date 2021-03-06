package com.acesso.acessobiosample.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

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

                LivenessXHomolog livenessX = new LivenessXHomolog(IntroFragment.this, ServiceGenerator.API_BASE_URL_PRD, "f968978f-1417-4d11-8dc4-59477deb3d36" , Hawk.get(SharedKey.AUTH_TOKEN));
                livenessX.openLivenessX(false);

//                Intent intent = new Intent(getActivity(), SelfieActivityHomolog.class);
//                intent.putExtra(CustomFragment.FRAGMENT, SelfieActivityHomolog.class);
//                startActivity(intent);

            }
        });

        return v;
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
                HashMap<String, String> result = data.getParcelableExtra(LivenessXHomolog.RESULT_OK);
                // TODO Update your TextView.
            }
        }

    }
}
