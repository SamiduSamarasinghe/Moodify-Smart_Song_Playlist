/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.tool.view;

import com.tool.control.MoodShuffler;
import com.tool.control.PlaylistSaveHelper;
import com.tool.control.PlaylistSorter;
import com.tool.control.YouTubeUrlHelper;
import com.tool.control.RemoveSong;
import com.tool.model.DoublyLinkedList;
import com.tool.model.Node;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

//vlcj imports
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;

public class MainFrame extends JFrame {
    
    private EmbeddedMediaPlayerComponent embeddedMediaPlayerComponent;
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
    // for smart auto play
    private Timer autoPlayTimer;
    private Timer songDurationTimer;
    private int remainingSeconds;
    
    private boolean autoPlayEnabled = false;
    private Timer autoPlayTimer; // for smart auto play
    private String streamUrl;

    //color types for each mood
    private static final Color CALM_COLOR = new Color(200, 230, 200);//green
    private static final Color NEUTRAL_COLOR = new Color(25, 240, 200);//yellow
    private static final  Color ENERGETIC_COLOR = new Color(255, 200, 200); //red
    private static final Color DEFAULT_COLOR = new Color(140, 240, 240);//gray
    
    
    public MainFrame() {
        playlist = PlaylistSaveHelper.loadPlaylistFromFile();
        playlistSorter = new PlaylistSorter();
        initializeUI();
        updatePlayListDisplay();
        
        //initialize the song duration timer
        songDurationTimer = new Timer(1000, e -> handleSongTimerTick()); //fires every second
        songDurationTimer.setRepeats(true);
    
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
    setLayout(new BorderLayout(10, 10));
    
    // Set modern look and feel
    try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
        e.printStackTrace();
    }
    
    getContentPane().setBackground(new Color(240, 240, 240)); // Light gray background

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

    //vlc player  creation
        
    embeddedMediaPlayerComponent = new EmbeddedMediaPlayerComponent();
    JPanel videoPanel = new JPanel(new BorderLayout());
    videoPanel.add(embeddedMediaPlayerComponent, BorderLayout.CENTER);
    add(videoPanel, BorderLayout.EAST);
        
    videoPanel.setPreferredSize(new Dimension(400, 300)); 

