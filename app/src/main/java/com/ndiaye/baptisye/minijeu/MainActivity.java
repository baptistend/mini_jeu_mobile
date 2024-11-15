package com.ndiaye.baptisye.minijeu;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;


public class MainActivity extends Activity implements SensorEventListener {

    public static final String PREFS_NAME = "game_preferences";
    public static final String TOTAL_GAMES_KEY = "total_games_played";
    private GameView gameView;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private long lastShakeTime;
    private float lastX, lastY, lastZ;
    private static final float SHAKE_THRESHOLD = 12.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Passage en plein écran
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Gérer le nombre total de jeux joués à partir des SharedPreferences
        SharedPreferences sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int totalGamesPlayed = sharedPref.getInt(TOTAL_GAMES_KEY, 0);

        totalGamesPlayed++;
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(TOTAL_GAMES_KEY, totalGamesPlayed);
        editor.apply();

        // Afficher le nombre total de jeux joués
        Toast.makeText(this, "Total games played: " + totalGamesPlayed, Toast.LENGTH_LONG).show();

        // Initialisation du SensorManager
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (accelerometer != null) {
                sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
            } else {
                Log.e("Sensor", "Accéléromètre non disponible sur cet appareil");
            }
        } else {
            Log.e("Sensor", "SensorManager non initialisé !");
        }


        // Définir la vue du jeu (GameView)
        gameView = new GameView(this);  // Instanciation directe de GameView
        setContentView(gameView);  // Affecter la vue GameView à l'écran
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            float diffX = Math.abs(x - lastX);
            float diffY = Math.abs(y - lastY);
            float diffZ = Math.abs(z - lastZ);

            if (diffX > SHAKE_THRESHOLD || diffY > SHAKE_THRESHOLD || diffZ > SHAKE_THRESHOLD) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastShakeTime > 5000) {
                    lastShakeTime = currentTime;

                    // Réduire la vitesse et afficher le pop-up
                    if (gameView != null) {
                        gameView.decreaseSpeed();
                        Log.d("ShakeDetection", "Secousse détectée !");
                    }
                }
            }
            lastX = x;
            lastY = y;
            lastZ = z;

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //pas utile
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sensorManager != null && accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}
