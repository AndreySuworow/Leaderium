package com.leaderium.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class full_description extends AppCompatActivity {

    String title = new String();
    String img = new String();
    String id = "";
    public String main_text = "";

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
        full_description.this.setTitle(title);

        // 1 - Кейс
        // 2 - Компания
        // 3 - Мероприятие


        switch (from) {
            case 1:
                full_URL = full_URL + "projects/" + id;
                Handler uiHandler1 = new Handler(Looper.getMainLooper());
                uiHandler1.post(new Runnable() {
                    @Override
                    public void run() {
                        Picasso.with(full_description.this)
                                .load(img)
                                .into(mainImg);
                    }
                });
                break;
            case 2:
                full_URL = full_URL + "partners/" + id;
                Handler uiHandler2 = new Handler(Looper.getMainLooper());
                uiHandler2.post(new Runnable() {
                    @Override
                    public void run() {
                        Picasso.with(full_description.this)
                                .load(img)
                                .into(mainImg);
                    }
                });
                break;
            case 3:
                Handler uiHandler3 = new Handler(Looper.getMainLooper());
                uiHandler3.post(new Runnable() {
                    @Override
                    public void run() {
                        Picasso.with(full_description.this)
                                .load(img)
                                .into(mainImg);
                    }
                });
                break;

        }


        ProektoriaParser parser = new ProektoriaParser();
        parser.execute();

        System.out.println("From main: " + main_text);
        desc.setText(main_text);
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
                switch (from) {
                    case 1:
                        final StringBuilder body_text = new StringBuilder();
                        body_text.delete(0, body_text.length());
                        body_text.append(doc.select("div.wrap").get(2).html().replaceAll("<div class=\"title-red\">\n" +
                                " ", "<b>").replaceAll("\n" +
                                "</div> ", "</b>\n").replaceAll("<li> ", "<li>").replaceAll("<li>", "<li>  ").replaceAll("\n\n", "\n").replaceAll("[\\n]{2,}", "\n").replaceAll("<b>Актуальность</b>", "Актуальность").trim());

                        body_text.append(doc.select("div.wrap").get(3).html().replaceAll("<div class=\"title-red\">\n" +
                                " ", "<b>").replaceAll("\n" +
                                "</div> ", "</b>\n").replaceAll(" Описание", "<b>").replaceAll("\n" +
                                "\n", "\n").replaceAll("<li> ", "<li>").replaceAll("<li>", "<li>  ").replaceAll("\n\n", "\n").replaceAll("[\\n]{2,}", "\n").replaceAll("<br>", "").replaceAll("<ol>", "\n").replaceAll("<li>", "  - ").replaceAll("</li>", "\n").replaceAll("<ul>", "\n").replaceAll("<p>", "").replaceAll("</p>", "").replaceAll("</b>", "\n").replaceAll("<b>", "\n\n").replaceAll("</ul>", "").replaceAll("</ol>", "").replaceAll("<b>", "").replaceAll("</b>", "").replaceAll("Описание", "\n\nОписание").replaceAll("Требования", "\nТребования").replaceAll("Результат", "\nРезультат").trim());
                        runOnUiThread(new Runnable() {
                            public void run() {
                                System.out.println(body_text.toString());
                                full_description.this.desc.setText(body_text.toString().replaceAll("[\\n]{2,}", "\n").trim());
                            }
                        });
                        break;
                    case 2:
                        final StringBuilder body_text2 = new StringBuilder();
                        body_text2.delete(0, body_text2.length());
                        body_text2.append(doc.select("div.wrap").get(2).html().replaceAll("<div class=\"title-red\">\n" +
                                " ", "<b>").replaceAll("\n" +
                                "</div> ", "</b>\n").replaceAll("<li> ", "<li>").replaceAll("<li>", "<li>  ").replaceAll("\n\n", "\n").replaceAll("[\\n]{2,}", "\n").replaceAll("<b>Актуальность</b>", "Актуальность").trim());

                        body_text2.append(doc.select("div.wrap").get(3).html().replaceAll("<div class=\"title-red\">\n" +
                                " ", "<b>").replaceAll("\n" +
                                "</div> ", "</b>\n").replaceAll(" Описание", "<b>").replaceAll("\n" +
                                "\n", "\n").replaceAll("<li> ", "<li>").replaceAll("<li>", "<li>  ").replaceAll("\n\n", "\n").replaceAll("[\\n]{2,}", "\n").replaceAll("<br>", "").replaceAll("<ol>", "\n").replaceAll("<li>", "  - ").replaceAll("</li>", "\n").replaceAll("<ul>", "\n").replaceAll("<p>", "").replaceAll("</p>", "").replaceAll("</b>", "\n").replaceAll("<b>", "\n\n").replaceAll("</ul>", "").replaceAll("</ol>", "").replaceAll("<b>", "").replaceAll("</b>", "").replaceAll("Описание", "\n\nОписание").replaceAll("Требования", "\nТребования").replaceAll("Результат", "\nРезультат").trim());
                        runOnUiThread(new Runnable() {
                            public void run() {
                                System.out.println(body_text2.toString());
                                full_description.this.desc.setText(body_text2.toString().replaceAll("[\\n]{2,}", "\n").trim());
                            }
                        });
                        break;
                    case 3:
                        final StringBuilder body_text3 = new StringBuilder();
                        body_text3.delete(0, body_text3.length());
                        body_text3.append(doc.select("div.program").text());
                        runOnUiThread(new Runnable() {
                            public void run() {
                                System.out.println(body_text3.toString());
                                full_description.this.desc.setText(body_text3.toString());
                            }
                        });
                        break;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            dialog.dismiss();
            return null;
        }

    }

    public void EverithingIsOK(String descr_text) {
        desc.setText(main_text);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
