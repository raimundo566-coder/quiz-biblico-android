package com.quizbiblico;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.quizbiblico.modelo.Pergunta;
import com.quizbiblico.solo.Partida;
import com.quizbiblico.solo.SoloService;

public class JogoActivity extends TelaBase {

    private static final int BRANCO = 0xFFF0E2C4;   // mesmo tom do bg_alternativa;
    private static final int VERDE = 0xFF2E7D32;
    private static final int VERMELHO = 0xFFC62828;

    private Partida partida;
    private SoloService solo;

    private TextView textoProgresso;
    private TextView textoPergunta;
    private Button[] botoesAlternativa;
    private Button botaoProxima;

    private Pergunta perguntaRespondida;
    private boolean respondida = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jogo);

        partida = app().getPartidaAtual();
        solo = app().getSolo();

        if (partida == null) {
            Toast.makeText(this, "Nenhuma partida em andamento.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        textoProgresso = findViewById(R.id.textoProgresso);
        textoPergunta = findViewById(R.id.textoPergunta);
        botaoProxima = findViewById(R.id.botaoProxima);

        botoesAlternativa = new Button[]{
                findViewById(R.id.botaoA),
                findViewById(R.id.botaoB),
                findViewById(R.id.botaoC),
                findViewById(R.id.botaoD)
        };

        for (int i = 0; i < botoesAlternativa.length; i++) {
            int indice = i;
            botoesAlternativa[i].setOnClickListener(v -> responder(indice));
        }

        botaoProxima.setOnClickListener(v -> avancar());

        Button botaoSair = findViewById(R.id.botaoSairJogo);
        botaoSair.setOnClickListener(v -> confirmarSaida());

        mostrarPerguntaAtual();
    }

    private void confirmarSaida() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Sair da partida?")
                .setMessage("As perguntas ja respondidas ficam salvas no seu progresso. So a partida atual sera encerrada.")
                .setPositiveButton("Sair", (dialog, which) -> {
                    app().setPartidaAtual(null);
                    finish();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarPerguntaAtual() {
        respondida = false;
        Pergunta pergunta = partida.atual();

        textoProgresso.setText("Pergunta " + partida.getNumeroAtual() + " de " + partida.getTotal()
                + "  |  Acertos: " + partida.getAcertos());
        textoPergunta.setText(pergunta.getTexto());

        String[] alternativas = pergunta.getAlternativas();
        for (int i = 0; i < botoesAlternativa.length; i++) {
            botoesAlternativa[i].setText(alternativas[i]);
            botoesAlternativa[i].setEnabled(true);
            botoesAlternativa[i].setBackgroundTintList(ColorStateList.valueOf(BRANCO));
        }

        botaoProxima.setVisibility(View.GONE);
    }

    private void responder(int indice) {
        if (respondida) {
            return;
        }
        respondida = true;

        perguntaRespondida = partida.atual();
        boolean acertou = solo.responder(partida, indice);

        for (Button botao : botoesAlternativa) {
            botao.setEnabled(false);
        }

        int correta = perguntaRespondida.getRespostaCorreta();
        botoesAlternativa[correta].setBackgroundTintList(ColorStateList.valueOf(VERDE));

        if (!acertou) {
            botoesAlternativa[indice].setBackgroundTintList(ColorStateList.valueOf(VERMELHO));
        }

        botaoProxima.setVisibility(View.VISIBLE);
    }

    private void avancar() {
        if (partida.temProxima()) {
            mostrarPerguntaAtual();
        } else {
            finalizarPartida();
        }
    }

    private void finalizarPartida() {
        int acertos = partida.getAcertos();
        int total = partida.getTotal();
        double percentual = partida.percentualAcerto();
        int nivel = partida.getNivel();
        String tipo = partida.getTipo().name();
        String valor = partida.getValor();

        app().setPartidaAtual(null);

        Intent intent = new Intent(this, ResultadoActivity.class);
        intent.putExtra("acertos", acertos);
        intent.putExtra("total", total);
        intent.putExtra("percentual", percentual);
        intent.putExtra("nivel", nivel);
        intent.putExtra("tipo", tipo);
        intent.putExtra("valor", valor);
        startActivity(intent);
        finish();
    }
}