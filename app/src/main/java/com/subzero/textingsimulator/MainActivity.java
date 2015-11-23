package com.subzero.textingsimulator;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;


public class MainActivity extends Activity {

    private ImageButton start;
    String filename = "score";
    BufferedReader inputStream;
    long oldScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Conversations.setCurrentScreen("Main");

        File file = new File(getFilesDir(), filename);
        if(file.exists()){
            try {
                inputStream = new BufferedReader(new FileReader(file));
                oldScore = Long.decode(inputStream.readLine());
                ScoreHolder.setOldScore(oldScore);
                inputStream.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        start = (ImageButton) findViewById(R.id.start);

        final ScaleAnimation growAnim = new ScaleAnimation(1.0f, 1.15f, 1.0f, 1.15f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        final ScaleAnimation shrinkAnim = new ScaleAnimation(1.15f, 1.0f, 1.15f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        growAnim.setDuration(2000);
        shrinkAnim.setDuration(2000);
        start.setAnimation(growAnim);
        growAnim.start();


        growAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                start.setAnimation(shrinkAnim);
                shrinkAnim.start();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        shrinkAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                start.setAnimation(growAnim);
                growAnim.start();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);
    }

    public void startGame(View view) {
        Conversations.setGameRunning(false);
        Conversations.resetVariables();
        ScoreHolder.resetVariables();
        finish();
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }
}
