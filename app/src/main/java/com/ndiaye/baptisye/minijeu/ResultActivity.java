package com.ndiaye.baptisye.minijeu;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;

public class ResultActivity extends Activity {
    public static final String RESULTS = "game_results";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        TableLayout tableLayout = findViewById(R.id.resultLayout);

        // Récupérer les données des SharedPreferences
        SharedPreferences sharedPref = getSharedPreferences(RESULTS, Context.MODE_PRIVATE);
        String lastLoseData = sharedPref.getString("results", "Aucune donnée");
        String results = sharedPref.getString("results", "");
        if (!results.isEmpty()) {
            // Diviser les résultats par ligne (chaque ligne contient une entrée)
            String[] resultLines = results.split("\n");

            // Pour chaque ligne de résultat, ajouter une nouvelle ligne dans le tableau
            for (String resultLine : resultLines) {
                TableRow row = getTableRow(resultLine);

                // Ajouter la TableRow au TableLayout
                tableLayout.addView(row);
            }
        } else {
            // Si aucun résultat, afficher un Toast informatif
            Toast.makeText(this, "Aucune donnée disponible.", Toast.LENGTH_SHORT).show();
        }
    Log.w("results ", lastLoseData);
    }

    private @NonNull TableRow getTableRow(String resultLine) {
        TableRow row = new TableRow(this);
        row.setGravity(Gravity.CENTER);  // Centrer les éléments dans la TableRow
        // Créer un TextView pour afficher chaque ligne de résultat
        TextView resultText = new TextView(this);
        resultText.setText(resultLine);
        resultText.setPadding(16, 16, 16, 16);  // Ajouter du padding autour du texte
        resultText.setTextColor(Color.BLACK);
        resultText.setGravity(Gravity.CENTER);  // Définir la couleur du texte en noir
        row.addView(resultText);
        return row;
    }
}
