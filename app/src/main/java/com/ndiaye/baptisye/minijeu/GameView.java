package com.ndiaye.baptisye.minijeu;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.Random;

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
    // Directions possibles
    private float SPEED = 5f; // Vitesse fixe pour le mouvement
    private static final int NORTH = 0;
    private static final int EAST = 1;
    private static final int SOUTH = 2;
    private static final int WEST = 3;
    private static final int[] DIRECTIONS = {NORTH, EAST, SOUTH, WEST};

    public GameView(Context context) {
        super(context);
        thread = new GameThread(getHolder(), this);
        SharedPreferences sharedPref = getContext().getSharedPreferences(MainActivity.PREFS_NAME, Context.MODE_PRIVATE);
        y = sharedPref.getInt(MainActivity.TOTAL_GAMES_KEY, 100);
        yCenter = getHeight() / 2;
        xCenter = getWidth() / 2;
        Log.w("valeur_y gv ", String.valueOf(y));

        random = new Random();
        changeDirection();

        setFocusable(true);
        getHolder().addCallback(this);
    }

    private void changeDirection() {
        // Choisir une direction aléatoire parmi les directions définies
        int direction = DIRECTIONS[random.nextInt(DIRECTIONS.length)];
        switch (direction) {
            case NORTH:
                directionX = 0;
                directionY = -SPEED; // Déplacement vers le nord
                break;
            case EAST:
                directionX = SPEED; // Déplacement vers l'est
                directionY = 0;
                break;
            case SOUTH:
                directionX = 0;
                directionY = SPEED; // Déplacement vers le sud
                break;
            case WEST:
                directionX = -SPEED; // Déplacement vers l'ouest
                directionY = 0;
                break;
        }
        SPEED +=2;
    }
    public void initCoordinates(){
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
        xCenter = width / 2; // Mettre à jour la largeur
        yCenter = height / 2; // Mettre à jour la hauteur
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (canvas != null) {
            Log.w("canvas ", "test");
            canvas.drawColor(Color.WHITE);
            Paint paint = new Paint();
            paint.setColor(Color.rgb(250, 0, 0));
            float radius = 50f;
            boolean isAtEdge = xCenter + x - radius <= 0 || xCenter + x + radius >= getWidth();

            // Vérifier les bords gauche et droit

            // Vérifier les bords haut et bas
            if (yCenter + y - radius <= 0 || yCenter + y + radius >= getHeight()) {
                isAtEdge = true;
            }

            // Optionnel : Changez la couleur du cercle si on est au bord
            if (isAtEdge) {
                elapsedTime = System.currentTimeMillis() - startTime; // Temps écoulé en millisecondes
                long elapsedTimeInSeconds = elapsedTime / 1000; // En secondes

                post(() -> {
                    // Afficher le Toast avec le temps de jeu
                    Toast.makeText(getContext(), "Vous avez perdu ! Temps de jeu : " + elapsedTimeInSeconds + " secondes", Toast.LENGTH_SHORT).show();
                });               initCoordinates();
            }
            // Dessiner le cercle centré
            canvas.drawCircle(xCenter + x, yCenter + y, radius, paint);
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
        // Mettre à jour la position
        x += directionX;
        y += directionY;
        elapsedTime = System.currentTimeMillis() - startTime; // Temps écoulé en millisecondes
        // Gérer les limites de la fenêtre pour que le cercle ne sorte pas
        if (xCenter + x < 0 || xCenter + x > getWidth()) {
            directionX *= -1; // Inverser la direction X
        }
        if (yCenter + y < 0 || yCenter + y > getHeight()) {
            directionY *= -1; // Inverser la direction Y
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            changeDirection(); // Changer la direction lors d'un toucher
            return true; // Indiquer que l'événement a été traité
        }
        return super.onTouchEvent(event);
    }
}
