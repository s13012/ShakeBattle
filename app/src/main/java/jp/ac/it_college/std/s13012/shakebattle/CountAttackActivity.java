package jp.ac.it_college.std.s13012.shakebattle;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;


public class CountAttackActivity extends Activity
        implements SensorEventListener{

    private TimeSurfaceView mTimeSurfaceView;
    private TextView message;
    private TextView mCountTextView;

    private SensorManager mSensorManager;
    private Sensor mSensor;


    private int mCounter = 0;
    private ShakeDiscriminator shakeDiscriminator;
    private boolean mGameIsRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count_attack);

        ((TextView)findViewById(R.id.label_goal_number))
                .setText(String.valueOf(CountSelectFragment.getGoalCount()));

        SurfaceView surface = (SurfaceView) findViewById(R.id.label_elapsed_time_count);
        mTimeSurfaceView = new TimeSurfaceView(surface);
        shakeDiscriminator = new ShakeDiscriminator();
        mCountTextView = (TextView) findViewById(R.id.current_count);
        //センサーの準備
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        ready();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //センサーの取得
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //センサーマネージャーへイベントリスナーを登録
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    private void ready() {
        message = (TextView) findViewById(R.id.message);
        message.setVisibility(View.VISIBLE);
        message.setText("Ready?");
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(
                new ScaleAnimation(1,2,1,2,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        );

        animationSet.setDuration(2000);
        animationSet.setFillAfter(true);
        message.setAnimation(animationSet);
        final Handler handler = new Handler();
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                message.setText("Go!");
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        message.setText("");
                        gameStart();
                    }
                }, 1000);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }


    private void gameStart() {
        mTimeSurfaceView.startMeasurement();
        mGameIsRunning = true;
    }

    private void gameEnd() {
        mTimeSurfaceView.endMeasurement();
        mGameIsRunning = false;
        message.setText("Finish!");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP && !mGameIsRunning) {
            gameEnd();
            Intent intent = new Intent(this, ResultActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            //振った回数が目標回数と同じになったらメソッドを抜ける
            if (Integer.parseInt(mCountTextView.getText().toString()) ==
                    CountSelectFragment.getGoalCount()) {
                gameEnd();
                return;
            }
            if (shakeDiscriminator.detectShake(sensorEvent) && mGameIsRunning) {
                mCountTextView.setText(String.valueOf(mCounter++));
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }
}
