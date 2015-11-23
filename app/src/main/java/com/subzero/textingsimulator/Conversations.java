package com.subzero.textingsimulator;

import android.util.Log;

import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Ryan on 03/07/2015.
 */
public class Conversations {

    // A HashMap containing the name of the receiver and the conversation
    static ConcurrentHashMap<String, ArrayList<Element>> conversations = new ConcurrentHashMap<String, ArrayList<Element>>();
    // A HashMap containing the name of the receiver and the depth
    static HashMap<String, Integer> conversationDepth = new HashMap<String, Integer>();
    static HashMap<String, String> initials = new HashMap<String, String>();
    static HashMap<String, Boolean> isMale = new HashMap<String, Boolean>();
    static HashMap<String, Boolean> hasEnded = new HashMap<String, Boolean>();
    static int totalConversations;
    static ArrayList<Integer> remainingConversations = new ArrayList<Integer>();
    static ConcurrentHashMap<String, Long> messageCheckTimes = new ConcurrentHashMap<String, Long>();
    static ConcurrentHashMap<String, Element> messageCheckResponses = new ConcurrentHashMap<String, Element>();
    static long conversationCheckTime;
    static int conversationDifference = 10;
    static int conversationsToBeAdded = 0;
    static int messageDifference = 3;
    static int messageMinimum = 3;
    static Thread thread;
    static boolean running;
    static String currentScreen;
    static boolean gameRunning = false;

    public static void start(){
        messageCheckTimes = new ConcurrentHashMap<String, Long>();
        messageCheckResponses = new ConcurrentHashMap<String, Element>();
        conversationCheckTime = System.currentTimeMillis() + (conversationDifference * 1000);
        running = true;
        run();
    }

    public static void run(){
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(running) {
                    checkConversationCheckTime();
                    if (!messageCheckTimes.isEmpty()) {
                        for (Map.Entry<String, Long> entry : messageCheckTimes.entrySet()) {
                            if(!ScoreHolder.isPaused()) {
                                if (System.currentTimeMillis() > entry.getValue()) {
                                    String name = entry.getKey();
                                    int currentDepth = conversationDepth.get(name) - 1;
                                    Log.d(name, "" + currentDepth);
                                    addMessage(name, GameActivity.readXMLFile.processResponse((currentDepth + 1), messageCheckResponses.get(name)), currentDepth + 2);
                                    ScoreHolder.modifyHasBeenScored(name, false);
                                    messageCheckTimes.remove(entry.getKey());
                                    messageCheckResponses.remove(entry.getKey());
                                }
                            }
                        }
                    }
                }
            }
        });
        thread.start();
    }

    public static void checkConversationCheckTime(){
        if(System.currentTimeMillis() > conversationCheckTime){
            conversationsToBeAdded++;
            conversationCheckTime = System.currentTimeMillis() + (conversationDifference * 1000);
        }
    }

    public static int getConversationsToBeAdded(){
        return conversationsToBeAdded;
    }

    public static void conversationAdded(){
        conversationsToBeAdded = 0;
    }


    public static void stop(){
        running = false;
    }

    public static void setCurrentScreen(String screen){
        currentScreen = screen;
    }

    public static void addMessageCheckTime(String name, Element response){
        Random rand = new Random();
        long checkTime = System.currentTimeMillis() + ((rand.nextInt(messageDifference + 1) + messageMinimum) * 1000);
        messageCheckTimes.put(name, checkTime);
        messageCheckResponses.put(name, response);
    }

    public static void addConversation(String name, ArrayList<Element> conversation){
        conversations.put(name, conversation);
        conversationDepth.put(name, 0);
    }

    public static void addMessage(String name, Element message, int depth){
        conversations.get(name).add(message);
        conversationDepth.remove(name);
        conversationDepth.put(name, depth);
        if(!hasEnded.containsKey(name)) {
            hasEnded.put(name, false);
        }
    }

    public static void addInitials(String name, String initial){
        initials.put(name, initial);
    }

    public static void addGender(String name, Boolean isMales){
        isMale.put(name, isMales);
    }

    public static void setHasEnded(String name, Boolean value){
        hasEnded.put(name, value);
    }

    public static void setTotalConversations(int value){
        totalConversations = value;

        remainingConversations = new ArrayList<Integer>();
        for(int i = 0; i < totalConversations; i++){
            remainingConversations.add(i);
        }
    }

    public static void setGameRunning(boolean value){
        gameRunning = value;
    }


    public static boolean isGameRunning(){
        return gameRunning;
    }

    public static long getMessageCheckTime(String name){
        if(messageCheckTimes.containsKey(name)){
            return messageCheckTimes.get(name);
        }else{
            return 0;
        }
    }

    public static String getCurrentScreen(){
        return currentScreen;
    }

    public static boolean doesMessageCheckTimesContains(String name){
        return messageCheckTimes.containsKey(name);
    }

    public static void extendMessageCheckTimes(long extraTime){
        long oldWaitTime;
        for(Map.Entry<String, Long> entry : messageCheckTimes.entrySet()) {
            String name = entry.getKey();
            oldWaitTime = entry.getValue();
            messageCheckTimes.put(name, (oldWaitTime + extraTime));
        }
    }

    public static int getRandomConversation(){
        if(remainingConversations.isEmpty()){
            setTotalConversations(totalConversations);
        }

        Random rand = new Random();
        int selectedConversationIndex = rand.nextInt(remainingConversations.size());
        int selectedConversation = remainingConversations.get(selectedConversationIndex);
        remainingConversations.remove(selectedConversationIndex);
        return selectedConversation;
    }

    public static ArrayList<Element> getConversation(String key){
        return conversations.get(key);
    }

    public static Element getMessage(String key, int depth){
        return conversations.get(key).get(depth);
    }

    public static int getDepth(String key){
        return conversationDepth.get(key);
    }

    public static String getInitials(String name){
        return initials.get(name);
    }

    public static Boolean getIsMale(String name){
        return isMale.get(name);
    }

    public static Boolean getHasEnded(String name){
        return hasEnded.get(name);
    }

    public static boolean hasAnyConversationEnded(){
        boolean hasEnded = false;
        final Set<String> keys = conversations.keySet();
        for(final String key : keys){
            if(getHasEnded(key)){
                removeConversation(key);
                hasEnded = true;
            }
        }

        return hasEnded;
    }

    public static void removeConversation(String key){
        conversations.remove(key);
        conversationDepth.remove(key);
        initials.remove(key);
        isMale.remove(key);
        hasEnded.remove(key);
        ScoreHolder.removeConversation(key);
    }

    public static void resetVariables(){
        conversations = new ConcurrentHashMap<String, ArrayList<Element>>();
        conversationDepth = new HashMap<String, Integer>();
        initials = new HashMap<String, String>();
        isMale = new HashMap<String, Boolean>();
        hasEnded = new HashMap<String, Boolean>();
        totalConversations = 0;
        remainingConversations = new ArrayList<Integer>();
        messageCheckTimes = new ConcurrentHashMap<String, Long>();
        messageCheckResponses = new ConcurrentHashMap<String, Element>();
        conversationCheckTime = 0;
        messageDifference = 3;
        messageMinimum = 3;
        thread = new Thread();
        running = false;
        gameRunning = false;
    }

    public static ConcurrentHashMap<String, ArrayList<Element>> getConversations() {
        return conversations;
    }
}
