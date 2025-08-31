package com.tool.controller;

import com.tool.model.DoublyLinkedList;
import com.tool.model.Node;
import javax.swing.JOptionPane;

public class AddfavSong {
    private DoublyLinkedList playlist;

    public AddfavSong(DoublyLinkedList playlist) {
        this.playlist = playlist;
    }

    // Business logic to toggle favorite status of a song
    public boolean toggleFavorite(String songName) {
        if (playlist == null || playlist.head == null) return false;

        Node current = playlist.head;
        while (current != null) {
            if (current.songName.equalsIgnoreCase(songName)) {
                // Toggle the favorite status
                current.setFavorite(!current.isFavorite());
                return true;
            }
            current = current.nextNode;
        }
        return false;
    }

    // Business logic to add a song to favorites
    public boolean addToFavorites(String songName) {
        if (playlist == null || playlist.head == null) return false;

        Node current = playlist.head;
        while (current != null) {
            if (current.songName.equalsIgnoreCase(songName)) {
                current.setFavorite(true);
                return true;
            }
            current = current.nextNode;
        }
        return false;
    }

    // Business logic to remove a song from favorites
    public boolean removeFromFavorites(String songName) {
        if (playlist == null || playlist.head == null) return false;

        Node current = playlist.head;
        while (current != null) {
            if (current.songName.equalsIgnoreCase(songName)) {
                current.setFavorite(false);
                return true;
            }
            current = current.nextNode;
        }
        return false;
    }
}