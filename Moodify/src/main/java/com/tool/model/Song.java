package com.tool.model;


public class Song {
    private String songName;
    private String artistName;
    private String songPath;
    private int moodScore;

    // Constants for validation
    public static final int MIN_MOOD_SCORE = 1;
    public static final int MAX_MOOD_SCORE = 10;
    public static final int DEFAULT_MOOD_SCORE = 5;


       //Constructor with all parameters including mood score

    public Song(String songName, String artistName, String songPath, int moodScore) {
        if (isValidSongData(songName, artistName, songPath)) {
            this.songName = songName.trim();
            this.artistName = artistName.trim();
            this.songPath = songPath.trim();
            this.moodScore = isValidMoodScore(moodScore) ? moodScore : DEFAULT_MOOD_SCORE;
        } else {
            throw new IllegalArgumentException("Invalid song data provided");
        }
    }


                    // Constructor without mood score (uses default)

    public Song(String songName, String artistName, String songPath) {
        this(songName, artistName, songPath, DEFAULT_MOOD_SCORE);
    }


                        // Validate song data

    private boolean isValidSongData(String songName, String artistName, String songPath) {
        return songName != null && !songName.trim().isEmpty() &&
                artistName != null && !artistName.trim().isEmpty() &&
                songPath != null && !songPath.trim().isEmpty();
    }


                          //  Validate mood score range

    private boolean isValidMoodScore(int moodScore) {
        return moodScore >= MIN_MOOD_SCORE && moodScore <= MAX_MOOD_SCORE;
    }

                 // Getters
    public String getSongName() {
        return songName;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getSongPath() {
        return songPath;
    }

    public int getMoodScore() {
        return moodScore;
    }

                 // Setters with validation
    public void setSongName(String songName) {
        if (songName != null && !songName.trim().isEmpty()) {
            this.songName = songName.trim();
        }
    }

    public void setArtistName(String artistName) {
        if (artistName != null && !artistName.trim().isEmpty()) {
            this.artistName = artistName.trim();
        }
    }

    public void setSongPath(String songPath) {
        if (songPath != null && !songPath.trim().isEmpty()) {
            this.songPath = songPath.trim();
        }
    }

    public void setMoodScore(int moodScore) {
        if (isValidMoodScore(moodScore)) {
            this.moodScore = moodScore;
        }
    }


               // Check if song matches search criteria

    public boolean matchesSongName(String searchTerm) {
        return songName.toLowerCase().contains(searchTerm.toLowerCase());
    }

    public boolean matchesArtistName(String searchTerm) {
        return artistName.toLowerCase().contains(searchTerm.toLowerCase());
    }

    public boolean matchesExactSongName(String songName) {
        return this.songName.equalsIgnoreCase(songName);
    }


                     // Get mood description based on score

    public String getMoodDescription() {
        if (moodScore <= 2) return "Very Sad";
        else if (moodScore <= 4) return "Sad";
        else if (moodScore <= 6) return "Neutral";
        else if (moodScore <= 8) return "Happy";
        else return "Very Happy";
    }


                        // Display formatted song information

    public String getDisplayInfo() {
        return String.format("♪ %s - %s | Mood: %d (%s)",
                songName, artistName, moodScore, getMoodDescription());
    }


                     // Get detailed information

    public String getDetailedInfo() {
        return String.format("Song: %s\nArtist: %s\nPath: %s\nMood Score: %d (%s)",
                songName, artistName, songPath, moodScore, getMoodDescription());
    }

    @Override
    public String toString() {
        return getDisplayInfo();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Song song = (Song) obj;
        return songName.equalsIgnoreCase(song.songName) &&
                artistName.equalsIgnoreCase(song.artistName);
    }

    @Override
    public int hashCode() {
        return songName.toLowerCase().hashCode() + artistName.toLowerCase().hashCode();
    }
}