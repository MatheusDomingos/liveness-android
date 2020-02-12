package com.acessobio.liveness.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.acessobio.liveness.R;
import com.acessobio.liveness.activity.SimpleViewActivity;
import com.orhanobut.hawk.Hawk;

public class WelcomeFragment extends CustomFragment{

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_welcome, null);

        Hawk.init(getActivity()).build();

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
