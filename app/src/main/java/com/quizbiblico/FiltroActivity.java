package com.quizbiblico;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import android.content.Intent;

public class FiltroActivity extends TelaBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtro);

        Button botaoPorLivro = findViewById(R.id.botaoPorLivro);
        Button botaoPorTestamento = findViewById(R.id.botaoPorTestamento);
        Button botaoGeral = findViewById(R.id.botaoGeral);
        Button botaoVoltar = findViewById(R.id.botaoVoltar);

        botaoPorLivro.setOnClickListener(v ->
                startActivity(new Intent(this, LivrosActivity.class)));

        botaoPorTestamento.setOnClickListener(v ->
                startActivity(new Intent(this, TestamentoActivity.class)));

        botaoGeral.setOnClickListener(v -> {
            Intent intent = new Intent(this, NiveisActivity.class);
            intent.putExtra("tipo", "GERAL");
            startActivity(intent);
        });

        botaoVoltar.setOnClickListener(v -> finish());
    }
}