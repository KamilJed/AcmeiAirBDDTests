package com.eti.acmeairBDD;

import okhttp3.*;
import okhttp3.Cookie;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class AcmeAirREST {
    private final String ACME_AIR_REST_URL = "http://localhost:9080/rest/api/";
    private final String ACME_AIR_REST_LOGIN = ACME_AIR_REST_URL + "login";
    private final String ACME_AIR_REST_FLIGHTS_QUERY = ACME_AIR_REST_URL + "flights/queryflights";
    private final String ACME_AIR_REST_CUSTOMER_QUERY = ACME_AIR_REST_URL + "customer/byid/%s";
    private final String ACME_AIR_REST_FLIGHT_BOOKING = ACME_AIR_REST_URL + "bookings/bookflights";
    private final String ACME_AIR_REST_FLIGHT_BOOKING_QUERY = ACME_AIR_REST_URL + "bookings/byuser/%s";
    private final String ACME_AIR_REST_FLIGH_BOOKING_CANCEL = ACME_AIR_REST_URL + "bookings/cancelbooking";

    private final int HTTP_OK_CODE = 200;

    private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
    OkHttpClient httpClient = new OkHttpClient.Builder()
            .cookieJar(new CookieJar() {
                @Override
                public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> list) {
                    List<Cookie> goodCookies = new ArrayList<>();
                    for (Cookie cookie : list){
                        if (!cookie.name().equals("sessionid") || !cookie.value().equals(""))
                            goodCookies.add(cookie);
                    }
                    cookieStore.put(httpUrl.host(), goodCookies);
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

        try(Response response = httpClient.newCall(request).execute()){
            loggedIn = response.code() == HTTP_OK_CODE;
        } catch (IOException ignored) {
        }
        return loggedIn;
    }

    public JSONObject searchFlights(String fromAirport, String toAirport, ZonedDateTime fromDate, ZonedDateTime returnDate, String oneWay){
        JSONObject flights = null;
        String fromDateString = DateTimeFormatter.ofPattern("E MMM d y").format(fromDate);
        String returnDateString = DateTimeFormatter.ofPattern("E MMM d y").format(returnDate);
        RequestBody requestBody = new FormBody.Builder()
                .add("fromAirport", fromAirport)
                .add("toAirport", toAirport)
                .add("fromDate", fromDateString)
                .add("returnDate", returnDateString)
                .add("oneWay", oneWay)
                .build();
        Request request = new Request.Builder()
                .url(ACME_AIR_REST_FLIGHTS_QUERY)
                .post(requestBody)
                .build();

        try(Response response = httpClient.newCall(request).execute()){
            if (response.code() == HTTP_OK_CODE){
                flights = new JSONObject(response.body().string());
            }
        } catch (IOException ignored) {
        }
        return flights;
    }

    public JSONObject getUserByLogin(String user){
        JSONObject userInfo = null;
        Request request = new Request.Builder()
                .url(String.format(ACME_AIR_REST_CUSTOMER_QUERY, user))
                .get().build();

        try(Response response = httpClient.newCall(request).execute()){
            if (response.code() == HTTP_OK_CODE){
                userInfo = new JSONObject(response.body().string());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return userInfo;
    }

    public boolean changeUserInformation(String user, String pass, String phoneNumber, String phoneNumberType, String city, String country, String postalCode, String stateProvnice, String streetAddress, String status){
        boolean changed = false;
        JSONObject newUserInfo = new JSONObject()
                .put("_id", user)
                .put("phoneNumber", phoneNumber)
                .put("phoneNumberType", phoneNumberType)
                .put("password", pass)
                .put("address", new JSONObject().put("city", city)
                        .put("country", country)
                        .put("postalCode", postalCode)
                        .put("stateProvince", stateProvnice)
                        .put("streetAddress1", streetAddress))
                .put("status", status);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), newUserInfo.toString());
        Request request = new Request.Builder()
                .url(String.format(ACME_AIR_REST_CUSTOMER_QUERY, user))
                .post(requestBody)
                .build();
        try(Response response = httpClient.newCall(request).execute()){
            changed = response.code() == HTTP_OK_CODE;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return changed;
    }

    public boolean bookFlights(String user, String toFlighId, String retFlightId, String oneWay){
        boolean booked = false;
        RequestBody requestBody = new FormBody.Builder()
                .add("userid", user)
                .add("toFlightId", toFlighId)
                .add("retFlightId", retFlightId)
                .add("oneWayFlight", oneWay)
                .build();
        Request request = new Request.Builder()
                .url(ACME_AIR_REST_FLIGHT_BOOKING)
                .post(requestBody)
                .build();
        try(Response response = httpClient.newCall(request).execute()){
            booked = response.code() == HTTP_OK_CODE;
        } catch (IOException ignored) {
        }
        return booked;
    }

    public JSONArray getBookingsByUser(String user){
        JSONArray bookings = null;
        Request request = new Request.Builder()
                .url(String.format(ACME_AIR_REST_FLIGHT_BOOKING_QUERY, user))
                .get().build();

        try(Response response = httpClient.newCall(request).execute()){
            if (response.code() == HTTP_OK_CODE){
                bookings = new JSONArray(response.body().string());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    public boolean cancelBooking(String user, String flightId){
        boolean canceled = false;
        RequestBody requestBody = new FormBody.Builder()
                .add("userid", user)
                .add("number", flightId)
                .build();
        Request request = new Request.Builder()
                .url(ACME_AIR_REST_FLIGH_BOOKING_CANCEL)
                .post(requestBody)
                .build();
        try(Response response = httpClient.newCall(request).execute()){
            canceled = response.code() == HTTP_OK_CODE;
        } catch (IOException ignored) {
        }
        return canceled;
    }
}