    // 5. Finalize and display the JFrame
    setMinimumSize(new Dimension(800, 600)); // Set minimum size
    pack();
    setLocationRelativeTo(null);
    setVisible(true);
}
    
    private JPanel createInputPanel() {
    JPanel panel = new JPanel(new GridBagLayout());
    panel.setBorder(BorderFactory.createTitledBorder(
        BorderFactory.createLineBorder(new Color(100, 100, 200), 2), 
        "Add New Song"
    ));
    panel.setBackground(new Color(250, 250, 250));

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(8, 8, 8, 8); // Increased padding
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1.0;

    // Row 0: Song Title
    gbc.gridx = 0; gbc.gridy = 0;
    JLabel titleLabel = new JLabel("Song Title:");
    titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
    panel.add(titleLabel, gbc);
    gbc.gridx = 1;
    titleTextField = new JTextField(20);
    titleTextField.setFont(new Font("Arial", Font.PLAIN, 12));
    panel.add(titleTextField, gbc);

    // Row 1: Artist
    gbc.gridx = 0; gbc.gridy = 1;
    JLabel artistLabel = new JLabel("Artist:");
    artistLabel.setFont(new Font("Arial", Font.BOLD, 12));
    panel.add(artistLabel, gbc);
    gbc.gridx = 1;
    artistTextField = new JTextField(20);
    artistTextField.setFont(new Font("Arial", Font.PLAIN, 12));
    panel.add(artistTextField, gbc);

    // Row 2: Duration
    gbc.gridx = 0; gbc.gridy = 2;
    JLabel durationLabel = new JLabel("Duration (MM:SS):");
    durationLabel.setFont(new Font("Arial", Font.BOLD, 12));
    panel.add(durationLabel, gbc);
    gbc.gridx = 1;
    durationTextField = new JTextField(10);
    durationTextField.setFont(new Font("Arial", Font.PLAIN, 12));
    durationTextField.setToolTipText("e.g., 3:45");
    panel.add(durationTextField, gbc);

    // Row 3: YouTube URL
    gbc.gridx = 0; gbc.gridy = 3;
    JLabel urlLabel = new JLabel("YouTube URL:");
    urlLabel.setFont(new Font("Arial", Font.BOLD, 12));
    panel.add(urlLabel, gbc);
    gbc.gridx = 1;
    urlTextField = new JTextField(20);
    urlTextField.setFont(new Font("Arial", Font.PLAIN, 12));
    panel.add(urlTextField, gbc);

    // Row 4: Mood Selection
    gbc.gridx = 0; gbc.gridy = 4;
    JLabel moodLabel = new JLabel("Mood:");
    moodLabel.setFont(new Font("Arial", Font.BOLD, 12));
    panel.add(moodLabel, gbc);
    gbc.gridx = 1;

    String[] moods = {"Select Mood", "Calm", "Neutral", "Energetic"};
    moodDropdown = new JComboBox<>(moods);
    moodDropdown.setFont(new Font("Arial", Font.PLAIN, 12));
    moodDropdown.setSelectedIndex(0);
    panel.add(moodDropdown, gbc);

    // Row 5: Add Song Button
    gbc.gridx = 0; gbc.gridy = 5;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.CENTER;
    JButton addButton = new JButton("Add Song");
    addButton.setFont(new Font("Arial", Font.BOLD, 12));
    addButton.setBackground(new Color(60, 180, 75)); // Green background
    addButton.setForeground(Color.WHITE); // White text
    addButton.setOpaque(true); // This makes the background color visible
    addButton.setBorderPainted(false); // This removes the border line
    addButton.setFocusPainted(false);
    
    addButton.addMouseListener(new java.awt.event.MouseAdapter() {
    public void mouseEntered(java.awt.event.MouseEvent evt) {
        addButton.setBackground(new Color(45, 160, 60)); // Darker green on hover
    }
    public void mouseExited(java.awt.event.MouseEvent evt) {
        addButton.setBackground(new Color(60, 180, 75)); // Original green
    }
});
    
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

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(150, 150, 200), 2), 
            "Playlist Controls"
        ));
        panel.setBackground(new Color(250, 250, 250));
    
        //previous button
        JButton prevButton = createIconButton("<-", "Previous");
        prevButton.addActionListener(e -> previousSong());
    
        //play / pause toggle button
        JToggleButton playPauseButton = createToggleButton(">", "||", "Play", "Pause");
        playPauseButton.addActionListener(e -> {
            if (playPauseButton.isSelected()) {
                pauseSong();
            } else {
                playSong();
            }
        });
        //next button
        JButton nextButton = createIconButton("->", "Next");
        nextButton.addActionListener(e -> nextSong());
    
        //skip backward
        JButton skipBackButton = createIconButton("<<", "Skip Back 10s");
        skipBackButton.addActionListener(e -> skipBackward());
    
        //skip forward
        JButton skipForwardButton = createIconButton(">>", "Skip Forward 10s");
        skipForwardButton.addActionListener(e -> skipForward());
    
        //mood shuffle 
        JButton shuffleButton = createIconButton("~~", "Mood Shuffle");
        shuffleButton.addActionListener(e -> performMoodShuffle());
    
        //sort button
        JButton sortButton = createIconButton("^^", "Sort");
        sortButton.addActionListener(e -> perfromMoodSort());
    
        //import button
        JButton importButton = createIconButton("Import️", "Import");
        importButton.addActionListener(e -> importPlaylistManual());
    
        //export button
        JButton exportButton = createIconButton("Export️", "Export");
        exportButton.addActionListener(e -> savePlaylistManual());
    
        //clear button
        JButton clearButton = createIconButton("Clear️", "Clear");
        clearButton.addActionListener(e -> clearPlaylist());
    
        //add buttons to panel in order
        panel.add(shuffleButton);
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        panel.add(skipBackButton);
        panel.add(prevButton);
        panel.add(playPauseButton);
        panel.add(nextButton);
        panel.add(skipForwardButton);
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        panel.add(sortButton);
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        panel.add(importButton);
        panel.add(exportButton);
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        panel.add(clearButton);
    
        return panel;
    }
    //helper method to create icon button
    private JButton createIconButton(String text, String tooltip) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setToolTipText(tooltip);
        button.setPreferredSize(new Dimension(60, 35));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setMargin(new Insets(2, 5, 2, 5));

        
        //add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(50, 110, 160));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(70, 130, 180));
            }
        });
        return button;
    }
    //helper method to create toggle button with play/pause states
    private JToggleButton createToggleButton(String playIcon, String pauseIcon, String playTooltip, String pauseTooltip){
        JToggleButton button = new JToggleButton(playIcon);
        button.setFont(new Font("Arial", Font.PLAIN, 16));
        button.setToolTipText(playTooltip);
        button.setPreferredSize(new Dimension(40, 40));
        button.setBackground(new Color(60, 180, 75)); // Green for play
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
            button.setMargin(new Insets(2, 5, 2, 5));

        
        //change color when toggled
        button.addItemListener(e -> {
            if (button.isSelected()) {
                button.setText(pauseIcon);
                button.setToolTipText(pauseTooltip);
                button.setBackground(new Color(200, 150, 50)); //orange for pause
            } else {
                button.setText(playIcon);
                button.setToolTipText(playTooltip);
                button.setBackground(new Color(60, 180, 75)); //green for play
            }
        });
        return button;
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
    
    private void perfromSort(){
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
            //store the current song name before shuffling
            String currentSongName = (currentNode != null) ? currentNode.songName : null;
            
            int intensity = chooseShuffleIntensity();
            MoodShuffler.moodBasedShuffle(playlist, MoodShuffler.MOOD_CALM, intensity);

            //restore the currentNode after shuffling
            if (currentSongName != null){
                currentNode = playlist.findNodeBySongName(currentSongName);
            }
            
            updatePlayListDisplay();
            JOptionPane.showMessageDialog(this, "Playlist shuffled based on mood!");
        } else {
            JOptionPane.showMessageDialog(this, "Playlist is empty!", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }
    //update theme based on mood
    private void updateThemeBasedOnMood(){
        Color themeColor = DEFAULT_COLOR;
        
        if (currentNode != null && isPlaying){
            switch(currentNode.getMoodScore()){
                case 1: 
                    themeColor = CALM_COLOR;
                    break;
                case 2: 
                    themeColor = NEUTRAL_COLOR;
                    break;
                case 3: 
                    themeColor = ENERGETIC_COLOR;
                    break;
                default:
                    themeColor = DEFAULT_COLOR;
            }
        }
        getContentPane().setBackground(themeColor); //apply theme to main components
        updatePanelColors(themeColor); //update all panels to match the theme
        applyThemeTransition(themeColor);
    }
    
    private void updatePanelColors(Color themeColor){
        Component[] components = getContentPane().getComponents(); //get all components and update background
        for (Component comp : components){
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                panel.setBackground(themeColor);
                
                updateChildComponents(panel, themeColor);
            }
        }
    }
    
    private void updateChildComponents(Container container, Color themeColor) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                panel.setBackground(themeColor);
                updateChildComponents(panel, themeColor);
            } else if (comp instanceof JList) {
                // Keep list background white for readability
                comp.setBackground(Color.WHITE);
            } else if (!(comp instanceof JButton) && !(comp instanceof JTextField)) {
                // Update other components but keep buttons and text fields standard
                comp.setBackground(themeColor);
            }
        }
    }
    
    private void applyThemeTransition(Color targetColor){
        //simple fade effect
        Timer transitionTimer = new Timer(50, null);
        transitionTimer.addActionListener(new ActionListener() {
            private int step = 0;
            private final int totalSteps = 10;
            private final Color startColor = getContentPane().getBackground();
            
            @Override
            public void actionPerformed(ActionEvent e){
                if (step >= totalSteps) {
                    transitionTimer.stop();
                    return;
                }
                float ratio = (float) step / totalSteps;
                int red = (int) (startColor.getRed() * (1 - ratio) + targetColor.getRed() * ratio);
                int green = (int) (startColor.getGreen() * (1 - ratio) + targetColor.getGreen() * ratio);
                int blue = (int) (startColor.getBlue() * (1 - ratio) + targetColor.getBlue() * ratio);
            
                Color intermediateColor = new Color(red, green, blue);
                getContentPane().setBackground(intermediateColor);
                updatePanelColors(intermediateColor);
            
                step++;
            }
        });
        transitionTimer.start();
    }
    //helper method to find node by song
    private Node findNodeBySongName(String songName){
        if (playlist == null || playlist.head == null) return null;
        
        Node current = playlist.head;
        while (current != null) {
            if (current.songName.equalsIgnoreCase(songName)){
                return current;
            }
            current = current.nextNode;
        }
        return null;
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
    
    //shuffle intensity method
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
    //play song method
    private void playSong(){
        if (playlist == null || playlist.head == null){
            JOptionPane.showMessageDialog(this, "Playlist is empty!", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if(currentNode == null){
            currentNode = playlist.head;
        }
        isPlaying = true;
        //start duration timer
        remainingSeconds = currentNode.getDuration();
        songDurationTimer.start();
        
        //update theme based on mood
        updateThemeBasedOnMood();
        
        //run the getStreamLink Method on a separeate thread to avoid frezzing main GUI
        new Thread(()->{
            streamUrl = YouTubeUrlHelper.getStreamLinkFromYouTube(currentNode.songPath);
            //
            if(streamUrl != null){
                embeddedMediaPlayerComponent.mediaPlayer().media().play(streamUrl);
                
                SwingUtilities.invokeLater(()->{
                    JOptionPane.showMessageDialog(this, "Now Playing: " + currentNode.songName + " - " + currentNode.artistName);
                    updatePlayListDisplay();
                });
            }
            else{
                SwingUtilities.invokeLater(()->{
                JOptionPane.showMessageDialog(this,"Failed to get stream URL!", "Error", JOptionPane.ERROR_MESSAGE);
                updatePlayListDisplay();
                });
            }
        }).start();
        
        JOptionPane.showMessageDialog(this, "Now Playing: " + currentNode.songName
        + " - " + currentNode.artistName + " (" + playlistSorter.formatDuration(currentNode.getDuration()) + " )");
        updatePlayListDisplay();        
    }

    //add timer for autu-play
    private void handleSongTimerTick(){
        if (isPlaying && currentNode != null) {
            remainingSeconds--;      
        
            if (remainingSeconds <= 0) {
                nextSong(); // Song finished, move to next automatically
            }
        }
    }
    
    
    private void pauseSong(){
        isPlaying = false;
        embeddedMediaPlayerComponent.mediaPlayer().controls().pause();
        songDurationTimer.stop(); //stop timer when paused
        
        updateThemeBasedOnMood();
        
        JOptionPane.showMessageDialog(this, "Playback Paused");
    }
    
    private void nextSong(){
        if (currentNode != null && currentNode.nextNode != null){
            currentNode = currentNode.nextNode;
                
                //reset timer for new song
                remainingSeconds = currentNode.getDuration();
                songDurationTimer.restart();
                
                updateThemeBasedOnMood();
                
                JOptionPane.showMessageDialog(this, "Now Playing: " + currentNode.songName + " - " + currentNode.artistName
                        + " (" + playlistSorter.formatDuration(currentNode.getDuration()) + ") ");                         
                    updatePlayListDisplay();                   
        }else {
            JOptionPane.showMessageDialog(this, "No Next Song Available!", "Info", JOptionPane.INFORMATION_MESSAGE);
            isPlaying = false;
            songDurationTimer.stop(); //stop timer when no more songs
            updatePlayListDisplay();
        }
    }
    
    private void previousSong(){
        if (currentNode != null && currentNode.previousNode != null){
            currentNode = currentNode.previousNode;
            
            //reset timer for the previous song
            remainingSeconds = currentNode.getDuration();
            songDurationTimer.restart();
            
            updateThemeBasedOnMood();
            
            JOptionPane.showMessageDialog(this, "Now Playing: " + currentNode.songName + " - " + currentNode.artistName
                + " (" + playlistSorter.formatDuration(currentNode.getDuration()) + ")");
            updatePlayListDisplay();
                       
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
    
    private void skipForward() {
        if (currentNode != null && isPlaying) {
            //skip 10secs
            remainingSeconds = Math.max(0, remainingSeconds - 10);
            
            if (remainingSeconds <= 0){
                nextSong(); //if we skip past the end, then go to next song
            } else {
                JOptionPane.showMessageDialog(this, "Skipped forward 10 seconds");
            }
        }
    }
    private void skipBackward(){
        if (currentNode != null && isPlaying) {
            //skip 10 secs
            remainingSeconds = Math.min(currentNode.getDuration(), remainingSeconds + 10);
            JOptionPane.showMessageDialog(this, "Skipped backward 10 seconds");
        }
    }
    

                //  REMOVE SONG FUNCTIONALITY
    
        private void addRightClickMenu()
             {
                 JPopupMenu popupMenu = new JPopupMenu();
                  JMenuItem removeItem = new JMenuItem("Remove Song");
    

                     removeItem.addActionListener(e -> removeSelectedSong());
                     popupMenu.add(removeItem);
    
                     playListJList.setComponentPopupMenu(popupMenu);
    
                    playListJList.addMouseListener(new java.awt.event.MouseAdapter()  
                    {
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
            // remove select  song 
              
      private void removeSelectedSong() {
                    int selectedIndex = playListJList.getSelectedIndex();
               if (selectedIndex == -1) {
              JOptionPane.showMessageDialog(this, "Please select a song to remove!", "Error", JOptionPane.WARNING_MESSAGE);
               return;
    }
    
                // Get song name from display
    
    String selectedValue = playListJList.getSelectedValue();
    String songName = selectedValue.split(" - ")[0].trim();
    
                // Show confirmation dialog
    
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
    

                //Use RemoveSong controller to remove
     
          RemoveSong removeController = new RemoveSong(playlist);
        boolean removed = removeController.removeSongFromPlaylist(songName);
    
    if (removed) {
        JOptionPane.showMessageDialog(this, "Song removed successfully: " + songName, "Success", JOptionPane.INFORMATION_MESSAGE);
        updatePlayListDisplay();
        PlaylistSaveHelper.savePlaylistToFile(playlist);
    } else {
        JOptionPane.showMessageDialog(this, "Failed to remove song: " + songName, "Error", JOptionPane.ERROR_MESSAGE);
    }
} 
    // Find the search button and add action listener
    
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
                   // search song 
        
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
    
    private void importPlaylistManual() {
    DoublyLinkedList importedPlaylist = PlaylistSaveHelper.loadPlaylistManual(this);
    if (importedPlaylist != null && importedPlaylist.head != null) {
        this.playlist = importedPlaylist;
        currentNode = null;
        isPlaying = false;
        updatePlayListDisplay();
        JOptionPane.showMessageDialog(this, "Playlist imported successfully!");
    }
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
