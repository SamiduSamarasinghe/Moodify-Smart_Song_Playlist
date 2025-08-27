package com.tool.view;

import com.tool.model.DoublyLinkedList;
import com.tool.model.Node;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;

public class PlaylistPanel extends JPanel {
    private DoublyLinkedList playlist;
    private JTable playlistTable;
    private PlaylistTableModel tableModel;

    public PlaylistPanel(DoublyLinkedList playlist){
        this.playlist = playlist;
        initialize();
    }
    private void initialize(){
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Current Playlist"));

        //create table model and table
        tableModel = new PlaylistTableModel(playlist);
        playlistTable = new JTable(tableModel);

        add(new JScrollPane(playlistTable), BorderLayout.CENTER);
    }
    public void refreshPlaylist(){
        tableModel.fireTableDataChanged();
    }

    //custom table model for the playlist
    private class PlaylistTableModel extends AbstractTableModel {
        private final String[] columnNames = {"Song", "Artist", "Mood", "Path"};
        private DoublyLinkedList playlist;
        public PlaylistTableModel(DoublyLinkedList playlist){
            this.playlist = playlist;
        }

        @Override
        public int getRowCount() {
            return playlist.length();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            //add method to DoblyLinkedList to get node by index

            Node node = getNodeAt(rowIndex);
            if (node == null) return  "";

            switch (columnIndex) {
                case 0: return node.songName;
                case 1: return node.artistName;
                case 2: return node.getMoodScore();
                case 3: return node.songPath;
                default: return "";
            }
        }
    }
    //helper method
    //need to add this to DoublyLinkedList class
    private Node getNodeAt(int index){
        //implementation to get node at specific index
        return null;
    }

}
