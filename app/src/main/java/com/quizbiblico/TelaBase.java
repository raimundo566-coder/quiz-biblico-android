package com.quizbiblico;

import androidx.appcompat.app.AppCompatActivity;

public abstract class TelaBase extends AppCompatActivity {

    protected QuizBiblicoApp app() {
        return (QuizBiblicoApp) getApplication();
    }

    @Override
    protected void onPause() {
        super.onPause();
        app().salvar();
    }
}