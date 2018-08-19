package com.valkyrie.nabeshimamac.presentcatch;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;

/**
 * Created by NabeshimaMAC on 16/04/26.
 */

public class GameView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    Bitmap presentImage;

    int score = 0;
    int life = 10;

    static final long FPS = 30;
    static final long FRAME_TIME = 1000 / FPS;

    SurfaceHolder surfaceHolder;
    Thread thread;

    Present present;

    int screenWidth, screenHeight;

    Player player;
    Bitmap playerImage;


    public GameView(Context context) {
        super(context);
        getHolder().addCallback(this);

        Resources resources = context.getResources();
        presentImage = BitmapFactory.decodeResource(resources, R.drawable.img_present0);

        playerImage = BitmapFactory.decodeResource(resources, R.drawable.img_play);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        Canvas canvas = holder.lockCanvas();
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(presentImage, 100, 200, null);
        holder.unlockCanvasAndPost(canvas);

        surfaceHolder = holder;
        thread = new Thread(this);
        thread.start();


    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        screenWidth = width;
        screenHeight = height;


    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        thread = null;

    }

    class Present {
        private static final int WIDTH = 100;
        private static final int HEIGHT = 100;

        float x, y;

        public Present() {
            Random random = new Random();
            x = random.nextInt(screenWidth - WIDTH);
            y = 0;
        }

        public void update() {
            y += 15.0f;
        }

        public void reset() {
            Random random = new Random();
            x = random.nextInt(screenWidth - WIDTH);
            y = 0;
        }
    }


    @Override
    public void run() {
        present = new Present();
        player = new Player();

        Paint textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setFakeBoldText(true);
        textPaint.setTextSize(100);

        while (thread != null) {
            Canvas canvas = surfaceHolder.lockCanvas();
            canvas.drawColor(Color.WHITE);
            canvas.drawBitmap(playerImage, player.x, player.y, null);

            canvas.drawBitmap(presentImage, present.x, present.y, null);


            if (player.isEnter(present)) {
                present.reset();
                score += 10;
            } else if (present.y > screenHeight) {
                present.reset();
                life--;
            } else {
                present.update();
            }
            present.update();


            canvas.drawText("SCORE :" + score, 50, 150, textPaint);
            canvas.drawText("LIFE :" + life, 50, 300, textPaint);

            try {
                Thread.sleep(FRAME_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (life <= 0) {
                canvas.drawText("Game Over", screenWidth / 3, screenHeight / 2, textPaint);
                surfaceHolder.unlockCanvasAndPost(canvas);
                break;

            }
            surfaceHolder.unlockCanvasAndPost(canvas);
        }

    }

    class Player {
        final int WIDTH = 200;
        final int HEIGHT = 200;

        float x, y;

        public Player() {
            x = 0;
            y = screenHeight - HEIGHT;
        }

        public void move(float diffX) {
            this.x += diffX;
            this.x = Math.max(0, x);
            this.x = Math.min(screenWidth - WIDTH, x);

        }

        public boolean isEnter(Present present) {
            if (present.x + Present.WIDTH > x && present.x < x + WIDTH &&
                    present.y + Present.HEIGHT > y && present.y < y + HEIGHT) {
                return true;
            }
            return false;
        }
    }

}
