package com.leaderium.android;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Badgeable;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;

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
        Toolbar toolbar = findViewById(R.id.toolbar);
       /*
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

                        new PrimaryDrawerItem().withName(R.string.drawer_item_events).withIcon(FontAwesome.Icon.faw_calendar).withBadge("7").withIdentifier(1),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_cases).withIcon(FontAwesome.Icon.faw_check).withBadge("12+"),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_courses).withIcon(FontAwesome.Icon.faw_signal).withBadge("6").withIdentifier(2),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_employer).withIcon(FontAwesome.Icon.faw_suitcase),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_bot).withIcon(FontAwesome.Icon.faw_cogs),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_profile).withIcon(FontAwesome.Icon.faw_user).withIdentifier(1)
               */

        new DrawerBuilder().withActivity(this)
                .withToolbar(toolbar)
                .withHeader(R.layout.drawer_header)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_item_events).withIcon(FontAwesome.Icon.faw_calendar).withBadge("7").withIdentifier(1),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_cases).withIcon(FontAwesome.Icon.faw_check).withBadge("12+"),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_courses).withIcon(FontAwesome.Icon.faw_signal).withBadge("6").withIdentifier(2),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_employer).withIcon(FontAwesome.Icon.faw_suitcase),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_bot).withIcon(FontAwesome.Icon.faw_cogs),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_profile).withIcon(FontAwesome.Icon.faw_user).withIdentifier(1)
                ).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                return false;
            }
        })
                .build();
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
        Intent toCase = new Intent(MainActivity.this, proektoria_companies.class);
        startActivity(toCase);
    }
}
