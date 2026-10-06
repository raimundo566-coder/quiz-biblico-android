package com.quizbiblico;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.quizbiblico.loja.Loja;
import com.quizbiblico.modelo.Nivel;
import com.quizbiblico.modelo.Usuario;

public class NiveisVipActivity extends TelaBase {

    private Usuario usuario;
    private Loja loja;
    private LinearLayout container;
    private Button botaoVip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_niveis_vip);

        usuario = app().getUsuario();
        loja = app().getLoja();

        container = findViewById(R.id.containerNiveisVip);
        botaoVip = findViewById(R.id.botaoTornarVip);

        botaoVip.setOnClickListener(comSom(v -> comprarOuAvisar(Loja.VIP)));

        Button botaoVoltar = findViewById(R.id.botaoVoltarNiveisVip);
        botaoVoltar.setOnClickListener(comSom(v -> finish()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (loja != null) {
            loja.setAoMudarTela(() -> runOnUiThread(this::montarLista));
        }
        montarLista();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (loja != null) {
            loja.setAoMudarTela(null);
        }
    }

    private void comprarOuAvisar(String id) {
        if (loja == null || !loja.comprar(this, id)) {
            Toast.makeText(this, "Loja indisponível no momento. Verifique a internet e tente de novo.",
                    Toast.LENGTH_LONG).show();
        }
    }

    private String precoOuEspera(String id) {
        String preco = (loja == null) ? null : loja.precoDe(id);
        return (preco == null) ? "..." : preco;
    }

    private void montarLista() {
        container.removeAllViews();

        botaoVip.setVisibility(usuario.isVip() ? View.GONE : View.VISIBLE);
        botaoVip.setText("Ser VIP - " + precoOuEspera(Loja.VIP) + " (libera tudo)");

        for (Nivel nivel : Nivel.values()) {
            TextView linha = new TextView(this);
            linha.setTextSize(16f);
            linha.setTextColor(ContextCompat.getColor(this, R.color.qb_texto_claro));
            linha.setPadding(0, 24, 0, 24);

            String idProduto = Loja.idDoNivel(nivel.getCodigo());

            String status;
            if (nivel.isGratuito()) {
                status = "Grátis";
            } else if (usuario.temAcessoAoNivel(nivel)) {
                status = "Liberado";
            } else {
                status = precoOuEspera(idProduto);
            }

            linha.setText(nivel.getCodigo() + " - " + nivel.getRotulo() + "  |  " + status);
            container.addView(linha);

            if (!nivel.isGratuito() && !usuario.temAcessoAoNivel(nivel)) {
                Button botaoComprar = new Button(this);
                botaoComprar.setText("Desbloquear " + nivel.getRotulo());
                botaoComprar.setBackgroundTintList(ColorStateList.valueOf(0xFFFFFFFF));
                botaoComprar.setTextColor(0xFF000000);
                botaoComprar.setOnClickListener(comSom(v -> comprarOuAvisar(idProduto)));
                container.addView(botaoComprar);
            }
        }
    }
}