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

import static com.valkyrie.nabeshimamac.presentcatch.R.drawable.img_play;

/**
 * Created by NabeshimaMAC on 16/04/26.
 */

public class GameView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    Bitmap presentImage;

//    スコア用の変数
    int score = 0;
//    ライフ用の変数
    int life = 10;

    //    Frame per second(FPS) という１秒間に何回画面を更新するかの値
    static final long FPS = 30;
    static final long FRAME_TIME = 1000 / FPS;

    SurfaceHolder surfaceHolder;
    Thread thread;

    Present present;

    //    画面の横幅と、高さを保存しておく変数
    int screenWidth, screenHeight;

    //    プレイヤークラスと、画像の変数を追加
    Player player;
    Bitmap playerImage;


    public GameView(Context context) {
        super(context);
        getHolder().addCallback(this);

        Resources resources = context.getResources();
//        プレゼントの画像の読み込み
        presentImage = BitmapFactory.decodeResource(resources, R.drawable.img_present0);
//        プレイヤーの画像の読み込み
        playerImage = BitmapFactory.decodeResource(resources, img_play);

    }

    //    runメソッドの中で処理を行うために、Threadを利用できるようにしないとNG。
    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        Canvas canvas = holder.lockCanvas();

//      背景を白で塗りつぶす処理
        canvas.drawColor(Color.WHITE);

//      座標(100,200)の場所にプレゼントの画像を描く
        canvas.drawBitmap(presentImage, 100, 200, null);

//        SurfaceViewにCanvasの内容を反映させる
        holder.unlockCanvasAndPost(canvas);

        surfaceHolder = holder;
        thread = new Thread(this);
        thread.start();

    }

    //    画面が回転した時など、surfaceViewの状態が変わった時に呼ばれるメソッド
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        screenWidth = width;
        screenHeight = height;

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        thread = null;

    }

    //    非同期で呼ばれるメソッド
    @Override
    public void run() {
//        プレゼントクラスを作る
        present = new Present();
//        プレイヤークラスを作る
        player = new Player();

//        スコアとライフ表示用のPaintクラス
        Paint textPaint = new Paint();
//        文字の色
        textPaint.setColor(Color.BLACK);
//        太文字かどうか
        textPaint.setFakeBoldText(true);
//        文字の大きさ
        textPaint.setTextSize(100);

//        ずっと画面を更新
        while (thread != null) {
            Canvas canvas = surfaceHolder.lockCanvas();
//            背景を白で塗りつぶす処理
            canvas.drawColor(Color.WHITE);
//            プレイヤーの画像を描きます
            canvas.drawBitmap(playerImage, player.x, player.y, null);
//            プレゼントの画像を描きます
            canvas.drawBitmap(presentImage, present.x, present.y, null);
//           プレゼントの位置を更新します
            present.update();

//            当たり判定を一番先に行う
            if (player.isEnter(present)) {
//            プレゼントをキャッチできた時
                present.reset();
//                スコアを+10する
                score += 10;
            } else if (present.y > screenHeight) {
//                Y座標が画面の高さを超えたのでresetする
                present.reset();
//                ライフを1減らす
                life--;
            } else {
                present.update();
            }
            present.update();

            canvas.drawText("SCORE :" + score, 50, 150, textPaint);
            canvas.drawText("LIFE :" + life, 50, 300, textPaint);

            try {
//                時間処理を停止させるメソッド
                Thread.sleep(FRAME_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

//            ゲームオーバーの表示
            if (life <= 0) {
                canvas.drawText("Game Over", screenWidth / 3, screenHeight / 2, textPaint);
                surfaceHolder.unlockCanvasAndPost(canvas);
                break;

            }
            surfaceHolder.unlockCanvasAndPost(canvas);
        }

    }

    class Present {
        //        ゲーム内でのプレゼントの大きさ
        private static final int WIDTH = 100;
        private static final int HEIGHT = 100;

        //        プレゼントの画像を表示するときの左上の座標を示す
        float x, y;

        public Present() {
            Random random = new Random();
//            0から(画面の端-プレゼントの横幅を引いた数)の中でランダムな値を作る処理
            x = random.nextInt(screenWidth - WIDTH);
            y = 0;
        }

        //        『y = y + 15.0f 』と同じ意味
        public void update() {
            y += 15.0f;
        }

        public void reset() {
            Random random = new Random();
//            0から(画面の端-プレゼントの横幅を引いた数)の中でランダムな値を作る処理
            x = random.nextInt(screenWidth - WIDTH);
            y = 0;
        }
    }


    class Player {
        //        ゲーム内でのプレイヤーの大きさ
        final int WIDTH = 200;
        final int HEIGHT = 200;

        float x, y;

        public Player() {
            x = 0;
            y = screenHeight - HEIGHT;
        }

        //        移動用のメソッド diffXはX軸の加速度
        public void move(float diffX) {
            this.x += diffX;
            this.x = Math.max(0, x);
            this.x = Math.min(screenWidth - WIDTH, x);

        }

        //        プレゼントとの当たり判定を行うメソッド
        public boolean isEnter(Present present) {
            if (present.x + Present.WIDTH > x && present.x < x + WIDTH &&
                    present.y + Present.HEIGHT > y && present.y < y + HEIGHT) {
                return true;
            }
            return false;
        }
    }


}
