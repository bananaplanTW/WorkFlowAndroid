package com.bananaplan.workflowandroid.data.loading;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;


public class RestfulUtils {

    private static final String TAG = "RestfulUtils";


    public static boolean isConnectToInternet(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /**
     * Get the JsonObject from the given url
     * Note: Do not run this method in UI thread
     *
     * @param urlString
     * @return The JsonObject from this url
     */
    public static String getJsonStringFromUrl(String urlString) {
        // [TODO] should we close input stream ?
        InputStream inputStream = null;
        String result = null;
        if (urlString != null) {
            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(3000);
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);

                conn.connect();
                int responseCode = conn.getResponseCode();
                Log.d("Restful api", "Response Code is : " + responseCode);
                inputStream = conn.getInputStream();

                result = getStringFromInputStream(inputStream);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * post request
     * @param urlString
     * @param bodyPair
     * @return
     */
    public static String restfulPostRequest (String urlString, HashMap<String, String> bodyPair) {

        InputStream inputStream;
        String result = null;
        HttpURLConnection conn = null;
        String bodyParamsString = URLUtils.buildQueryString(bodyPair);
        if (urlString != null) {
            try {
                // [TODO] should login
                URL url = new URL(urlString);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("x-user-id", "qY7FdM7wnjevqmfws");
                conn.setRequestProperty("x-auth-token", "el1UPAsSmVf8F1LEKf8tRb8Ny5jAgOdK2qLNHztb7Cj");
                conn.setReadTimeout(3000);
                conn.setConnectTimeout(3000);
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.getOutputStream().write(bodyParamsString.getBytes("UTF-8"));

                Log.d("Restful api", "Connecting url " + urlString);
                conn.connect();
                Log.d("Restful api", "Response Code is : " + conn.getResponseCode());
                inputStream = conn.getInputStream();

                result = getStringFromInputStream(inputStream);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                conn.disconnect();
            }
        }
        return result;
    }

    public static InputStream restfulGetRequest (String urlString) {
        InputStream inputStream;

        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(3000);
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            conn.connect();
            int responseCode = conn.getResponseCode();
            Log.d("Restful api", "Response Code is : " + responseCode);
            inputStream = conn.getInputStream();
            return inputStream;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getStringFromInputStream(InputStream in) {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        try {
            String s;
            br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            while ( (s = br.readLine()) != null) {
                sb.append(s);
            }
        } catch (UnsupportedEncodingException e) {
            Log.d("Error","Unsupport UTF-8 data type");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }
}
