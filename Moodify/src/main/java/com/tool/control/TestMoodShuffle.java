package com.tool.control;

public class TestMoodShuffle {
    public static void main(String[] args){
        //create new playList
        DoublyLinkedList myPlaylist = new DoublyLinkedList();

        //add songs with mood scores
        myPlaylist.insertEnd("Calm Song", "Artist A,", " path1", 2);
        myPlaylist.insertEnd("Energetic Song", "Artist,B ", "  path2", 9);
        myPlaylist.insertEnd("Neutral Song", "Artist C,", " path3", 5);
        myPlaylist.insertEnd("Very Calm Song", "Artist D,", " path4", 1);
        myPlaylist.insertEnd("Hyper Song", "Artist E,", " path5", 10);

        //display the original playlist
        System.out.println("--- Original Playlist ---");
        myPlaylist.printForward();

        //perform the mood-basedd shuffle
        MoodShuffler.moodBasedShuffle(myPlaylist);

        System.out.println("\n--- After Mood Shuffle ---");
        myPlaylist.printForward();
    }
}
