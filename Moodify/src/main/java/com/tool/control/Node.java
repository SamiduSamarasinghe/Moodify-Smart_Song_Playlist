package com.tool.control;

public class Node {
    Node nextNode;
    Node previousNode;

    String songName;
    String artistName;
    String songPath;

//    int skipCount;
//    int playCount;
//    int songScore;

    public Node(String songName,String artistName,String songPath){
        this.songName = songName;
        this.artistName = artistName;
        this.songPath = songPath;
    }
}
