package com.acesso.acessobiosample.services;

import com.acesso.acessobiosample.utils.enumetators.SharedKey;
import com.orhanobut.hawk.Hawk;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthenticationInterceptor implements Interceptor {

    private static boolean auth = false;

    private static final String API_KEY = "7e426bc2-652e-4bce-b6a1-7922fa44ebc9";

    public AuthenticationInterceptor(boolean auth) {
        this.auth = auth;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request original = chain.request();

        Request.Builder builder = original.newBuilder();

        if (auth) {
            builder.addHeader("APIKEY", API_KEY);
            builder.addHeader("Login", Hawk.get(SharedKey.NAME));
            builder.addHeader("Password",Hawk.get(SharedKey.PASSWORD));
        } else {
            builder.addHeader("APIKEY", API_KEY);
            builder.addHeader("Authorization", Hawk.get(SharedKey.AUTH_TOKEN, ""));
        }

        Request request = builder.build();
        return chain.proceed(request);

    }
}