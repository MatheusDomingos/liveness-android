package com.acesso.acessobiosample.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import androidx.annotation.Nullable;


import com.acesso.acessobiosample.R;
import com.acesso.acessobiosample.activity.FormViewActivity;

import com.acesso.acessobiosample.activity.SimpleViewActivity;
import com.acesso.acessobiosample.dto.GetAuthTokenResponse;
import com.acesso.acessobiosample.dto.GetProcessByUserResponse;
import com.acesso.acessobiosample.services.BioService;
import com.acesso.acessobiosample.services.ServiceGenerator;
import com.acesso.acessobiosample.utils.enumetators.SharedKey;
import com.orhanobut.hawk.Hawk;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WelcomeFragment extends CustomFragment{

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Hawk.init(getActivity()).build();

        View v = inflater.inflate(R.layout.fragment_welcome, null);

        Hawk.put(SharedKey.NAME, "ADMIN");
        Hawk.put(SharedKey.PASSWORD, "Ac3ss0#66");


        Button btStart = ((Button) v.findViewById(R.id.btStart));
        btStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), SimpleViewActivity.class);
                intent.putExtra(CustomFragment.FRAGMENT, IntroFragment.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            }
        });

        return v;
    }



}
