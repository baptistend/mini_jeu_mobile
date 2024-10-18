package com.ndiaye.baptisye.minijeu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);  // On utilisera un layout XML pour le design de cette activité

        Button startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lancer le jeu lorsqu'on appuie sur le bouton
                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                startActivity(intent);
                finish();  // Terminer cette activité pour qu'on ne puisse pas revenir en arrière
            }
        });
    }
}