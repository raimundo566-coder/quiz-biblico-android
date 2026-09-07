package com.quizbiblico;

import android.content.Context;
import android.content.res.AssetManager;

import com.quizbiblico.dados.LeitorCSV;
import com.quizbiblico.modelo.Pergunta;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CarregadorDeAcervo {

    public static List<Pergunta> carregarTudo(Context context) throws IOException {
        AssetManager assets = context.getAssets();
        String[] arquivos = assets.list("dados");
        Arrays.sort(arquivos);

        LeitorCSV leitor = new LeitorCSV();
        List<Pergunta> todas = new ArrayList<>();

        for (String nome : arquivos) {
            if (!nome.endsWith(".csv")) {
                continue;
            }

            InputStream fluxo = assets.open("dados/" + nome);
            Reader leitorDeTexto = new InputStreamReader(fluxo, StandardCharsets.UTF_8);
            todas.addAll(leitor.carregar(leitorDeTexto, nome));
        }

        return todas;
    }
}