package com.subzero.textingsimulator;

import android.content.Context;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import java.util.ArrayList;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class ReadXMLFile{

    Document document;
    Element currentNode;
    String origin;
    String[] responses;
    Element[] responseNodes;
    int currentDepth;
    int numOfChildren;
    Random rand;
    int conversation;
    Element node;

    public ReadXMLFile(Context context){
        try{
            document = parseXML(new InputSource(context.getResources().openRawResource(R.raw.conversations)));
            //http://www.mkyong.com/java/how-to-read-xml-file-in-java-dom-parser/
            responses = new String[4];
            responseNodes = new Element[4];

            Conversations.setTotalConversations(document.getElementsByTagName("conversation").getLength());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Element selectRandomConversation(){
        currentDepth = 0;
        origin = new String();
        responses = new String[4];
        responseNodes = new Element[50];
        //numOfChildren = document.getElementsByTagName("conversation").getLength();
        rand = new Random();
        //conversation = rand.nextInt(numOfChildren);
        conversation = Conversations.getRandomConversation();

        node = (Element)document.getElementsByTagName("conversation").item(conversation);
        node = (Element)node.getElementsByTagName("message"+currentDepth).item(0); // Selects message
        currentNode = (Element)node.getElementsByTagName("contents").item(0);
        origin = currentNode.getTextContent();

        currentDepth = 1;
        numOfChildren = node.getElementsByTagName("message"+(currentDepth)).getLength();
        for(int i = 0; i < numOfChildren; i++){
            responseNodes[i] = (Element)node.getElementsByTagName("message"+(currentDepth)).item(i);
            responses[i] = responseNodes[i].getElementsByTagName("contents").item(0).getTextContent();

        }
        return node;
    }

    /**
     *
     * @param selectedNode
     * @param currentDepth
     * @return  A HashMap containing the response element and the current depth
     */
    public static ArrayList<Element> getResponses(Element selectedNode, int currentDepth){
        ArrayList<Element> responseNodes = new ArrayList<Element>();
        int children = selectedNode.getElementsByTagName("message"+(currentDepth+1)).getLength();
        for(int i = 0; i < children; i++) {
            responseNodes.add((Element) selectedNode.getElementsByTagName("message" + (currentDepth+1)).item(i));
        }
        return responseNodes;
    }

    public static String getMessageScore(Element selectedNode, int currentDepth){
        return selectedNode.getElementsByTagName("score").item(0).getTextContent();
    }

    public static ArrayList<Element> getResponsesText(ArrayList<Element> responseNode){
        ArrayList<Element> responses = new ArrayList<Element>();
        for(Element e : responseNode){
            responses.add((Element)e.getElementsByTagName("contents").item(0));
        }
        return responses;
    }

    public static Element getElementText(Element node){
        return (Element)node.getElementsByTagName("contents").item(0);
    }

    public Element processResponse(int currentDepth, Element selectedNode){
        node = selectedNode; // Message
        currentNode = (Element)selectedNode.getElementsByTagName("contents").item(0); // Text
        responseNodes = new Element[4];
        responses = new String[4];

        currentDepth++;
        numOfChildren = node.getElementsByTagName("message"+currentDepth).getLength();
        conversation = rand.nextInt(numOfChildren);
        node = (Element)node.getElementsByTagName("message"+currentDepth).item(conversation); // Message
        currentNode = (Element)node.getElementsByTagName("contents").item(0); // Text
        return node;
    }

    public Document parseXML(InputSource source) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(false);
            dbf.setValidating(false);
            DocumentBuilder db = dbf.newDocumentBuilder();
            return db.parse(source);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
