package com.tool.gui;

import com.tool.control.DoublyLinkedList;
import com.tool.control.MoodShuffler;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ControlPanel extends JPanel {
    private DoublyLinkedList playlist;
    private PlaylistPanel playlistPanel;
    private JComboBox<String> moodComboBox;
    private JComboBox<String> intensityComboBox;

    public ControlPanel(DoublyLinkedList playlist, PlaylistPanel playlistPanel) {
        this.playlist = playlist;
        this.playlistPanel = playlistPanel;
        initialize();
    }

    private void initialize() {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        setBorder(BorderFactory.createEtchedBorder());

        // Add Song Button
        JButton addButton = new JButton("Add Song");
        addButton.addActionListener(this::addSong);
        add(addButton);

        // Remove Song Button
        JButton removeButton = new JButton("Remove Selected");
        removeButton.addActionListener(this::removeSong);
        add(removeButton);

        // Mood Selection
        add(new JLabel("Mood:"));
        moodComboBox = new JComboBox<>(new String[]{"Calm", "Neutral", "Energetic"});
        add(moodComboBox);

        // Intensity Selection
        add(new JLabel("Intensity:"));
        intensityComboBox = new JComboBox<>(new String[]{"Light", "Medium", "High"});
        add(intensityComboBox);

        // Shuffle Button
        JButton shuffleButton = new JButton("Shuffle");
        shuffleButton.addActionListener(this::shufflePlaylist);
        add(shuffleButton);

        // Sort Button
        JButton sortButton = new JButton("Sort by Mood");
        sortButton.addActionListener(this::sortPlaylist);
        add(sortButton);
    }

    private void addSong(ActionEvent e) {
        // Open dialog to add new song
        JTextField songField = new JTextField();
        JTextField artistField = new JTextField();
        JTextField pathField = new JTextField();
        JSpinner moodSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 10, 1));

        Object[] message = {
                "Song Name:", songField,
                "Artist:", artistField,
                "Path:", pathField,
                "Mood (1-10):", moodSpinner
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add New Song", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            playlist.insertEnd(songField.getText(), artistField.getText(), pathField.getText(),
                    (Integer) moodSpinner.getValue());
            playlistPanel.refreshPlaylist();
        }
    }

    private void removeSong(ActionEvent e) {
        // Implement remove functionality
        JOptionPane.showMessageDialog(this, "Remove functionality to be implemented");
    }

    private void shufflePlaylist(ActionEvent e) {
        int selectedMood = moodComboBox.getSelectedIndex() + 1; // Convert to 1,2,3
        int selectedIntensity = intensityComboBox.getSelectedIndex() + 1; // Convert to 1,2,3

        MoodShuffler.moodBasedShuffle(playlist, selectedMood, selectedIntensity);
        playlistPanel.refreshPlaylist();
    }

    private void sortPlaylist(ActionEvent e) {
        // Implement sort functionality
        JOptionPane.showMessageDialog(this, "Sort functionality to be implemented");
    }
}