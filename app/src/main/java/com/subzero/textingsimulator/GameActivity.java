package com.subzero.textingsimulator;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Element;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;


public class GameActivity extends FragmentActivity implements NoticeDialogFragment.NoticeDialogListener{

    boolean isMale = true;
    Random rand = new Random();

    LinearLayout linear;
    String name = "";
    String initials = "";
    int missedMessages = 0;
    String messages = "(+"+missedMessages+")";
    String surnameHolder;

    Typeface textFont;

    public static ReadXMLFile readXMLFile;

    ImageView pointer;
    Thread thread;
    boolean running;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(0, 0);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Conversations.setCurrentScreen("Game");

        TextView tv = (TextView) findViewById(R.id.title);
        Typeface titleFont = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");
        tv.setTypeface(titleFont);
        textFont = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");

        pointer = (ImageView) findViewById(R.id.pointer);

        readXMLFile = new ReadXMLFile(this);

        linear = (LinearLayout) findViewById(R.id.linear_outer);

        //if(!Conversations.isGameRunning()) {

        addNewConversation();
        addNewConversation();
        addNewConversation();
        addNewConversation();
        addNewConversation();
        addNewConversation();
        addNewConversation();
        addNewConversation();
        addNewConversation();
        addNewConversation();
        remakeConversations(); // Otherwise they're in a jumble!

        Conversations.start();
        ScoreHolder.startTimer();

