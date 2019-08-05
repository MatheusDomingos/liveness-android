package com.example.acessobiosample.services;

import com.example.acessobiosample.dto.AuthenticateRequest;
import com.example.acessobiosample.dto.AuthenticateResponse;
import com.example.acessobiosample.dto.CreateProcessRequest;
import com.example.acessobiosample.dto.CreateProcessResponse;
import com.example.acessobiosample.dto.ExecuteProcessResponse;
import com.example.acessobiosample.dto.FaceInsertRequest;
import com.example.acessobiosample.dto.FaceInsertResponse;
import com.example.acessobiosample.dto.GetAuthTokenResponse;
import com.example.acessobiosample.dto.GetProcessByUserResponse;
import com.example.acessobiosample.dto.GetProcessResponse;
import com.example.acessobiosample.dto.GetSubjectResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface BioService {

    @GET("subject/{cpf}")
    Call<GetSubjectResponse> getSubject(@Path("cpf") String cpf);

    @GET("user/authToken")
    Call<GetAuthTokenResponse> getAuthToken();

    @GET("process/{user}/list")
    Call<GetProcessByUserResponse> getProcesses(@Path("user") String user);

    @POST("process/create/{type}")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<CreateProcessResponse> createProcess(@Path("type") String type,
                                              @Body CreateProcessRequest request);

    @POST("process/{process}/faceInsert")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<FaceInsertResponse> faceInsert(@Path("process") String process,
                                        @Body FaceInsertRequest request);

    @POST("subject/{cpf}/authenticate")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<AuthenticateResponse> authenticate(@Path("cpf") String cpf,
                                            @Body AuthenticateRequest request);

    @GET("process/{process}/execute")
    Call<ExecuteProcessResponse> executeProcess(@Path("process") String process);

    @GET("process/{process}")
    Call<GetProcessResponse> getProcess(@Path("process") String process);



}
