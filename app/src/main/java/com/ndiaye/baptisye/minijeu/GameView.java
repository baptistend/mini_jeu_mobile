package com.ndiaye.baptisye.minijeu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
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
    private float SPEED = 5f; // Vitesse  pour le mouvement
    private SpeechRecognizer speechRecognizer;
    public boolean isRecognizing = false;
    private Bitmap bitmap; // Pour stocker l'image à dessiner
    private SurfaceHolder surfaceHolder;
    public GameView(Context context) {
        super(context);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.micro_on); // Remplacez par votre image
        initSpeechRecognizer(context); // Initialiser le recognizer vocal

        thread = new GameThread(getHolder(), this);
        SharedPreferences sharedPref = getContext().getSharedPreferences(MainActivity.PREFS_NAME, Context.MODE_PRIVATE);
        y = sharedPref.getInt(MainActivity.TOTAL_GAMES_KEY, 100);
        yCenter = getHeight() / 2;
        xCenter = getWidth() / 2;
        random = new Random();
        changeDirection(); // Initialiser avec une direction aléatoire

        setFocusable(true);
        getHolder().addCallback(this);
    }
    private void updateImageView() {

        if (isRecognizing) {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.micro_on); // Remplacez par votre image
            // Mettez l'image pour "reconnaissance"
        } else {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.micro_off); // Remplacez par votre image
            // Mettez l'image par défaut
        }
    }
    // Méthode pour initialiser le SpeechRecognizer
    private void initSpeechRecognizer(Context c) {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(c);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                isRecognizing = true;
                updateImageView();
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
                updateImageView();
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
                updateImageView();
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
               break;
           case 0:
               SPEED /= 2;
               break;
           case 1:
               SPEED /= 3;
               break;

           case 2:
               SPEED /= 4;
               break;

           case 3:
               SPEED /=5;
               break;

           case 4:
               SPEED /=6;
               break;
           case 5:
               SPEED /=7;
               break;


        }
    }

    // Méthode pour changer la direction en fonction d'une commande
    private void changeDirection(Direction direction) {
        switch (direction) {
            case NORTH:
                directionX = 0;
                directionY = -SPEED;
                break;
            case EAST:
                directionX = SPEED;
                directionY = 0;
                break;
            case SOUTH:
                directionX = 0;
                directionY = SPEED;
                break;
            case WEST:
                directionX = -SPEED;
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

        SPEED += 2;
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
        SPEED = 5F;
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

            canvas.drawColor(Color.WHITE);
            Paint paint = new Paint();
            paint.setColor(Color.rgb(250, 0, 0));
            float radius = 50f;
            boolean isAtEdge = xCenter + x - radius <= 0 || xCenter + x + radius >= getWidth();

            if (yCenter + y - radius <= 0 || yCenter + y + radius >= getHeight()) {
                isAtEdge = true;
            }

            if (isAtEdge) {
                elapsedTime = System.currentTimeMillis() - startTime; // Temps écoulé en millisecondes
                long elapsedTimeInSeconds = elapsedTime / 1000;

                post(() -> {
                    Toast.makeText(getContext(), "Vous avez perdu ! Temps de jeu : " + elapsedTimeInSeconds + " secondes", Toast.LENGTH_SHORT).show();
                });
                initCoordinates();
            }

            canvas.drawCircle(xCenter + x, yCenter + y, radius, paint);
            if (bitmap != null) {
                // Calculer la position pour centrer l'image en haut
                int width = getWidth();
                int height = getHeight();
                int imageWidth = 24;
                int imageHeight = 24;

                // Calculer les coordonnées pour centrer l'image horizontalement
                int left = (width - imageWidth) / 2;
                int top = 0; // positionner en haut
                // Dessiner l'image sur le canvas

                canvas.drawBitmap(bitmap, left, top, null);
            }
        }
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
