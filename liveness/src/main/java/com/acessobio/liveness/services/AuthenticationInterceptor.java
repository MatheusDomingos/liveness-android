package com.acessobio.liveness.services;

import com.acessobio.liveness.utils.enumetators.SharedKey;
import com.orhanobut.hawk.Hawk;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthenticationInterceptor implements Interceptor {

    private static boolean auth = false;

    private static final String API_KEY = "f968978f-1417-4d11-8dc4-59477deb3d36";

    public AuthenticationInterceptor(boolean auth) {
        this.auth = auth;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request original = chain.request();

        Request.Builder builder = original.newBuilder();

        if (auth) {
            builder.addHeader("X-AcessoBio-APIKEY", API_KEY);
            builder.addHeader("X-Login", Hawk.get(SharedKey.NAME));
            builder.addHeader("X-Password",Hawk.get(SharedKey.PASSWORD));
        } else {
            builder.addHeader("X-AcessoBio-APIKEY", API_KEY);
            builder.addHeader("Authentication", Hawk.get(SharedKey.AUTH_TOKEN, ""));
        }

        Request request = builder.build();
        return chain.proceed(request);

    }
}