package com.example.acessobiosample.services;

import java.util.Arrays;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {

    public static final String API_BASE_URL_HML = "https://biodevelopment.acesso.io/crediario/desenvolvimento3/services/v2/credService.svc/";
    public static final String API_BASE_URL_PRD = "https://www2.acesso.io/seres/services/v2/credService.svc/";

    private static OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
    private static OkHttpClient okHttpClient;

    private static Retrofit.Builder builder = new Retrofit.Builder()
                                                    .baseUrl(API_BASE_URL_PRD)
                                                    .addConverterFactory(GsonConverterFactory.create());

    private static final List<Protocol> protocols = Arrays.asList(Protocol.HTTP_1_1);

    private static Retrofit retrofit = builder.build();

    public static <S> S createService(Class<S> serviceClass) {
        return createService(serviceClass, false);
    }

    public static <S> S createService(Class<S> serviceClass, boolean auth) {


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

    public static <S> S createService(Class<S> serviceClass, boolean auth, String user, String password) {


        AuthenticationInterceptor interceptor = new AuthenticationInterceptor(auth, user, password);

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