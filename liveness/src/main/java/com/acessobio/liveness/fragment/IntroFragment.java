package com.acessobio.liveness.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.acessobio.liveness.R;
import com.acessobio.liveness.activity.SelfieActivity;
import com.orhanobut.hawk.Hawk;

import java.util.Objects;

public class IntroFragment extends CustomFragment{

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

               // exibirMensagemEdt("Se identifique para o teste", "Insira seu nome");

                Intent intent = new Intent(getActivity(), SelfieActivity.class);
                startActivity(intent);



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

}
