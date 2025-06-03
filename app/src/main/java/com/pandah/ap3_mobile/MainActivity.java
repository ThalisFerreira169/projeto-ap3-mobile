package com.pandah.ap3_mobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "MainActivity";

    // Sons
    private SoundPool soundPool;
    private int clickSoundId;
    private boolean soundLoaded = false;
    private MediaPlayer bgMusic;

    // DBHelper
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar DBHelper
        dbHelper = new DBHelper(this);

        // Ler nome do jogador das SharedPreferences
        SharedPreferences prefs = getSharedPreferences("player_data", MODE_PRIVATE);
        String playerName = prefs.getString("player_name", null);

        if (playerName != null)
        {
            // Salvar no banco SQLite
            dbHelper.addPlayer(playerName);

            // Ler todos os jogadores no banco e mostrar no log
            Cursor cursor = dbHelper.getAllPlayers();

            int idIndex = cursor.getColumnIndexOrThrow(DBHelper.COLUMN_ID);
            int nameIndex = cursor.getColumnIndexOrThrow(DBHelper.COLUMN_NAME);

            while (cursor.moveToNext())
            {
                int id = cursor.getInt(idIndex);
                String name = cursor.getString(nameIndex);
                Log.d(TAG, "Player in DB: ID=" + id + ", Name=" + name);
            }
            cursor.close();
        }

        ImageView anim_logo = findViewById(R.id.game_logo);
        ImageView anim_button = findViewById(R.id.play_game_button);

        Animation anim_1 = AnimationUtils.loadAnimation(this, R.anim.shake_anim);
        anim_logo.startAnimation(anim_1);
        Animation anim_2 = AnimationUtils.loadAnimation(this, R.anim.scale_anim);
        anim_button.startAnimation(anim_2);

        // Iniciar música de fundo
        bgMusic = MediaPlayer.create(this, R.raw.game_menu_song);
        bgMusic.setLooping(true);
        bgMusic.setVolume(0.9f, 0.9f);
        bgMusic.start();

        // Inicializar SoundPool
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(1)
                .setAudioAttributes(audioAttributes)
                .build();

        clickSoundId = soundPool.load(this, R.raw.click_button, 1);
        soundPool.setOnLoadCompleteListener((soundPool, sampleId, status) ->
        {
            if (status == 0) soundLoaded = true;
        });

        anim_button.setOnClickListener(v ->
        {
            if (soundLoaded)
            {
                soundPool.play(clickSoundId, 1f, 1f, 1, 0, 1f);
            }

            Intent intent = new Intent(MainActivity.this, CutsceneActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if (bgMusic != null && bgMusic.isPlaying())
        {
            bgMusic.pause();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (bgMusic != null)
        {
            bgMusic.start();
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (bgMusic != null)
        {
            bgMusic.release();
            bgMusic = null;
        }
        if (soundPool != null)
        {
            soundPool.release();
            soundPool = null;
        }
    }
}
