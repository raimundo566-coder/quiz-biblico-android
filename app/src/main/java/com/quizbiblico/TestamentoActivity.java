package com.quizbiblico;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class TestamentoActivity extends TelaBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testamento);

        Button botaoAT = findViewById(R.id.botaoAT);
        Button botaoNT = findViewById(R.id.botaoNT);
        Button botaoVoltar = findViewById(R.id.botaoVoltarTestamento);

        botaoAT.setOnClickListener(v -> abrirNiveis("AT"));
        botaoNT.setOnClickListener(v -> abrirNiveis("NT"));
        botaoVoltar.setOnClickListener(v -> finish());
    }

    private void abrirNiveis(String testamento) {
        Intent intent = new Intent(this, NiveisActivity.class);
        intent.putExtra("tipo", "TESTAMENTO");
        intent.putExtra("valor", testamento);
        startActivity(intent);
    }
}