package com.quizbiblico;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

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
    protected void onPause() {
        super.onPause();
        app().salvar();
    }
}
