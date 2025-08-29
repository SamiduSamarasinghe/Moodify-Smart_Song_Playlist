/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.tool.view;

import com.tool.control.MoodShuffler;
import com.tool.control.PlaylistSaveHelper;
import com.tool.control.PlaylistSorter;
import com.tool.model.DoublyLinkedList;
import com.tool.model.Node;
import java.awt.*;
import javax.swing.*;

public class MainFrame extends JFrame {
    
    private DoublyLinkedList playlist; //data model
    private JList<String> playListJList; //display songss names
    private DefaultListModel<String> listModel; //the data model for jlist
    private PlaylistSorter playlistSorter;
    
    //input feilds
    private JTextField titleTextField;
    private JTextField artistTextField;
    private JTextField durationTextField;
    private JSlider moodSlider;
    private JTextField searchField;
    
    private JTextField urlTextField;
    private JComboBox<String> moodDropdown;
    
    private Node currentNode; //to track the currently playing song
    private boolean isPlaying = false; //to track play, pause 
    
    private boolean autoPlayEnabled = false;
    private Timer autoPlayTimer; // for smart auto play
    
    public MainFrame() {
        playlist = PlaylistSaveHelper.loadPlaylistFromFile();
        playlistSorter = new PlaylistSorter();
        initializeUI();
        updatePlayListDisplay();
    
    // Add window listener to save on exit
    addWindowListener(new java.awt.event.WindowAdapter() {
        @Override
        public void windowClosing(java.awt.event.WindowEvent windowEvent) {
            PlaylistSaveHelper.savePlaylistToFile(playlist);
        }
    });
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
        addRightClickMenu();
      
        
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
    urlTextField = new JTextField(20);
    panel.add(urlTextField, gbc);

    // Row 4: Mood Selection
    gbc.gridx = 0; gbc.gridy = 4;
    panel.add(new JLabel("Mood:"), gbc);
    gbc.gridx = 1;
    
    String[] moods = {"Select Mood", "Calm", "Neutral", "Energetic"};
    moodDropdown = new JComboBox<>(moods);
    moodDropdown.setSelectedIndex(0);
    panel.add(moodDropdown, gbc);

    // Row 5: Add Song Button
    gbc.gridx = 0; gbc.gridy = 5;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.CENTER;
    JButton addButton = new JButton("Add Song");
    
    addButton.addActionListener(e -> {
        String title = titleTextField.getText().trim();
        String artist = artistTextField.getText().trim();
        String durationStr = durationTextField.getText().trim();
        String url = urlTextField.getText().trim();
        int moodScore = moodDropdown.getSelectedIndex();
        
        if (title.isEmpty() || artist.isEmpty() || durationStr.isEmpty() || url.isEmpty() || moodScore == 0) {
        JOptionPane.showMessageDialog(this, 
                "Please fill all fields and select a mood!", 
                "Input Error", JOptionPane.ERROR_MESSAGE);
        return;
    }
        int durationInSeconds = 0;
    try {
        String[] parts = durationStr.split(":");
        if (parts.length == 2) {
            int minutes = Integer.parseInt(parts[0]);
            int seconds = Integer.parseInt(parts[1]);
            durationInSeconds = minutes * 60 + seconds;
        } else {
            throw new NumberFormatException("Invalid format");
        }
    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, 
                "Invalid duration format. Please use MM:SS (e.g., 3:45).", 
                "Input Error", JOptionPane.ERROR_MESSAGE);
        return;
    }
    playlist.insertSong(title, artist, durationInSeconds, url, moodScore);
    
    PlaylistSaveHelper.savePlaylistToFile(playlist);
    
    titleTextField.setText("");
    artistTextField.setText("");
    durationTextField.setText("");
    urlTextField.setText("");
    moodDropdown.setSelectedIndex(0);

    updatePlayListDisplay();

    JOptionPane.showMessageDialog(this, "Song added successfully and playlist saved!");
    });
    panel.add(addButton, gbc);

