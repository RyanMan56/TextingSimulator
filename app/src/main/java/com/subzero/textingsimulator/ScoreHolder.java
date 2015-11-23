package com.subzero.textingsimulator;

import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import java.util.HashMap;

/**
 * Created by Ryan on 13/07/2015.
 */
public class ScoreHolder {

    private static int score = 50;
    private static float angle = 0;
    private static float gameActivityAngle = 0;
    private static HashMap<String, String> currentMood = new HashMap<String, String>();
    private static HashMap<String, Boolean> hasBeenScored = new HashMap<String, Boolean>();
    private static long startTime, currentTimer, pausedTimer;
    private static long oldScore;
    private static boolean paused = false;
    private static boolean applicationPaused;

    public static boolean isPointerSetup = false;

    /*public static void setIsApplicationPaused(boolean value){
        if((value == applicationPaused) || (isPaused()))
            return;
        else {
            if (!value) {
                resumeTimer();
            }
            if (value) {
                pauseTimer();
            }
        }
    }*/

    public static boolean isPaused(){
        return paused;
    }

    public static long getOldScore(){
        return oldScore;
    }

    public static void setOldScore(long value){
        oldScore = value;
    }

    public static int getScore(){
        return score;
    }

    public static float getAngle(){ return angle; }

    public static float getGameActivityAngle(){
        return gameActivityAngle;
    }

    public static void setGameActivityAngle(float _angle){
        gameActivityAngle = _angle;
    }

    public static String getCurrentMood(String name){
        return currentMood.get(name);
    }

    public static void modifyHasBeenScored(String key, boolean value){
        hasBeenScored.put(key, value);
    }

    public static boolean getHasBeenScored(String key){
        return hasBeenScored.get(key);
    }

    public static boolean checkHasBeenScored(String key){
        return hasBeenScored.containsKey(key);
    }

    public static void processScore(String name, String messageScore){

        currentMood.put(name, messageScore);

        switch (messageScore){
            case "Excellent":
                score += 10;
                break;
            case "Good":
                score += 5;
                break;
            case "Meh":
                score += 0;
                break;
            case "Bad":
                score -= 5;
                break;
            case "Evil":
                score -= 10;
                break;
        }
        if(score > 100){
            score = 100;
        }
        if(score < 0){
            score = 0;
        }
    }

    public static float setupAngle(){
        angle = (score/(0.425f))-117.75f;
        return angle;
    }

    public static void removeConversation(String key){
        currentMood.remove(key);
        hasBeenScored.remove(key);
    }

    public static void resetVariables(){
        score = 50;
        angle = 0;
        gameActivityAngle = 0;
        isPointerSetup = false;
        paused = false;
    }

    public static void startTimer(){
        startTime = 0;
        currentTimer = 0;
        startTime = System.currentTimeMillis();
        paused = false;
    }

    public static long pauseTimer(){
        currentTimer += System.currentTimeMillis() - startTime;
        pausedTimer = System.currentTimeMillis();
        paused = true;
        return currentTimer;
    }

    public static void resumeTimer(){
        startTime = System.currentTimeMillis();
        pausedTimer = System.currentTimeMillis() - pausedTimer;
        Conversations.extendMessageCheckTimes(pausedTimer);
        paused = false;
    }

    public static long stopTimer(){
        paused = true;
        return pauseTimer();
    }

}
