package ru.startandroid.c5momentalpainting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class HelpActivity extends AppCompatActivity {

    SharedPreferences sPrefHelp;
	ImageView imgLeft, imgRight;
    TextView twText, twMessage;//, twPageID;
    Button btnPageLeft, btnPageRight;
    int[] images_left_id, images_right_id;
	int id_page, max_page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        getSupportActionBar().hide();

        sPrefHelp = getSharedPreferences(getString(R.string.PREF_FILE_HELP), MODE_PRIVATE);
        twText = findViewById(R.id.helpTextViewText);
        twMessage = findViewById(R.id.helpTextViewMessage);
        //twPageID = findViewById(R.id.helpTextViewPageID);
		imgLeft = findViewById(R.id.helpImageLeft);
		imgRight = findViewById(R.id.helpImageRight);
        btnPageLeft = findViewById(R.id.helpButtonLeft);
        btnPageRight = findViewById(R.id.helpButtonRight);
        findViewById(R.id.helpButtonExit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HelpActivity.this.finish();
            }
        });
		
		images_left_id = new int[] { 
			R.drawable.help11, R.drawable.help21, R.drawable.help31, R.drawable.help41, 
			R.drawable.help51, R.drawable.help61, R.drawable.help71, R.drawable.help81 };
		images_right_id = new int[] { 
			R.drawable.help12, 0,                 R.drawable.help32, R.drawable.help42,
			R.drawable.help52, 0,                 0,                 R.drawable.help82 };

        Intent intent = getIntent();
        id_page = intent.getIntExtra("id_page", -1);
        if (id_page == -1)
        {
            // Загрузка всех страниц
            id_page = 0;
            max_page = sPrefHelp.getInt("MAX_PAGE_ID", 1);
            btnPageLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LoadPage(id_page - 1);
                }
            });
            btnPageRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LoadPage(id_page + 1);
                }
            });
        }
        else
        {
            // Показ одной страницы
            max_page = 9999;
            findViewById(R.id.helpLayoutMove).setVisibility(View.INVISIBLE);
            //twPageID.setVisibility(View.INVISIBLE);
        }
        LoadPage(id_page);
    }

    void LoadPage(int page)
    {
        if (page < 0 || page > max_page) return;

        id_page = page;
        String text = new String[] {
                "Передвигайте кристаллы по полю, нажимая на стрелочки. Все кристаллы должны попасть на поля своего цвета.",
                "Игра стала сложнее! На уровнях появились стены, которые в отличии от обычных кристаллов нельзя двигать.",
                "Вы можете получать ключи, если будете проходить уровни за определенное кол-во шагов или быстрее определенного времени. Ключи нужны для разблокировки других зон.",
                "В Черной зоне можно двигать лишь те кристаллы, которые отмечены крестом. Продумывайте свои шаги.",
                "В Стабильной зоне любой кристалл, попавший на свое место, будет заморожен, и вы не сможете его двигать. Будьте внимательны!",
                "В Слепой зоне вы не будете видеть, куда нужно поставить кристалл. Играйте вслепую!",
                "Ситуация усложняется! Теперь вы не будете видеть, где именно находятся кристаллы. Но теперь видно, куда их надо поставить.",
                "Катастрофа! Мало того, что кристаллы будут снова замораживаться при попадании на свое место, так еще и двигать можно лишь те, что с крестом. Жуть, в общем."
        } [page];
        twText.setText(text);
        btnPageLeft.setVisibility(page > 0 ? View.VISIBLE : View.INVISIBLE);
        btnPageRight.setVisibility(page < max_page ? View.VISIBLE : View.INVISIBLE);
        imgLeft.setImageResource(images_left_id [page]);

        int id_right = images_right_id[page];
        if (id_right == 0)
            imgRight.setVisibility(View.GONE);
        else
        {
            imgRight.setVisibility(View.VISIBLE);
            imgRight.setImageResource(images_right_id[page]);
        }
    }
}
