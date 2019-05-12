package ru.startandroid.c5momentalpainting;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MenuActivity extends AppCompatActivity {

    SharedPreferences sPrefLevels, sPrefProgress;
    TextView tbKeysBlue, tbKeysRed;
    TextView tbModeName, tbModeLabel;
    Button btnLevels;
    ImageButton[] mode_buttons;
    int[] mode_resource_images;
    int id_active_button = -1;
    boolean[] mode_enabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        getSupportActionBar().hide();

        tbKeysBlue  = findViewById(R.id.menuTextViewKeysBlue);
        tbKeysRed   = findViewById(R.id.menuTextViewKeysRed);
        tbModeName  = findViewById(R.id.menuTextViewModeName);
        tbModeLabel = findViewById(R.id.menuTextViewModeLabel);
        sPrefLevels = getSharedPreferences(getString(R.string.PREF_FILE_LEVELS), MODE_PRIVATE);
        sPrefProgress = getSharedPreferences(getString(R.string.PREF_FILE_PROGRESS), MODE_PRIVATE);
        mode_buttons = new ImageButton[] {
                findViewById(R.id.menuButtonMode0),
                findViewById(R.id.menuButtonMode1),
                findViewById(R.id.menuButtonMode2),
                findViewById(R.id.menuButtonMode3),
                findViewById(R.id.menuButtonMode4),
        };
        mode_resource_images = new int[] { R.drawable.menu_image_mode0,
                R.drawable.menu_image_mode1,
                R.drawable.menu_image_mode2,
                R.drawable.menu_image_mode3,
                R.drawable.menu_image_mode4 };


        SharedPreferences sPrefOptions = getSharedPreferences(getString(R.string.PREF_FILE_OPTIONS), MODE_PRIVATE);
        int value_music = sPrefOptions.getInt(getString(R.string.PREF_OPTIONS_VOLUME_MUSIC), 60);
        startService(new Intent(this, MusicService.class).putExtra("volume", value_music));

        try {
            SoundController.Initialize(MenuActivity.this);

            // Синхронизация
            SharedPreferences.Editor ed = sPrefLevels.edit();
            SharedPreferences.Editor ed_prog = sPrefProgress.edit();
            int[] count_levels = new int[] { 0, 0, 0, 0, 0 };
            for (int i = 0; i < count_levels.length; i++)
            {
                for (int k = 0; k < 160; k++) {
                    if (k <= 3 && sPrefLevels.getInt("M" + i + "L" + k, 0) == 0)
                        ed.putInt("M" + i + "L" + k, 1);
                    if (sPrefLevels.getInt("M" + i + "L" + k, 0) == 2)
                        count_levels[i]++;
                }
                ed_prog.putInt("COMPLETED_LEVELS_M" + i, count_levels[i]);
            }

            if (sPrefProgress.getBoolean(getString(R.string.PREF_PROGRESS_IS_FIRST_ENTRY), true)) {
                ed_prog.putBoolean(getString(R.string.PREF_PROGRESS_IS_FIRST_ENTRY), false);
                for (int i = 0; i < 5; i++)
                    for (int j = 1; j <= 3; j++)
                        if (sPrefLevels.getInt("M" + i + "L" + j, 0) == 0)
                            ed.putInt("M" + i + "L" + j, 1);
            }
            ed_prog.commit();
            //ed.putBoolean(getString(R.string.PREF_LEVELS_M1), false);
            //ed.putBoolean(getString(R.string.PREF_LEVELS_M2), false);
            //ed.putBoolean(getString(R.string.PREF_LEVELS_M3), false);
            //ed.putBoolean(getString(R.string.PREF_LEVELS_M4), false);
            ed.commit();

            //ed.putBoolean(getString(R.string.PREF_LEVELS_M4), true);

            /*
            // Открытие уровней
            SharedPreferences.Editor ed_levels = sPrefLevels.edit();
            for (int i = 0; i < 160; i++)
            {
                for (int m = 0; m <= 3; m++) {
                    int levelstate = sPrefLevels.getInt("M" + m + "L" + i, 0);
                    if (levelstate == 0)
                        ed_levels.putInt("M" + m + "L" + i, 1);
                }
            }
            ed_levels.commit();
*/

            btnLevels = findViewById(R.id.menuButtonLevels);
            btnLevels.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (id_active_button == -1 || !mode_enabled[id_active_button]) return;

                    Intent intent = new Intent(MenuActivity.this, MenuLevelActivity.class)
                            .putExtra("type", id_active_button);
                    startActivity(intent);
                }
            });

            /*
            Button btnWave = (Button) findViewById(R.id.menuButtonMode0);
            btnWave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        boolean isFirstEntry = sPrefProgress.getBoolean(getString(R.string.PREF_PROGRESS_IS_FIRST_ENTRY), true);
                        if (isFirstEntry)
                        {
                            SharedPreferences.Editor ed_progress = sPrefProgress.edit();
                            ed_progress.putBoolean(getString(R.string.PREF_PROGRESS_IS_FIRST_ENTRY), false);
                            ed_progress.commit();

                            SharedPreferences.Editor ed_levels = sPrefLevels.edit();
                            for (int i = 0; i < 4; i++)
                                for (int j = 1; j <= 3; j++)
                                    ed_levels.putInt("M" + i + "L" + j, 1);
                            ed_levels.commit();
                            //startActivity(new Intent(MenuActivity.this, GameNormalActivity.class).putExtra("level_id", 0));
                            startActivity(new Intent(MenuActivity.this, HelpActivity.class).putExtra("button_event", "mode0"));
                        }
                        else startActivity(new Intent(MenuActivity.this, MenuLevelActivity.class).putExtra("type", 0));
                    }
                    catch (Exception e)
                    {
                        Button btnWave = (Button) findViewById(R.id.menuButtonExit);
                        btnWave.setText(e.getMessage());
                    }
                }
            });
*/
            //ButtonOpenActivity(R.id.menuButtonMode0, new Intent(MenuActivity.this, MenuLevelActivity.class).putExtra("type", 0));
            //ButtonOpenActivity(R.id.menuButtonMode1, new Intent(MenuActivity.this, MenuLevelActivity.class).putExtra("type", 1));
            //ButtonOpenActivity(R.id.menuButtonMode2, new Intent(MenuActivity.this, MenuLevelActivity.class).putExtra("type", 2));
            //ButtonOpenActivity(R.id.menuButtonMode3, new Intent(MenuActivity.this, MenuLevelActivity.class).putExtra("type", 3));
            //ButtonOpenActivity(R.id.menuButtonMode4, new Intent(MenuActivity.this, MenuLevelActivity.class).putExtra("type", 4));

            ButtonOpenActivity(R.id.menuButtonHelp,
                    new Intent(MenuActivity.this, HelpActivity.class).putExtra("button_event", "exit"));

            ButtonOpenActivity(R.id.menuButtonOptions,
                    new Intent(MenuActivity.this, OptionsActivity.class));

            Button btnExit = (Button) findViewById(R.id.menuButtonExit);
            btnExit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MenuActivity.this.finish();
                }
            });
        }
        catch (Exception e)
        {
            ShowMessage(e.getMessage());
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        id_active_button = -1;
        btnLevels.setVisibility(View.INVISIBLE);
        tbModeName .setText("");
        tbModeLabel.setText("");
        mode_enabled = new boolean[] { true,
            sPrefLevels.getBoolean(getString(R.string.PREF_LEVELS_M1), false),
            sPrefLevels.getBoolean(getString(R.string.PREF_LEVELS_M2), false),
            sPrefLevels.getBoolean(getString(R.string.PREF_LEVELS_M3), false),
            sPrefLevels.getBoolean(getString(R.string.PREF_LEVELS_M4), false) };


        for (int i = 0; i < mode_buttons.length; i++)
        {
            ImageButton btn = mode_buttons[i];
            mode_buttons[i].setImageResource(mode_enabled[i] ? mode_resource_images[i] : R.drawable.menu_lock_white);
            if (mode_enabled[i])
            {
                btn.setBackgroundResource(R.drawable.menu_button_level);
                btn.setImageResource(mode_resource_images[i]);
            }
            else
            {
                btn.setBackgroundResource(R.drawable.menu_button_dark1);
            }
        }

        ButtonSetMode(R.id.menuButtonMode0, 0);
        ButtonSetMode(R.id.menuButtonMode1, 1);
        ButtonSetMode(R.id.menuButtonMode2, 2);
        ButtonSetMode(R.id.menuButtonMode3, 3);
        ButtonSetMode(R.id.menuButtonMode4, 4);
        ClearButtons();

        //ButtonCheckModeEnabled(R.id.menuButtonMode1, R.string.PREF_LEVELS_M1);
        //ButtonCheckModeEnabled(R.id.menuButtonMode2, R.string.PREF_LEVELS_M2);
        //ButtonCheckModeEnabled(R.id.menuButtonMode3, R.string.PREF_LEVELS_M3);
        //ButtonCheckModeEnabled(R.id.menuButtonMode4, R.string.PREF_LEVELS_M4);

        int count_blue = sPrefProgress.getInt(getString(R.string.PREF_PROGRESS_COUNT_KEYS_BLUE), 0);
        int count_red  = sPrefProgress.getInt(getString(R.string.PREF_PROGRESS_COUNT_KEYS_RED),  0);
        tbKeysBlue.setText(String.format("%02d", count_blue));
        tbKeysRed.setText(String.format("%02d", count_red));
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        stopService(new Intent(MenuActivity.this, MusicService.class));
    }

    void ClearButtons()
    {
        for (int i = 0; i < mode_enabled.length; i++)
            mode_buttons[i].setBackgroundResource(mode_enabled[i] ?
                    R.drawable.menu_button_level :
                    R.drawable.menu_button_cleared_dark);
    }

    void ButtonSetMode(int id, final int id_mode)
    {
        final ImageButton btn = (ImageButton) findViewById(id);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    boolean is_active = (id_active_button == id_mode);
                    if (!is_active)
                    {
                        ClearButtons();
                        id_active_button = id_mode;
                        int id_name  = new int[] { R.string.menu_mode0, R.string.menu_mode1, R.string.menu_mode2, R.string.menu_mode3, R.string.menu_mode4 } [id_mode];
                        tbModeName .setText(getString(id_name));
                        if (mode_enabled[id_mode])
                        {
                            int id_res   = new int[] { R.drawable.menu_button_cleared_blue, R.drawable.menu_button_cleared_green, R.drawable.menu_button_cleared_yellow, R.drawable.menu_button_cleared_red, R.drawable.menu_button_cleared_violet } [id_mode];
                            btn.setBackgroundResource(id_res);
                            btnLevels.setVisibility(View.VISIBLE);
                            tbModeLabel.setText("Пройдено " +
                                    sPrefProgress.getInt("COMPLETED_LEVELS_M" + id_mode, 0)  +
                                    " уровней из " +
                                    LevelController.GetLevelCount(id_mode));
                        }
                        else
                        {
                            int id_label = new int[] { R.string.menu_mode0_label, R.string.menu_mode1_label, R.string.menu_mode2_label, R.string.menu_mode3_label, R.string.menu_mode4_label } [id_mode];
                            tbModeLabel.setText(getString(id_label));
                            btnLevels.setVisibility(View.INVISIBLE);
                            btn.setBackgroundResource(R.drawable.menu_button_dark1);
                        }
                    }
                    else if (mode_enabled[id_mode]) {
                        //Intent intent = new Intent(MenuActivity.this, MenuLevelActivity.class)
                        //        .putExtra("type", id_mode);
                        //startActivity(intent);
                    }

                }
                catch (Exception e)
                {
                    Button btnWave = (Button) findViewById(R.id.menuButtonExit);
                    btnWave.setText(e.getMessage());
                }
            }
        });
    }

    void ButtonOpenActivity(int id, Intent intent_)
    {
        Button btnWave = (Button) findViewById(id);
        btnWave.setTag(intent_);
        btnWave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = (Intent) v.getTag();
                    startActivity(intent);
                }
                catch (Exception e)
                {
                    Button btnWave = (Button) findViewById(R.id.menuButtonExit);
                    btnWave.setText(e.getMessage());
                }
            }
        });
    }

    void ShowMessage(String mess)
    {
        Button btn = (Button) findViewById(R.id.menuButtonExit);
        btn.setText(btn.getText() + " " + mess);
    }
}
