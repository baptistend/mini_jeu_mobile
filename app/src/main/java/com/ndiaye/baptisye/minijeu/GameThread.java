package com.ndiaye.baptisye.minijeu;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.view.SurfaceHolder;

public class GameThread extends Thread {
    private SurfaceHolder surfaceHolder;
    private GameView gameView;
    private boolean running;
    private Handler handler = new Handler();

    public GameThread(SurfaceHolder surfaceHolder, GameView gameView) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.gameView = gameView;
    }

    @Override
    public void run() {
        // Utilisation du postDelayed pour mettre à jour l'état toutes les 100ms
        /*
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (running) {
                    Canvas canvas = null;
                    try {

                        canvas = surfaceHolder.lockCanvas();
                        synchronized (surfaceHolder) {
                            if (canvas != null) {
                                gameView.update();
                                Paint paint = new Paint();
                                paint.setColor(Color.rgb(250, 0, 0));
                                canvas.drawRect(0, 0, 20, 200,paint );
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
*/
        // Boucle principale de jeu, qui s'exécute toutes les 60ms
        while (running) {
            try {
                sleep(60);  // Pause pour simuler une fréquence d'image (environ 60 FPS)
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
