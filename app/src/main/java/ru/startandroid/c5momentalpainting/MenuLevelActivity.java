package ru.startandroid.c5momentalpainting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.opengl.Visibility;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Map;

public class MenuLevelActivity extends AppCompatActivity {

    SharedPreferences sPrefLevels, sPrefProgress, sPrefHelp;
    LinearLayout gridLayout;
    TextView tvLabel;
    Button buttonLeft, buttonRight;
    Button[] list_buttons;
    char[] list_keys;
    int buttonColor, textColor, buttonClearedID, buttonClearedKeyID;
    int page_width = 4, page_height = 5, page_size, max_page, max_level, page = 0;
    int mode_game = 0;
    int SCREEN_WIDTH, BUTTON_HEIGHT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_level);
        getSupportActionBar().hide();

        try
        {
            gridLayout = findViewById(R.id.levelGrid);
            tvLabel = findViewById(R.id.levelTitleLabel);
            sPrefLevels = getSharedPreferences(getString(R.string.PREF_FILE_LEVELS), MODE_PRIVATE);
            sPrefProgress = getSharedPreferences(getString(R.string.PREF_FILE_PROGRESS), MODE_PRIVATE);
            sPrefHelp = getSharedPreferences(getString(R.string.PREF_FILE_HELP), MODE_PRIVATE);
            page = 0;
            page_size = page_width * page_height;
            list_buttons = new Button[page_size];

            buttonLeft = findViewById(R.id.levelButtonLeft);
            buttonLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LoadPage(page - 1, true);
                }
            });

            buttonRight = findViewById(R.id.levelButtonRight);
            buttonRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LoadPage(page + 1, true);
                }
            });

            // Размер экрана
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            SCREEN_WIDTH = size.x;
            BUTTON_HEIGHT = (SCREEN_WIDTH - 110) / 4;

            // Выбор цвета
            mode_game = getIntent().getIntExtra("type", 0);
			max_level   = LevelController.GetLevelCount(mode_game);
			buttonColor = new int[] { Color.WHITE, Color.BLACK, Color.TRANSPARENT, Color.BLACK, Color.BLACK } [mode_game];
			textColor   = new int[] { Color.BLACK, Color.WHITE, Color.WHITE,       Color.RED ,  Color.RED   } [mode_game];
            buttonClearedID = new int[] {
                    R.drawable.menu_button_cleared_blue,
                    R.drawable.menu_button_cleared_green,
                    R.drawable.menu_button_cleared_yellow,
                    R.drawable.menu_button_cleared_red,
                    R.drawable.menu_button_cleared_violet  } [mode_game];
            buttonClearedKeyID = new int[] {
                    R.drawable.menu_button_key_blue,
                    R.drawable.menu_button_key_green,
                    R.drawable.menu_button_key_yellow,
                    R.drawable.menu_button_key_red,
                    R.drawable.menu_button_key_violet  } [mode_game];
            int labelID = new int[] { R.string.menu_mode0, R.string.menu_mode1, R.string.menu_mode2, R.string.menu_mode3, R.string.menu_mode4 } [mode_game];
            tvLabel.setText(labelID);
            max_page = (max_level - 1) / page_size;

            // Создание кнопок
            int k = 0;
            for (int i = 0; i < page_height; i++) {
                LinearLayout layout = GetLayoutParamsRow();
                for (int j = 0; j < page_width; j++) {
                    Button btn = new Button(this);
                    btn.setTextColor(Color.WHITE);
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Button btn = (Button) v;
                            int id = Integer.parseInt(btn.getText().toString()) - 1;
                            startActivity(new Intent(MenuLevelActivity.this, GameNormalActivity.class)
                                    .putExtra("level_id", mode_game * 1000 + id));
                            //MenuLevelActivity.this.finish();
                        }
                    });
                    list_buttons[k++] = btn;
                    layout.addView(btn, GetLayoutParamsButton());
                    //btn.setHeight(btn.getMeasuredWidth());
                }
                gridLayout.addView(layout);
            }
        }
        catch (Exception e)
        {
            buttonLeft.setText(e.getMessage());
        }


        // Если открытие происходит в первый раз, то записываем в sPrefHelp
        // (внутри метода LoadModeFromMenuActivity) и тут же открываем активити помощи и затем игры
        int page_id = HelpController.LoadModeFromMenuActivity(this, sPrefHelp, mode_game);
        if (page_id != -1) {
            startActivity(new Intent(MenuLevelActivity.this, GameNormalActivity.class)
                    .putExtra("level_id", mode_game * 1000));
            startActivity(new Intent(MenuLevelActivity.this, HelpActivity.class)
                    .putExtra("id_page", page_id));
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        // Ключи
        String[][] levels = LevelController.GetLevelpack(mode_game);
        list_keys = new char[max_level];
        for (int i = 0; i < max_level; i++)
        {
            list_keys[i] = ' ';
            for (String s : levels[i])
                if ((s.charAt(0) == 'T' || s.charAt(0) == 'C' || s.charAt(0) == 'С')
                        && !sPrefProgress.getBoolean("M" + mode_game + "L" + (i + 1) + "KEYBLUE", false)
                        && !sPrefProgress.getBoolean("M" + mode_game + "L" + (i + 1) + "KEYRED",  false))
                    list_keys[i] = s.charAt(0);
        }

        LoadPage(page, false);
    }

    LinearLayout GetLayoutParamsRow(){
        LinearLayout rez = new LinearLayout(this);
        rez.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        rez.setOrientation(LinearLayout.HORIZONTAL);
        return rez;
    }

    LinearLayout.LayoutParams GetLayoutParamsButton() {
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lparams.setMargins(0, 0, 0, 0);
        lparams.weight = 1;
        lparams.width = 0;
        lparams.height = BUTTON_HEIGHT;
        lparams.topMargin = lparams.bottomMargin = lparams.leftMargin = lparams.rightMargin = 10;
        return lparams;
    }


    void LoadPage(int page_new, boolean animate)
    {
        try
        {
            final int sign = page_new < page ? -1 : 1;

            page = page_new;
            buttonLeft .setVisibility(page_new != 0        ? View.VISIBLE : View.INVISIBLE);
            buttonRight.setVisibility(page_new != max_page ? View.VISIBLE : View.INVISIBLE);
            for (int i = 0; i < page_size; i++)
            {
                final Button btn = list_buttons[i];
                int x = i % 4;
                int y = i / 4;
                final int k = page * page_size + i + 1;
                btn.setVisibility(k <= max_level ? View.VISIBLE : View.INVISIBLE);
                //btn.setBackgroundColor(Color.rgb(10, 40 + 5 * id, 110 + 10 * id + 3 * i));
                if (!animate) {
                    if (btn.getVisibility() == View.VISIBLE)
                        SetButtonState(btn, k);
                    continue;
                }

                TranslateAnimation anim = new TranslateAnimation(0, -sign * SCREEN_WIDTH, 0, 0);
                anim.setDuration(200 + 30 * x + 15 * y);
                //anim.setStartTime(100 * x + 60 * y);
                anim.setAnimationListener(new TranslateAnimation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) { }
                    @Override
                    public void onAnimationRepeat(Animation animation) { }
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        if (btn.getVisibility() == View.VISIBLE)
                            SetButtonState(btn, k);
                        TranslateAnimation anim2 = new TranslateAnimation(sign * SCREEN_WIDTH, 0, 0, 0);
                        anim2.setDuration(200);
                        btn.setAnimation(anim2);
                    }
                });
                btn.clearAnimation();
                btn.setAnimation(anim);
            }
        }
        catch (Exception e)
        {
            buttonLeft.setText(e.getMessage());
        }
    }

    void SetButtonState(Button btn, int k)
    {
        switch (sPrefLevels.getInt("M" + mode_game + "L" + k, 0))
        {
            // Locked
            case 0:
                //btn.setBackgroundColor(Color.LTGRAY);
                btn.setBackgroundResource(R.drawable.menu_button_lock);
                btn.setText("");
                btn.setEnabled(false);
                break;

            // Normal
            case 1:
                //btn.setBackgroundColor(buttonColor);
                btn.setBackgroundResource(list_keys[k - 1] == ' ' ? R.drawable.menu_button_level : R.drawable.menu_button_key_level);
                btn.setText(String.valueOf(k));
                btn.setEnabled(true);
                break;

            // Cleared level
            case 2:
                //btn.setBackgroundColor(getResources().getColor(R.color.button_cleared));
                btn.setBackgroundResource(list_keys[k - 1] == ' ' ? buttonClearedID : buttonClearedKeyID);
                btn.setText(String.valueOf(k));
                btn.setEnabled(true);
                break;
        }

        /*
                int drawableResource = 0;
                switch (list_keys[k - 1])
                {
                    case 'T': drawableResource = R.drawable.item_key_red;  break;
                    case 'C': drawableResource = R.drawable.item_key_blue; break;
                    case 'С': drawableResource = R.drawable.item_key_blue; break;
                }
                if (drawableResource != 0) {
                    Drawable img = getResources().getDrawable(drawableResource);
                    img.setBounds(0, 0, BUTTON_HEIGHT / 4, BUTTON_HEIGHT / 4);
                    btn.setCompoundDrawables(null, img, null, null);
                } else
                    btn.setCompoundDrawables(null, null, null, null);
                    */
    }
}
