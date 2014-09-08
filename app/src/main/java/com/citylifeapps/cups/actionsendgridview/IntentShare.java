package com.citylifeapps.cups.actionsendgridview;

import android.content.Intent;
import android.graphics.drawable.Drawable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yonatan Moskovich on 07/09/2014
 */
public class IntentShare {

    private Drawable bitmap;
    private String label;
    private Intent intent=new Intent(Intent.ACTION_SEND);
    private int precedence=99;
    private Map<String,Integer> precedenceMap= new HashMap<String,Integer>();


    public IntentShare() {
        this.intent.setType("text/plain");
    }

    public void setBitmap(Drawable bitmap) {
        this.bitmap=bitmap;
    }

    public void setLabel(String label) {

        this.label=label;

        if (precedenceMap.containsKey(label.toUpperCase())) {
            precedence=precedenceMap.get(label.toUpperCase());
        }
    }

    public void setPackageName(String packageName) {
        this.intent.setPackage(packageName);
    }

    public void setSubject(String subject) {
        this.intent.putExtra(Intent.EXTRA_SUBJECT,subject);
    }

    public void setPayload(String payload) {
        this.intent.putExtra(Intent.EXTRA_TEXT,payload);
    }

    public void setIntent(Intent intent) {
        this.intent=intent;
    }

    public Drawable getBitmap() {
        return bitmap;
    }

    public String getLabel() {
        return label;
    }

    public Intent getIntent() {
        return intent;
    }

    public int getPrecedence() {
        return precedence;
    }

    public void setPrecedenceMap(Map<String,Integer> precedenceMap) {
        this.precedenceMap=precedenceMap;

        Map<String,Integer> UpperCasedMap = new HashMap<String, Integer>();

        for (Map.Entry<String, Integer> entry : precedenceMap.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();

            UpperCasedMap.put(key.toUpperCase(),value);
        }

        precedenceMap.clear();
        precedenceMap.putAll(UpperCasedMap);
    }

}
