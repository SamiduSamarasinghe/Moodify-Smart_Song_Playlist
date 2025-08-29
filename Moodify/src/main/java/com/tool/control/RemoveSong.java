package com.tool.control;

import com.tool.model.DoublyLinkedList;
import com.tool.model.Node;
import javax.swing.JOptionPane;

public class RemoveSong {
    private DoublyLinkedList playlist;

    public RemoveSong(DoublyLinkedList playlist) {
        this.playlist = playlist;
    }

    
      //Business logic to remove song from playlist
     
    public boolean removeSongFromPlaylist(String songName) {
        if (playlist == null || playlist.head == null) return false;
        
        Node current = playlist.head;
        while (current != null) {
            if (current.songName.equalsIgnoreCase(songName)) {
                // Remove the node using existing DoublyLinkedList methods
                if (current == playlist.head) {
                    playlist.deleteBegin();
                } else if (current == playlist.tail) {
                    playlist.deleteEnd();
                } else {
                    current.previousNode.nextNode = current.nextNode;
                    current.nextNode.previousNode = current.previousNode;
                }
                return true;
            }
            current = current.nextNode;
        }
        return false;
    }
}