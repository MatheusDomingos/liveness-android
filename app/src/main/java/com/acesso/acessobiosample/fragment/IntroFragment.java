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
import com.orhanobut.hawk.Hawk;

public class IntroFragment extends CustomFragment{

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_intro, null);

        Hawk.init(getActivity()).build();

        Button btTakeIntro = ((Button) v.findViewById(R.id.btTakeIntro));
        btTakeIntro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), FormViewActivity.class);
                intent.putExtra(CustomFragment.FRAGMENT, NewProcessFragment.class);
                startActivity(intent);

            }
        });

        return v;
    }

}
