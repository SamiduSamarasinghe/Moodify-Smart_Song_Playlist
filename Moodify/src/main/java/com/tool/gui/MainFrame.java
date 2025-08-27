package com.tool.gui;

import com.tool.control.DoublyLinkedList;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private DoublyLinkedList playlist;
    private PlaylistPanel playlistPanel;
    private ControlPanel controlPanel;

    public MainFrame() {
        super("Moodify - Smart Playlist Manager");
        this.playlist = new DoublyLinkedList();
        initializeGUI();
    }

    private void initializeGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null); // Center on screen

        // Create main panels
        playlistPanel = new PlaylistPanel(playlist);
        controlPanel = new ControlPanel(playlist, playlistPanel);

        // Add to content pane
        setLayout(new BorderLayout());
        add(playlistPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        setVisible(true);
    }
    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainFrame();
        });
    }
}