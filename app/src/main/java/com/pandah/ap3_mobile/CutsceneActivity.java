package com.pandah.ap3_mobile;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class CutsceneActivity extends Activity
{
    // Elementos XML
    private TextView cutsceneText;
    private EditText nameInput;
    private ImageView startButton;

    // Elementos cutscene
    private int phraseIndex = 0;
    private int charIndex = 0;
    private final long delay = 100;
    private final Handler handler = new Handler();
    private boolean isTyping = false;

    // SoundPool para efeitos sonoros
    private SoundPool soundPool;
    private int soundIdLetter;
    private boolean soundLoaded = false;

    // Frases da cutscene
    private final String[] phrases = {
            "Em um mundo onde as notas baixas ameaçam todos os estudantes...",
            "Onde a cada final de semestre a possibilidade de que as lendas se confirmem...",
            "E que a NAF venha ameaçar as férias de todos...",
            "Surge um herói...",
            "Você!"
    };

    // Função para digitação com som
    private void typeNextChar(String phrase, Runnable onFinish)
    {
        if (charIndex < phrase.length())
        {
            isTyping = true;
            cutsceneText.setText(phrase.substring(0, charIndex + 1));

            if (soundLoaded)
            {
                soundPool.play(soundIdLetter, 1f, 1f, 1, 0, 1f);
            }

            charIndex++;
            handler.postDelayed(() -> typeNextChar(phrase, onFinish), delay);
        }
        else
        {
            isTyping = false;
            if (onFinish != null) onFinish.run();
        }
    }

    // Mostra a próxima frase
    private void showNextPhrase()
    {
        if (phraseIndex < phrases.length)
        {
            charIndex = 0;
            typeNextChar(phrases[phraseIndex], null);
        }
        else
        {
            showEnterName();
        }
    }

    // Digitar nome do usuário
    private void showEnterName()
    {
        charIndex = 0;
        String finalMessage = "Digite seu nome:";
        typeNextChar(finalMessage, () ->
        {
            nameInput.setVisibility(View.VISIBLE);
            startButton.setVisibility(View.VISIBLE);
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cutscene);

        // Referências XML
        cutsceneText = findViewById(R.id.cutsceneText);
        nameInput = findViewById(R.id.nameInput);
        startButton = findViewById(R.id.startButton);

        nameInput.setVisibility(View.GONE);
        startButton.setVisibility(View.GONE);

        // Configura SoundPool
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(1)
                .setAudioAttributes(audioAttributes)
                .build();

        // Carrega som da letra
        soundIdLetter = soundPool.load(this, R.raw.type_sound, 1);

        soundPool.setOnLoadCompleteListener((soundPool1, sampleId, status) ->
        {
            if (status == 0)
                soundLoaded = true;
        });

        // Avança frases ao clicar na tela
        View rootView = findViewById(android.R.id.content);
        rootView.setOnClickListener(v ->
        {
            if (!isTyping)
            {
                phraseIndex++;
                showNextPhrase();
            }
        });

        startButton.setOnClickListener(v ->
        {
            String nome = nameInput.getText().toString().trim();
            if (!nome.isEmpty())
            {
                // Salvar nome do jogador nas SharedPreferences
                getSharedPreferences("player_data", MODE_PRIVATE)
                        .edit()
                        .putString("player_name", nome)
                        .apply();

                // Abrir PlayerMenu
                Intent intent = new Intent(CutsceneActivity.this, PlayerMenu.class);
                intent.putExtra("playerName", nome);
                startActivity(intent);
                finish();
            }
            else
            {
                nameInput.setError("Digite um nome!");
            }
        });

        showNextPhrase();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (soundPool != null)
        {
            soundPool.release();
            soundPool = null;
        }
    }
}
