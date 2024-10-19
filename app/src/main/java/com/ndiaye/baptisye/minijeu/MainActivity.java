package com.ndiaye.baptisye.minijeu;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {

    public static final String PREFS_NAME = "game_preferences";
    public static final String TOTAL_GAMES_KEY = "total_games_played";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        SharedPreferences sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int totalGamesPlayed = sharedPref.getInt(TOTAL_GAMES_KEY, 0);

        totalGamesPlayed++;
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(TOTAL_GAMES_KEY, totalGamesPlayed);
        editor.apply();

        Toast.makeText(this, "Total games played: " + totalGamesPlayed, Toast.LENGTH_LONG).show();

        setContentView(new GameView(this));
    }

}
