package ru.startandroid.c5momentalpainting;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Math.abs;


public class GameNormalActivity extends AppCompatActivity {
    static final int ARROW_TYPE_LEFT  = 0;
    static final int ARROW_TYPE_RIGHT = 1;
    static final int ARROW_TYPE_UP    = 2;
    static final int ARROW_TYPE_DOWN  = 3;
    static final int CELL_TYPE_WALL  = -1;
    static final int CELL_TYPE_CROSS  = -2;
    static final int CELL_TYPE_NULL  = -3;
    static final int ANIMATION_DURATION  = 50;
    static final int MAX_STEPS = 9999999;
    static final int MAX_TIME  = 9999999;
    static final int LAYOUT_BUTTON_MARGIN = 0;
    int LAYOUT_BUTTON_WIDTH = 10;
    int LAYOUT_BUTTON_HEIGHT = 10;
    int[] colors = { 0, R.color.colorB1, R.color.colorB2, R.color.colorB3, R.color.colorB4,
            R.color.colorB5, R.color.colorB6, R.color.colorB7, R.color.colorB8 };
    int[] images_back = { 0, 0, 0, 0, 0,
            0, R.drawable.play_a6, R.drawable.play_a7, R.drawable.play_a8};
    int[] images = { 0, R.drawable.play_b1, R.drawable.play_b2, R.drawable.play_b3, R.drawable.play_b4,
            R.drawable.play_b5, R.drawable.play_b6, R.drawable.play_b7, R.drawable.play_b8};

    Random rand = new Random();
    SharedPreferences sPrefLevels, sPrefProgress, sPrefHelp;
    ConstraintLayout gridLayout;
    LinearLayout gridResult, gridCurrent, panelRecord, llKeyBlue, llKeyRed;
    ProgressBar pbProgress;
    ImageView imgKeyBlue, imgKeyRed;
    TextView tvKeyBlue, tvKeyRed, tvLevel, tvLevelName, tvProgress, tvError;
    TextView tvRecordSteps, tvRecordTime, tvRecordStepsNew, tvRecordTimeNew, tvRecordMessage;
    Button btnRetry, btnSteps, btnTime, btnExit;
    int[] levels_locked;

    ImageButton[][] list_buttons_result, list_buttons_current;
    String[][] levels;
    int[][] current_values;
    int[][] result_values;
    int mode_game, level_id, max_level, size, count_result, count_steps, count_steps_max, count_time, count_time_max;
    boolean is_mode1 = false;
    boolean is_mode2 = false;
    boolean is_mode3 = false;
    boolean is_mode2_inverted = false;
    boolean is_mode_purity = false;
    boolean is_level_completed = false;
    boolean is_options_swipe = false;
    boolean mutex = false;
    ArrayList wall_rows = new ArrayList(), wall_columns = new ArrayList();
    ArrayList blocked_rows = new ArrayList(), blocked_columns = new ArrayList();

