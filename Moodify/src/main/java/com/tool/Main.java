package com.tool;

import com.tool.model.Song;
import com.tool.model.PlaylistManager;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("Testing Fixed PlaylistManager (Member 2) \n");

        PlaylistManager manager = new PlaylistManager();

        // Test adding songs
        System.out.println("1. Testing ADD operations:");
        System.out.println(manager.addSong("Shape of You", "Ed Sheeran", "/music/shape.mp3", 8));
        System.out.println("Removed Blinding Lights: " + manager.removeSongByName("Blinding Lights"));
        System.out.println("Added Watermelon Sugar: " + manager.addSong("Watermelon Sugar", "Harry Styles", "/music/watermelon.mp3", 9));

        // Try adding duplicate
        System.out.println("Attempted duplicate: " + manager.addSong("Shape of You", "Ed Sheeran", "/music/shape2.mp3", 6) + " (should be false)");

        manager.displayPlaylist();

        // Test search operations
        System.out.println("\n2. Testing SEARCH operations:");
        List<Song> shapeResults = manager.searchSongsByName("Shape");
        System.out.println("Search 'Shape' found: " + shapeResults.size() + " songs");
        for (Song song : shapeResults) {
            System.out.println("  - " + song.getDisplayInfo());
        }

        List<Song> edResults = manager.searchSongsByArtist("Ed");
        System.out.println("Search 'Ed' found: " + edResults.size() + " songs");

        // Test remove operations
        System.out.println("\n3. Testing REMOVE operations:");
        System.out.println("Removed Blinding Lights: " + manager.removeSongByName("Blinding Lights"));

        manager.displayPlaylist();

        System.out.println("\n Member 2 Functionality Test Complete! ");
    }
}