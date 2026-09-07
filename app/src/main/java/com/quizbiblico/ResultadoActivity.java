package com.quizbiblico;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.quizbiblico.modelo.TipoFiltro;
import com.quizbiblico.solo.Partida;
import com.quizbiblico.solo.SoloService;

public class ResultadoActivity extends TelaBase {

    private int nivel;
    private TipoFiltro tipo;
    private String valor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado);

        int acertos = getIntent().getIntExtra("acertos", 0);
        int total = getIntent().getIntExtra("total", 0);
        double percentual = getIntent().getDoubleExtra("percentual", 0.0);

        nivel = getIntent().getIntExtra("nivel", 1);
        tipo = TipoFiltro.valueOf(getIntent().getStringExtra("tipo"));
        valor = getIntent().getStringExtra("valor");

        TextView textoResumo = findViewById(R.id.textoResumo);
        textoResumo.setText("Voce acertou " + acertos + " de " + total
                + "\n(" + String.format("%.0f", percentual) + "%)");

        Button botaoJogarNovamente = findViewById(R.id.botaoJogarNovamente);
        botaoJogarNovamente.setOnClickListener(v -> {
            SoloService solo = app().getSolo();
            try {
                Partida partida = solo.iniciar(nivel, tipo, valor);
                app().setPartidaAtual(partida);
                startActivity(new Intent(this, JogoActivity.class));
                finish();
            } catch (IllegalStateException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        Button botaoVoltar = findViewById(R.id.botaoVoltarResultado);
        botaoVoltar.setOnClickListener(v -> finish());
    }
}