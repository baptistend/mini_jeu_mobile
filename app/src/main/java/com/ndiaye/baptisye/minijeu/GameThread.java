package com.ndiaye.baptisye.minijeu;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.view.SurfaceHolder;

import java.util.Random;

public class GameThread extends Thread {
    private SurfaceHolder surfaceHolder;
    private GameView gameView;
    private boolean running;
    private Handler handler = new Handler();
    private Random random;
    public GameThread(SurfaceHolder surfaceHolder, GameView gameView) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.gameView = gameView;
    }

    @Override
    public void run() {
        // Utilisation du postDelayed pour mettre à jour l'état toutes les 100ms

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (running) {
                    Canvas canvas = null;
                    try {

                        canvas = surfaceHolder.lockCanvas();
                        synchronized (surfaceHolder) {
                            if (canvas != null) {
                                //TODO
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (canvas != null) {
                            try {
                                // Libère le canvas après le dessin
                                surfaceHolder.unlockCanvasAndPost(canvas);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    // Replanifie l'exécution après 100 ms
                    handler.postDelayed(this, 100);
                }
            }
        }, 100);

        while (running) {
            try {
                sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Canvas canvas = null;

            try {
                // Verrouille le canvas pour dessiner
                canvas = surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {
                    if (canvas != null) {
                        gameView.update();  // Met à jour l'état du jeu
                        gameView.draw(canvas);  // Dessine sur le canvas
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (canvas != null) {
                    try {
                        // Libère le canvas après le dessin
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    // Permet de contrôler l'état du thread de jeu (arrêt ou exécution)
    public void setRunning(boolean isRunning) {
        running = isRunning;
    }
}