    // Player and Timer and Gesture thing
    //GestureDetector gesture;
    Timer mTimer;
    MyTimerTask mMyTimerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_normal);
        getSupportActionBar().hide();

        try {
            try{
                //gesture = new GestureDetector(this, new LearnGesture());
                sPrefLevels = getSharedPreferences(getString(R.string.PREF_FILE_LEVELS), MODE_PRIVATE);
                sPrefProgress = getSharedPreferences(getString(R.string.PREF_FILE_PROGRESS), MODE_PRIVATE);
                sPrefHelp = getSharedPreferences(getString(R.string.PREF_FILE_HELP), MODE_PRIVATE);
                gridLayout = findViewById(R.id.gameGridLayout);
                gridResult = findViewById(R.id.gameGridResult);
                gridCurrent = findViewById(R.id.gameGridCurrent);
                panelRecord = findViewById(R.id.gamePanelRecord);
                llKeyBlue = findViewById(R.id.gameLayoutBlueKey);
                llKeyRed = findViewById(R.id.gameLayoutRedKey);
                imgKeyBlue = findViewById(R.id.gameImageBlueKey);
                imgKeyRed = findViewById(R.id.gameImageRedKey);
                pbProgress = findViewById(R.id.gameProgressBar);
                tvError = findViewById(R.id.gameTextViewError);
                tvProgress = findViewById(R.id.gameTextViewCorrect);
                tvLevel = findViewById(R.id.gameLabelLevelID);
                tvLevelName = findViewById(R.id.gameLabelLevelName);
                tvKeyBlue = findViewById(R.id.gameTextViewBlueKey);
                tvKeyRed = findViewById(R.id.gameTextViewRedKey);
                tvRecordSteps = findViewById(R.id.gameTextViewRecordSteps);
                tvRecordTime = findViewById(R.id.gameTextViewRecordTime);
                tvRecordStepsNew = findViewById(R.id.gameTextViewRecordStepsNew);
                tvRecordTimeNew = findViewById(R.id.gameTextViewRecordTimeNew);
                tvRecordMessage = findViewById(R.id.gameTextViewRecordMessage);
                btnRetry = findViewById(R.id.gameButtonRetry);
                btnSteps = findViewById(R.id.gameButtonSteps);
                btnTime = findViewById(R.id.gameButtonTime);
                btnExit = findViewById(R.id.gameButtonExit);
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
            catch (Exception e)
            {
                ShowMessage("1_TitleButtons", e);
            }

            try{
                // Сатори Комейдзи Рекорд
                Button btnRecordRetry = findViewById(R.id.gameButtonRecordRetry);
                btnRecordRetry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LoadLevel(level_id);
                        panelRecord.setVisibility(View.INVISIBLE);
                    }
                });
                Button btnRecordExit = findViewById(R.id.gameButtonRecordExit);
                btnRecordExit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        GameNormalActivity.this.finish();
                    }
                });
                Button btnRecordNext = findViewById(R.id.gameButtonRecordNext);
                btnRecordNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LoadLevel(++level_id);
                        panelRecord.setVisibility(View.INVISIBLE);
                    }
                });
                panelRecord.setVisibility(View.INVISIBLE);

                // Кнопки снизу
                btnRetry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LoadLevel(level_id);
                    }
                });
                btnExit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        GameNormalActivity.this.finish();
                    }
                });
            }
            catch (Exception e)
            {
                ShowMessage("2_TitleButtons", e);
            }

            try{
                // Таймер
                mTimer = new Timer();
                mMyTimerTask = new MyTimerTask();
                mTimer.schedule(mMyTimerTask, 0, 1000);
            }
            catch (Exception e)
            {
                ShowMessage("5_TitleTimer", e);
            }

            try{
                // Получение номера уровня
                Intent intent = getIntent();
                int extra = intent.getIntExtra("level_id", 0);
                level_id = extra % 1000;
                mode_game = extra / 1000;

                SharedPreferences sPrefOptions = getSharedPreferences(getString(R.string.PREF_FILE_OPTIONS), MODE_PRIVATE);
                is_options_swipe = sPrefOptions.getBoolean(getString(R.string.PREF_OPTIONS_IS_SWIPE), false);
                is_mode_purity = sPrefOptions.getBoolean(getString(R.string.PREF_OPTIONS_IS_PURITY_MODE), false);
            }
            catch (Exception e)
            {
                ShowMessage("6_TitleEntent", e);
            }

            try{
                // Поиск заблокированных уровней
                String sPrefStart = "M" + mode_game + "L";
                levels_locked = new int[] { 0, 0, 0 };
                int id = 0;
                for (int i = 1; i <= 150; i++)
                {
                    if (sPrefLevels.getInt(sPrefStart + i, 0) != 2)
                    {
                        levels_locked[id] = i;
                        if (++id == 3) break;
                    }
                }
                ShowMessage("Levels::", levels_locked[0] + " " + levels_locked[1] + " " + levels_locked[2]);
            }
            catch (Exception e)
            {
                ShowMessage("7_TitleGetLevelID", e);
            }

            String name = "*KIND_OF_NULL";
            try
            {
                // Выбор уровней
                max_level = LevelController.GetLevelCount(mode_game);
                levels    = LevelController.GetLevelpack(mode_game);

                // Фон
                ConstraintLayout layout = (ConstraintLayout)findViewById(R.id.game_main_layout);
                layout.setBackgroundResource(new int[] {
                        R.drawable.back6,
                        R.drawable.back2,
                        R.drawable.back3,
                        R.drawable.back4,
                        R.drawable.back5
                } [mode_game]);
            }
            catch (Exception e)
            {
                ShowMessage("8_TitleGameMode", e);
            }

            try
            {
                // Получение характеристик уровня
                int level_id_tmp = level_id;
                while (levels[level_id_tmp][0].charAt(0) != 'S') level_id_tmp--;
                int size = Integer.parseInt(levels[level_id_tmp][0].substring(1));

                // Название уровня всегда должно идти вторым в списке после размера уровня (если оно есть)
                level_id_tmp = level_id;
                while (name == "*KIND_OF_NULL") {
                    for (String code : levels[level_id_tmp])
                        if (code.charAt(0) == 'N')
                            name = code.length() > 1 ? code.substring(1) : "";
                    level_id_tmp--;
                }
            }
            catch (Exception e)
            {
                ShowMessage("9_TitleGetLevelName", e);
            }

            try
            {
                SetName(name);
                LoadLevel(level_id);
            }
            catch (Exception e)
            {
                ShowMessage("0_YumeNikki", e);
            }
        } catch (Exception ex) {
            ShowMessage("Title", ex);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    void SetSize(int value) {
        try {
            //ShowMessage("SetSize:" + value);
            if (value == 0) return;

            size = value;
            gridCurrent.removeAllViews();
            gridResult.removeAllViews();

            // Values arrays
            list_buttons_result = new ImageButton[size + 2][size + 2];
            list_buttons_current = new ImageButton[size + 2][size + 2];
            current_values = new int[size][size];
            result_values = new int[size][size];

            // Set HEIGHT of buttons
            Point screenSize = new Point();
            getWindowManager().getDefaultDisplay().getSize(screenSize);
            int margin_horiz = new int[] { 100, 80, 50, 30, 20, 10, 0, 0, 0, 0, 0, 0, 0, 0, 0 }[size - 2];  // 2 -> 16
            if (is_options_swipe) {
                LinearLayout.LayoutParams newLayoutParams = (LinearLayout.LayoutParams) gridLayout.getLayoutParams();
                newLayoutParams.leftMargin = margin_horiz;
                newLayoutParams.rightMargin = margin_horiz;
                gridLayout.setLayoutParams(newLayoutParams);
            }
            LAYOUT_BUTTON_HEIGHT = !is_options_swipe ?
                    (screenSize.x / (size + 2)) :
                    ((screenSize.x - 2 * margin_horiz) / size);

            if (is_options_swipe) CreateFieldSwipe();
            else               CreateFieldArrows();
        }
        catch (Exception ex)
        {
            ShowMessage("SetSize", ex);
        }
    }

    void LoadLevel(int value){
        try {
            is_level_completed = false;
            level_id = value;

            HelpController.LoadLevelFromGameActivity(this, sPrefHelp, mode_game, value);

            // Режимы игры
            String mode_code = levels[value][0];
            is_mode1 = (mode_game == 1 || mode_game == 4 || mode_code == "M1");
            is_mode2 = (mode_game == 2 ||                   mode_code == "M2");
            is_mode3 = (mode_game == 3 || mode_game == 4 || mode_code == "M3");
            is_mode2_inverted = mode_game == 2 && (level_id >= 20 && level_id <= 39  || level_id == 51 || level_id == 59);
            if (is_mode_purity)
            {
                is_mode2 = is_mode2 || mode_game == 3 || (mode_game == 0 && level_id % 3 == 0);
                is_mode3 = is_mode3 || mode_game == 0 || mode_game == 1 || mode_game == 2;
                is_mode2_inverted = is_mode2_inverted || (mode_game == 0 && level_id % 12 == 0);
            }

            ClearField();
            for (String code : levels[value]) {
                String[] code_mass = code.split("_");
                if (code_mass.length == 1)
                {
                    // Специальные коды
                    String v = code.substring(1);
                    switch (code.charAt(0))
                    {
                        case 'S': SetSize(Integer.parseInt(v)); break;
                        case 'N': SetName(v); break;
                        case 'M': break;
                        case 'C':
                        case 'c':
                        case 'С':   // Русские, мать их, буквы С
                        case 'с':
                            count_steps_max = Integer.parseInt(v);
                            llKeyBlue.setVisibility(View.VISIBLE);
                            tvKeyBlue.setText(count_steps_max + " steps");
                            tvKeyBlue.setTextColor(!sPrefProgress.getBoolean("M" + mode_game + "L" + (level_id + 1) + "KEYBLUE", false)
                                    ? ContextCompat.getColor(this, R.color.colorRoyalBlue) : Color.GRAY);

                            /*
                            imgKeyBlue.setVisibility(
                                    !sPrefProgress.getBoolean("M" + mode_game + "L" + (level_id + 1) + "KEYBLUE", false) ?
                                            View.VISIBLE :
                                            View.INVISIBLE);
                                            */
                            break;
                        case 'T':
                        case 't':
                            count_time_max = Integer.parseInt(v);
                            llKeyRed.setVisibility(View.VISIBLE);
                            tvKeyRed.setText(count_time_max + " seconds");
                            tvKeyRed.setTextColor(!sPrefProgress.getBoolean("M" + mode_game + "L" + (level_id + 1) + "KEYRED", false)
                                    ? Color.RED : Color.GRAY);
                            break;
                        case 'A': for (char ch : code.substring(1).toCharArray()) GenerateRandomCellCurrent(Integer.parseInt(String.valueOf(ch))); break;
                        case 'B': for (char ch : code.substring(1).toCharArray()) GenerateRandomCellResult (Integer.parseInt(String.valueOf(ch))); break;
                        default: for (char ch : code.toCharArray()) GenerateRandomCell(Integer.parseInt(String.valueOf(ch))); break;
                    }
                }
                else
                {
                    // Массивы для массовой расстановки ячеек по местам
                    // A100_201_302_403_522, B144_243_342_441_522
                    String[] cell_mass = code.substring(1).split("_");
                    switch (code.charAt(0)) {
                        case 'A': for (String cell : cell_mass) current_values[getch(cell, 1)][getch(cell, 2)] = getch(cell, 0); break;
                        case 'B': for (String cell : cell_mass)  result_values[getch(cell, 1)][getch(cell, 2)] = getch(cell, 0); break;
                        case 'W': for (String cell : cell_mass) {
                            int p2 = getch(cell, 0), p3 = getch(cell, 1);
                            current_values[p2][p3] = result_values[p2][p3] = CELL_TYPE_WALL;
                            //SetValue(p2, p3, CELL_TYPE_WALL, CELL_TYPE_WALL);
                        }
                            CheckWallCells();
                            break;
                    }
                }
            }

            int count_correct = GetCountCorrect();
            count_result = GetCountResult();
            btnRetry.setVisibility(View.VISIBLE);
            btnExit.setText("Выход");
            tvLevel.setText("Level " + (value + 1));
            btnSteps.setText("" + count_steps);
            btnTime .setText("00:00");
            btnSteps.setTextColor(count_steps_max == MAX_STEPS ? Color.WHITE : ContextCompat.getColor(this, R.color.game_achievement));
            btnTime .setTextColor(count_time_max  == MAX_TIME  ? Color.WHITE : ContextCompat.getColor(this, R.color.game_achievement));
            pbProgress.setSecondaryProgress(0);
            pbProgress.setMax(count_result);
            pbProgress.setSecondaryProgress(count_correct);
            tvProgress.setText(count_correct + " / " + count_result);
            for (int i = 0; i < size; i++)
                for (int j = 0; j < size; j++)
                    SetValue(i, j, current_values[i][j], result_values[i][j]);

            // Проверки
            if (is_mode1) CheckCrossCells();  // Это тоже костыль, для проверки появления стрелок на момент начала игры. Тут весь код - один сплошной костыль
            if (is_mode3) CheckParalyzeCells();
        } catch (Exception ex) {
            ShowMessage("LoadLevel", ex);
        }
    }

    void CheckEndOfLevel() {
        try
        {
            if (CheckValues())
            {
                is_level_completed = true;

                SharedPreferences.Editor ed = sPrefLevels.edit();
                for (int i = 0; i < 3; i++) {
                    if (levels_locked[i] == level_id + 1) {
                        int max = levels_locked[0];
                        if (levels_locked[1] > max) max = levels_locked[1];
                        if (levels_locked[2] > max) max = levels_locked[2];

                        levels_locked[i] = max + 1;
                        if (sPrefLevels.getInt("M" + mode_game + "L" + (max + 1), 0) == 0)
                            ed.putInt("M" + mode_game + "L" + (max + 1), 1);
                    }
                }

                // (KOMEIJI) RECORDS
                String level_code = "M" + mode_game + "L" + (level_id + 1);
                int record_time = sPrefLevels.getInt(level_code + "_TIME", MAX_TIME);
                if (count_time < record_time) {
                    ed.putInt(level_code + "_TIME", count_time);
                    tvRecordTimeNew.setText("Новый рекорд!");
                    record_time = count_time;
                }
                else tvRecordStepsNew.setText("");
                int record_steps = sPrefLevels.getInt(level_code + "_STEPS", MAX_STEPS);
                if (count_steps < record_steps) {
                    ed.putInt(level_code + "_STEPS", count_steps);
                    tvRecordStepsNew.setText("Новый рекорд!");
                    record_steps = count_steps;
                }
                else tvRecordTimeNew.setText("");
                panelRecord.setVisibility(View.VISIBLE);
                tvRecordSteps.setText("" + record_steps);
                tvRecordTime .setText("" + record_time);
                tvRecordMessage.setText("");


                // COUNT COMPLETED LEVELS
                if (sPrefLevels.getInt(level_code, 0) != 2)
                {
                    int count_completed_levels = sPrefProgress.getInt("COMPLETED_LEVELS_M" + mode_game, 0);

                    SharedPreferences.Editor ed_prog = sPrefProgress.edit();
                    ed_prog.putInt("COMPLETED_LEVELS_M" + mode_game, count_completed_levels + 1);
                    ed_prog.commit();
                }
                ed.putInt(level_code, 2);
                //ShowMessage("Levels::", levels_locked[0] + " " + levels_locked[1] + " " + levels_locked[2]);


                // Открытие режимов
                int keys_count = sPrefProgress.getInt(getString(R.string.PREF_PROGRESS_COUNT_KEYS_BLUE), 0) +
                        sPrefProgress.getInt(getString(R.string.PREF_PROGRESS_COUNT_KEYS_RED), 0);
                if (!sPrefLevels.getBoolean(getString(R.string.PREF_LEVELS_M1), false) &&
                        sPrefProgress.getInt("COMPLETED_LEVELS_M0", 0) >= 22 &&
                        keys_count >= 2)
                    ed.putBoolean(getString(R.string.PREF_LEVELS_M1), true);
                if (!sPrefLevels.getBoolean(getString(R.string.PREF_LEVELS_M3), false) &&
                        sPrefProgress.getInt("COMPLETED_LEVELS_M0", 0) >= 44 &&
                        sPrefProgress.getInt("COMPLETED_LEVELS_M1", 0) >= 22 &&
                        keys_count >= 8)
                    ed.putBoolean(getString(R.string.PREF_LEVELS_M3), true);
                if (!sPrefLevels.getBoolean(getString(R.string.PREF_LEVELS_M2), false) &&
                        sPrefProgress.getInt("COMPLETED_LEVELS_M0", 0) >= 66 &&
                        sPrefProgress.getInt("COMPLETED_LEVELS_M1", 0) >= 44 &&
                        sPrefProgress.getInt("COMPLETED_LEVELS_M3", 0) >= 22 &&
                        keys_count >= 24)
                    ed.putBoolean(getString(R.string.PREF_LEVELS_M2), true);
                if (!sPrefLevels.getBoolean(getString(R.string.PREF_LEVELS_M4), false) &&
                        sPrefProgress.getInt("COMPLETED_LEVELS_M0", 0) >= 88 &&
                        sPrefProgress.getInt("COMPLETED_LEVELS_M1", 0) >= 66 &&
                        sPrefProgress.getInt("COMPLETED_LEVELS_M3", 0) >= 44 &&
                        sPrefProgress.getInt("COMPLETED_LEVELS_M2", 0) >= 22 &&
                        keys_count >= 48)
                    ed.putBoolean(getString(R.string.PREF_LEVELS_M4), true);


                // BLUE KEY
                if (count_steps_max != MAX_STEPS && count_steps <= count_steps_max)
                {
                    String code = "M" + mode_game + "L" + (level_id + 1) + "KEYBLUE";
                    if (!sPrefProgress.getBoolean(code, false))
                    {
                        String s = getString(R.string.PREF_PROGRESS_COUNT_KEYS_BLUE);
                        tvRecordMessage.setText("Получен ключ!");
                        int blue_keys_count = sPrefProgress.getInt(s, 0);
                        SharedPreferences.Editor ed_progress = sPrefProgress.edit();
                        ed_progress.putInt(s, blue_keys_count + 1);
                        ed_progress.putBoolean(code, true);
                        ed_progress.commit();
                    }
                }

                // RED KEY
                if (count_time_max != MAX_TIME && count_time <= count_time_max)
                {
                    String code = "M" + mode_game + "L" + (level_id + 1) + "KEYRED";
                    if (!sPrefProgress.getBoolean(code, false))
                    {
                        String s = getString(R.string.PREF_PROGRESS_COUNT_KEYS_RED);
                        tvRecordMessage.setText("Получен ключ!");
                        int red_keys_count = sPrefProgress.getInt(s, 0);
                        SharedPreferences.Editor ed_progress = sPrefProgress.edit();
                        ed_progress.putInt(s, red_keys_count + 1);
                        ed_progress.putBoolean(code, true);
                        ed_progress.commit();
                    }
                }
                ed.commit();

                // Выход
                if (level_id > max_level)
                    GameNormalActivity.this.finish();

                //btnRetry.setVisibility(View.INVISIBLE);
                //btnExit.setText("Дальше");

                /*
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        LoadLevel(++level_id);
                    }
                }, 1000);
                */
            }
        }
        catch (Exception e){
            ShowMessage("CheckEndOfLevel", e);
        }
    }

    //region Basic game things
    void ClearField() {
        try {
            count_time = 0;
            count_time_max = MAX_TIME;
            count_steps = 0;
            count_steps_max = MAX_STEPS;
            llKeyBlue.setVisibility(View.INVISIBLE);
            llKeyRed.setVisibility(View.INVISIBLE);
            wall_rows.clear();
            wall_columns.clear();
            blocked_rows.clear();
            blocked_columns.clear();
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    current_values[i][j] = 0;
                    result_values[i][j] = 0;
                }
                if (!is_options_swipe)
                {
                    EnableRow(i, true);
                    EnableColumn(i, true);
                }
            }
        } catch (Exception ex) {
            ShowMessage("ClearField", ex);
        }
    }

    boolean CheckValues() {
        int count_correct = GetCountCorrect();
        tvProgress.setText(count_correct + " / " + count_result);
        pbProgress.setSecondaryProgress(count_correct);
        return count_correct == count_result;
    }

    int GetCountCorrect() {
        int cc = 0;
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                if (current_values[i][j] > 0 && current_values[i][j] == result_values[i][j])
                    cc++;
        return cc;
    }

    int GetCountResult() {
        int cc = 0;
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                if (result_values[i][j] > 0)
                    cc++;
        return cc;
    }

    void SetValueCurrent(int i, int j, int val, boolean is_change_image) {
        current_values[i][j] = val;
        if (is_change_image && !is_mode2_inverted)
            GetButton(i, j).setImageResource(images[val]);
    }

    void SetValue(int i, int j, int current_val, int result_val) {
        ImageButton btn_current = GetButton(i, j);
        ImageButton btn_result = GetResultButton(i, j);
        switch (result_val)
        {
            // Wall (invisible)
            case CELL_TYPE_NULL:
                current_values[i][j] = result_values[i][j] = CELL_TYPE_NULL;
                btn_current.setBackgroundColor(0);
                btn_current.setImageResource(0);
                btn_result.setBackgroundColor(0);
                btn_result.setImageResource(0);
                EnableRow(i, false);
                EnableColumn(j, false);
                break;

            // Wall
            case CELL_TYPE_WALL:
                current_values[i][j] = result_values[i][j] = CELL_TYPE_WALL;
                btn_result.setBackgroundResource(R.drawable.play_wall);
                btn_result.setImageResource(0);
                btn_current.setBackgroundColor(0);
                btn_current.setImageResource(0);
                EnableRow(i, false);
                EnableColumn(j, false);
                break;

            // Cross
            case CELL_TYPE_CROSS: break;

            // Normal cell
            default:
                result_values[i][j] = result_val;

                if (result_val == 0 || (is_mode2 && !is_mode2_inverted))
                    btn_result.setBackgroundColor(0);
                else if (images_back[result_val] != 0)
                    btn_result.setBackgroundResource(images_back[result_val]);
                else
                    btn_result.setBackgroundColor(getResources().getColor(colors[result_val]));
                SetValueCurrent(i, j, current_val, true);
                GetButton(i, j).setBackgroundResource(R.drawable.play_current_background);
                break;
        }
    }
    //endregion

    //region Moves
    boolean MoveCode(int code) {
        //ShowMessage("MoveCode:" + code);
        return Move(code / 100, code % 100);
    }

    boolean Move(int type, int row_column) {
        //if (is_level_completed || count_steps >= count_steps_max || count_time >= count_time_max) return;
        if (is_level_completed) return false;

        if ((type == ARROW_TYPE_UP || type == ARROW_TYPE_DOWN) && blocked_columns.contains(row_column) ||
                (type == ARROW_TYPE_LEFT || type == ARROW_TYPE_RIGHT) && blocked_rows.contains(row_column))
        {
            SoundController.PlaySound(3);
            return false;
        }

        // Сохранение кол-ва правильных
        int count_correct = GetCountCorrect();

        // Ход
        switch (type)
        {
            case ARROW_TYPE_UP:    MoveColumnUp(row_column);   break;
            case ARROW_TYPE_DOWN:  MoveColumnDown(row_column); break;
            case ARROW_TYPE_LEFT:  MoveRowLeft(row_column);    break;
            case ARROW_TYPE_RIGHT: MoveRowRight(row_column);   break;
        }

        // Звук хода
        int del_correct = GetCountCorrect() - count_correct;
        if      (del_correct > 0) SoundController.PlaySound(1);
        else if (del_correct < 0) SoundController.PlaySound(2);
        else                      SoundController.PlaySound(0);

        // Обновление кол-ва шагов
        count_steps++;
        btnSteps.setText("" + count_steps);
        if (count_time > count_time_max)
            btnSteps.setTextColor(Color.WHITE);

        if (is_mode1) CheckCrossCells();
        if (is_mode3) CheckParalyzeCells();

        // Метод инвертирования направления стрелки для строки или столбца
        // Использовался для режима двухцветной кошки, но не сработал
        //ShowMessage("Apple");
        //if (type == ARROW_TYPE_UP || type == ARROW_TYPE_DOWN)
        //    InverseColumn(row);
        //else
        //    InverseRow(row);

        CheckEndOfLevel();
        return true;
    }

    void MoveRowLeft(int id_row) {
        int temp = current_values[id_row][0];
        SetAnimation(id_row, 0, 0, 1 - size, current_values[id_row][1], false);
        for (int j = 1; j < size - 1; j++)
            SetAnimation(id_row, j, 0, 1, current_values[id_row][j + 1], false);
        SetAnimation(id_row, size - 1, 0, 1, temp, true);
    }

    void MoveRowRight(int id_row) {
        int temp = current_values[id_row][size - 1];
        SetAnimation(id_row, size - 1, 0, size - 1, current_values[id_row][size - 2], false);
        for (int i = size - 2; i > 0; i--)
            SetAnimation(id_row, i, 0, -1, current_values[id_row][i - 1], false);
        SetAnimation(id_row, 0, 0 , -1, temp, true);
    }

    void MoveColumnUp(int id_column) {
        int temp = current_values[0][id_column];
        SetAnimation(0, id_column, 1 - size, 0, current_values[1][id_column], false);
        for (int j = 1; j < size - 1; j++)
            SetAnimation(j, id_column, 1, 0, current_values[j + 1][id_column], false);
        SetAnimation(size - 1, id_column, 1, 0, temp, true);
    }

    void MoveColumnDown(int id_column) {
        int temp = current_values[size - 1][id_column];
        SetAnimation(size - 1, id_column, size - 1, 0, current_values[size - 2][id_column], false);
        for (int i = size - 1; i > 0; i--)
            SetAnimation(i, id_column, -1, 0, current_values[i - 1][id_column], false);
        SetAnimation(0, id_column, -1, 0, temp, true);
    }

    void SetAnimation(final int row, final int column, int row_del, int column_del, int new_value, final boolean is_next_move_animate) {
        final int new_current_value = (new_value != -1) ? new_value : current_values[row + row_del][column + column_del];
        mutex = true;
        SetValueCurrent(row, column, new_current_value, false);

        ImageButton btn = GetButton(row, column);
        TranslateAnimation anim = new TranslateAnimation(0, -LAYOUT_BUTTON_HEIGHT * column_del, 0, -LAYOUT_BUTTON_HEIGHT * row_del);
        anim.setDuration(ANIMATION_DURATION);
        anim.setAnimationListener(new TranslateAnimation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }
            @Override
            public void onAnimationRepeat(Animation animation) { }
            @Override
            public void onAnimationEnd(Animation animation) {
                SetValueCurrent(row, column, new_current_value, true);

                // Анимация следующего хода (только при свайпе)
                if (is_next_move_animate) {
                    if (is_options_swipe && ++animIndex < touchIndex)
                        MoveCode(touchCode[animIndex]);
                    else mutex = false;
                }
            }
        });
        btn.clearAnimation();
        btn.setAnimation(anim);
    }

    //endregion

    //region Generate Random Field and ModeChecks
    void GenerateRandomCellCurrent(int val) {
        int id1, id2;
        do {
            id1 = rand.nextInt(size);
            id2 = rand.nextInt(size);
        }
        while (current_values[id1][id2] != 0);
        current_values[id1][id2] = val;
    }

    void GenerateRandomCellResult(int val) {
        int id1, id2;
        do {
            id1 = rand.nextInt(size);
            id2 = rand.nextInt(size);
        }
        while (result_values[id1][id2] != 0);
        result_values[id1][id2] = val;
    }

    void GenerateRandomCell(int val) {
        GenerateRandomCellCurrent(val);
        GenerateRandomCellResult(val);
    }

    void CheckWallCells() {
        wall_rows.clear();
        wall_columns.clear();
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                if (result_values[i][j] == CELL_TYPE_WALL)
                {
                    if (!wall_rows.contains(i))    wall_rows.add(i);
                    if (!wall_columns.contains(j)) wall_columns.add(j);
                }

        blocked_rows.clear();
        blocked_rows.addAll(wall_rows);
        blocked_columns.clear();
        blocked_columns.addAll(wall_columns);

        for (int i = 0; i < wall_rows.size(); i++)
            for (int j = 0; j < wall_columns.size(); j++) {
                int ki = (int)wall_rows.get(i);
                int kj = (int)wall_columns.get(j);
                if (result_values[ki][kj] != CELL_TYPE_WALL) {
                    current_values[ki][kj] = result_values[ki][kj] = CELL_TYPE_NULL;
                }
            }

            /*
        String mess = "Rows:";
        for (int i = 0; i < blocked_rows.size(); i++)
            mess += blocked_rows.get(i);
        mess += " Columns:";
        for (int i = 0; i < blocked_columns.size(); i++)
            mess += blocked_columns.get(i);
        ShowMessage(mess);
        */
    }

    void CheckCrossCells() {
        for (int k = 0; k < size; k++) {
            EnableRow(k, false);
            EnableColumn(k, false);
        }

        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                if (current_values[i][j] == 8)
                {
                    if (!wall_rows.contains(i))    EnableRow(i, true);
                    if (!wall_columns.contains(j)) EnableColumn(j, true);
                }

        /*
        String mess = "Rows:";
        for (int i = 0; i < blocked_rows.size(); i++)
            mess += blocked_rows.get(i);
        mess += " Columns:";
        for (int i = 0; i < blocked_columns.size(); i++)
            mess += blocked_columns.get(i);
        ShowMessage(mess);
        */

    }

    void CheckParalyzeCells() {
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++) {
                if (wall_rows.contains(i) && wall_columns.contains(j)) continue;
                if (current_values[i][j] > 0 && current_values[i][j] == result_values[i][j]) {
                    if (!wall_rows.contains(i)) {
                        wall_rows.add(i);
                        for (Object k : wall_columns) {
                            GetButton(i, (int)k).setBackgroundResource(0);
                            //GetButton(i, (int)k).setImageResource(0);
                            //GetResultButton(i, (int) k).setBackgroundResource(0);
                            GetResultButton(i, (int) k).setImageResource(0);
                        }
                    }
                    if (!wall_columns.contains(j)) {
                        wall_columns.add(j);
                        for (Object k : wall_rows) {
                            GetButton((int)k, j).setBackgroundResource(0);
                            //GetButton((int)k, j).setImageResource(0);
                            //GetResultButton((int)k, j).setBackgroundResource(0);
                            GetResultButton((int)k, j).setImageResource(0);
                        }
                    }
                    EnableRow(i, false);
                    EnableColumn(j, false);
                }
            }
