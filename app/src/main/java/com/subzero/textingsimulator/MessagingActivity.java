package com.subzero.textingsimulator;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.w3c.dom.Element;

import java.util.ArrayList;


public class MessagingActivity extends Activity {

    LinearLayout messagingLayout;
    ScrollView messagingScroll;
    TextView title; //originMessage;
    TextView reply1, reply2, reply3, reply4;
    int conversationNumber;
    String name;
    String shortName;
    ImageView pointer;
    ImageView mood;
    Thread thread;
    boolean running;
    boolean shouldRestart;
    RotateAnimation rotateAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(0, 0);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        //ScoreHolder.setIsApplicationPaused(false);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        conversationNumber = extras.getInt("CONVERSATION_NUMBER");
        title = (TextView) findViewById(R.id.messaging_title);
        if (extras.getString("MESSAGE").contains(" ")) {
            shortName = extras.getString("MESSAGE").split("\\ ")[0];
        }
        title.setText(shortName);
        pointer = (ImageView) findViewById(R.id.messaging_pointer);
        mood = (ImageView) findViewById(R.id.mood);
        name = extras.getString("MESSAGE");

        Conversations.setCurrentScreen(name); // TODO Restart activity if user is currently here

        reply1 = (TextView) findViewById(R.id.reply1);
        reply1.setText("");
        reply1.setBackgroundResource(R.drawable.blank);
        reply2 = (TextView) findViewById(R.id.reply2);
        reply2.setText("");
        reply2.setBackgroundResource(R.drawable.blank);
        reply3 = (TextView) findViewById(R.id.reply3);
        reply3.setText("");
        reply3.setBackgroundResource(R.drawable.blank);
        reply4 = (TextView) findViewById(R.id.reply4);
        reply4.setText("");
        reply4.setBackgroundResource(R.drawable.blank);

        messagingLayout = (LinearLayout) findViewById(R.id.messaging_layout);
        messagingScroll = (ScrollView) findViewById(R.id.messaging_scroll);

        boolean sent = false;
        if (Conversations.getDepth(name) == 0)
            addMessage(ReadXMLFile.getElementText(Conversations.getMessage(name, 0)).getTextContent(), sent);
        else
            for (int i = 0; i < Conversations.getConversation(name).size(); i++) {
                addMessage(ReadXMLFile.getElementText(Conversations.getMessage(name, i)).getTextContent(), sent);
                if (sent)
                    sent = false;
                else
                    sent = true;
            }

        final ArrayList<Element> responseNodes = ReadXMLFile.getResponses(Conversations.getMessage(name, Conversations.getDepth(name)), Conversations.getDepth(name));
        final ArrayList<Element> responses = ReadXMLFile.getResponsesText(responseNodes);
        final int currentDepth = Conversations.getDepth(name);

        if (!ScoreHolder.checkHasBeenScored(name)) {
            ScoreHolder.modifyHasBeenScored(name, false);
        }

        if (!ScoreHolder.getHasBeenScored(name)) {

            String messageScore = ReadXMLFile.getMessageScore(Conversations.getMessage(name, Conversations.getDepth(name)), Conversations.getDepth(name));
            ScoreHolder.processScore(name, messageScore);

            ScoreHolder.modifyHasBeenScored(name, true);

        }

        float fromDegrees = ScoreHolder.getAngle();
        float toDegrees = ScoreHolder.setupAngle();

        String currentMood = ScoreHolder.getCurrentMood(name);
        switch (currentMood) {
            case "Excellent":
                mood.setImageResource(R.drawable.chuffed_new);
                break;
            case "Good":
                mood.setImageResource(R.drawable.happy_new);
                break;
            case "Meh":
                mood.setImageResource(R.drawable.meh_new);
                break;
            case "Bad":
                mood.setImageResource(R.drawable.angry_new);
                break;
            case "Evil":
                mood.setImageResource(R.drawable.upset_new);
                break;

        }

        rotateAnim = new RotateAnimation(fromDegrees, toDegrees, 13, 47);
        rotateAnim.setDuration(250);
        rotateAnim.setFillAfter(true);
        pointer.setAnimation(rotateAnim);
        rotateAnim.start();
        ScoreHolder.setGameActivityAngle(toDegrees);

        if(ScoreHolder.getScore() <= 0){
            running = false;
            finish();
            Intent intent2 = new Intent(this, EndActivity.class);
            startActivity(intent2);
        }

