package com.leaderium.android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AuthActivity extends AppCompatActivity {

    private static String CLIENT_ID = "00377526f378589fda0206767a3eb14c";
    private static String REDIRECT_URI = "https://leaderium.herokuapp.com";
    private static String RESPONSE_TYPE = "code";
    private static String OAUTH_URL = "https://leader-id.ru/oauth/authorize";
    private static String RESPONSE_URL = "https://leaderium.herokuapp.com/access_token?code=";
    SharedPreferences sPref;
    WebView web;
    String authCode;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        web = (WebView) findViewById(R.id.web_auth);
        web.getSettings().setJavaScriptEnabled(true);
        web.loadUrl(OAUTH_URL + "?client_id=" + CLIENT_ID + "&redirect_uri=" + REDIRECT_URI + "&response_type=" + RESPONSE_TYPE);
        web.setWebViewClient(new WebViewClient() {
            boolean authComplete = false;
            Intent authIntent = new Intent();

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }


            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                if (url.contains("?code=") && !authComplete) {
                    Uri uri = Uri.parse(url);
                    authCode = uri.getQueryParameter("code");
                    new ParseTask().execute();
                    Log.i("", "AuthCode: " + authCode);
                    authIntent.putExtra("code", authCode);
                    setResult(RESULT_OK, authIntent);

                } else if (url.contains("error=access_denied")) {
                    Log.i("", "ACCESS_DENIED_HERE");
                    authIntent.putExtra("code", authCode);
                    authComplete = true;
                    Toast.makeText(getApplicationContext(), "Error Occured", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_CANCELED, authIntent);
                }

            }
        });
    }

    private void finishActivity() {
        finish();
    }

    private class ParseTask extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(RESPONSE_URL + authCode);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                resultJson = buffer.toString();
                Log.wtf("rag", RESPONSE_URL + authCode);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultJson;
        }

        @Override
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);
            // выводим целиком полученную json-строку
            Log.d("rag", resultJson);

            JSONObject dataJsonObj;

            try {
                dataJsonObj = new JSONObject(resultJson);
                String access_token = dataJsonObj.getString("access_token");
                int expires_in = dataJsonObj.getInt("expires_in");
                String refresh_token = dataJsonObj.getString("refresh_token");
                int user_id = dataJsonObj.getInt("user_id");

                sPref = getSharedPreferences("auth_settings", MODE_PRIVATE);
                SharedPreferences.Editor ed = sPref.edit();
                ed.putString("access_token", access_token);
                ed.putInt("expires_in", expires_in);
                ed.putString("refresh_token", refresh_token);
                ed.putInt("user_id", user_id);
                ed.apply();

            } catch (JSONException e) {
                e.printStackTrace();
            }
            finishActivity();
        }
    }
}
