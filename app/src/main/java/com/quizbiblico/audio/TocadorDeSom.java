package com.quizbiblico.audio;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;

import com.quizbiblico.R;

public class TocadorDeSom {

    private final SoundPool soundPool;
    private final int idAbertura;
    private final int idAcerto;
    private final int idErro;
    private final int idBotao;
    private final boolean[] carregado = new boolean[4];

    public TocadorDeSom(Context context) {
        AudioAttributes atributos = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(3)
                .setAudioAttributes(atributos)
                .build();

        soundPool.setOnLoadCompleteListener((pool, sampleId, status) -> {
            if (status == 0) {
                marcarCarregado(sampleId);
            }
        });

        idAbertura = soundPool.load(context, R.raw.som_abertura, 1);
        idAcerto = soundPool.load(context, R.raw.som_acerto, 1);
        idErro = soundPool.load(context, R.raw.som_erro, 1);
        idBotao = soundPool.load(context, R.raw.som_botao, 1);
    }

    private void marcarCarregado(int sampleId) {
        if (sampleId == idAbertura) carregado[0] = true;
        else if (sampleId == idAcerto) carregado[1] = true;
        else if (sampleId == idErro) carregado[2] = true;
        else if (sampleId == idBotao) carregado[3] = true;
    }

    public void tocarAbertura() { tocar(idAbertura, 0); }
    public void tocarAcerto() { tocar(idAcerto, 1); }
    public void tocarErro() { tocar(idErro, 2); }
    public void tocarBotao() { tocar(idBotao, 3); }

    private void tocar(int id, int indice) {
        if (carregado[indice]) {
            soundPool.play(id, 1f, 1f, 1, 0, 1f);
        }
    }
}