        if(!Conversations.doesMessageCheckTimesContains(name)) {
            if ((responses.size() >= 1))
                if (!responses.get(0).equals(null)) {
                    reply1.setBackgroundResource(R.drawable.layout_bg_s);
                    reply1.setText(responses.get(0).getTextContent());
                    reply1.setPadding(dpToPx(5), dpToPx(5), dpToPx(5), dpToPx(5));
                    reply1.setGravity(Gravity.CENTER);
                    reply1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            running = false;
                            Conversations.addMessage(name, responseNodes.get(0), (currentDepth + 1));
                            Conversations.addMessageCheckTime(name, responseNodes.get(0));
                            //Conversations.addMessage(name, GameActivity.readXMLFile.processResponse((currentDepth + 1), responseNodes.get(0)), currentDepth + 2);
                            //ScoreHolder.modifyHasBeenScored(name, false);
                            endActivity();
                        }
                    });
                }
            if ((responses.size() >= 2))
                if (!responses.get(1).equals(null)) {
                    reply2.setBackgroundResource(R.drawable.layout_bg_s);
                    reply2.setText(responses.get(1).getTextContent());
                    reply2.setPadding(dpToPx(5), dpToPx(5), dpToPx(5), dpToPx(5));
                    reply2.setGravity(Gravity.CENTER);
                    reply2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            running = false;
                            Conversations.addMessage(name, responseNodes.get(1), (currentDepth + 1));
                            Conversations.addMessageCheckTime(name, responseNodes.get(1));
                            //Conversations.addMessage(name, GameActivity.readXMLFile.processResponse((currentDepth + 1), responseNodes.get(1)), (currentDepth + 2));
                            //ScoreHolder.modifyHasBeenScored(name, false);
                            endActivity();
                        }
                    });
                }
            if ((responses.size() >= 3))
                if (!responses.get(2).equals(null)) {
                    reply3.setBackgroundResource(R.drawable.layout_bg_s);
                    reply3.setText(responses.get(2).getTextContent());
                    reply3.setPadding(dpToPx(5), dpToPx(5), dpToPx(5), dpToPx(5));
                    reply3.setGravity(Gravity.CENTER);
                    reply3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            running = false;
                            Conversations.addMessage(name, responseNodes.get(2), (currentDepth + 1));
                            Conversations.addMessageCheckTime(name, responseNodes.get(2));
                            //Conversations.addMessage(name, GameActivity.readXMLFile.processResponse((currentDepth + 1), responseNodes.get(2)), (currentDepth + 2));
                            //ScoreHolder.modifyHasBeenScored(name, false);
                            endActivity();
                        }
                    });
                }
            if ((responses.size() >= 4))
                if (!responses.get(3).equals(null)) {
                    reply4.setBackgroundResource(R.drawable.layout_bg_s);
                    reply4.setText(responses.get(3).getTextContent());
                    reply4.setPadding(dpToPx(5), dpToPx(5), dpToPx(5), dpToPx(5));
                    reply4.setGravity(Gravity.CENTER);
                    reply4.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            running = false;
                            Conversations.addMessage(name, responseNodes.get(3), (currentDepth + 1));
                            Conversations.addMessageCheckTime(name, responseNodes.get(3));
                            //Conversations.addMessage(name, GameActivity.readXMLFile.processResponse((currentDepth + 1), responseNodes.get(3)), (currentDepth + 2));
                            //ScoreHolder.modifyHasBeenScored(name, false);
                            endActivity();
                        }
                    });
                }
        }

        if (responses.size() == 0) {
            Conversations.setHasEnded(name, true);
            reply1.setBackgroundResource(R.drawable.layout_bg_s);
            reply1.setText("End Conversation");
            reply1.setPadding(dpToPx(5), dpToPx(5), dpToPx(5), dpToPx(5));
            reply1.setGravity(Gravity.CENTER);
            reply1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        messagingScroll.post(new Runnable() {
            @Override
            public void run() {
                messagingScroll.fullScroll(View.FOCUS_DOWN);
            }
        });

        running = true;
        run(); // TODO THIS IS THE PROBLEM CHILD
    }

    // TODO Change it from restarting Activity to refreshing the variables

    public void run(){
        thread = new Thread(new Runnable() { // TODO Problem arises because too many of these are being created
            @Override
            public void run() {
                while (running){
                    if(Conversations.getCurrentScreen() == name) {
                        if (Conversations.doesMessageCheckTimesContains(name)) {
                            if ((Conversations.getMessageCheckTime(name) != 0) && (System.currentTimeMillis() > Conversations.getMessageCheckTime(name))) {
                                shouldRestart = true;
                                if (shouldRestart) {
                                    //ScoreHolder.modifyHasBeenScored(name, false);
                                    endActivity();
                                    shouldRestart = false;
                                }

                            }
                        }
                    }
                }
            }
        });
        thread.start();
    }

    private void endActivity(){
        running = false;
        Intent intent = getIntent();
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    public void addMessage(String message, boolean sent) {
        TextView text = new TextView(this);
        text.setText(message);
        text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (sent) {
            text.setBackgroundResource(R.drawable.layout_bg_s);
            layoutParams.setMargins(dpToPx(0), 0, dpToPx(10), dpToPx(10));
            layoutParams.gravity = Gravity.RIGHT;
        } else {
            text.setBackgroundResource(R.drawable.layout_bg);
            layoutParams.setMargins(0, dpToPx(0), dpToPx(100), dpToPx(10));
            text.setGravity(Gravity.LEFT);
        }
        text.setPadding(15, 15, 15, 15);

        messagingLayout.addView(text, layoutParams);
    }

    public int dpToPx(int dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public void onBackPressed() {
        if(rotateAnim.hasEnded()) {
            running = false;
            //ScoreHolder.modifyHasBeenScored(name, true); // TODO Don't forget about this when it comes to adding delay to receiving message!
            finish();
            overridePendingTransition(0, 0);
            //startActivity(new Intent(this, GameActivity.class)); // TODO THIS IS ALSO A PROBLEM CHILD
        }
    }

    public void back(View view){
        onBackPressed();
    }

    /*@Override
    protected void onPause() {
        super.onPause();
        ScoreHolder.setIsApplicationPaused(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ScoreHolder.setIsApplicationPaused(false);
    }*/
}