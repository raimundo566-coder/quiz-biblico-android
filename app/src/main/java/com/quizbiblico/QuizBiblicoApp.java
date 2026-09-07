package com.quizbiblico;

import android.app.Application;

import com.quizbiblico.dados.BancoDePerguntas;
import com.quizbiblico.modelo.Pergunta;
import com.quizbiblico.modelo.PersistenciaUsuario;
import com.quizbiblico.modelo.Usuario;
import com.quizbiblico.progresso.PersistenciaProgresso;
import com.quizbiblico.progresso.Progresso;
import com.quizbiblico.solo.Partida;
import com.quizbiblico.solo.SoloService;

import java.io.IOException;
import java.util.List;

public class QuizBiblicoApp extends Application {

    private PersistenciaProgresso arquivistaProgresso;
    private PersistenciaUsuario arquivistaUsuario;
    private Progresso progresso;
    private Usuario usuario;
    private BancoDePerguntas banco;
    private SoloService solo;
    private String erroDeCarregamento;
    private Partida partidaAtual;

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            List<Pergunta> perguntas = CarregadorDeAcervo.carregarTudo(this);
            banco = new BancoDePerguntas(perguntas);

            String caminhoProgresso = getFilesDir().getAbsolutePath() + "/progresso.json";
            arquivistaProgresso = new PersistenciaProgresso(caminhoProgresso);
            progresso = arquivistaProgresso.carregar();

            String caminhoUsuario = getFilesDir().getAbsolutePath() + "/usuario.json";
            arquivistaUsuario = new PersistenciaUsuario(caminhoUsuario);
            usuario = arquivistaUsuario.carregar();

            solo = new SoloService(banco, progresso, usuario);

        } catch (IOException e) {
            erroDeCarregamento = e.getMessage();
        }
    }

    public void salvar() {
        if (progresso == null || usuario == null) {
            return;
        }
        try {
            arquivistaProgresso.salvar(progresso);
            arquivistaUsuario.salvar(usuario);
        } catch (IOException e) {
            // sem tela pra avisar aqui
        }
    }

    public BancoDePerguntas getBanco() { return banco; }
    public Progresso getProgresso() { return progresso; }
    public Usuario getUsuario() { return usuario; }
    public SoloService getSolo() { return solo; }
    public String getErroDeCarregamento() { return erroDeCarregamento; }

    public Partida getPartidaAtual() { return partidaAtual; }
    public void setPartidaAtual(Partida partidaAtual) { this.partidaAtual = partidaAtual; }
}