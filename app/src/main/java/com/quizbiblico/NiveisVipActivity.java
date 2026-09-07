package com.quizbiblico;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.quizbiblico.modelo.Nivel;
import com.quizbiblico.modelo.Usuario;

public class NiveisVipActivity extends TelaBase {

    private Usuario usuario;
    private LinearLayout container;
    private Button botaoVip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_niveis_vip);

        usuario = app().getUsuario();

        container = findViewById(R.id.containerNiveisVip);
        botaoVip = findViewById(R.id.botaoTornarVip);

        botaoVip.setOnClickListener(v ->
                Toast.makeText(this, "Em breve: compra dentro do app.", Toast.LENGTH_SHORT).show());

        Button botaoVoltar = findViewById(R.id.botaoVoltarNiveisVip);
        botaoVoltar.setOnClickListener(v -> finish());

        montarLista();
    }

    private void montarLista() {
        botaoVip.setVisibility(usuario.isVip() ? View.GONE : View.VISIBLE);

        for (Nivel nivel : Nivel.values()) {
            TextView linha = new TextView(this);
            linha.setTextSize(16f);
            linha.setPadding(0, 24, 0, 24);

            String status;
            if (nivel.isGratuito()) {
                status = "Gratis";
            } else if (usuario.temAcessoAoNivel(nivel)) {
                status = "Liberado";
            } else {
                status = "R$ 3,90";
            }

            linha.setText(nivel.getCodigo() + " - " + nivel.getRotulo() + "  |  " + status);
            container.addView(linha);

            if (!nivel.isGratuito() && !usuario.temAcessoAoNivel(nivel)) {
                Button botaoComprar = new Button(this);
                botaoComprar.setText("Desbloquear " + nivel.getRotulo());
                botaoComprar.setBackgroundTintList(ColorStateList.valueOf(0xFFFFFFFF));
                botaoComprar.setTextColor(0xFF000000);
                botaoComprar.setOnClickListener(v ->
                        Toast.makeText(this, "Em breve: compra dentro do app.", Toast.LENGTH_SHORT).show());
                container.addView(botaoComprar);
            }
        }
    }
}