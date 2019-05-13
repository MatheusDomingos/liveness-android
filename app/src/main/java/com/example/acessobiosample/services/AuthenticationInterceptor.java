package com.example.acessobiosample.services;

import com.example.acessobiosample.utils.enumetators.SharedKey;
import com.orhanobut.hawk.Hawk;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthenticationInterceptor implements Interceptor {

    private static boolean auth = false;
    private String user;
    private String password;

    public static String authToken;
    public static final String API_KEY = "7E426BC2-652E-4BCE-B6A1-7922FA44EBC9";

    public AuthenticationInterceptor(boolean auth, String user, String password) {
        this.auth = auth;
        this.user = user;
        this.password = password;
    }

    public AuthenticationInterceptor(boolean auth) {
        this.auth = auth;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();

        Request.Builder builder = original.newBuilder();

        if (auth) {
            builder.addHeader("X-AcessoBio-APIKEY", API_KEY);
            builder.addHeader("X-Login", this.user);
            builder.addHeader("X-Password", this.password);
        } else {
            builder.addHeader("X-AcessoBio-APIKEY", API_KEY);
            builder.addHeader("Authentication", Hawk.get(SharedKey.AUTH_TOKEN, ""));
        }

        Request request = builder.build();
        return chain.proceed(request);
    }
}