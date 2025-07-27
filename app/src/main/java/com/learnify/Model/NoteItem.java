package com.learnify.Model;

public class NoteItem {
    private float timestamp;
    private String text;

    public NoteItem() {} // Required for Firestore

    public NoteItem(float timestamp, String text) {
        this.timestamp = timestamp;
        this.text = text;
    }

    public float getTimestamp() { return timestamp; }
    public String getText() { return text; }
}
