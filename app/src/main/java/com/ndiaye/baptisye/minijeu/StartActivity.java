package com.ndiaye.baptisye.minijeu;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class StartActivity extends Activity {
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private boolean permissionToRecordAccepted = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // Vérifier et demander la permission RECORD_AUDIO
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
        }

        // Configuration du bouton de démarrage du jeu
        Button startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(v -> {
            // Vérifier si la permission a été accordée avant de démarrer le jeu
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                // Lancer l'activité principale (MainActivity)
                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                startActivity(intent);
                finish();  // Terminer cette activité
            } else {
                Toast.makeText(StartActivity.this, "La permission d'enregistrement audio est requise.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            permissionToRecordAccepted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
        }

        // Si la permission n'est pas accordée, afficher un message et fermer l'activité
        if (!permissionToRecordAccepted) {
            Toast.makeText(this, "La permission d'enregistrement audio est requise.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}