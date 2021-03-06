package com.acesso.acessobiosample.services;

import com.acesso.acessobiosample.dto.AuthenticateRequest;
import com.acesso.acessobiosample.dto.AuthenticateResponse;
import com.acesso.acessobiosample.dto.CreateProcessRequest;
import com.acesso.acessobiosample.dto.CreateProcessResponse;
import com.acesso.acessobiosample.dto.ExecuteProcessResponse;
import com.acesso.acessobiosample.dto.FaceInsertRequest;
import com.acesso.acessobiosample.dto.FaceInsertResponse;
import com.acesso.acessobiosample.dto.GetAuthTokenResponse;
import com.acesso.acessobiosample.dto.GetProcessByUserResponse;
import com.acesso.acessobiosample.dto.GetProcessResponse;
import com.acesso.acessobiosample.dto.GetSubjectResponse;
import com.acesso.acessobiosample.dto.LivenessBillingRequest;
import com.acesso.acessobiosample.dto.LivenessBillingResponse;
import com.acesso.acessobiosample.dto.LivenessRequest;
import com.acesso.acessobiosample.dto.LivenessResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface BioService {

    @GET("subject/{cpf}")
    Call<GetSubjectResponse> getSubject(@Path("cpf") String cpf);

    @GET("token")
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

    @POST("app/liveness/{process}")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<LivenessResponse> liveness(@Path("process") String process,
                                      @Body LivenessRequest request);

    @POST("liveness/billing")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<LivenessBillingResponse> livenessBilling(@Body LivenessBillingRequest request);



}
