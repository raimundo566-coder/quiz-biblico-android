package com.quizbiblico.progresso;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

public class PersistenciaProgresso {

    private static final String CAMINHO_PADRAO = "dados/progresso.json";

    private final File arquivo;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public PersistenciaProgresso() {
        this(CAMINHO_PADRAO);
    }

    public PersistenciaProgresso(String caminho) {
        this.arquivo = new File(caminho);
    }

    public void salvar(Progresso progresso) throws IOException {
        File pasta = arquivo.getParentFile();
        if (pasta != null) {
            pasta.mkdirs();   // cria a pasta se faltar; se ja existe, nao faz nada
        }

        // OutputStreamWriter com UTF_8 e o que garante que os acentos sejam gravados certo
        try (Writer escritor = new OutputStreamWriter(
                new FileOutputStream(arquivo), StandardCharsets.UTF_8)) {
            gson.toJson(progresso, escritor);
        }
    }

    public Progresso carregar() throws IOException {
        if (!arquivo.exists()) {
            return new Progresso();
        }

        try (Reader leitor = new InputStreamReader(
                new FileInputStream(arquivo), StandardCharsets.UTF_8)) {
            Progresso lido = gson.fromJson(leitor, Progresso.class);
            return (lido == null) ? new Progresso() : lido;
        } catch (JsonSyntaxException e) {
            throw new IOException("Progresso corrompido: " + arquivo.getAbsolutePath(), e);
        }
    }

    public File getArquivo() {
        return arquivo;
    }
}
