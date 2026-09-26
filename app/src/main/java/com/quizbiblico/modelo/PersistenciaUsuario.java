package com.quizbiblico.modelo;

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

public class PersistenciaUsuario {

    private static final String CAMINHO_PADRAO = "dados/usuario.json";

    private final File arquivo;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public PersistenciaUsuario() {
        this(CAMINHO_PADRAO);
    }

    public PersistenciaUsuario(String caminho) {
        this.arquivo = new File(caminho);
    }

    public void salvar(Usuario usuario) throws IOException {
        File pasta = arquivo.getParentFile();
        if (pasta != null) {
            pasta.mkdirs();   // cria a pasta se faltar; se ja existe, nao faz nada
        }

        // OutputStreamWriter com UTF_8 e o que garante que os acentos sejam gravados certo
        try (Writer escritor = new OutputStreamWriter(
                new FileOutputStream(arquivo), StandardCharsets.UTF_8)) {
            gson.toJson(usuario, escritor);
        }
    }

    public Usuario carregar() throws IOException {
        if (!arquivo.exists()) {
            return new Usuario();
        }

        try (Reader leitor = new InputStreamReader(
                new FileInputStream(arquivo), StandardCharsets.UTF_8)) {
            Usuario lido = gson.fromJson(leitor, Usuario.class);
            return (lido == null) ? new Usuario() : lido;
        } catch (JsonSyntaxException e) {
            throw new IOException("Usuario corrompido: " + arquivo.getAbsolutePath(), e);
        }
    }

    public File getArquivo() {
        return arquivo;
    }
}