        Conversations.setGameRunning(true);
    //}
        //else{
        //    onRestart();
        //}
        running = true;
        //run();
    }

    public void addNewConversation(){
        isMale = rand.nextBoolean();
        TextView tempIcon = new TextView(this);
        final TextView tempName = new TextView(this);
        TextView tempMessages = new TextView(this);
        LinearLayout tempLayout = new LinearLayout(this);
        ImageView line = new ImageView(this);
        if(isMale){
            tempIcon.setBackgroundResource(R.drawable.m2);
            name = getStreamTextByLine("maleNames.txt", rand.nextInt(100));
            if((name != null) && (name != ""))
                initials = Character.toString(name.charAt(0));
            surnameHolder = (" "+getStreamTextByLine("surnames.txt", rand.nextInt(100)));
            if((surnameHolder != null) && (surnameHolder.length() > 1)) {
                initials += (Character.toString(surnameHolder.charAt(1)));
                name += surnameHolder;
            }
        }else{
            tempIcon.setBackgroundResource(R.drawable.f2);
            name = getStreamTextByLine("femaleNames.txt", rand.nextInt(100));
            if((name != null) && (name != ""))
                initials = Character.toString(name.charAt(0));
            surnameHolder = (" "+getStreamTextByLine("surnames.txt", rand.nextInt(100)));
            if((surnameHolder != null) && (surnameHolder.length() > 1)) {
                initials += (Character.toString(surnameHolder.charAt(1)));
                name += surnameHolder;
            }
        }

        tempIcon.setGravity(Gravity.CENTER);
        tempIcon.setText(initials);
        tempIcon.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        tempIcon.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        tempLayout.addView(tempIcon);

        tempName.setText(name);
        tempName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        tempLayout.addView(tempName);

        tempMessages.setText(messages);
        tempMessages.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        tempLayout.addView(tempMessages);

        line.setImageResource(R.drawable.line);

        tempIcon.setPadding(dpToPx(0), dpToPx(0), 0, 0); // TEXT
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)tempIcon.getLayoutParams();
        params.setMargins(dpToPx(10), dpToPx(0), 0, dpToPx(0)); // BACKGROUND
        tempIcon.setLayoutParams(params);
        tempName.setPadding(dpToPx(10), dpToPx(0), 0, 0);
        tempMessages.setPadding(dpToPx(5), dpToPx(0), 0, 0);
        tempLayout.setPadding(0, dpToPx(10), 0, 0);
        line.setScaleType(ImageView.ScaleType.FIT_XY);
        line.setPadding(0, dpToPx(5), 0, 0);

        tempLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                running = false;
                finish();
                Intent messagesIntent = new Intent(GameActivity.this, MessagingActivity.class);
                Bundle extras = new Bundle(); // TODO Make intent and bundle global variable array and change for each removal of conversation
                extras.putCharSequence("MESSAGE", tempName.getText());
                messagesIntent.putExtras(extras);

                startActivity(messagesIntent);
            }
        });

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linear.addView(tempLayout, layoutParams);
        linear.addView(line);

        Conversations.addConversation(name, new ArrayList<Element>());
        Conversations.addMessage(name, readXMLFile.selectRandomConversation(), Conversations.getDepth(name)); // TODO We are here. Make everything work from here and book notes.
        Conversations.addInitials(name, initials);
        Conversations.addGender(name, isMale);

        // Handle missed messages here.
        messages = "(+"+missedMessages+")";
    }

    private void remakeConversations(){
        linear.removeAllViews();
        final Set<String> keys = Conversations.getConversations().keySet();
        for(final String key : keys){
            remakeConversation(key);
        }

    }

    private void remakeConversation(String name){
        TextView tempIcon = new TextView(this);
        final TextView tempName = new TextView(this);
        TextView tempMessages = new TextView(this);
        LinearLayout tempLayout = new LinearLayout(this);
        ImageView line = new ImageView(this);
        initials = Conversations.getInitials(name);
        isMale = Conversations.getIsMale(name);
        if(isMale){
            tempIcon.setBackgroundResource(R.drawable.m2);
        }else{
            tempIcon.setBackgroundResource(R.drawable.f2);
        }

        tempIcon.setGravity(Gravity.CENTER);
        tempIcon.setText(initials);
        tempIcon.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        tempIcon.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        tempLayout.addView(tempIcon);

        tempName.setText(name);
        tempName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        tempLayout.addView(tempName);

        tempMessages.setText(messages);
        tempMessages.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        tempLayout.addView(tempMessages);

        line.setImageResource(R.drawable.line);

        tempIcon.setPadding(dpToPx(0), dpToPx(0), 0, 0); // TEXT
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)tempIcon.getLayoutParams();
        params.setMargins(dpToPx(10), dpToPx(0), 0, dpToPx(0)); // BACKGROUND
        tempIcon.setLayoutParams(params);
        tempName.setPadding(dpToPx(10), dpToPx(0), 0, 0);
        tempMessages.setPadding(dpToPx(5), dpToPx(0), 0, 0);
        tempLayout.setPadding(0, dpToPx(10), 0, 0);
        line.setScaleType(ImageView.ScaleType.FIT_XY);
        line.setPadding(0, dpToPx(5), 0, 0);

        tempLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent messagesIntent = new Intent(GameActivity.this, MessagingActivity.class);
                Bundle extras = new Bundle(); // TODO Make intent and bundle global variable array and change for each removal of conversation
                extras.putCharSequence("MESSAGE", tempName.getText());
                messagesIntent.putExtras(extras);

                startActivity(messagesIntent);
            }
        });

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linear.addView(tempLayout, layoutParams);
        linear.addView(line);

        // Handle missed messages here.
        messages = "(+"+missedMessages+")";
    }


    public void run(){
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("Conversations to be added",""+Conversations.getConversationsToBeAdded());
                while(running){
                    if(Conversations.getConversationsToBeAdded() != 0){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for(int i = 0; i < Conversations.getConversationsToBeAdded(); i++){
                                    addNewConversation();
                                }
                                Conversations.conversationAdded();
                                remakeConversations();
                            }
                        });
                    }
                }
            }
        });
        thread.start();
    }

    public int dpToPx(int dp){
        final float scale = getResources().getDisplayMetrics().density;
        return (int)(dp * scale + 0.5f);
    }

    private String getStreamTextByLine(String fileName, int lineNumber) {
        String strOut = "";
        String line = "";
        int counter = 1;
        AssetManager assetManager = getAssets();
        try {
            InputStream in = assetManager.open(fileName);
            if (in != null) {
                InputStreamReader input = new InputStreamReader(in);
                BufferedReader buffreader = new BufferedReader(input);
                while ((line = buffreader.readLine()) != null) {
                    if (counter == lineNumber) {
                        strOut = line;
                    }
                    counter++;
                }
                in.close();
            } else {
                Log.e("Input Stream Problem",
                        "Input stream of text file is null");
            }
        } catch (Exception e) {
            Log.e("0003:Error in get stream", e.getMessage());
        }
        return strOut;
    }

    public void pause(View view){
        DialogFragment pauseDialog = new NoticeDialogFragment();
        pauseDialog.show(getFragmentManager(), "pauseGame");
        pauseDialog.setCancelable(false);
    }

    @Override
    public void onBackPressed() {
        pause(findViewById(R.id.back_button));
    }

    @Override
    public void onRestart(){
        super.onRestart();
        running = true;
        if(Conversations.hasAnyConversationEnded()) {
        Conversations.hasAnyConversationEnded();
            remakeConversations();
        }
        float fromDegrees = ScoreHolder.getGameActivityAngle();
        float toDegrees = ScoreHolder.setupAngle();

        RotateAnimation rotateAnim = new RotateAnimation(fromDegrees, toDegrees, 13, 47);
        //rotateAnim.setDuration(500);
        rotateAnim.setFillAfter(true);
        pointer.setAnimation(rotateAnim);
        rotateAnim.start();
        ScoreHolder.setGameActivityAngle(toDegrees);
   }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        ScoreHolder.resumeTimer();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        running = false;
        finish();
        overridePendingTransition(0, 0);
        startActivity(new Intent(this, MainActivity.class));
    }
/*
    @Override
    protected void onPause() {
        super.onPause();
        ScoreHolder.setIsApplicationPaused(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ScoreHolder.setIsApplicationPaused(false);
    }
    */
}