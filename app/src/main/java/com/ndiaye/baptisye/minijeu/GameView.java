package com.ndiaye.baptisye.minijeu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;
import android.media.Image;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.stream.Collectors;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private GameThread thread;
    private int xCenter, yCenter;
    private float x = 0; // Position X
    private float y = 0; // Position Y
    private float directionX; // Vitesse directionnelle sur l'axe X
    private float directionY; // Vitesse directionnelle sur l'axe Y
    Random random;
    private long startTime; // Temps de début de la partie
    private long elapsedTime;
    private Direction current_direction;
    private float PREVIOUS_SPEED = 5f; // Vitesse  pour le mouvement
    private float CURRENT_SPEED = 5f; // Vitesse  pour le mouvement

    private SpeechRecognizer speechRecognizer;
    public boolean isRecognizing = false;

    private int ballColor = Color.rgb(255,0,0);
    private final float NORMAL_SPEED = 5f;
    public GameView(Context context) {
        super(context);


        initSpeechRecognizer(context); // Initialiser le recognizer vocal

        thread = new GameThread(getHolder(), this);
        SharedPreferences sharedPref = getContext().getSharedPreferences(MainActivity.PREFS_NAME, Context.MODE_PRIVATE);

        y = sharedPref.getInt(MainActivity.TOTAL_GAMES_KEY, 100);
        yCenter = getHeight() / 2;
        xCenter = getWidth() / 2;
        random = new Random();
        changeDirection();

        setFocusable(true);
        getHolder().addCallback(this);

    }

    // Méthode pour initialiser le SpeechRecognizer
    private void initSpeechRecognizer(Context c) {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(c);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                isRecognizing = true;
            }

            @Override
            public void onBeginningOfSpeech() {}

            @Override
            public void onRmsChanged(float rmsdB) {}

            @Override
            public void onBufferReceived(byte[] buffer) {}

            @Override
            public void onEndOfSpeech() {}

            @Override
            public void onError(int error) {
                isRecognizing = false;

            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    String match = matches.get(0).toLowerCase(Locale.ROOT);
                    Log.w("voiceCommand", "partial Premier mot reconnu : " + match + matches + Virelangues.trouverNiveauVirelangue(match));
                    processVoiceCommand(Virelangues.trouverNiveauVirelangue(match));
                }

                isRecognizing = false;
                speechRecognizer.stopListening();

            }

            @Override
            public void onPartialResults(Bundle partialResults) {


            }

            @Override
            public void onEvent(int eventType, Bundle params) {}
        });
    }

    // Méthode pour démarrer la reconnaissance vocale
    public void startVoiceRecognition() {
        if (speechRecognizer != null && !isRecognizing) {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);  // Limite le nombre de résultats
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getContext().getPackageName());
            intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);  // Activer les résultats partiels
            speechRecognizer.startListening(intent);
        }
    }

    // Méthode pour traiter les commandes vocales
    private void processVoiceCommand(Integer command) {
       switch (command){
           case -1:
               ballColor = Color.rgb(255,0,0);

               break;
           case 0:
           case 1:
               PREVIOUS_SPEED = CURRENT_SPEED;

               CURRENT_SPEED *= 0.75F;
               ballColor = Color.rgb(0,0,255);
               Toast.makeText(getContext(), "Virelangue trouvé ! Ralentissement de la balle ...", Toast.LENGTH_SHORT).show();

               break;
           case 2:
           case 3:
               PREVIOUS_SPEED = CURRENT_SPEED;

               CURRENT_SPEED *= 0.5F;
               ballColor = Color.rgb(0,0,255);
               Toast.makeText(getContext(), "Virelangue trouvé ! Ralentissement de la balle ...", Toast.LENGTH_SHORT).show();

               break;

           case 4:
           case 5:
               PREVIOUS_SPEED = CURRENT_SPEED;
               CURRENT_SPEED *= 0.25F;
               ballColor = Color.rgb(0,0,255);
               Toast.makeText(getContext(), "Virelangue trouvé ! Ralentissement de la balle ...", Toast.LENGTH_SHORT).show();

               break;

        }



    }

    public void decreaseSpeed() {
        if(!(directionX==0 && directionY==0 && CURRENT_SPEED==5F)){
            if(CURRENT_SPEED>4F){
                CURRENT_SPEED *= 0.9F;
                changeDirection(current_direction);
                Toast.makeText(getContext(), "Jolie secousse, vitesse réduite !", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getContext(), "Trop facile ! Ta vitesse est faible rééssaye plus tard", Toast.LENGTH_SHORT).show();
            }
         }
    }


    // Méthode pour changer la direction en fonction d'une commande
    private void changeDirection(Direction direction) {
        switch (direction) {
            case NORTH:
                directionX = 0;
                directionY = -CURRENT_SPEED;
                break;
            case EAST:
                directionX = CURRENT_SPEED;
                directionY = 0;
                break;
            case SOUTH:
                directionX = 0;
                directionY = CURRENT_SPEED;
                break;
            case WEST:
                directionX = -CURRENT_SPEED;
                directionY = 0;
                break;
        }
    }

    // Méthode pour changer la direction de manière aléatoire (comme avant)
    private void changeDirection() {
        List<Direction> availableDirections = Arrays.stream(Direction.values())
                .filter(dir -> dir != current_direction)
                .collect(Collectors.toList());

        Direction newDirection = availableDirections.get(random.nextInt(availableDirections.size()));

        current_direction = newDirection;
        changeDirection(newDirection);
        PREVIOUS_SPEED = CURRENT_SPEED;
        CURRENT_SPEED *= 1.25F;
    }

    public void initCoordinates() {
        SharedPreferences sharedPref = getContext().getSharedPreferences(MainActivity.PREFS_NAME, Context.MODE_PRIVATE);
        startTime = System.currentTimeMillis(); // Initialiser le temps de début

        y = sharedPref.getInt(MainActivity.TOTAL_GAMES_KEY, 100);
        yCenter = getHeight() / 2;
        xCenter = getWidth() / 2;
        x = 0;
        directionX = 0; // Vitesse directionnelle sur l'axe X
        directionY = 0;
        CURRENT_SPEED = 5F;
        PREVIOUS_SPEED = 5F;

    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        thread.setRunning(true);
        thread.start();
        startTime = System.currentTimeMillis(); // Initialiser le temps de début
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        xCenter = width / 2;
        yCenter = height / 2;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (canvas != null) {

            canvas.drawColor(Color.LTGRAY);
            Paint paint = new Paint();
            paint.setColor(ballColor);
            float radius = 50f;
            int width = getWidth();
            int imageWidth = 24; // largeur de l'image
            int imageHeight = 26; // hauteur de l'image

            // Calculer les coordonnées pour centrer l'image horizontalement
            int left = (width - imageWidth) / 2;
            int top = 0; // positionner en haut
            int bottom = top + imageHeight; // calculer la hauteur
            boolean isAtEdge = xCenter + x - radius <= 0 || xCenter + x + radius >= getWidth();

            if (yCenter + y -bottom- radius <= 0 || yCenter + y + radius >= getHeight()) {
                isAtEdge = true;
            }

            if (isAtEdge) {

                //handle Lose
                handleLose();

            }

            canvas.drawCircle(xCenter + x, yCenter + y, radius, paint);

        }
    }

    private void handleLose() {
        // Récupérer les SharedPreferences
        SharedPreferences sharedPref = getContext().getSharedPreferences(ResultActivity.RESULTS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        // Calculer le temps écoulé
        elapsedTime = System.currentTimeMillis() - startTime;
        long elapsedTimeInSeconds = elapsedTime / 1000;

        // Obtenir la date actuelle
        Date time = new Date();
        String formattedTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(time);

        // Préparer la donnée à stocker
        String dataToStore = formattedTime + " | Temps : " + elapsedTimeInSeconds + " secondes";

        // Récupérer les anciennes données, si elles existent, et les concaténer
        String existingResults = sharedPref.getString("results", "");
        if (!existingResults.isEmpty()) {
            dataToStore = existingResults + "\n" + dataToStore;  // Ajouter la nouvelle donnée à l'existante
        }

        // Sauvegarder les nouvelles données dans SharedPreferences
        editor.putString("results", dataToStore);
        editor.apply();

        // Afficher un message de Toast
        post(() -> {
            Toast.makeText(getContext(), "Vous avez perdu ! Temps de jeu : " + elapsedTimeInSeconds + " secondes", Toast.LENGTH_SHORT).show();
        });

        // Initialiser les coordonnées
        initCoordinates();
    }


    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                thread.setRunning(false);
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            retry = false;
        }
    }

    public void update() {
        x += directionX;
        y += directionY;
        if (CURRENT_SPEED >= PREVIOUS_SPEED) {
            ballColor = Color.rgb(255, 0, 0); // Changer la couleur en rouge si la vitesse est supérieure à la vitesse normale
        }
        else{
            ballColor = Color.rgb(0,255, 0);
        }
        elapsedTime = System.currentTimeMillis() - startTime;
        if (xCenter + x < 0 || xCenter + x > getWidth()) {
            directionX *= -1;
        }
        if (yCenter + y < 0 || yCenter + y > getHeight()) {
            directionY *= -1;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            changeDirection();
            return true;
        }
        return super.onTouchEvent(event);
    }
}
