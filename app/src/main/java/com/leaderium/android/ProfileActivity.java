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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    String access_token;
    int user_id;
    SharedPreferences sPref;
    TextView nameView;
    TextView companyView;
    TextView workView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        nameView = findViewById(R.id.name_view);
        companyView = findViewById(R.id.work_view);
        workView = findViewById(R.id.profession_view);
        sPref = getSharedPreferences("auth_settings", MODE_PRIVATE);

        access_token = sPref.getString("access_token", "");
        user_id = sPref.getInt("user_id", 0);


        new ProfileTask().execute();
    }

    private class JobsTask extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson2 = "";

        Bitmap bmp;
        String LEADER_API_URL = "http://leaderium.herokuapp.com/get_top_jobs";

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(LEADER_API_URL + "?current_job=" + workView.getText() + "&n=5");

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                Log.wtf("url", url.toString());
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                resultJson2 = buffer.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultJson2;
        }

        @Override
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);

            JSONArray jobs;

            try {
                jobs = new JSONArray(resultJson2);
                Log.wtf("af", resultJson2);
                RadioGroup rg = findViewById(R.id.jobs_group);
                rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int i) {
                        int selectedId = radioGroup.getCheckedRadioButtonId();
                        RadioButton radioSexButton = (RadioButton) findViewById(selectedId);
                        String text = radioSexButton.getText().toString();
                        SharedPreferences.Editor ed = sPref.edit();
                        ed.putString("selected_job", text);
                        Log.wtf("ddd", text);
                        Intent authIntent = new Intent();
                        authIntent.putExtra("selected_job", text);
                        setResult(RESULT_OK, authIntent);
                        ed.apply();
                        finish();
                    }
                });
                for (int i = 0; i < jobs.length(); i++) {
                    RadioButton newRadioButton = new RadioButton(ProfileActivity.this);
                    Log.wtf("af", jobs.getString(i));
                    newRadioButton.setText(jobs.getString(i));
                    rg.addView(newRadioButton);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private class ProfileTask extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";
        URL urlBitmap;
        Bitmap bmp;
        String LEADER_API_URL = "https://leader-id.ru/api/";

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
                    bmp = BitmapFactory.decodeStream((InputStream) urlBitmap.getContent());
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
                CircleImageView iv = findViewById(R.id.profile_image);

                iv.setImageBitmap(bmp);
                dataJsonObj = new JSONObject(resultJson);
                String name = dataJsonObj.getString("LastName") + " " + dataJsonObj.getString("FirstName") + " " + dataJsonObj.getString("FatherName");
                JSONObject work = dataJsonObj.getJSONObject("Work");
                String position = work.getString("Position");
                JSONObject company = work.getJSONObject("Company");
                String company_name = company.getString("Name");

                nameView.setText(name);
                companyView.setText(company_name);
                workView.setText(position);
                new JobsTask().execute();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
