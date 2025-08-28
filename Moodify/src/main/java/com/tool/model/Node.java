package com.tool.model;

import java.io.Serializable;

public class Node implements Serializable {
    // Add serialVersionUID for version control
    private static final long serialVersionUID = 1L;
    
    public Node nextNode;
    public Node previousNode;

    public String songName;
    public String artistName;
    public String songPath;
    int moodScore;
    // Add duration field
    private int duration; // in seconds

    // Updated constructor to include duration
    public Node(String songName, String artistName, String songPath, int duration) {
        this.songName = songName;
        this.artistName = artistName;
        this.songPath = songPath;
        this.duration = duration;
        this.moodScore = 5; // assign default score
    }

    // Updated constructor for when mood score is provided
    public Node(String songName, String artistName, String songPath, int duration, int moodScore) {
        this.songName = songName;
        this.artistName = artistName;
        this.songPath = songPath;
        this.duration = duration;
        this.moodScore = moodScore;
    }

    public int getMoodScore() {
        return moodScore;
    }
    
    public void setMoodScore(int moodScore) {
        this.moodScore = moodScore;
    }
    
    public int getDuration() {
        return duration;
    }
    
    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public String toString(){
        return "Song: " + songName + '\'' +
                "Artist: " + artistName +
                "Mood: " + moodScore;
    }
}