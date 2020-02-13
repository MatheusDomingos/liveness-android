package com.acesso.acessobiosample.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.acesso.acessobiosample.R;
import com.acesso.acessobiosample.activity.AuthenticationActivity;
import com.acesso.acessobiosample.activity.SelfieActivity;
import com.acesso.acessobiosample.activity.SimpleViewActivity;
import com.orhanobut.hawk.Hawk;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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

            Button btExit = (Button)v.findViewById(R.id.btExit);
            Button btFeedback = (Button)v.findViewById(R.id.btFeedback);

            btExit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity() , SimpleViewActivity.class);
                    intent.putExtra(CustomFragment.FRAGMENT, WelcomeFragment.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            });

            btFeedback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);//common intent
                    intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback Liveness");
                    intent.putExtra(Intent.EXTRA_TEXT, "" );
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"suportebio@acessodigital.com.br"});
                    startActivity(Intent.createChooser(intent, ""));

                }
            });

        }else{
            v = inflater.inflate(R.layout.fragment_liveness_error, null);

            Button btTryAgain = v.findViewById(R.id.btTryAgain);
            btTryAgain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), SelfieActivity.class);
                    startActivity(intent);
                }
            });
        }


        Hawk.init(getActivity()).build();


        return v;
    }


}
