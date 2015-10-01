package com.bananaplan.workflowandroid.data.loading;

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


public class RestfulUtils {

    private static final String TAG = "RestfulUtils";


    /**
     * Get the JsonObject from the given url
     * Note: Do not run this method in UI thread
     *
     * @param urlString
     * @return The JsonObject from this url
     */
    public static String getJsonStringFromUrl(String urlString) {
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

    public static class GetRequest extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... urlStrings) {
            InputStream inputStream = null;
            String result = null;
            if (urlStrings[0] != null) {
                try {
                    URL url = new URL(urlStrings[0]);
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
    }

    public static class PostRequest extends AsyncTask<String, Integer, String> {

        private String body;
        public PostRequest (String b) {
            body = b;
        }
        @Override
        protected String doInBackground(String... urlStrings) {
            OutputStream outputStream = null;
            InputStream inputStream = null;
            String result = null;
            HttpURLConnection conn = null;
            if (urlStrings[0] != null) {
                try {
                    URL url = new URL(urlStrings[0]);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setReadTimeout(15000);
                    conn.setConnectTimeout(15000);
                    conn.setDoInput(true);
                    conn.setDoOutput(true);

                    Log.d("Restful api", "Calling: " + urlStrings[0]);
                    outputStream = conn.getOutputStream();
                    outputStream.write(body.getBytes("UTF-8"));

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
                } finally {
                    conn.disconnect();
                }
            }
            return result;
        }
    }


    //extract response content
//    public static String getStatusCode(HttpResponse response){
//        if(response == null)
//            return null;
//        StringBuilder sb = new StringBuilder();
//        String line = "";
//        try{
//            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
//            while ((line = rd.readLine()) != null) {
//                sb.append(line);
//            }
//        } catch (IOException io){
//
//        }
//        return sb.toString();
//    }

//    public static HttpResponse restfulPostRequest(String endpoint, List<NameValuePair> pairs) {
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);
//        final HttpParams httpParams = new BasicHttpParams();
//        HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
//        HttpConnectionParams.setSoTimeout(httpParams, 3000);
//        HttpClient client = new DefaultHttpClient(httpParams);
//        HttpPost post;
//        if (BuildConfig.DEBUG) {
//            post = new HttpPost(Constant.AWS_HOST_DEBUG + endpoint);
//        } else {
//            post = new HttpPost(Constant.AWS_HOST + endpoint);
//        }
//
//
//        HttpResponse response = null;
//        try {
//            post.setEntity(new UrlEncodedFormEntity(pairs));
//            response = client.execute(post);
//
//        } catch (ConnectTimeoutException cte) {
//            Log.e("connection timeout ", "time out !!!!");
//        } catch (UnsupportedEncodingException uee) {
//
//        } catch (ClientProtocolException cpe) {
//            //ignore this exception for now
//        } catch (IOException ioe) {
//            //ignore this exception for now
//        }
//        return response;
//    }

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
