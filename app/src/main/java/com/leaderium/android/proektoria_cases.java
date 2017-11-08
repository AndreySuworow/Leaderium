package com.leaderium.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardProvider;
import com.dexafree.materialList.card.OnActionClickListener;
import com.dexafree.materialList.card.action.TextViewAction;
import com.dexafree.materialList.view.MaterialListView;
import com.squareup.picasso.RequestCreator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class proektoria_cases extends AppCompatActivity {


    public MaterialListView mListView;
    public ArrayList<String> id = new ArrayList<String>();
    public ArrayList<String> img = new ArrayList<String>();
    public ArrayList<String> title = new ArrayList<String>();
    public ArrayList<String> description = new ArrayList<String>();
    public ArrayList<String> jobs = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proektoria_cases);
        mListView = (MaterialListView) findViewById(R.id.material_listview);

        ProektoriaCases parser = new ProektoriaCases();
        parser.execute();
    }


    public void fillArray() {
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < img.size(); i++) {
            cards.add(setCases(i));
        }
        mListView.getAdapter().addAll(cards);
    }

    public Card setCases(final int position) {
        for (int i = 0; i < description.size(); i++){
            if (description.get(i).length() > 30){
                description.get(i).replaceAll(Pattern.quote(description.get(i)), description.get(i).substring(0,30));
            }
        }
        for (int i = 0; i < title.size(); i++){
            if (title.get(i).length() > 10){
                title.get(i).replaceAll(Pattern.quote(title.get(i)), title.get(i).substring(0,10));
            }
        }



        StringBuilder descriptionCase = new StringBuilder();
        descriptionCase.append(description.get(position));

        return new Card.Builder(this)
                .withProvider(new CardProvider())
                .setLayout(R.layout.material_image_with_buttons_card)
                .setTitle(title.get(position))
                .setDescription(description.get(position))
                .setDrawable(img.get(position))
                .setDrawableConfiguration(new CardProvider.OnImageConfigListener() {
                    @Override
                    public void onImageConfigure(@NonNull RequestCreator requestCreator) {
                        requestCreator.resize(65,40);
                    }
                })
                .addAction(R.id.right_text_button, new TextViewAction(this)
                        .setText("Derecha")
                        .setTextResourceColor(R.color.accent_material_dark)
                        .setListener(new OnActionClickListener() {
                            @Override
                            public void onActionClicked(View view, Card card) {
                                Toast.makeText(proektoria_cases.this, "You have pressed the right button", Toast.LENGTH_SHORT).show();
                            }
                        }))
                .addAction(R.id.left_text_button, new TextViewAction(this)
                        .setText("Izquierda")
                        .setTextResourceColor(R.color.black_button)
                        .setListener(new OnActionClickListener() {
                            @Override
                            public void onActionClicked(View view, Card card) {
                                Log.d("ADDING", "CARD");
                                Toast.makeText(proektoria_cases.this, "Added new card", Toast.LENGTH_SHORT).show();
                            }
                        }))
                .endConfig()
                .build();
    }

    public class ProektoriaCases extends AsyncTask {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = new ProgressDialog(proektoria_cases.this);
            dialog.setMessage("Загрузка...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Object doInBackground(Object[] objects) {

            try {
                Document doc = Jsoup.connect("http://proektoria.online/projects/").get();


                // ID
                // FORMAT: ladaarktika
                String id_edit = doc.select("div.catalog-block-item-title").toString();
                String[] id_edit_mas = id_edit.trim().split("");
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < id_edit_mas.length; i++) {
                    sb.append(id_edit_mas[i]);
                    if (sb.toString().endsWith("href=\"/projects/")){
                        sb.delete(0,sb.length());
                        while (id_edit_mas[i].endsWith("\"") == false){
                            i++;
                            sb.append(id_edit_mas[i]);
                        }
                        id.add(sb.delete(sb.length()-2, sb.length()).toString());
                        sb.delete(0, sb.length());
                    }
                }


                // IMG
                // FORMAT: http://209315.selcdn.ru/projectoria/95192c98/23361ec3/4331c736291e.png
                for (int i = 0; i < doc.select("img").attr("src").length(); i++){
                    String img_url_to_check = doc.select("img").get(i).attr("src").toString();
                    if (img_url_to_check.contains("209315.selcdn.ru")){
                        img.add(img_url_to_check);
                    }
                }


                // Description
                // FORMAT: Задача кейса состоит в том, чтобы дополнить конструкцию существующего автомобиля в области энергоэффективности, приемлемых габаритов, морозоустойчивости и проходимости.
                for (int i = 0; i < doc.select("div.catalog-block-item-desc").size(); i++) {
                    description.add(doc.select("div.catalog-block-item-desc").get(i).text());
                }

                // TITLE
                // FORMAT: Создание LADA 4x4 Arktika. Российский внедорожник для автономных зимних экспедиций в условиях крайнего севера.
                for (int i = 0; i < doc.select("div.catalog-block-item-title").select("a").size(); i++){
                    title.add(doc.select("div.catalog-block-item-title").select("a").get(i).text().trim());
                }

//                // JOBS
//                // FORMAT: Инженер-конструктор транспортных систем, Промышленный дизайнер
//                for (int i = 0; i < doc.select("div.catalog-block-item-properties-values").size(); i++){
//                    jobs.add(doc.select("div.catalog-block-item-properties-values").get(i).text().trim());
//                }

                System.out.println("id: " + id.size()+"\n"+
                "img: " + img.size() + "\n"+
                "desc: " + description.size() + "\n" +
                "title: " + title.size() + "\n");
//                "jobs: " + jobs.size() + "\n");

            } catch (IOException e) {
                e.printStackTrace();
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    fillArray();
                }
            });
            dialog.dismiss();
            return null;
        }

    }

}