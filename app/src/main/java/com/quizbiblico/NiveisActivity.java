package com.quizbiblico;

import android.animation.AnimatorInflater;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

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
        textoTitulo.setText(tipo == TipoFiltro.GERAL ? "Niveis (Geral)" : "Niveis (" + valor + ")");

        LinearLayout container = findViewById(R.id.containerNiveis);
        SoloService solo = app().getSolo();

        float densidade = getResources().getDisplayMetrics().density;
        int alturaBotao = (int) (70 * densidade);
        int margemBaixo = (int) (8 * densidade);
        for (Nivel nivel : Nivel.values()) {
            AppCompatButton botaoNivel = new AppCompatButton(this);

            boolean liberado = solo.podeJogar(nivel.getCodigo());
            int restam = solo.restantes(nivel.getCodigo(), tipo, valor);
            String cadeado = liberado ? "" : "[BLOQUEADO] ";

            botaoNivel.setText(cadeado + nivel.getCodigo() + " - " + nivel.getRotulo());
            botaoNivel.setBackgroundResource(R.drawable.botao_couro);
            botaoNivel.setTextColor(ContextCompat.getColor(this, R.color.qb_dourado));
            botaoNivel.setAllCaps(false);
            botaoNivel.setStateListAnimator(
                    AnimatorInflater.loadStateListAnimator(this, R.animator.botao_pressionado));

            LinearLayout.LayoutParams parametros = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, alturaBotao);
            parametros.bottomMargin = margemBaixo;
            botaoNivel.setLayoutParams(parametros);

            botaoNivel.setOnClickListener(v -> {
                try {
                    Partida partida = solo.iniciar(nivel.getCodigo(), tipo, valor);
                    app().setPartidaAtual(partida);
                    startActivity(new Intent(this, JogoActivity.class));
                } catch (IllegalStateException e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

            container.addView(botaoNivel);
        }

        AppCompatButton botaoVoltar = findViewById(R.id.botaoVoltarNiveis);
        botaoVoltar.setOnClickListener(v -> finish());
    }
}