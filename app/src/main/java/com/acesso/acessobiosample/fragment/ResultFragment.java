package com.acesso.acessobiosample.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.acesso.acessobiosample.R;
import com.orhanobut.hawk.Hawk;

import java.util.Map;

public class ResultFragment extends CustomFragment {

    private Map<String, String> livenessResult;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v;

        Intent intent = getActivity().getIntent();

        Boolean isLiveness = intent.getBooleanExtra("isLiveness", true);

        if(isLiveness) {

            v = inflater.inflate(R.layout.fragment_liveness_success, null);

            Bitmap bitmapClose = intent.getParcelableExtra("bitmapClose");
            Bitmap bitmapAfar = intent.getParcelableExtra("bitmapAfar");

            ImageView ivClose = ((ImageView) v.findViewById(R.id.ivClose));
            ivClose.setImageBitmap(bitmapClose);

            ImageView ivAfar = ((ImageView) v.findViewById(R.id.ivAfar));
            ivAfar.setImageBitmap(bitmapAfar);

        }else{
            v = inflater.inflate(R.layout.fragment_liveness_error, null);
        }


        Hawk.init(getActivity()).build();


        return v;
    }


}
