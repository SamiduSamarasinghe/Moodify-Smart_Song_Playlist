package com.tool.control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MoodShuffler {

    public static final int MOOD_CALM = 1;
    public static final int MOOD_NEUTRAL = 2;
    public static final int MOOD_ENERGETIC = 3;

    // New Constants for Shuffle Intensity
    public static final int INTENSITY_LIGHT = 1;
    public static final int INTENSITY_MEDIUM = 2;
    public static final int INTENSITY_HIGH = 3;

    // Overloaded method for backward compatibility (uses medium intensity by default)
    public static void moodBasedShuffle(DoublyLinkedList playlist, int targetMood) {
        moodBasedShuffle(playlist, targetMood, INTENSITY_MEDIUM);
    }

    // Updated main method with intensity parameter
    public static void moodBasedShuffle(DoublyLinkedList playlist, int targetMood, int intensity) {
        if(playlist == null || playlist.head == null || playlist.head == playlist.tail) {
            System.out.println("Playlist is empty or has only one song. No shuffle needed.");
            return;
        }

        // 1. Separate the songs into mood categories
        List<Node> calmSongs = new ArrayList<>();
        List<Node> neutralSongs = new ArrayList<>();
        List<Node> energeticSongs = new ArrayList<>();

        Node currentNode = playlist.head;
        while (currentNode != null) {
            int score = currentNode.getMoodScore();
            if(score <= 3) {
                calmSongs.add(currentNode);
            } else if(score <= 7) {
                neutralSongs.add(currentNode);
            } else {
                energeticSongs.add(currentNode);
            }
            currentNode = currentNode.nextNode;
        }

        // 2. Apply the chosen intensity
        switch(intensity) {
            case INTENSITY_LIGHT:
                // Shuffle each category individually but maintain group order
                Collections.shuffle(calmSongs);
                Collections.shuffle(neutralSongs);
                Collections.shuffle(energeticSongs);

                // Rebuild playlist in original group order (calm -> neutral -> energetic)
                playlist.clear();
                addNodeListToEnd(playlist, calmSongs);
                addNodeListToEnd(playlist, neutralSongs);
                addNodeListToEnd(playlist, energeticSongs);
                System.out.println("Light shuffle applied: Songs shuffled within their mood groups.");
                return;

            case INTENSITY_MEDIUM:
                // Your original algorithm - shuffle groups and reorder by target mood
                Collections.shuffle(calmSongs);
                Collections.shuffle(neutralSongs);
                Collections.shuffle(energeticSongs);
                break;

            case INTENSITY_HIGH:
                // Combine all songs and shuffle completely (ignore moods)
                List<Node> allSongs = new ArrayList<>();
                allSongs.addAll(calmSongs);
                allSongs.addAll(neutralSongs);
                allSongs.addAll(energeticSongs);
                Collections.shuffle(allSongs);

                playlist.clear();
                addNodeListToEnd(playlist, allSongs);
                System.out.println("High intensity shuffle applied: Fully random!");
                return;

            default:
                System.out.println("Invalid intensity. Using MEDIUM.");
                // Proceed with MEDIUM logic
                Collections.shuffle(calmSongs);
                Collections.shuffle(neutralSongs);
                Collections.shuffle(energeticSongs);
                break;
        }

        // 3. For MEDIUM intensity: rebuild playlist based on target mood priority
        playlist.clear();
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
        System.out.println("Playlist has been shuffled based on mood! Priority: " +
                getMoodName(targetMood) + ", Intensity: " + getIntensityName(intensity));
    }

    // Helper method to add a list of nodes to the end of the playlist
    private static void addNodeListToEnd(DoublyLinkedList playlist, List<Node> nodes) {
        for (Node node : nodes) {
            playlist.insertEnd(node.songName, node.artistName, node.songPath, node.getMoodScore());
        }
    }

    // Helper method to get mood name for output
    private static String getMoodName(int moodType) {
        switch (moodType) {
            case MOOD_CALM: return "Calm";
            case MOOD_NEUTRAL: return "Neutral";
            case MOOD_ENERGETIC: return "Energetic";
            default: return "Unknown";
        }
    }

    // New helper method to get intensity name for output
    private static String getIntensityName(int intensity) {
        switch (intensity) {
            case INTENSITY_LIGHT: return "LIGHT";
            case INTENSITY_MEDIUM: return "MEDIUM";
            case INTENSITY_HIGH: return "HIGH";
            default: return "UNKNOWN";
        }
    }
}