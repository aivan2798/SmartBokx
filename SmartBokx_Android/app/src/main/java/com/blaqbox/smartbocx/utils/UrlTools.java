package com.blaqbox.smartbocx.utils;

import android.util.Log;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UrlTools {
    public static String check(String url) {
        Log.i("url tool check on: ",url);
        OkHttpClient client = new OkHttpClient.Builder()
                .followRedirects(false) // Disable automatic redirection
                .build();

        Request request = new Request.Builder()
                .url(url)
                .head() // Use HEAD method to get headers only
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (isRedirect(response)) {
                // Return the value of the 'Location' header if the response is a redirect
                String location = response.header("Location");
                return location != null ? location : url;
            } else {
                // Return the original URL if no redirect
                return url;
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Return original URL in case of an error
            return url;
        }
    }


    public static void check(String url, Callback http_callback) {
        Log.i("url tool check on: ",url);
        OkHttpClient client = new OkHttpClient.Builder()
                .followRedirects(false) // Disable automatic redirection
                .build();

        Request request = new Request.Builder()
                .url(url)
                .head() // Use HEAD method to get headers only
                .build();

        client.newCall(request).enqueue(http_callback);
        /*try (Response response = client.newCall(request).execute()) {
            if (isRedirect(response)) {
                // Return the value of the 'Location' header if the response is a redirect
                String location = response.header("Location");
                return location != null ? location : url;
            } else {
                // Return the original URL if no redirect
                return url;
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Return original URL in case of an error
            return url;
        }*/
    }

    // Helper method to check if the response is a redirect
    public static boolean isRedirect(Response response) {
        int code = response.code();
        return code >= 300 && code < 400;
    }

    public static boolean isValidUrl(String url) {
        try {
            new URL(url);
            return true; // If no exception is thrown, it's a valid URL
        } catch (MalformedURLException e) {
            return false; // If an exception is thrown, it's not a valid URL
        }
    }
}
