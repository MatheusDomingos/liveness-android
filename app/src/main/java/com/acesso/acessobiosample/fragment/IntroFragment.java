package com.acesso.acessobiosample.fragment;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.Nullable;

import com.acesso.acessobiosample.R;
import com.acesso.acessobiosample.activity.FormViewActivity;
import com.acesso.acessobiosample.activity.SelfieActivity;
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

                Intent intent = new Intent(getActivity(), SelfieActivity.class);
                startActivity(intent);

            }
        });

        return v;
    }

}
