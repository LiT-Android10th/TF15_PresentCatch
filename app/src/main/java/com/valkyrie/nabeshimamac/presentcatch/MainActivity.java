package com.valkyrie.nabeshimamac.presentcatch;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private GameView gameView;

    //    SensorManagerを新しく宣言する
    private SensorManager sensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameView = new GameView(this);
        setContentView(gameView);

//        SensorManagerの取得
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
//        加速度の取得
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            Log.d("SensorValues", "\nX軸:" + event.values[0] +
                    "\nY軸:" + event.values[1] +
                    "\nZ軸:" + event.values[2]);

            if (gameView.player != null) {
//                X軸の加速度の値を引数として渡す
                gameView.player.move(-event.values[0]);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (!sensors.isEmpty()) {
            sensorManager.registerListener(this, sensors.get(0), SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    @Override
    protected void onPause() {
//        センサー処理の中止　
        sensorManager.unregisterListener(this);
        super.onPause();
    }
}

