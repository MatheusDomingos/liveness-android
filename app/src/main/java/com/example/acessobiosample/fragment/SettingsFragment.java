package com.example.acessobiosample.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.acessobiosample.R;
import com.example.acessobiosample.activity.AuthenticationActivity;
import com.example.acessobiosample.adapter.HomeAdapter;
import com.example.acessobiosample.dto.Process;
import com.example.acessobiosample.support.RecyclerItemClickListener;
import com.example.acessobiosample.utils.enumetators.SharedKey;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;

/**
 * Created by matheusdomingos on 25/07/17.
 */
public class SettingsFragment extends CustomFragment {


    private Switch livenessSwitch;
    private Switch autocaptureSwitch;
    private Switch countRegressiveSwitch;
    private Button btLogout;
    private TextView tvName;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, null);

        livenessSwitch = v.findViewById(R.id.liveness_sw);
        autocaptureSwitch = v.findViewById(R.id.autocapture_sw);
        countRegressiveSwitch = v.findViewById(R.id.countregressive_sw);
        btLogout = v.findViewById(R.id.btExit);
        tvName = v.findViewById(R.id.tvNome);

        String name =  Hawk.get(SharedKey.NAME, "").toUpperCase();

        tvName.setText(name);

        livenessSwitch.setChecked(Hawk.get(SharedKey.LIVENESS, false));
        autocaptureSwitch.setChecked(Hawk.get(SharedKey.AUTOCAPTURE, false));
        countRegressiveSwitch.setChecked(Hawk.get(SharedKey.COUNT_REGRESSIVE, false));


        btLogout.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Hawk.delete(SharedKey.CPF);
                                            Hawk.delete(SharedKey.NAME);
                                            Hawk.delete(SharedKey.AUTH_TOKEN);
                                            Hawk.delete(SharedKey.INSTANCE);


                                            Hawk.delete(SharedKey.AUTOCAPTURE);
                                            Hawk.delete(SharedKey.AUTOCAPTURE_VALUE);
                                            Hawk.delete(SharedKey.COUNT_REGRESSIVE);

                                            Intent intent = new Intent(getActivity() , AuthenticationActivity.class);
                                            intent.putExtra(CustomFragment.FRAGMENT, LoginFragment.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                        }
                                    });


        attachListener();


        return v;
    }


    private void attachListener() {
        // liveness
        livenessSwitch.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
            Hawk.put(SharedKey.LIVENESS, isChecked);
        });

        // auto capture
        autocaptureSwitch.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
            Hawk.put(SharedKey.AUTOCAPTURE, isChecked);

            if (isChecked) {
                countRegressiveSwitch.setEnabled(true);
            }else{
                countRegressiveSwitch.setChecked(false);
                countRegressiveSwitch.setEnabled(false);
            }
        });

        // glasses
        countRegressiveSwitch.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
            Hawk.put(SharedKey.COUNT_REGRESSIVE, isChecked);
        });
    }



}
