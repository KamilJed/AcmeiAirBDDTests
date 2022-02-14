package com.eti.acmeairBDD;

import okhttp3.*;
import okhttp3.Cookie;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class AcmeAirREST {
    private final String ACME_AIR_REST_URL = "http://localhost:9080/acmeair-webapp/rest/api/";
    private final String ACME_AIR_REST_LOGIN = ACME_AIR_REST_URL + "login";
    private final String ACME_AIR_REST_FLIGHTS_QUERY = ACME_AIR_REST_URL + "flights/queryflights";
    private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
    OkHttpClient httpClient = new OkHttpClient.Builder()
            .cookieJar(new CookieJar() {
                @Override
                public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> list) {
                    cookieStore.put(httpUrl.host(), list);
                }

                @NotNull
                @Override
                public List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {
                    List<Cookie> cookies = cookieStore.get(httpUrl.host());
                    return cookies != null ? cookies : new ArrayList<>();
                }
            })
            .build();

    public boolean logInUser(String user, String password){
        boolean loggedIn = false;
        RequestBody requestBody = new FormBody.Builder()
                .add("login", user)
                .add("password", password)
                .build();
        Request request = new Request.Builder()
                .url(ACME_AIR_REST_LOGIN)
                .post(requestBody)
                .build();

        try {
            Response response = httpClient.newCall(request).execute();
            loggedIn = response.code() == 200;
        } catch (IOException ignored) {
        }
        return loggedIn;
    }

    public JSONObject searchFlights(String fromAirport, String toAirport, String fromDate, String returnDate, String oneWay){
        JSONObject flights = null;
        RequestBody requestBody = new FormBody.Builder()
                .add("fromAirport", "CDG")
                .add("toAirport", "LHR")
                .add("fromDate", "Mon Feb 14 2022")
                .add("returnDate", "Mon Feb 14 2022")
                .add("oneWay", "false")
                .build();
        Request request = new Request.Builder()
                .url(ACME_AIR_REST_FLIGHTS_QUERY)
                .post(requestBody)
                .build();

        try {
            Response response = httpClient.newCall(request).execute();
            if (response.code() == 200){
                flights = new JSONObject(response.body().string());
            }
        } catch (IOException ignored) {
        }
        return  flights;
    }
}
