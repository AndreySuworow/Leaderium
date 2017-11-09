package com.leaderium.android;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

public class EventsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);


    }

  /*  private class EventsTask extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";
        URL urlBitmap;
        Bitmap bmp;
        String API_URL = "https://leaderium.herokuapp.com/";

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
                TextView nameView = findViewById(R.id.name_view);
                TextView companyView = findViewById(R.id.work_view);
                TextView workView = findViewById(R.id.profession_view);
                nameView.setText(name);
                companyView.setText(company_name);
                workView.setText(position);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }*/
}
