package com.quizbiblico;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.content.Intent;

import java.util.List;

public class LivrosActivity extends TelaBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_livros);

        ListView listaLivros = findViewById(R.id.listaLivros);
        List<String> livros = app().getBanco().livrosDisponiveis();

        ArrayAdapter<String> adaptador = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, livros);
        listaLivros.setAdapter(adaptador);

        listaLivros.setOnItemClickListener((parent, view, posicao, id) -> {
            String livroEscolhido = livros.get(posicao);
            Intent intent = new Intent(this, NiveisActivity.class);
            intent.putExtra("tipo", "LIVRO");
            intent.putExtra("valor", livroEscolhido);
            startActivity(intent);
        });
    }
}