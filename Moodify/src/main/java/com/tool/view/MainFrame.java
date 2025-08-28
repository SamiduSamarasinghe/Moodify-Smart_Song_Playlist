/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.tool.view;

import javax.swing.*;
import java.awt.*;
import com.tool.model.DoublyLinkedList;
import com.tool.model.Node;

import com.tool.control.MoodShuffler;



public class MainFrame extends JFrame {
    
    private DoublyLinkedList playlist; //data model
    private JList<String> playListJList; //display songss names
    private DefaultListModel<String> listModel; //the data model for jlist
    
    //input feilds
    private JTextField titleTextField;
    private JTextField artistTextField;
    private JTextField durationTextField;
    private JSlider moodSlider;
    private JTextField searchField;
    
    private JTextField urlTextField;
    private JComboBox<String> moodDropdown;


    public MainFrame() {
        playlist = new DoublyLinkedList();
        initializeUI();
    }
    private void initializeUI() {
        // 1. Basic JFrame setup
        setTitle("Music Playlist Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10)); // Main layout with gaps
        getContentPane().setBackground(Color.LIGHT_GRAY);

        // 2. Build the North Panel (Input Form)
        JPanel inputPanel = createInputPanel();
        add(inputPanel, BorderLayout.NORTH);

        // 3. Build the Center Panel (Playlist View)
        JPanel centerPanel = createCenterPanel();
        add(centerPanel, BorderLayout.CENTER);

        // 4. Build the South Panel (Controls)
        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.SOUTH);

        // 5. Finalize and display the JFrame
        pack(); // Sizes the window to fit its components
        setLocationRelativeTo(null); // Center on screen
        setVisible(true);
    }
    private JPanel createInputPanel() {
    JPanel panel = new JPanel(new GridBagLayout());
    panel.setBorder(BorderFactory.createTitledBorder("Add New Song"));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5); // Padding
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1.0;

    // Row 0: Song Title
    gbc.gridx = 0; gbc.gridy = 0;
    panel.add(new JLabel("Song Title:"), gbc);
    gbc.gridx = 1;
    titleTextField = new JTextField(20);
    panel.add(titleTextField, gbc);

    // Row 1: Artist
    gbc.gridx = 0; gbc.gridy = 1;
    panel.add(new JLabel("Artist:"), gbc);
    gbc.gridx = 1;
    artistTextField = new JTextField(20);
    panel.add(artistTextField, gbc);

    // Row 2: Duration
    gbc.gridx = 0; gbc.gridy = 2;
    panel.add(new JLabel("Duration (MM:SS):"), gbc);
    gbc.gridx = 1;
    durationTextField = new JTextField(10);
    durationTextField.setToolTipText("e.g., 3:45");
    panel.add(durationTextField, gbc);

    // Row 3: YouTube URL (NEW FIELD)
    gbc.gridx = 0; gbc.gridy = 3;
    panel.add(new JLabel("YouTube URL:"), gbc);
    gbc.gridx = 1;
    JTextField urlTextField = new JTextField(20); // Create this as a class field if needed elsewhere
    panel.add(urlTextField, gbc);

    // Row 4: Mood Selection (NEW DROPDOWN INSTEAD OF SLIDER)
    gbc.gridx = 0; gbc.gridy = 4;
    panel.add(new JLabel("Mood:"), gbc);
    gbc.gridx = 1;
    
    // Create the dropdown with mood options
    String[] moods = {"Select Mood", "Calm", "Neutral", "Energetic"};
    JComboBox<String> moodDropdown = new JComboBox<>(moods);
    moodDropdown.setSelectedIndex(0); // Start with "Select Mood"
    panel.add(moodDropdown, gbc);

    // Row 5: Add Song Button
    gbc.gridx = 0; gbc.gridy = 5;
    gbc.gridwidth = 2; // Make button span both columns
    gbc.anchor = GridBagConstraints.CENTER;
    JButton addButton = new JButton("Add Song");
    panel.add(addButton, gbc);

    return panel;
    }
    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Search bar at the top of the center panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search:"));
        searchField = new JTextField(20);
        searchPanel.add(searchField);
        JButton searchButton = new JButton("Go");
        searchPanel.add(searchButton);
        panel.add(searchPanel, BorderLayout.NORTH);

        // The main playlist display
        listModel = new DefaultListModel<>();
        playListJList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(playListJList);
        scrollPane.setPreferredSize(new Dimension(500, 300));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Playlist Controls"));

        String[] buttonLabels = {"Play", "Pause", "Next", "Previous", "Sort by Mood", "Mood Shuffle", "Clear All"};
        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            // Add your action listeners here later
            if (label.equals("Mood Shuffle")) {
                button.addActionListener(e -> performMoodShuffle());
            }
            panel.add(button);
        }

        return panel;
    }
    // YOUR METHOD TO HANDLE THE SHUFFLE
    private void performMoodShuffle() {
        // This is where you call your MoodShuffler code
        if (playlist != null && playlist.head != null) {
            // For now, using defaults. You can add a dialog to choose mood/intensity later.
            MoodShuffler.moodBasedShuffle(playlist, MoodShuffler.MOOD_CALM, MoodShuffler.INTENSITY_MEDIUM);
            updatePlayListDisplay(); // Refresh the JList
            JOptionPane.showMessageDialog(this, "Playlist shuffled based on mood!");
        } else {
            JOptionPane.showMessageDialog(this, "Playlist is empty!", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }
    // Method to update the JList from the DoublyLinkedList
    private void updatePlayListDisplay() {
        listModel.clear();
        if (playlist != null && playlist.head != null) {
            Node current = playlist.head;
            while (current != null) {
                listModel.addElement(current.songName + " - " + current.artistName + " [" + current.getMoodScore() + "]");
                current = current.nextNode;
            }
        }
    }
    public static void main(String[] args) {
        // Use this to start your application
        SwingUtilities.invokeLater(() -> new MainFrame());
    }
}

/*
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        playListJList = new javax.swing.JList<>();
        jPanel2 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Moodify Playlist");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jScrollPane1.setViewportView(playListJList);

        jButton1.setText("jButton1");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jButton1)
                .addGap(0, 25, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(63, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addGap(14, 14, 14))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(156, 156, 156)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(179, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(77, 77, 77)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(93, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(158, 158, 158)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(93, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
*/
    
/*
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList<String> playListJList;
    // End of variables declaration//GEN-END:variables
*/
