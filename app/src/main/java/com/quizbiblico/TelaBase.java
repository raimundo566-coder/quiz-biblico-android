package com.quizbiblico;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public abstract class TelaBase extends AppCompatActivity {

    protected QuizBiblicoApp app() {
        return (QuizBiblicoApp) getApplication();
    }

    protected View.OnClickListener comSom(View.OnClickListener acao) {
        return v -> {
            app().getSom().tocarBotao();
            acao.onClick(v);
        };
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);

        View moldura = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(moldura, (v, insets) -> {
            Insets barras = insets.getInsets(WindowInsetsCompat.Type.systemBars()
                    | WindowInsetsCompat.Type.displayCutout());
            v.setPadding(barras.left, barras.top, barras.right, barras.bottom);
            return WindowInsetsCompat.CONSUMED;
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        app().salvar();
    }
}