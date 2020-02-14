package com.acesso.acessobiosample.services;

import com.acesso.acessobiosample.utils.enumetators.SharedKey;
import com.orhanobut.hawk.Hawk;

import java.util.Arrays;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {

    public static final String API_BASE_URL_HML = "https://crediariohomolog.acesso.io/blackpanther/services/v2/credService.svc/";
    public static final String API_BASE_URL_PRD = "https://www2.acesso.io/seres/services/v3/acessoservice.svc/";

    private String instanceURL = "";

    private static OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
    private static OkHttpClient okHttpClient;

    private static Retrofit.Builder builder = new Retrofit.Builder()
                                                    .baseUrl(API_BASE_URL_HML)
                                                    .addConverterFactory(GsonConverterFactory.create());

    private static final List<Protocol> protocols = Arrays.asList(Protocol.HTTP_1_1);

    private static Retrofit retrofit = builder.build();

    public static <S> S createService(Class<S> serviceClass) {
        return createService(serviceClass, false, API_BASE_URL_PRD);
    }

    public static <S> S createService(Class<S> serviceClass, String instanceURL) {
        return createService(serviceClass, false, instanceURL);
    }

    public static <S> S createService(Class<S> serviceClass, boolean auth, String url) {

        builder = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create());

        AuthenticationInterceptor interceptor = new AuthenticationInterceptor(auth);

        httpClientBuilder.protocols(protocols);

        if (!httpClientBuilder.interceptors().contains(interceptor)) {
            httpClientBuilder.addInterceptor(interceptor);

            if (okHttpClient == null) {
                okHttpClient = httpClientBuilder.build();
            }

            builder.client(okHttpClient);
            retrofit = builder.build();
        }

        return retrofit.create(serviceClass);
    }

}