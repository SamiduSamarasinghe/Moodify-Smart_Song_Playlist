package com.tool.control;

public class TestMoodShuffle {
    public static void main(String[] args){
        DoublyLinkedList myPlaylist = new DoublyLinkedList();
        myPlaylist.insertEnd("Calm Song", "Artist A", "path1", 2);
        myPlaylist.insertEnd("Energetic Song", "Artist B", "path2", 9);
        myPlaylist.insertEnd("Neutral Song", "Artist C", "path3", 5);
        myPlaylist.insertEnd("Very Calm Song", "Artist D", "path4", 1);
        myPlaylist.insertEnd("Hyper Song", "Artist E", "path5", 10);

        System.out.println("--- Original Playlist ---");
        myPlaylist.printForward();

        // Test LIGHT Intensity
        System.out.println("\n--- After LIGHT Shuffle (Calm Priority) ---");
        MoodShuffler.moodBasedShuffle(myPlaylist, MoodShuffler.MOOD_CALM, MoodShuffler.INTENSITY_LIGHT);
        myPlaylist.printForward();

        // Test HIGH Intensity
        System.out.println("\n--- After HIGH Shuffle (Calm Priority - should be random) ---");
        MoodShuffler.moodBasedShuffle(myPlaylist, MoodShuffler.MOOD_CALM, MoodShuffler.INTENSITY_HIGH);
        myPlaylist.printForward();

        // Test MEDIUM Intensity using the old method for compatibility
        System.out.println("\n--- After MEDIUM Shuffle (Energetic Priority) ---");
        MoodShuffler.moodBasedShuffle(myPlaylist, MoodShuffler.MOOD_ENERGETIC); // This uses the old method, defaults to MEDIUM
        myPlaylist.printForward();
    }
}
