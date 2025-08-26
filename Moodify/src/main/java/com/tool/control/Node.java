package com.tool.control;

public class Node {
    Node nextNode;
    Node previousNode;

    String songName;
    String artistName;
    String songPath;
    int moodScore; //new field added(Sa)


//    int skipCount;
//    int playCount;
//    int songScore;

    //old constructor
    public  Node(String songName, String artistName, String songPath) {
        this.songName = songName;
        this.artistName = artistName;
        this.songPath = songPath;
        this.moodScore = 5; // assign default score
    }

    //new constructor for when mood score is provided(Sa)
    public Node(String songName,String artistName,String songPath ,int moodScore) {
        this.songName = songName;
        this.artistName = artistName;
        this.songPath = songPath;
        this.moodScore = moodScore;
    }

    public int getMoodScore() {
        return moodScore;
    }
    public void setMoodScore(int moodScore) {
        this.moodScore = moodScore;
    }
}
