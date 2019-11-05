package com.acesso.acessobiosample.fragment;

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

import com.acesso.acessobiosample.R;
import com.acesso.acessobiosample.activity.AuthenticationActivity;
import com.acesso.acessobiosample.utils.enumetators.SharedKey;
import com.bumptech.glide.Glide;
import com.orhanobut.hawk.Hawk;

/**
 * Created by matheusdomingos on 25/07/17.
 */
public class SettingsFragment extends CustomFragment {


    private Switch livenessSwitch;
    private Switch autocaptureSwitch;
    private Switch countRegressiveSwitch;
    private Button btLogout;
    private TextView tvName;
    private TextView tvInstance;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, null);

        livenessSwitch = v.findViewById(R.id.liveness_sw);
        autocaptureSwitch = v.findViewById(R.id.autocapture_sw);
        countRegressiveSwitch = v.findViewById(R.id.countregressive_sw);
        btLogout = v.findViewById(R.id.btExit);
        tvName = v.findViewById(R.id.tvNome);
        tvInstance = v.findViewById(R.id.tvInstance);


        String name =  Hawk.get(SharedKey.NAME, "").toUpperCase();
        tvName.setText(name);


        if(Hawk.contains(SharedKey.INSTANCE)) {
            if(Hawk.get(SharedKey.INSTANCE).equals("https://crediariohomolog.acesso.io/treinamento/services/v2/credService.svc/"))  {
                tvInstance.setText("Treinamento");
            } else{
                tvInstance.setText("Produção");
            }

        }

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
