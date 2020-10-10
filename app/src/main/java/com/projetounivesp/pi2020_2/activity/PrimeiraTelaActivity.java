package com.projetounivesp.pi2020_2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.projetounivesp.pi2020_2.R;

/**
 * Created by Gislene
 */

public class PrimeiraTelaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed (new Runnable(){
            public void run(){
                abrirAutenticacao();
            }
        }, 3000);
    }

    private void abrirAutenticacao (){
        Intent i = new Intent(PrimeiraTelaActivity.this, AutenticacaoActivity.class);
        startActivity(i);
        finish();
    }

}