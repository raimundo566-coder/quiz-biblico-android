package com.quizbiblico;

import android.animation.AnimatorInflater;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import com.quizbiblico.modelo.Nivel;
import com.quizbiblico.modelo.TipoFiltro;
import com.quizbiblico.solo.Partida;
import com.quizbiblico.solo.SoloService;


public class NiveisActivity extends TelaBase {

    private TipoFiltro tipo;
    private String valor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_niveis);

        String tipoTexto = getIntent().getStringExtra("tipo");
        tipo = TipoFiltro.valueOf(tipoTexto);
        valor = getIntent().getStringExtra("valor");

        TextView textoTitulo = findViewById(R.id.textoTituloNiveis);
        textoTitulo.setText(tipo == TipoFiltro.GERAL ? "Níveis (Geral)" : "Níveis (" + valor + ")");

        LinearLayout container = findViewById(R.id.containerNiveis);
        SoloService solo = app().getSolo();

        float densidade = getResources().getDisplayMetrics().density;
        int alturaBotao = (int) (70 * densidade);
        int margemBaixo = (int) (8 * densidade);
        for (Nivel nivel : Nivel.values()) {
            AppCompatButton botaoNivel = new AppCompatButton(this);

            boolean liberado = solo.podeJogar(nivel.getCodigo());
            int restam = solo.restantes(nivel.getCodigo(), tipo, valor);
            String cadeado = liberado ? "" : " [BLOQUEADO]";

            String textoCompleto = nivel.getCodigo() + " - " + nivel.getRotulo() + cadeado;
            SpannableString textoComTamanhos = new SpannableString(textoCompleto);
            textoComTamanhos.setSpan(new RelativeSizeSpan(0.6f), textoCompleto.length() - cadeado.length(), textoCompleto.length(), 0);
            botaoNivel.setText(textoComTamanhos);
            botaoNivel.setBackgroundResource(R.drawable.botao_couro);
            botaoNivel.setTextColor(ContextCompat.getColor(this, R.color.qb_dourado));
            botaoNivel.setAllCaps(false);
            botaoNivel.setBackgroundResource(R.drawable.botao_couro);
            botaoNivel.setTextColor(ContextCompat.getColor(this, R.color.qb_dourado));
            botaoNivel.setAllCaps(false);
            botaoNivel.setTextSize(17);
            botaoNivel.setStateListAnimator(
                    AnimatorInflater.loadStateListAnimator(this, R.animator.botao_pressionado));
            botaoNivel.setStateListAnimator(
                    AnimatorInflater.loadStateListAnimator(this, R.animator.botao_pressionado));

            LinearLayout.LayoutParams parametros = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, alturaBotao);
            parametros.bottomMargin = margemBaixo;
            botaoNivel.setLayoutParams(parametros);

            botaoNivel.setOnClickListener(comSom(v -> {
                if (solo.podeJogar(nivel.getCodigo())
                        && solo.restantes(nivel.getCodigo(), tipo, valor) == 0) {
                    confirmarZerar(nivel);
                    return;
                }
                try {
                    Partida partida = solo.iniciar(nivel.getCodigo(), tipo, valor);
                    app().setPartidaAtual(partida);
                    startActivity(new Intent(this, JogoActivity.class));
                } catch (IllegalStateException e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }));

            container.addView(botaoNivel);
        }

        AppCompatButton botaoVoltar = findViewById(R.id.botaoVoltarNiveis);
        botaoVoltar.setOnClickListener(comSom(v -> finish()));
    }

    private void confirmarZerar(Nivel nivel) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Recomeçar o nível " + nivel.getRotulo() + "?")
                .setMessage("Você já respondeu todas as perguntas deste nível aqui. "
                        + "Para jogar de novo, o progresso dele será zerado. "
                        + "Os outros níveis e livros não mudam.")
                .setPositiveButton("Zerar", (dialog, which) -> {
                    app().getSolo().zerar(nivel.getCodigo(), tipo, valor);
                    app().salvar();
                    Toast.makeText(this, "Progresso zerado. Toque no nível para jogar.",
                            Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}