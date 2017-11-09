package com.leaderium.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;

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

public class ProektoriaCompaniesActivity extends AppCompatActivity {


    public MaterialListView mListView;
    ArrayList<String> id = new ArrayList<String>();
    ArrayList<String> logo = new ArrayList<String>();
    ArrayList<String> title = new ArrayList<String>();
    ArrayList<String> description = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proektoria_companies);

        mListView = (MaterialListView) findViewById(R.id.material_listview);

        ProektoriaCompenies parser = new ProektoriaCompenies();
        parser.execute();

    }



    public void fillArray() {
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            cards.add(setCases(i));
        }
        mListView.getAdapter().addAll(cards);
    }

    public Card setCases(final int position) {
        for (int i = 0; i < description.size(); i++){
            if (description.get(i).length() > 10){
                description.get(i).replaceAll(Pattern.quote(description.get(i)), description.get(i).substring(0,10)+"...");
            }
            //description.get(i).
        }


        StringBuilder descriptionCase = new StringBuilder();
        descriptionCase.append(description.get(position));

        return new Card.Builder(this)
                .withProvider(new CardProvider())
                .setLayout(R.layout.material_basic_image_buttons_card_layout)
                .setTitle(title.get(position))
                .setTitleGravity(Gravity.CENTER)
                .setDrawable(logo.get(position))
                .setDrawableConfiguration(new CardProvider.OnImageConfigListener() {
                    @Override
                    public void onImageConfigure(@NonNull RequestCreator requestCreator) {
                        //requestCreator.resize(300,1);
                    }
                })
                .addAction(R.id.right_text_button, new TextViewAction(this)
                        .setText("Подробнее")
                        .setTextResourceColor(R.color.orange_button)
                        .setListener(new OnActionClickListener() {
                            @Override
                            public void onActionClicked(View view, Card card) {
                                Intent toFullDescription = new Intent(ProektoriaCompaniesActivity.this, full_description.class);
                                toFullDescription.putExtra("key", 2);
                                toFullDescription.putExtra("id", id.get(position));
                                toFullDescription.putExtra("img", logo.get(position));
                                toFullDescription.putExtra("title", title.get(position));
                                startActivity(toFullDescription);
                            }
                        }))
                .endConfig()
                .build();
    }

    public class ProektoriaCompenies extends AsyncTask {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = new ProgressDialog(ProektoriaCompaniesActivity.this);
            dialog.setMessage("Загрузка...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Object doInBackground(Object[] objects) {

            try {
                Document doc = Jsoup.connect("http://proektoria.online/partners/").get();

                // ID
                // FORMAT: rosteh
                for (int i = 0; i < doc.select("div.catalog-block-item-title").size(); i++) {
                    id.add(doc.select("div.catalog-block-item-title").select("a").get(i).attr("href").replaceAll("/partners/","").replaceAll("/","").trim());
                }

                // LOGO
                // FORMAT: http://209315.selcdn.ru/projectoria/95192c98/624c8eb3/b668f61cc4d5.png
                for (int i = 0; i < doc.select("div.hexogon-large-1").size(); i++) {
                    logo.add(doc.select("div.hexogon-large-1").select("div.icon").select("img").get(i).attr("src").toString().trim());
                }

                // TITLE
                // FORMAT: Ростех
                for (int i = 0; i < doc.select("div.catalog-block-item-title").size(); i++) {
                    title.add(doc.select("div.catalog-block-item-title").select("a").get(i).text().toString().trim());
                }

                // DESCRIPTION
                // FORMAT: Госкорпорация Ростех – российская корпорация, созданная в 2007 г. для содействия разработке, производству и экспорту высокотехнологичной промышленной продукции гражданского и военного назначения. В её состав входят более 700 организации, из которых в настоящее время сформировано 9 холдинговых компаний в оборонно-промышленном комплексе и 6 – в гражданских отраслях промышленности...
                for (int i = 0; i < doc.select("div.catalog-block-item-desc").size(); i++) {
                    description.add(doc.select("div.catalog-block-item-desc").get(i).text().toString().trim());
                }

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
