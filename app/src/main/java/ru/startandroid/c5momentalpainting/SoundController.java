package ru.startandroid.c5momentalpainting;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Vyacheslav on 08.04.2019.
 */

public class SoundController {
    // Перенести сюда все, что связано со звуками ил NormalActivity
    // И спросить у Димы, пропадает ли иногда звук
    static final int SOUND_BUFFER = 6;

    static MediaPlayer[] players;
    static int player_id;
    static boolean is_initialized = false;

    public static void Initialize(Context context)
    {
        if (is_initialized) return;

        // Звуки
        int[] sign_sounds = { R.raw.click, R.raw.click_up, R.raw.click_down, R.raw.click_error };
        player_id = 0;
        players = new MediaPlayer[SOUND_BUFFER * sign_sounds.length];
        for (int k = 0; k < SOUND_BUFFER; k++)
            for (int i = 0; i < sign_sounds.length; i++)
                players[k + i * SOUND_BUFFER] = MediaPlayer.create(context, sign_sounds[i]);

        is_initialized = true;
    }

    public static void SetVolume(float volume_sounds)
    {
        // Громкость
        //SharedPreferences sPrefOptions = getSharedPreferences(getString(R.string.PREF_FILE_OPTIONS), MODE_PRIVATE);
        //float volume_sounds = 0.01f * sPrefOptions.getInt(getString(R.string.PREF_OPTIONS_VOLUME_SOUNDS), 100);


        for (MediaPlayer mp : players) mp.setVolume(volume_sounds, volume_sounds);
    }

    public static void PlaySound(int sound_id)    {
        players[sound_id * SOUND_BUFFER + player_id].start();
        if (++player_id >= SOUND_BUFFER)
            player_id = 0;
    }

    public static void Destroy()
    {
        for (MediaPlayer mp : players)
            if (mp != null)
            {
                mp.release();
                mp = null;
            }
        is_initialized = false;
    }

}
