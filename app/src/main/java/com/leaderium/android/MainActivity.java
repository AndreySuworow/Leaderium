package com.leaderium.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    String LEADER_API_URL = "https://leader-id.ru/api/";

    String access_token;
    int expires_in;
    String refresh_token;
    int user_id;
    SharedPreferences sPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sPref = getSharedPreferences("auth_settings", MODE_PRIVATE);
        if (!sPref.contains("access_token")) {
            Intent intent = new Intent(this, AuthActivity.class);
            startActivityForResult(intent, 1);
        } else {
            access_token = sPref.getString("access_token", "");
            expires_in = sPref.getInt("expires_in", 0);
            refresh_token = sPref.getString("refresh_token", "");
            user_id = sPref.getInt("user_id", 0);
        }
        new ProfileTask().execute();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onActivityResult(int request, int result, Intent data) {
        String code;
        if (result == RESULT_OK) {
            access_token = sPref.getString("access_token", "");
            expires_in = sPref.getInt("expires_in", 0);
            refresh_token = sPref.getString("refresh_token", "");
            user_id = sPref.getInt("user_id", 0);
            Log.wtf("rag", "access_token: " + access_token);
            Log.wtf("rag", "expires_in: " + expires_in);
            Log.wtf("rag", "refresh_token: " + refresh_token);
            Log.wtf("rag", "user_id: " + user_id);
            // editText.line
            new ProfileTask().execute();
        }
    }

    private class ProfileTask extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";
        URL urlBitmap;
        Bitmap bmp;

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(LEADER_API_URL + "user/get?access_token=" + access_token + "&Id=" + user_id);

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

                JSONObject data = new JSONObject(resultJson);
                if (data.has("Photo")) {
                    JSONObject photo = data.getJSONObject("Photo");
                    urlBitmap = new URL(photo.getString("Large"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultJson;
        }

        @Override
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);

            JSONObject dataJsonObj;

            try {
                dataJsonObj = new JSONObject(resultJson);
                String name = dataJsonObj.getString("LastName") + " " + dataJsonObj.getString("FirstName") + " " + dataJsonObj.getString("FatherName");
                JSONObject work = dataJsonObj.getJSONObject("Work");
                String position = work.getString("Position");
                JSONObject company = work.getJSONObject("Company");
                String company_name = company.getString("Name");

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
    public void toCases (View view){
        Intent toCase = new Intent(MainActivity.this, proektoria_cases.class);
        startActivity(toCase);
    }
}
