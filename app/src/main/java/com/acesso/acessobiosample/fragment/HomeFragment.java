package com.acesso.acessobiosample.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.acesso.acessobiosample.R;
import com.acesso.acessobiosample.activity.AuthenticationActivity;
import com.acesso.acessobiosample.activity.FormViewActivity;
import com.acesso.acessobiosample.activity.SimpleViewActivity;
import com.acesso.acessobiosample.adapter.HomeAdapter;
import com.acesso.acessobiosample.dto.GetAuthTokenResponse;
import com.acesso.acessobiosample.dto.GetProcessByUserResponse;
import com.acesso.acessobiosample.dto.GetProcessByUserResult;
import com.acesso.acessobiosample.services.BioService;
import com.acesso.acessobiosample.services.ServiceGenerator;
import com.acesso.acessobiosample.support.RecyclerItemClickListener;
import com.acesso.acessobiosample.utils.dialog.SweetAlertDialog;
import com.acesso.acessobiosample.utils.enumetators.SharedKey;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by matheusdomingos on 25/07/17.
 */
@SuppressWarnings("unchecked")
public class HomeFragment extends CustomFragment {

    private RecyclerView rvProcess;
    private HomeAdapter homeAdapter;
    private ImageButton btConfig;
    private Button btRegister;

    private  SweetAlertDialog dialog;

    private ArrayList<GetProcessByUserResult> processes;

    private Boolean isLoading = true;

    private Boolean isClickedSettings = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, null);

        Hawk.init(getActivity()).build();

        if(!Hawk.contains(SharedKey.INSTANCE)) {
            Hawk.delete(SharedKey.CPF);
            Hawk.delete(SharedKey.NAME);
            Hawk.delete(SharedKey.AUTH_TOKEN);

            Hawk.delete(SharedKey.AUTOCAPTURE);
            Hawk.delete(SharedKey.AUTOCAPTURE_VALUE);
            Hawk.delete(SharedKey.COUNT_REGRESSIVE);

            Intent intent = new Intent(getActivity() , AuthenticationActivity.class);
            intent.putExtra(CustomFragment.FRAGMENT, LoginFragment.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        rvProcess = ((RecyclerView) v.findViewById(R.id.list_simple_view));
        TextView tv_no_results = ((TextView) v.findViewById(R.id.tv_no_results));
        btConfig = ((ImageButton) v.findViewById(R.id.btConfig));
        btRegister = ((Button) v.findViewById(R.id.btRegister));

        rvProcess.setHasFixedSize(true);
        setDecorationLine(rvProcess);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rvProcess.setLayoutManager(mLayoutManager);


        btConfig.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!isClickedSettings) {
                    isClickedSettings = true;
                    Intent intent = new Intent(getActivity(), SimpleViewActivity.class);
                    intent.putExtra(CustomFragment.FRAGMENT, SettingsFragment.class);
                    startActivity(intent);
                }

            }
        }));

        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isLoading = true;
                Intent intent = new Intent(getActivity(), FormViewActivity.class);
                intent.putExtra(CustomFragment.FRAGMENT, NewProcessFragment.class);
                startActivity(intent);

            }
        });



        rvProcess.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(),
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

//                        Cities citie = arrCity.get(position);
//
//                        Intent intent =  new Intent();
//                        intent.putExtra("city", citie.getName());
//                        intent.putExtra("cityId", Integer.toString(citie.getId()));
//
//                        getActivity().setResult(Activity.RESULT_OK, intent);
//                        getActivity().finish();

                    }
                }));

        if(processes != null) {
            if(processes.size() > 0){
                homeAdapter = new HomeAdapter(processes, R.layout.row_process, getActivity());
                rvProcess.setAdapter(homeAdapter);
            }
        }

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        isClickedSettings = false;
        getProcess(isLoading);


    }


    private void getProcess(Boolean loading) {


        if(loading) {
            dialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
            dialog.getProgressHelper().setBarColor(Color.parseColor("#2980ff"));
            dialog.setCancelable(false);
            dialog.setTitleText("Atualizando...");
            dialog.show();
        }

        isLoading = false;


        ServiceGenerator
                .createService(BioService.class, true, false,  Hawk.get(SharedKey.INSTANCE))
                .getAuthToken()
                .enqueue(new Callback<GetAuthTokenResponse>() {

                    @Override
                    public void onResponse(Call<GetAuthTokenResponse> call , Response<GetAuthTokenResponse> response) {

                        GetAuthTokenResponse body = response.body();

                        if (body != null && body.isValid()) {
                            Hawk.put(SharedKey.AUTH_TOKEN, body.getGetAuthTokenResult().getAuthToken());

                            ServiceGenerator
                                    .createService(BioService.class)
                                    .getProcesses(Hawk.get(SharedKey.NAME))
                                    .enqueue(new Callback<GetProcessByUserResponse>() {

                                        @Override
                                        public void onResponse(Call<GetProcessByUserResponse> call, Response<GetProcessByUserResponse> response) {

                                            dialog.dismiss();


                                            GetProcessByUserResponse body = response.body();

                                            if (body != null && body.isValid()) {
                                                setAdapter(body.getGetProcessByUserResults());
                                            }
                                            else {
                                                showSnackbarError(body != null ? body.getMessageError() : "Erro ao recuperar token");
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<GetProcessByUserResponse> call, Throwable t) {
                                            showSnackbarError(t.getMessage());
                                            dialog.dismiss();

                                        }
                                    });


                        }
                        else {
                            dialog.dismiss();

                            showSnackbarError(body != null ? body.getMessageError() : "Erro ao recuperar token");
                        }
                    }

                    @Override
                    public void onFailure(Call<GetAuthTokenResponse> call, Throwable t) {
                        dialog.dismiss();

                        showSnackbarError(t.getMessage());
                    }
                });




    }

    public void setAdapter(ArrayList<GetProcessByUserResult> processes) {
        if(processes != null) {
            if(processes.size() > 0){
              //  Collections.reverse(processes);
                homeAdapter = new HomeAdapter(processes, R.layout.row_process, getActivity());
                rvProcess.setAdapter(homeAdapter);
            }
        }
    }



}
