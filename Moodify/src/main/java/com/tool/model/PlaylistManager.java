package com.tool.model;

import com.tool.control.DoublyLinkedList;
import java.util.ArrayList;
import java.util.List;


public class PlaylistManager {
    private DoublyLinkedList playlist;
    private List<Song> songRegistry; // Keep track of songs for advanced operations

    public PlaylistManager() {
        this.playlist = new DoublyLinkedList();
        this.songRegistry = new ArrayList<>();
    }

    //  ADD SONG OPERATIONS


           //Add song to the beginning of playlist

    public  String addSongToBeginning(Song song){
        try {
            if (song == null) {
                return "Error: Cannot add null song";
            }

            // Check if song already exists

            if (songExists(song.getSongName(), song.getArtistName())) {
                return "Song already exists" + song.getDisplayInfo(); // Song already exists
            }

            playlist.insertBeginning(song.getSongName(), song.getArtistName(),
                    song.getSongPath(), song.getMoodScore());

            // Add to our registry at the beginning

            songRegistry.add(0, song);
            return "Song added to beginning: " + song.getDisplayInfo();
        } catch (Exception e) {
            return "Error adding song: " + e.getMessage();
        }
    }


            // Add song to the end of playlist (default behavior)

    public String addSong(Song song) {
        try {
            if (song == null) {
                return "Error: Cannot add null song";
            }

            // Check if song already exists

            if (songExists(song.getSongName(), song.getArtistName())) {
                return "Song already exists: " + song.getDisplayInfo(); // Song already exists
            }

            playlist.insertEnd(song.getSongName(), song.getArtistName(),
                    song.getSongPath(), song.getMoodScore());

            // Add to our registry

            songRegistry.add(song);
            return "Song added : " + song.getDisplayInfo();
        } catch (Exception e) {
            return "Error adding song: " + e.getMessage();
        }
    }


          //Add song with just basic info (uses default mood score)

    public String addSong(String songName, String artistName, String songPath) {
        Song song = new Song(songName, artistName, songPath);
        return addSong(song);
    }


         // Add song with all details including mood score

    public String addSong(String songName, String artistName, String songPath, int moodScore) {
        Song song = new Song(songName, artistName, songPath, moodScore);
        return addSong(song);
    }

              //  REMOVE SONG OPERATIONS


            // Remove song by exact name match

    public String removeSongByName(String songName) {
        if (songName == null || songName.trim().isEmpty()) {
            return "Error: Song name is null or empty";
        }

        return removeSongByNameAndArtist(songName, null);
    }


            // Remove song by name and artist

    public String removeSongByNameAndArtist(String songName, String artistName) {

        // Find the song in our registry
        Song songToRemove = null;
        int indexToRemove = -1;

        for (int i = 0; i < songRegistry.size(); i++) {
            Song song = songRegistry.get(i);
            boolean nameMatches = song.getSongName().equalsIgnoreCase(songName);
            boolean artistMatches = (artistName == null) ||
                    song.getArtistName().equalsIgnoreCase(artistName);

            if (nameMatches && artistMatches) {
                songToRemove = song;
                indexToRemove = i;
                break;
            }
        }

        if (songToRemove == null) {
            return "Song not Found" + songName + (artistName != null ? "by"+ artistName :""); // Song not found
        }

        // Remove from registry
        songRegistry.remove(indexToRemove);

        // Rebuild the playlist from scratch (workaround for private fields)
        rebuildPlaylist();

        return "Song removed: " + songToRemove.getDisplayInfo();
    }


              // Remove first song from playlist

    public String removeFirstSong() {
        if (songRegistry.isEmpty()) {
            return "Playlist is empty - nothing to remove";
        }
        Song removedSong = songRegistry.get(0);
        songRegistry.remove(0);
        rebuildPlaylist();
        return " Removed first song: " + removedSong.getDisplayInfo();
    }


                  // Remove last song from playlist

    public String removeLastSong() {
        if (songRegistry.isEmpty()) {
            return "Playlist is empty - nothing to remove";
        }
        Song removedSong = songRegistry.get(songRegistry.size() - 1);
        songRegistry.remove(songRegistry.size() - 1);
        rebuildPlaylist();
        return " Removed last song: " + removedSong.getDisplayInfo();
    }


           //Helper method to rebuild playlist after removal

    private void rebuildPlaylist() {
        // Clear the existing playlist
        playlist.clear();

        // Re-add all songs from registry
        for (Song song : songRegistry) {
            playlist.insertEnd(song.getSongName(), song.getArtistName(),
                    song.getSongPath(), song.getMoodScore());
        }
    }

                  //  SEARCH OPERATIONS


               // Search songs by name (partial match, case-insensitive)

    public List<Song> searchSongsByName(String searchTerm) {
        List<Song> results = new ArrayList<>();

        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return results;
        }

        for (Song song : songRegistry) {
            if (song.matchesSongName(searchTerm)) {
                results.add(song);
            }
        }

        return results;
    }


             // Search songs by artist (partial match, case-insensitive)

    public List<Song> searchSongsByArtist(String searchTerm) {
        List<Song> results = new ArrayList<>();

        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return results;
        }

        for (Song song : songRegistry) {
            if (song.matchesArtistName(searchTerm)) {
                results.add(song);
            }
        }

        return results;
    }


                          // Search for exact song match

    public Song findExactSong(String songName, String artistName) {
        for (Song song : songRegistry) {
            if (song.getSongName().equalsIgnoreCase(songName) &&
                    song.getArtistName().equalsIgnoreCase(artistName)) {
                return song;
            }
        }
        return null; // Not found
    }


     // Check if a song exists in the playlist

    public boolean songExists(String songName, String artistName) {
        return findExactSong(songName, artistName) != null;
    }

    //  UTILITY METHODS


                    // Get all songs as a list

    public List<Song> getAllSongs() {
        return new ArrayList<>(songRegistry);
    }


                   // Get playlist size

    public int getPlaylistSize() {
        return songRegistry.size();
    }


                   // Check if playlist is empty

    public boolean isEmpty() {
        return songRegistry.isEmpty();
    }


               // Clear entire playlist

    public void clearPlaylist() {
        playlist.clear();
        songRegistry.clear();
    }


                // Display entire playlist

    public void displayPlaylist() {
        if (isEmpty()) {
            System.out.println("Playlist is empty.");
            return;
        }

        System.out.println("\n=== Current Playlist ===");
        for (int i = 0; i < songRegistry.size(); i++) {
            System.out.printf("%d. %s\n", (i + 1), songRegistry.get(i).getDisplayInfo());
        }
        System.out.printf("Total songs: %d\n", songRegistry.size());
    }


           // Get current song (first song in playlist)

    public Song getCurrentSong() {
        if (songRegistry.isEmpty()) {
            return null;
        }
        return songRegistry.get(0);
    }


     // Get last song in playlist

    public Song getLastSong() {
        if (songRegistry.isEmpty()) {
            return null;
        }
        return songRegistry.get(songRegistry.size() - 1);
    }


                  // Display raw playlist using DoublyLinkedList's method

    public void displayRawPlaylist() {
        System.out.println("\n=== Raw Playlist (DoublyLinkedList) ===");
        playlist.printForward();
    }
}