package com.quizbiblico;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends TelaBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textoStatus = findViewById(R.id.textoStatus);
        Button botaoSolo = findViewById(R.id.botaoSolo);
        Button botaoGincana = findViewById(R.id.botaoGincana);
        Button botaoNiveis = findViewById(R.id.botaoNiveis);
        Button botaoSair = findViewById(R.id.botaoSair);

        if (app().getErroDeCarregamento() != null) {
            textoStatus.setText("Falhou: " + app().getErroDeCarregamento());
        } else {
            textoStatus.setText(app().getBanco().total() + " perguntas, "
                    + app().getBanco().livrosDisponiveis().size() + " livros carregados");
        }

        botaoSolo.setOnClickListener(v ->
                startActivity(new Intent(this, FiltroActivity.class)));

        botaoGincana.setOnClickListener(v ->
                Toast.makeText(this, "Em breve!", Toast.LENGTH_SHORT).show());

        botaoNiveis.setOnClickListener(v ->
                startActivity(new Intent(this, NiveisVipActivity.class)));

        botaoSair.setOnClickListener(v -> finish());
    }
}