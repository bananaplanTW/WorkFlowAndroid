package com.bananaplan.workflowandroid.data.loading;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.RecoverySystem;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import okio.BufferedSink;


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

    private static final String IMGUR_CLIENT_ID = "...";
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    private static final OkHttpClient client = new OkHttpClient();
    public static String restfulPostFileRequest(String urlString, HashMap<String, String> bodyPair, String filePath) {
        String responseString = "";
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
//        String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
//Log.d("DAZZZZ", encodedImage);

//
//        InputStream inputStream;
//        String result = null;
//        HttpURLConnection conn = null;
//        String bodyParamsString = URLUtils.buildQueryString(bodyPair);
//        try {
//            conn = (HttpURLConnection) new URL(urlString).openConnection();
//            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//            conn.setRequestProperty("x-user-id", "qY7FdM7wnjevqmfws");
//            conn.setRequestProperty("x-auth-token", "el1UPAsSmVf8F1LEKf8tRb8Ny5jAgOdK2qLNHztb7Cj");
//            conn.setRequestMethod("POST");
//            conn.setReadTimeout(3000);
//            conn.setConnectTimeout(3000);
//            conn.setDoInput(true);
//            conn.setDoOutput(true);
//            conn.getOutputStream().write(encodedImage.getBytes("UTF-8"));
//
//            Log.d("Restful api", "Connecting url " + urlString);
//            conn.connect();
//            Log.d("Restful api", "Response Code is : " + conn.getResponseCode());
//            inputStream = conn.getInputStream();
//
//            result = getStringFromInputStream(inputStream);
//
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (ProtocolException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return result;



        FormEncodingBuilder formBodyBuilder = new FormEncodingBuilder();
        if (bodyPair != null) {
            Iterator iter = bodyPair.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                formBodyBuilder.add((String) entry.getKey(), (String) entry.getValue());
            }
        }
        File f = new File(filePath);
        RequestBody formBody = formBodyBuilder.build();
        Request request = new Request.Builder()
                .header("Content-Type", "image/png")
                .header("size", "" + f.length())
                .url(urlString)
                //.post(formBody)
                //.post(requestBody)
                .post(RequestBody.create(MediaType.parse("image/png"), new File(filePath)))
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            responseString = response.body().string();
            Log.d("DAZZZZ", responseString);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseString;


//        InputStream inputStream;
//        String result = null;
//        HttpURLConnection conn = null;
//        String bodyParamsString = URLUtils.buildQueryString(bodyPair);
//        if (urlString != null) {
//            try {
//                RequestBody requestBody = new MultipartBuilder()
//                .type(MultipartBuilder.FORM)
//                .addPart(
//                        Headers.of("Content-Disposition", "form-data; name=\"title\""),
//                        RequestBody.create(null, "Square Logo"))
//                .addPart(
//                        Headers.of("Content-Disposition", "form-data; name=\"image\""),
//                        RequestBody.create(MEDIA_TYPE_PNG, new File(filePath)))
//                .build();
//
//                // [TODO] should login
//                URL url = new URL(urlString);
//                conn = (HttpURLConnection) url.openConnection();
//                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//                conn.setRequestProperty("x-user-id", "qY7FdM7wnjevqmfws");
//                conn.setRequestProperty("x-auth-token", "el1UPAsSmVf8F1LEKf8tRb8Ny5jAgOdK2qLNHztb7Cj");
//                conn.setReadTimeout(3000);
//                conn.setConnectTimeout(3000);
//                conn.setDoInput(true);
//                conn.setDoOutput(true);
//
//                FileInputStream f = new FileInputStream(new File(filePath));
//                BufferedReader br = new BufferedReader(new InputStreamReader(f));
//                OutputStream os = conn.getOutputStream();
//                String b;
//                while ((b = br.readLine()) != null) {
//                    os.write(b.getBytes("utf-8"));
//                    os.flush();
//                }
//
//
//
//                Log.d("Restful api", "Connecting url " + urlString);
//                conn.connect();
//                Log.d("Restful api", "Response Code is : " + conn.getResponseCode());
//                inputStream = conn.getInputStream();
//                result = getStringFromInputStream(inputStream);
//
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            } catch (ProtocolException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } finally {
//                conn.disconnect();
//            }
//        }
//
//        return result;


//        FileInputStream fileInputStream = null;
//        HttpURLConnection conn = null;
//        DataOutputStream dos = null;
//        String lineEnd = "\r\n";
//        String twoHyphens = "--";
//        String boundary = "*****";
//        int bytesRead, bytesAvailable, bufferSize;
//        byte[] buffer;
//        int maxBufferSize = 1 * 1024 * 1024;
//
//        try {
//            fileInputStream = new FileInputStream(new File(filePath));
//            URL url = new URL(urlString);
//
//            // Open a HTTP  connection to  the URL
//            conn = (HttpURLConnection) url.openConnection();
//            conn.setDoInput(true); // Allow Inputs
//            conn.setDoOutput(true); // Allow Outputs
//            conn.setUseCaches(false); // Don't use a Cached Copy
//            conn.setRequestMethod("POST");
//            conn.setRequestProperty("Connection", "Keep-Alive");
//            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
//            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
//            conn.setRequestProperty("uploaded_file", filePath);
//
//            dos = new DataOutputStream(conn.getOutputStream());
//
//            dos.writeBytes(twoHyphens + boundary + lineEnd);
//            dos.writeBytes("Content-Disposition: form-data; name=" + filePath + ";filename=\""
//                            + filePath + "\"" + lineEnd);
//
//            dos.writeBytes(lineEnd);
//
//            // create a buffer of  maximum size
//            bytesAvailable = fileInputStream.available();
//
//            bufferSize = Math.min(bytesAvailable, maxBufferSize);
//            buffer = new byte[bufferSize];
//
//            // read file and write it into form...
//            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
//
//            while (bytesRead > 0) {
//
//                dos.write(buffer, 0, bufferSize);
//                bytesAvailable = fileInputStream.available();
//                bufferSize = Math.min(bytesAvailable, maxBufferSize);
//                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
//
//            }
//
//            // send multipart form data necesssary after file data...
//            dos.writeBytes(lineEnd);
//            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
//
//            // Responses from the server (code and message)
//            int serverResponseCode = conn.getResponseCode();
//            String serverResponseMessage = conn.getResponseMessage();
//
//            Log.i("uploadFile", "HTTP Response is : "
//                    + serverResponseMessage + ": " + serverResponseCode);
//
//            if(serverResponseCode == 200){
//                Log.d("DAZZZZ", "duccess");
////                runOnUiThread(new Runnable() {
////                    public void run() {
////
////                        String msg = "File Upload Completed.\n\n See uploaded file here : \n\n"
////                                +" http://www.androidexample.com/media/uploads/"
////                                +uploadFileName;
////
////                        messageText.setText(msg);
////                        Toast.makeText(UploadToServer.this, "File Upload Complete.",
////                                Toast.LENGTH_SHORT).show();
////                    }
////                });
//            }
//
//            //close the streams //
//            fileInputStream.close();
//            dos.flush();
//            dos.close();
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (ProtocolException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return "";
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
