package com.example.acessobiosample.fragment;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.acessobiosample.R;
import com.example.acessobiosample.activity.FormViewActivity;
import com.example.acessobiosample.activity.SimpleViewActivity;
import com.example.acessobiosample.adapter.HomeAdapter;
import com.example.acessobiosample.dto.GetAuthTokenResponse;
import com.example.acessobiosample.dto.GetProcessByUserResponse;
import com.example.acessobiosample.dto.GetProcessByUserResult;
import com.example.acessobiosample.dto.Process;
import com.example.acessobiosample.services.BioService;
import com.example.acessobiosample.services.ServiceGenerator;
import com.example.acessobiosample.support.RecyclerItemClickListener;
import com.example.acessobiosample.utils.dialog.SweetAlertDialog;
import com.example.acessobiosample.utils.enumetators.SharedKey;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by matheusdomingos on 25/07/17.
 */
public class HomeFragment extends CustomFragment {

    private RecyclerView rvProcess;
    private HomeAdapter homeAdapter;
    private ImageButton btConfig;
    private Button btRegister;

    private  SweetAlertDialog dialog;

    private ArrayList<GetProcessByUserResult> processes;

    private Boolean isLoading = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, null);


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

                Intent intent = new Intent(getActivity(), SimpleViewActivity.class);
                intent.putExtra(CustomFragment.FRAGMENT, SettingsFragment.class);
                startActivity(intent);

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
                .createService(BioService.class, true, Hawk.get(SharedKey.NAME), Hawk.get(SharedKey.PASSWORD))
                .getAuthToken()
                .enqueue(new Callback<GetAuthTokenResponse>() {

                    @Override
                    public void onResponse(Call<GetAuthTokenResponse> call, Response<GetAuthTokenResponse> response) {

                        GetAuthTokenResponse body = response.body();

                        if (body != null && body.isValid()) {
                            Hawk.put(SharedKey.AUTH_TOKEN, body.getGetAuthTokenResult().getAuthToken());

                            ServiceGenerator
                                    .createService(BioService.class, false)
                                    .getProcesses()
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