/*
        String mess = "Rows:";
        for (int i = 0; i < blocked_rows.size(); i++)
            mess += blocked_rows.get(i);
        mess += " Columns:";
        for (int i = 0; i < blocked_columns.size(); i++)
            mess += blocked_columns.get(i);
        ShowMessage(mess);
        */
    }

    void EnableRow(int row, boolean is_enabled) {
        //ShowMessage("EnableRow: " + row + " -- " + is_enabled);
        if (!is_enabled && !blocked_rows.contains(row)) blocked_rows.add(row);
        else if (is_enabled) blocked_rows.remove((Object)row);

        if (!is_options_swipe)
        {
            int v = is_enabled ? View.VISIBLE : View.INVISIBLE;
            list_buttons_result[row + 1][0].setVisibility(v);
            list_buttons_result[row + 1][size + 1].setVisibility(v);
            if (!is_enabled)
            {
                list_buttons_result[row + 1][0].clearAnimation();
                list_buttons_result[row + 1][size + 1].clearAnimation();
            }
        }
    }

    void EnableColumn(int column, boolean is_enabled) {
        //ShowMessage("EnableColumn: " + column + " -- " + is_enabled);
        if (!is_enabled && !blocked_columns.contains(column)) blocked_columns.add(column);
        else if (is_enabled) blocked_columns.remove((Object)column);

        if (!is_options_swipe) {
            int v = is_enabled ? View.VISIBLE : View.INVISIBLE;
            list_buttons_result[0][column + 1].setVisibility(v);
            list_buttons_result[size + 1][column + 1].setVisibility(v);
            if (!is_enabled) {
                list_buttons_result[0][column + 1].clearAnimation();
                list_buttons_result[size + 1][column + 1].clearAnimation();
            }
        }
    }
    //endregion

    //region Create Field
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
        lparams.height = LAYOUT_BUTTON_HEIGHT;
        lparams.leftMargin = LAYOUT_BUTTON_MARGIN;
        lparams.rightMargin = LAYOUT_BUTTON_MARGIN;
        lparams.topMargin = LAYOUT_BUTTON_MARGIN;
        lparams.bottomMargin = LAYOUT_BUTTON_MARGIN;
        return lparams;
    }

    void CreateFieldArrows() {
        // 1. Result Field
        // Up row
        LinearLayout rf = GetLayoutParamsRow();
        CreateInvisibleButton(rf, list_buttons_result, 0, 0);
        for (int j = 0; j < size; j++)
            CreateArrowButton(rf, list_buttons_result, 0, j + 1, ARROW_TYPE_UP);
        CreateInvisibleButton(rf, list_buttons_result, 0, size + 1);
        gridResult.addView(rf);

        // Play rows
        for (int i = 0; i < size; i++) {
            LinearLayout r = GetLayoutParamsRow();
            CreateArrowButton(r, list_buttons_result, i + 1, 0, ARROW_TYPE_LEFT);
            for (int j = 0; j < size; j++)
                CreatePlayButton(r, list_buttons_result, i + 1, j + 1);
            CreateArrowButton(r, list_buttons_result,i + 1, size + 1, ARROW_TYPE_RIGHT);
            gridResult.addView(r);
        }

        // Down row
        LinearLayout rl = GetLayoutParamsRow();
        CreateInvisibleButton(rl, list_buttons_result, size + 1, 0);
        for (int j = 0; j < size; j++)
            CreateArrowButton(rl, list_buttons_result, size + 1, j + 1, ARROW_TYPE_DOWN);
        CreateInvisibleButton(rl, list_buttons_result, size + 1, size + 1);
        gridResult.addView(rl);


        // 2. Current Field
        // Up row
        LinearLayout cf = GetLayoutParamsRow();
        CreateInvisibleButton(cf, list_buttons_current, 0, size + 1);
        gridCurrent.addView(cf);

        // Play rows
        for (int i = 0; i < size; i++) {
            LinearLayout r = GetLayoutParamsRow();
            CreateInvisibleButton(r, list_buttons_current, i + 1, 0);
            for (int j = 0; j < size; j++)
                CreatePlayButton(r, list_buttons_current, i + 1, j + 1);
            CreateInvisibleButton(r, list_buttons_current, i + 1, size + 1);
            gridCurrent.addView(r);
        }

        // Down row
        LinearLayout cl = GetLayoutParamsRow();
        CreateInvisibleButton(cl, list_buttons_current, size + 1, 0);
        gridCurrent.addView(cl);
    }

    void CreateFieldSwipe() {
        // 1. Result Field
        for (int i = 0; i < size; i++) {
            LinearLayout r = GetLayoutParamsRow();
            for (int j = 0; j < size; j++)
                CreatePlayButton(r, list_buttons_result, i + 1, j + 1);
            gridResult.addView(r);
        }

        // 2. Current Field
        for (int i = 0; i < size; i++) {
            LinearLayout r = GetLayoutParamsRow();
            for (int j = 0; j < size; j++)
                CreatePlayButton(r, list_buttons_current, i + 1, j + 1);
            gridCurrent.addView(r);
        }
    }

    void CreateInvisibleButton(LinearLayout layout, ImageButton[][] list_buttons, int i, int j) {
        ImageButton btn = new ImageButton(this);
        btn.setVisibility(View.INVISIBLE);
        layout.addView(btn, GetLayoutParamsButton());
        list_buttons[i][j] = btn;
    }

    void CreateArrowButton(LinearLayout layout, ImageButton[][] list_buttons, int i, int j, int type) {
        ImageButton btn = new ImageButton(this);
        int anim_dx = 0, anim_dy = 0, anim_duration = 0;        // Animation things
        int row = 0, id_image = 0;
        switch (type)
        {
            case ARROW_TYPE_UP:    id_image = R.drawable.game_swipe_up;    row = j; anim_dy = -1; anim_duration =  j; break;
            case ARROW_TYPE_DOWN:  id_image = R.drawable.game_swipe_down;  row = j; anim_dy =  1; anim_duration = -j + size * 3; break;
            case ARROW_TYPE_LEFT:  id_image = R.drawable.game_swipe_left;  row = i; anim_dx = -1; anim_duration = -i + size * 4; break;
            case ARROW_TYPE_RIGHT: id_image = R.drawable.game_swipe_right; row = i; anim_dx =  1; anim_duration =  i + size * 1; break;
        }

        TranslateAnimation anim = new TranslateAnimation(400 * anim_dx, 0,400 * anim_dy, 0);
        anim.setDuration(60 * anim_duration);
        btn.setAnimation(anim);
        btn.setBackgroundResource(id_image);
        btn.setTag(type * 100 + row - 1);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int tag = (int)((ImageButton)v).getTag();
                    Move(tag / 100, tag % 100);
                } catch (Exception e){
                    ShowMessage("MoveError", e);
                }
            }
        });

        list_buttons[i][j] = btn;
        layout.addView(btn, GetLayoutParamsButton());
    }

    void CreatePlayButton(LinearLayout layout, ImageButton[][] list_buttons, int i, int j) {
        ImageButton btn = new ImageButton(this);
        btn.setTag(new int[] { i - 1, j - 1 });
        if (is_options_swipe) {
            // SWIPE
            btn.setOnTouchListener(touchListener);
            if (list_buttons == list_buttons_result || size > 7) {
                btn.setAdjustViewBounds(true);
                btn.setPadding(0, 0, 0, 0);
            }
        }
        else
        {
            // ARROWS
            if (list_buttons == list_buttons_result || size > 7) {
                btn.setAdjustViewBounds(true);
                btn.setPadding(0, 0, 0, 0);
            }
        }
        layout.addView(btn, GetLayoutParamsButton());
        list_buttons[i][j] = btn;
    }
    //endregion

    //region Gesture - Touch things
    float touchX, touchY;
    int touchIndex, animIndex, touchRow, touchColumn;
    int[] touchCode = new int[99];      // 99 перемещений точно хватит. Я уверен, что даже 20ти хватило бы за глазаz
    View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (!is_options_swipe || mutex || !(v instanceof ImageButton)) return false;

            float x = event.getX();
            float y = event.getY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: // нажатие
                    int[] touchViewTag = (int[])v.getTag();
                    touchX = x;
                    touchY = y;
                    touchRow = touchViewTag[0];
                    touchColumn = touchViewTag[1];
                    touchIndex = 0;
                    animIndex = 0;
                    break;
                case MotionEvent.ACTION_MOVE: // движение
                    // Left and right
                    if (abs(x - touchX) > LAYOUT_BUTTON_HEIGHT)
                    {
                        ImageButton btn = GetResultButton(touchRow, touchColumn);
                        if (x > touchX) {
                            btn.setImageResource(R.drawable.game_swipe_right);

                            touchX += LAYOUT_BUTTON_HEIGHT;
                            touchCode[touchIndex++] = 100 * ARROW_TYPE_RIGHT + touchRow;
                            if (++touchColumn >= size) touchColumn -= size;
                        } else {
                            btn.setImageResource(R.drawable.game_swipe_left);

                            touchX -= LAYOUT_BUTTON_HEIGHT;
                            touchCode[touchIndex++] = 100 * ARROW_TYPE_LEFT + touchRow;
                            if (--touchColumn < 0) touchColumn += size;
                        }
                    }

                    // Up and down
                    if (abs(y - touchY) > LAYOUT_BUTTON_HEIGHT)
                    {
                        ImageButton btn = GetResultButton(touchRow, touchColumn);
                        if (y > touchY) {
                            btn.setImageResource(R.drawable.game_swipe_down);

                            touchY += LAYOUT_BUTTON_HEIGHT;
                            touchCode[touchIndex++] = 100 * ARROW_TYPE_DOWN + touchColumn;
                            if (++touchRow >= size) touchRow -= size;
                        } else {
                            btn.setImageResource(R.drawable.game_swipe_up);

                            touchY -= LAYOUT_BUTTON_HEIGHT;
                            touchCode[touchIndex++] = 100 * ARROW_TYPE_UP + touchColumn;
                            if (--touchRow < 0) touchRow += size;
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP: // отпускание
                case MotionEvent.ACTION_CANCEL:
                    // Отмена отрисовки
                    for (int i = 0; i < size; i++)
                        for (int j = 0; j < size; j++)
                            GetResultButton(i, j).setImageResource(0);

                    // Движение
                    animIndex = 0;
                    MoveCode(touchCode[0]);

                    /*
                    for (int i = 0; i < touchIndex; i++)
                        Move(touchCode[i] / 100, touchCode[i] % 100);

                    /*
                    float dx = x - touchX;
                    float dy = y - touchY;
                    int[] tag = (int[])v.getTag();
                    if (dy >  3 * abs(dx)) Move(ARROW_TYPE_DOWN,  tag[1]);
                    if (dy < -3 * abs(dx)) Move(ARROW_TYPE_UP,    tag[1]);
                    if (dx >  3 * abs(dy)) Move(ARROW_TYPE_RIGHT, tag[0]);
                    if (dx < -3 * abs(dy)) Move(ARROW_TYPE_LEFT,  tag[0]);
                    */
                    break;
            }
            return true;

            //return gesture.onTouchEvent(event);
        }
    };
