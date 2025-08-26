package com.tool.control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MoodShuffler {

    public static final int MOOD_CALM = 1;
    public static final int MOOD_NEUTRAL = 2;
    public static final int MOOD_ENERGETIC = 3;

    //this method takes a DoublyLinkedList and performs the mood-based shuffle on it
    public static void moodBasedShuffle(DoublyLinkedList playlist, int targetMood){
        if(playlist == null || playlist.head == null || playlist.head == playlist.tail){
            System.out.println("Playlist is empty or has only one song.No shuffle needed.");
            return;
        }

        // 1. separate the songs into mood categories
        List<Node> calmSongs = new ArrayList<>();
        List<Node> neutralSongs = new ArrayList<>();
        List<Node> energeticSongs = new ArrayList<>();

        Node currentNode = playlist.head;
        while (currentNode != null){
            int score = currentNode.getMoodScore();
            if(score <= 3){
                calmSongs.add(currentNode);
            }else if(score <= 7){
                neutralSongs.add(currentNode);
            }else {
                energeticSongs.add(currentNode);
            }
            currentNode = currentNode.nextNode;
        }
        // 2. shuffle each category individually
        Collections.shuffle(calmSongs);
        Collections.shuffle(neutralSongs);
        Collections.shuffle(energeticSongs);

        // 3. clear the original playlist
        playlist.clear();

        //4. rebuild the playlist based on user's target mood
        switch(targetMood) {
            case MOOD_CALM:

                addNodeListToEnd(playlist, calmSongs);
                addNodeListToEnd(playlist, neutralSongs);
                addNodeListToEnd(playlist, energeticSongs);
                break;
            case MOOD_NEUTRAL:
                addNodeListToEnd(playlist, neutralSongs);
                addNodeListToEnd(playlist, calmSongs);
                addNodeListToEnd(playlist, energeticSongs);
                break;
            case MOOD_ENERGETIC:
                addNodeListToEnd(playlist, energeticSongs);
                addNodeListToEnd(playlist, neutralSongs);
                addNodeListToEnd(playlist, calmSongs);
                break;
            default:
                System.out.println("Invalid mood choice. Using default order.");
                addNodeListToEnd(playlist, calmSongs);
                addNodeListToEnd(playlist, neutralSongs);
                addNodeListToEnd(playlist, energeticSongs);
        }
        System.out.println("Playlist has been shuffled based on mood! Priority: " + getMoodName(targetMood));
    }

    //helper method to add a list of nodes to the end of the playlist
    private static void addNodeListToEnd(DoublyLinkedList playlist, List<Node> nodes){
        for (Node node : nodes){
            //use the method that includes moodScore to show the data
            playlist.insertEnd(node.songName, node.artistName, node.songPath, node.getMoodScore());
        }
    }

    //helper method to get mood name for output
    private static String getMoodName(int moodType){
        switch (moodType){
            case MOOD_CALM: return "Calm";
            case MOOD_NEUTRAL: return "Neutral";
            case MOOD_ENERGETIC: return "Energetic";
            default: return "Unknown";
        }
    }
}
