package ru.startandroid.c5momentalpainting;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

public class MusicService extends Service {
    private static final String TAG = "MusicService";
    MediaPlayer player;

    public MusicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        player = MediaPlayer.create(this, R.raw.music01);
        player.setLooping(true); // зацикливаем
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        float volume = 0.01f * intent.getIntExtra("volume", 100);
        player.setVolume(volume, volume);
        //player.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        player.stop();
        player.release();
    }

    public void SetVolume(float volume)
    {
        player.setVolume(volume, volume);
    }
}
