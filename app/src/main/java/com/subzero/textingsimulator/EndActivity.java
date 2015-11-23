package com.subzero.textingsimulator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;

public class EndActivity extends Activity {

    private long score;
    private TextView currentScore, highScore;
    String filename = "score";
    FileOutputStream outputStream;
    BufferedReader inputStream;
    long oldScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);

        Conversations.setCurrentScreen("End");
        Conversations.stop();
        Conversations.setGameRunning(false);

        File file = new File(getFilesDir(), filename);
        score = ScoreHolder.stopTimer() / 1000;
        currentScore = (TextView)findViewById(R.id.current_score);
        currentScore.setText(""+score);

        if(!file.exists()){
            writeScore(score);
        }else{
            try {
                inputStream = new BufferedReader(new FileReader(file));
                oldScore = Long.decode(inputStream.readLine());
                inputStream.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        if(score > oldScore){
            writeScore(score);
            oldScore = score;
        }

        highScore = (TextView)findViewById(R.id.high_score);
        highScore.setText(""+oldScore);

    }

    private void writeScore(long score){
        try{
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(Long.toString(score).getBytes());
            outputStream.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void mainMenu(View view){
        finish();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void newGame(View view){
        Conversations.resetVariables();
        ScoreHolder.resetVariables();
        finish();
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }
}
