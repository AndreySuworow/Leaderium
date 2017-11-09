package com.leaderium.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;

public class full_description extends AppCompatActivity {

    String title = new String();
    String img = new String();
    String id = "";

    String full_URL = "http://proektoria.online/";
    public ImageView mainImg;
    public TextView desc;
    int from = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_description);
        mainImg = (ImageView) findViewById(R.id.main_backdrop);
        desc = (TextView) findViewById(R.id.desc);
        Intent intent = getIntent();
        from = getIntent().getIntExtra("key", 0);
        id = getIntent().getStringExtra("id");
        title = getIntent().getStringExtra("title");
        img = getIntent().getStringExtra("img");

        // 1 - Кейс
        // 2 - Компания
        // 3 - Мероприятие



        switch (from) {
            case 1:
                full_URL = full_URL + "projects/" + id;
                break;
            case 2:
                full_URL = full_URL + "partners/" + id;
                break;
                /*
                case 3:
                full_URL = full_URL + "projects/" + id;
                break;*/

        }

        ProektoriaParser parser = new ProektoriaParser();
        parser.execute();
    }

    public void toBot(View view) {

    }

    public class ProektoriaParser extends AsyncTask {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = new ProgressDialog(full_description.this);
            dialog.setMessage("Загрузка...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Object doInBackground(Object[] objects) {

            try {
                Document doc = Jsoup.connect(full_URL).get();
                switch (from){
                    case 1:
                        Picasso.with(full_description.this).load(img).into(mainImg); // НА ЭТО РУГАЕТСЯ Method call should happen from the main thread. УТРОМ ИСПРАВЛЮ
                        desc.setText(doc.select("main.main-container").text());
                        break;
                    case 2:
                        Picasso.with(full_description.this).load(img).into(mainImg);
                        desc.setText(doc.select("main.main-container").text());
                        break;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            dialog.dismiss();
            return null;
        }

    }
}
