package com.acessobio.liveness.services;

import com.acessobio.liveness.dto.LivenessRequest;
import com.acessobio.liveness.dto.LivenessResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface BioService {

    @POST("app/liveness/{process}")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<LivenessResponse> liveness(@Path("process") String process,
                                    @Body LivenessRequest request);


}