/*
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.gesture.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    class LearnGesture extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return true;
        }
        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
            try
            {
                float dx = event2.getX() - event1.getX();
                float dy = event2.getY() - event1.getY();
                float dd = dy / dx;
                if (dy >  3 * abs(dx)) MoveColumnDown(0);
                if (dy < -3 * abs(dx)) MoveColumnUp(0);
                if (dx >  3 * abs(dy)) MoveRowRight(0);
                if (dx < -3 * abs(dy)) MoveRowLeft(0);
                ShowMessage("Move " + dd);
                return true;
            }
            catch (Exception e)
            {
                ShowMessage("MoveError", e);
                return false;
            }
        }
    }
*/
    //endregion

    //region Another (not anime)
    void ShowMessage(String mess) {
        //tvError.setText(tvError.getText() + " " + mess);
    }

    void ShowMessage(String title, String mess) {
        ShowMessage(title + ": " + mess);
    }

    void ShowMessage(String title, Exception e) {
        ShowMessage(title + ": " + e.toString());
    }

    void SetName(String value) {
        tvLevelName.setText(value);
    }

    ImageButton GetButton(int row, int column)
    {
        return list_buttons_current[row + 1][column + 1];
    }

    ImageButton GetResultButton(int row, int column)    {
        return list_buttons_result[row + 1][column + 1];
    }

    int getch(char ch) {
        return Integer.parseInt(String.valueOf(ch));
    }
    int getch(String str, int index) {
        return Integer.parseInt(str.substring(index, index + 1));
    }

    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            try {
                if (is_level_completed) return;

                count_time++;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int time = count_time;
                        int minutes = (int)(time / 60);
                        int seconds = (int)(time % 60);
                        btnTime.setText(String.format("%02d:%02d", minutes, seconds));
                        if (count_time > count_time_max)
                            btnTime.setTextColor(Color.WHITE);
                    }
                });
            }
            catch (Exception e) {
                ShowMessage("TimerError", e);
            }
        }
    }
    //endregion

}