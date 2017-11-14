package com.leaderium.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardProvider;
import com.dexafree.materialList.card.OnActionClickListener;

import com.dexafree.materialList.view.MaterialListView;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    String LEADER_API_URL = "https://leader-id.ru/api/";

    String access_token;
    int expires_in;
    String refresh_token;
    int user_id;
    String job;
    SharedPreferences sPref;

    public MaterialListView mListView;
    public ArrayList<String> id = new ArrayList<String>();
    public ArrayList<String> img = new ArrayList<String>();
    public ArrayList<String> title = new ArrayList<String>();
    public ArrayList<String> description = new ArrayList<String>();
    public ArrayList<String> jobs = new ArrayList<String>();

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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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
        mListView = findViewById(R.id.material_listview_events);
        new DrawerBuilder().withActivity(this)
                .withToolbar(toolbar)
                .withHeader(R.layout.drawer_header)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_item_events).withIcon(FontAwesome.Icon.faw_calendar).withBadge("7").withIdentifier(1),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_cases).withIcon(FontAwesome.Icon.faw_check).withBadge("10+"),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_courses).withIcon(FontAwesome.Icon.faw_signal).withBadge("6").withIdentifier(2),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_employer).withIcon(FontAwesome.Icon.faw_suitcase),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_bot).withIcon(FontAwesome.Icon.faw_cogs),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_profile).withIcon(FontAwesome.Icon.faw_user).withIdentifier(1)
                ).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                switch (position) {
                    case 1:
                        break;
                    case 2:
                        Intent Proektoria_intent = new Intent(MainActivity.this, ProektoriaCasesActivity.class);
                        startActivity(Proektoria_intent);
                        break;
                    case 3:
                        /*Intent Proektoria_intent = new Intent(MainActivity.this, ProektoriaCasesActivity.class);
                        startActivity(Proektoria_intent);*/
                        break;
                    case 4:
                        Intent Employers_intent = new Intent(MainActivity.this, ProektoriaCompaniesActivity.class);
                        startActivity(Employers_intent);
                        break;
                    case 5:
                     /*   Intent Bot_intent = new Intent(MainActivity.this, ProektoriaCompaniesActivity.class);
                        startActivity(Employers_intent);*/
                        break;
                    case 6:
                        break;
                    case 7:
                        Intent Profile_intent = new Intent(MainActivity.this, ProfileActivity.class);
                        startActivity(Profile_intent);
                        break;
                }
                return false;
            }
        })
                .build();
        new ProfileTask().execute();
        if (!sPref.contains("selected_job")) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Для продолжения, вам необходимо выбрать предпочитаемую профессию!", Toast.LENGTH_SHORT);
            toast.show();
            Intent Profile_intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivityForResult(Profile_intent, 2);

        } else {
            job = sPref.getString("selected_job", "");
            Log.wtf("sad", job);

            new EventsTask().execute();
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onActivityResult(int request, int result, Intent data) {
        String code;
        if (result == RESULT_OK && request == 1) {
            access_token = sPref.getString("access_token", "");
            expires_in = sPref.getInt("expires_in", 0);
            refresh_token = sPref.getString("refresh_token", "");
            user_id = sPref.getInt("user_id", 0);
            Log.wtf("rag", "access_token: " + access_token);
            Log.wtf("rag", "expires_in: " + expires_in);
            Log.wtf("rag", "refresh_token: " + refresh_token);
            Log.wtf("rag", "user_id: " + user_id);
            // editText.line
        }
        if (request == 2) {
            job = sPref.getString("selected_job", "");
            Log.wtf("sad", job);
        }
    }


    public void fillArray() {
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < img.size(); i++) {
            cards.add(setCases(i));
        }
        mListView.getAdapter().addAll(cards);
    }

    public Card setCases(final int position) {


        StringBuilder descriptionCase = new StringBuilder();
        descriptionCase.append(description.get(position));

        return new Card.Builder(this)
                .withProvider(new CardProvider())
                .setLayout(R.layout.material_big_image_card_layout)
                .setTitle(title.get(position))
                .setDescription(description.get(position))
                .setDrawable(img.get(position))

                .addAction(R.id.title, new TextViewAct(this)
                        .setText(title.get(position))
                        .setTextResourceColor(R.color.md_white_1000)
                        .setListener(new OnActionClickListener() {
                            @Override
                            public void onActionClicked(View view, Card card) {
                                Log.d("ADDING", "CARD");
                                Toast.makeText(MainActivity.this, "Added new card", Toast.LENGTH_SHORT).show();
                            }
                        }))
                .endConfig()
                .build();
    }

    private class EventsTask extends AsyncTask {
        ProgressDialog dialog;
        String resultJson;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Обновление мероприятий");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(Object[] objects) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL("https://leaderium.herokuapp.com/get_similar_by_job?access_token=" + access_token + "&job_title=" + URLEncoder.encode(job));

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
                Log.wtf("dad", url.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }

            dialog.dismiss();

            return resultJson;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            try {
                JSONArray arr;
                arr = new JSONArray(resultJson);
                // IMG
                // FORMAT: http://209315.selcdn.ru/projectoria/95192c98/23361ec3/4331c736291e.png
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject object = arr.getJSONObject(i);
                    if (!object.has("error")) {
                        img.add(object.getJSONObject("Logo").getString("Large"));
                        description.add(object.getString("Info"));
                        title.add(object.getString("FullName"));
                    }
                }


//                // JOBS
//                // FORMAT: Инженер-конструктор транспортных систем, Промышленный дизайнер
//                for (int i = 0; i < doc.select("div.catalog-block-item-properties-values").size(); i++){
//                    jobs.add(doc.select("div.catalog-block-item-properties-values").get(i).text().trim());
//                }


//                "jobs: " + jobs.size() + "\n");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    fillArray();
                }
            });
        }
    }

    /* private class EventTask extends AsyncTask<Void, Void, String> {

         HttpURLConnection urlConnection = null;
         BufferedReader reader = null;
         String resultJson = "";
         URL urlBitmap;
         Bitmap bmp;

         @Override
         protected String doInBackground(Void... params) {
             try {
                 URL url = new URL("https://leaderium.herokuapp.com/get_similar_by_job?access_token="+access_token+"&job_title="+job+"&candidates_n=10&events_n=10&similar_n=10");

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
                 Log.wtf("AAA", access_token);
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
     public void toCases(View view) {
         Intent toCase = new Intent(MainActivity.this, ProektoriaCasesActivity.class);
         startActivity(toCase);
     }*/
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
                Log.wtf("AAA", access_token);
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

    public void toCases(View view) {
        Intent toCase = new Intent(MainActivity.this, ProektoriaCasesActivity.class);
        startActivity(toCase);
    }
    /*
    public void toBot(View view) {
        Intent toBot = new Intent(MainActivity.this, BotAshot.class);
        startActivity(toBot);
    }
    */
}
