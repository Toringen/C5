package ru.startandroid.c5momentalpainting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

public class OptionsActivity extends AppCompatActivity {

    SharedPreferences sPref;
    SeekBar sbSounds, sbMusic;
    TextView tvSounds, tvMusic;
    CheckBox cbSwipe, cbModePurity;
    int value_sounds, value_music;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        getSupportActionBar().hide();

        sPref = getSharedPreferences(getString(R.string.PREF_FILE_OPTIONS), MODE_PRIVATE);
        value_sounds = sPref.getInt(getString(R.string.PREF_OPTIONS_VOLUME_SOUNDS), 80);
        value_music = sPref.getInt(getString(R.string.PREF_OPTIONS_VOLUME_MUSIC), 60);

        Button btnExit = (Button) findViewById(R.id.optionsButtonExit);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OptionsActivity.this.finish();
            }
        });

        sbSounds = (SeekBar) findViewById(R.id.optionsSeekBarSounds);
        tvSounds = (TextView) findViewById(R.id.optionsTextViewSoundsValue);
        sbSounds.setProgress(value_sounds);
        tvSounds.setText(String.valueOf(value_sounds));
        sbSounds.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvSounds.setText(String.valueOf(progress));
                value_sounds = progress;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        sbMusic = (SeekBar) findViewById(R.id.optionsSeekBarMusic);
        tvMusic = (TextView) findViewById(R.id.optionsTextViewMusicValue);
        sbMusic.setProgress(value_music);
        tvMusic.setText(String.valueOf(value_music));
        sbMusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvMusic.setText(String.valueOf(progress));
                value_music = progress;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        cbSwipe = findViewById(R.id.optionsCbSwipe);
        cbSwipe.setChecked(!sPref.getBoolean(getString(R.string.PREF_OPTIONS_IS_SWIPE), false));

        cbModePurity = findViewById(R.id.optionsCbModePurity);
        cbModePurity.setChecked(sPref.getBoolean(getString(R.string.PREF_OPTIONS_IS_PURITY_MODE), false));

        sbSounds.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.optionsSeekBarSounds), PorterDuff.Mode.MULTIPLY));
        sbMusic.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.optionsSeekBarMusic), PorterDuff.Mode.MULTIPLY));
    }

    @Override
    protected void onDestroy()
    {
        SharedPreferences.Editor ed = sPref.edit();
        ed.putInt(getString(R.string.PREF_OPTIONS_VOLUME_SOUNDS), value_sounds);
        ed.putInt(getString(R.string.PREF_OPTIONS_VOLUME_MUSIC), value_music);
        ed.putBoolean(getString(R.string.PREF_OPTIONS_IS_SWIPE), !cbSwipe.isChecked());
        ed.putBoolean(getString(R.string.PREF_OPTIONS_IS_PURITY_MODE), cbModePurity.isChecked());
        ed.commit();
        super.onDestroy();
    }
}
