package com.acesso.acessobiosample.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.acesso.acessobiosample.R;
import com.acesso.acessobiosample.activity.SelfieActivity;
import com.orhanobut.hawk.Hawk;

import java.util.Map;
import java.util.Objects;

public class ResultFragment extends CustomFragment {

    private Map<String, String> livenessResult;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v;
        v = inflater.inflate(R.layout.fragment_liveness_success, null);

        Bundle b = getActivity().getIntent().getExtras();

        Intent intent = getActivity().getIntent();
        Bitmap bitmapClose = (Bitmap) intent.getParcelableExtra("bitmapClose");
        Bitmap bitmapAfar = (Bitmap) intent.getParcelableExtra("bitmapAfar");

        ImageView ivClose = ((ImageView) v.findViewById(R.id.ivClose));
        ivClose.setImageBitmap(bitmapClose);

        ImageView ivAfar = ((ImageView) v.findViewById(R.id.ivAfar));
        ivAfar.setImageBitmap(bitmapAfar);

//        if (b != null) {
//            livenessResult = b.getParcelable("livenessResult");
//        }
//
//        String isLive  =  livenessResult.get("isLive");
//
//        if(isLive.equals("1")) {
//            v = inflater.inflate(R.layout.fragment_liveness_success, null);
//        }else{
//            v = inflater.inflate(R.layout.fragment_liveness_success, null);
//        }

        Hawk.init(getActivity()).build();


        return v;
    }


}