    return panel;
    }
    
    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search:"));
        searchField = new JTextField(20);
        searchPanel.add(searchField);
        JButton searchButton = new JButton("Go");
        searchPanel.add(searchButton);
        
        JButton favoritesButton = new JButton("⭐ Favorites");
        favoritesButton.addActionListener(e -> showFavorites());
        searchPanel.add(favoritesButton);
        
        panel.add(searchPanel, BorderLayout.NORTH);
        
    searchButton.addActionListener(e -> searchSongs());
    
    searchPanel.add(searchButton);
    panel.add(searchPanel, BorderLayout.NORTH);

        listModel = new DefaultListModel<>();
        playListJList = new JList<>(listModel);
        playListJList.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mouseClicked(java.awt.event.MouseEvent e) {
            if (e.getClickCount() == 2) {
                int index = playListJList.locationToIndex(e.getPoint());
                if (index >= 0) {
                    toggleFavorite(index);
                }
            }
        }
        });
        
        JScrollPane scrollPane = new JScrollPane(playListJList);
        scrollPane.setPreferredSize(new Dimension(500, 300));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }
    
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Playlist Controls"));
        
        JCheckBox autoPlayCheckbox = new JCheckBox("Auto-Play");
        autoPlayCheckbox.addActionListener(e -> autoPlayEnabled = autoPlayCheckbox.isSelected());
        panel.add(autoPlayCheckbox);

        // ADDED "Save Playlist" BUTTON TO THE BUTTON LABELS ARRAY
        String[] buttonLabels = {"Play", "Pause", "Next", "Previous", "Sort", "Mood Shuffle", "Save Playlist", "Clear All"};
        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            
            switch (label) {
                case "Play":
                    button.addActionListener(e -> playSong());
                    break;
                case "Pause":
                    button.addActionListener(e -> pauseSong());
                    break;
                case "Next": 
                    button.addActionListener(e -> nextSong());
                    break;
                case "Previous":
                    button.addActionListener(e -> previousSong());
                    break;
                case "Mood Shuffle":
                    button.addActionListener(e -> performMoodShuffle());
                    break;
                case "Clear All":
                    button.addActionListener(e -> clearPlaylist());
                    break;
                case "Sort":
                    button.addActionListener(e-> perfromMoodSort());
                    break;
                case "Save Playlist": // NEW CASE FOR SAVE PLAYLIST BUTTON
                    button.addActionListener(e -> savePlaylistManual());
                    break;
                default:
                    break;
            }            
            panel.add(button);
        }
        return panel;
    }
    
    // NEW METHOD TO HANDLE MANUAL SAVE PLAYLIST
    private void savePlaylistManual() {
        if (playlist == null || playlist.head == null) {
            JOptionPane.showMessageDialog(this, 
                "Playlist is empty! Nothing to save.", 
                "Save Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        PlaylistSaveHelper.savePlaylistmanual(this, playlist);
    }
    
    private void perfromMoodSort(){
        String[] sortOptions = {"Sort By Mood","Sort By Duration"};
        
        int result = JOptionPane.showOptionDialog(
                null,"Select Sorting Method","Sort Options",
                JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,null,sortOptions,sortOptions[0]);
        
        if(result == 0){
            String[] moodList = {"Calm","Neutral","Energetic"};
        
            String selectedMood = (String)JOptionPane.showInputDialog(this,"Select a mood to sort by",
                    "Mood Sort",JOptionPane.QUESTION_MESSAGE,null,moodList,moodList[0]);

            playlistSorter.sortByMood(playlist,selectedMood);
            updatePlayListDisplay();
        }
        else{
            String[] timeOptions = {"Accending Order","Deccending Order"};
            
            String selectedMood = (String)JOptionPane.showInputDialog(this,"Select a Option to sort by",
            "Time Sort",JOptionPane.QUESTION_MESSAGE,null,timeOptions,timeOptions[0]);
            
            if(selectedMood == "Accending Order"){
                playlistSorter.sortByTime(playlist, true);
            }
            else{
                playlistSorter.sortByTime(playlist, false);
            }
            updatePlayListDisplay();
        }
    }
    
    private void performMoodShuffle() {
        if (playlist != null && playlist.head != null) {
            
            int intensity = chooseShuffleIntensity();
            MoodShuffler.moodBasedShuffle(playlist, MoodShuffler.MOOD_CALM, intensity);

            updatePlayListDisplay();
            JOptionPane.showMessageDialog(this, "Playlist shuffled based on mood!");
        } else {
            JOptionPane.showMessageDialog(this, "Playlist is empty!", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void updatePlayListDisplay() {
        listModel.clear();
        if (playlist != null && playlist.head != null) {
            Node current = playlist.head;
            while (current != null) {
                String songInfo = current.songName + " - " + current.artistName + 
                        " [ " + current.getMoodScore() + " ] "+ " - "
                        + playlistSorter.formatDuration(current.getDuration());
                
                if (current == currentNode && isPlaying){
                    songInfo = "▶ " + songInfo;
                }
                listModel.addElement(songInfo);
                current = current.nextNode;
                
            }
        }
    }
    
    private int chooseShuffleIntensity() {
        String[] options = {"LIGHT", "MEDIUM", "HIGH"};
    int choice = JOptionPane.showOptionDialog(this,
        "Choose Shuffle Intensity:",
        "Shuffle Intensity",
        JOptionPane.DEFAULT_OPTION,
        JOptionPane.QUESTION_MESSAGE,
        null,
        options,
        options[1]
    );
    
    if (choice == 0) return MoodShuffler.INTENSITY_LIGHT;
    if (choice == 1) return MoodShuffler.INTENSITY_MEDIUM;
    if (choice == 2) return MoodShuffler.INTENSITY_HIGH;
    return MoodShuffler.INTENSITY_MEDIUM;
    }
    
    private void playSong(){
        if (playlist == null || playlist.head == null){
            JOptionPane.showMessageDialog(this, "Playlist is empty!", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if(currentNode == null){
            currentNode = playlist.head;
        }
        isPlaying = true;
        
        JOptionPane.showMessageDialog(this, "Now Playing: " + currentNode.songName
        + " - " + currentNode.artistName);
        updatePlayListDisplay();        
    }
    
    private void pauseSong(){
        isPlaying = false;
        JOptionPane.showMessageDialog(this, "Playback Paused");
    }
    
    private void nextSong(){
        if (currentNode != null && currentNode.nextNode != null){
            currentNode = currentNode.nextNode;
            JOptionPane.showMessageDialog(this, "Next: " + currentNode.songName);
            updatePlayListDisplay();
            
            if (autoPlayEnabled) {
                isPlaying = true;
                playSong();
            } else {
                JOptionPane.showMessageDialog(this, "Next: " + currentNode.songName);
            }
            
        }else {
            JOptionPane.showMessageDialog(this, "No Next Song Available!", "Info", JOptionPane.INFORMATION_MESSAGE);
            isPlaying = false;
            updatePlayListDisplay();
        }
    }
    
    private void previousSong(){
        if (currentNode != null && currentNode.previousNode != null){
            currentNode = currentNode.previousNode;
            JOptionPane.showMessageDialog(this, "Previous: " + currentNode.songName);
            updatePlayListDisplay();
            
            if (autoPlayEnabled) {
                isPlaying = true;
                playSong();
            } else {
                JOptionPane.showMessageDialog(this, "Next: " + currentNode.songName);
            }
        }else{
            JOptionPane.showMessageDialog(this, "No Previous Song Available", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void clearPlaylist(){
        if (playlist != null){
            playlist.clear();
            currentNode = null;
            isPlaying = false;
            updatePlayListDisplay();
            JOptionPane.showMessageDialog(this, "Playlist Cleared!");
        }
    }
    
    private void addRightClickMenu() {
    JPopupMenu popupMenu = new JPopupMenu();
    JMenuItem removeItem = new JMenuItem("Remove Song");
    
    removeItem.addActionListener(e -> removeSelectedSong());
    popupMenu.add(removeItem);
    
    playListJList.setComponentPopupMenu(popupMenu);
    
    playListJList.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mousePressed(java.awt.event.MouseEvent e) {
            if (e.isPopupTrigger()) {
                int index = playListJList.locationToIndex(e.getPoint());
                if (index != -1) {
                    playListJList.setSelectedIndex(index);
                }
            }
        }
    });
}

    private void removeSelectedSong() {
    int selectedIndex = playListJList.getSelectedIndex();
    if (selectedIndex == -1) {
        JOptionPane.showMessageDialog(this, "Please select a song to remove!", "Error", JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    String selectedValue = playListJList.getSelectedValue();
    String songName = selectedValue.split(" - ")[0].trim();
    
    int confirm = JOptionPane.showConfirmDialog(this, 
        "Are you sure you want to remove this song?\n\n" +
        "Song: " + songName + "\n" +
        "Note.Song will be removed from playlist",
        "Confirm Remove Song",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.WARNING_MESSAGE);
    
    if (confirm != JOptionPane.YES_OPTION) {
        return;
    }
    
    boolean removed = removeSongFromPlaylist(songName);
    
    if (removed) {
        JOptionPane.showMessageDialog(this, "Song removed successfully: " + songName, "Success", JOptionPane.INFORMATION_MESSAGE);
        updatePlayListDisplay();
        PlaylistSaveHelper.savePlaylistToFile(playlist);
    } else {
        JOptionPane.showMessageDialog(this, "Failed to remove song: " + songName, "Error", JOptionPane.ERROR_MESSAGE);
    }
}

private boolean removeSongFromPlaylist(String songName) {
    if (playlist == null || playlist.head == null) return false;
    
    Node current = playlist.head;
    while (current != null) {
        if (current.songName.equalsIgnoreCase(songName)) {
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
    
    private void setupSearchFunctionality() {
    Component[] components = ((JPanel)playListJList.getParent().getParent().getComponent(0)).getComponents();
    for (Component comp : components) {
        if (comp instanceof JPanel) {
            Component[] searchComponents = ((JPanel)comp).getComponents();
            for (Component searchComp : searchComponents) {
                if (searchComp instanceof JButton && ((JButton)searchComp).getText().equals("Go")) {
                    ((JButton)searchComp).addActionListener(e -> searchSongs());
                    break;
                }
            }
        }
    }
}

    private void searchSongs() {
    String searchTerm = searchField.getText().trim();
    
    if (searchTerm.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please enter a search term!", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }
    
    java.util.List<String> results = new java.util.ArrayList<>();
    Node current = playlist.head;
    
    while (current != null) {
        if (current.songName.toLowerCase().contains(searchTerm.toLowerCase()) ||
            current.artistName.toLowerCase().contains(searchTerm.toLowerCase())) {
            
            String songInfo = current.songName + " - " + current.artistName + 
                " [ " + current.getMoodScore() + " ] " + " - " +
                playlistSorter.formatDuration(current.getDuration());
            
            results.add(songInfo);
        }
        current = current.nextNode;
    }
    
    if (results.isEmpty()) {
        JOptionPane.showMessageDialog(this, 
            "No songs found with: '" + searchTerm + "'", 
            "Search Results", 
            JOptionPane.INFORMATION_MESSAGE);
    } else {
        StringBuilder message = new StringBuilder("Found " + results.size() + " song(s):\n\n");
        for (int i = 0; i < results.size(); i++) {
            message.append(i + 1).append(". ").append(results.get(i)).append("\n");
        }
        
        JOptionPane.showMessageDialog(this, 
            message.toString(), 
            "Search Results for: '" + searchTerm + "'", 
            JOptionPane.INFORMATION_MESSAGE);
    }
}
    
    private Node getNodeAtIndex(int index) {
        if (playlist == null || index < 0) return null;
    
        Node current = playlist.head;
        int currentIndex = 0;
    
        while (current != null && currentIndex < index) {
            current = current.nextNode;
            currentIndex++;
        }
    
        return current;
    }
    
    private void savePlaylist() {
        PlaylistSaveHelper.savePlaylistToFile(playlist);
    }
    
    private void toggleFavorite(int index){
        Node node = getNodeAtIndex(index);
        if (node != null) {
            node.setFavorite(!node.isFavorite());
            updatePlayListDisplay();
            savePlaylist();
        }
    }
    
    private void showFavorites() {
        listModel.clear();
        Node current = playlist.head;
        while (current != null){
            if (current.isFavorite()){
                String songInfo = "⭐ " + current.songName + " - " + current.artistName +
                        " [ " + current.getMoodScore() + " ] ";
                listModel.addElement(songInfo);
            }
            current = current.nextNode;
        }
    }
    
    public static void main(String[] args) {
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
