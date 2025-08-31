/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.tool.view;


import com.tool.controller.*;
import com.tool.model.DoublyLinkedList;
import com.tool.model.Node;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.ArrayList;
import javax.swing.*;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;

//vlcj imports
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;

public class MainFrame extends JFrame {
    
    private EmbeddedMediaPlayerComponent embeddedMediaPlayerComponent;
    private EmbeddedMediaPlayer embeddedMediaPlayer;
    private DoublyLinkedList playlist; //data model
    private JList<String> playListJList; //display songss names
    private DefaultListModel<String> listModel; //the data model for jlist
    private PlaylistSorter playlistSorter;

    //buttons
    private JButton favoritesButton;
    
    //input feilds
    private JTextField titleTextField;
    private JTextField artistTextField;
    private JTextField durationTextField;
    private JSlider moodSlider;
    private JTextField searchField;
    
    private JTextField urlTextField;
    private JComboBox<String> moodDropdown;
    
    private JSlider songProgress;
    private Label songLegnth;
    private Node currentNode; //to track the currently playing song
    private boolean isPlaying = false; //to track play, pause 
    private Timer songDurationTimer;
    private int remainingSeconds;
    private boolean showingFavorites = false;
    
    private boolean autoPlayEnabled = false;
    private Timer autoPlayTimer; // for smart auto play
    
    private String streamUrl;
    private long currentTime;
    private long newTime;
    private long totalDuration;  
    
    //color types for each mood (5 shades each)
    private static final Color[] CALM_COLORS = {
        new Color(230, 255, 230),    // Very light green
        new Color(200, 240, 200),    // Light green
        new Color(170, 225, 170),    // Medium light green
        new Color(140, 210, 140),    // Medium green
        new Color(110, 190, 110)     // Dark green
    };

    private static final Color[] NEUTRAL_COLORS = {
        new Color(255, 250, 200),    // Very light yellow
        new Color(255, 240, 180),    // Light yellow
        new Color(255, 230, 150),    // Medium light yellow
        new Color(255, 220, 120),    // Medium yellow
        new Color(255, 210, 90)      // Dark yellow
    };

    private static final Color[] ENERGETIC_COLORS = {
        new Color(255, 220, 220),    // Very light red
        new Color(255, 200, 200),    // Light red
        new Color(255, 180, 180),    // Medium light red
        new Color(255, 160, 160),    // Medium red
        new Color(255, 140, 140)     // Dark red
    };

    private static final Color[] DEFAULT_COLORS = {
        new Color(240, 240, 255),    // Very light blue
        new Color(220, 220, 240),    // Light blue
        new Color(200, 200, 220),    // Medium light blue
        new Color(180, 180, 200),    // Medium blue
        new Color(160, 160, 180)     // Dark blue
    };
    private Timer colorTransitionTimer;
    private Color currentBackgroundColor;
    private Color targetBackgroundColor;
    private int currentColorIndex = 0;
    private int transitionStep = 0;
    private final int TRANSITION_STEPS = 30; //smoothness of transition
    private final int TRANSITION_DELAY = 50; //ms between steps

    
    
    public MainFrame() {
        playlist = PlaylistSaveHelper.loadPlaylistFromFile();
        playlistSorter = new PlaylistSorter();
        initializeUI();
        initializeColorTransition();
        startMoodColorCycling();
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
    inputPanel.setName("inputPanel");
    add(inputPanel, BorderLayout.NORTH);

    // 3. Build the Center Panel (Playlist View)
    JPanel centerPanel = createCenterPanel();
    centerPanel.setName("centerPanel");
    add(centerPanel, BorderLayout.CENTER);
    addRightClickMenu();
  
    
    // 4. Build the South Panel (Controls)
    JPanel controlPanel = createControlPanel();
    controlPanel.setName("controlPanel");
    songProgress = new JSlider(0, 1000); //to show song progress
    controlPanel.add(songProgress, BorderLayout.CENTER);
    add(controlPanel, BorderLayout.SOUTH);

    try{
    //vlc player  creation
        
    embeddedMediaPlayerComponent = new EmbeddedMediaPlayerComponent(); 
    JPanel videoPanel = new JPanel(new BorderLayout());
    videoPanel.add(embeddedMediaPlayerComponent, BorderLayout.CENTER);
    add(videoPanel, BorderLayout.EAST);
    
    //update the JSliderPosition depending on songs current position using the Timer
        songDurationTimer = new Timer(500, e -> {
            if (embeddedMediaPlayerComponent != null &&
                embeddedMediaPlayerComponent.mediaPlayer().status().isPlaying()) {
                
                float position = embeddedMediaPlayerComponent.mediaPlayer().status().position();
                int sliderValue = (int) (position * 1000);
                songProgress.setValue(sliderValue);
            }
        });
        songDurationTimer.start();

    //event to allow the user to go through the song using the JSlider
        songProgress.addChangeListener(e -> {
            if (songProgress.getValueIsAdjusting() && embeddedMediaPlayerComponent != null) {
                float newPos = songProgress.getValue() / 1000f;
                embeddedMediaPlayerComponent.mediaPlayer().controls().setPosition(newPos);
            }
        });
    
    
        
    videoPanel.setPreferredSize(new Dimension(400, 300)); 
    
    //this execption mostly happen if the user don't have VLC player not installed on pc
    }catch(Exception e){
        System.out.println(e.getMessage());
        JOptionPane.showMessageDialog(
        this,
        "VLC Media Player not found on your system.\n" +
        "Please install VLC from https://www.videolan.org/vlc/ to enable video/audio playback.",
        "VLC Not Found",
        JOptionPane.ERROR_MESSAGE
    );

    // Disable playback but let the playlist manager run
    embeddedMediaPlayerComponent = null;
    embeddedMediaPlayer = null;
    }

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

    // Row 5: Add Song Button and Help Button
    gbc.gridx = 1; 
    gbc.gridy = 5;
    gbc.gridwidth = 1; // Only span one column
    gbc.anchor = GridBagConstraints.CENTER;
    JButton addButton = new JButton("Add Song");
    addButton.setFont(new Font("Arial", Font.BOLD, 12));
    addButton.setBackground(new Color(60, 180, 75)); // Green background
    addButton.setForeground(Color.WHITE);
    addButton.setOpaque(true);
    addButton.setBorderPainted(false);
    addButton.setFocusPainted(false);
    // Add button to the left side
    panel.add(addButton, gbc);
    
    gbc.gridx = 0; 
    gbc.gridy = 5;
    JButton helpButton = new JButton("Help me Moodify");
    helpButton.setFont(new Font("Arial", Font.BOLD, 12));
    helpButton.setBackground(new Color(70, 130, 180)); // Blue background
    helpButton.setForeground(Color.WHITE);
    helpButton.setOpaque(true);
    helpButton.setBorderPainted(false);
    helpButton.setFocusPainted(false);
    panel.add(helpButton, gbc);
    
    helpButton.addMouseListener(new java.awt.event.MouseAdapter(){
        public void mouseEntered(java.awt.event.MouseEvent e){
                helpButton.setBackground(new Color(50, 100, 150)); //Darker Blue background
        }
        public void mouseExited(java.awt.event.MouseEvent e){
            helpButton.setBackground(new Color(70, 130, 180)); // Blue background
        }
    });
    
    addButton.addMouseListener(new java.awt.event.MouseAdapter() {
    public void mouseEntered(java.awt.event.MouseEvent evt) {
        addButton.setBackground(new Color(45, 160, 60)); // Darker green on hover
    }
    public void mouseExited(java.awt.event.MouseEvent evt) {
        addButton.setBackground(new Color(60, 180, 75)); // Original green
    }
});
    helpButton.addActionListener(e->{
        int choice = JOptionPane.showConfirmDialog(
            this,
            "Not sure about the mood?\n\n" +
            "Click YES and Moodify will help you!\n\n" +
            "We’ll play the first 10 seconds of your song.\n" +
            "While it plays, tap the SPACEBAR to the beat.\n\n" +
            "Moodify will figure out the perfect mood for the Song!",
            "Help Me Moodify",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        final BpmDetector bpmDetector = new BpmDetector();
        if(choice == 0){

            List<Long> taps = new ArrayList<>();

            JDialog tapDialog = new JDialog(this, "Tap the Beat", true);
            JLabel info = new JLabel("Tap SPACEBAR to the beat!", SwingConstants.CENTER);
            tapDialog.add(info);
            tapDialog.setSize(300, 150);
            tapDialog.setLocationRelativeTo(this);
            
        //run the getStreamLink Method on a separeate thread to avoid frezzing main GUI
        new Thread(()->{
            streamUrl = YouTubeUrlHelper.getStreamLinkFromYouTube(urlTextField.getText());
            //
            if(streamUrl != null){
                embeddedMediaPlayerComponent.mediaPlayer().media().play(streamUrl);
                    
                    // Add listener to detect when playback starts
                    embeddedMediaPlayerComponent.mediaPlayer().events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
                        @Override
                        public void playing(MediaPlayer mediaPlayer) {
                            // Start 10s timer AFTER playback begins
                            new Thread(() -> {
                                try {
                                    Thread.sleep(20_000); //stop after 20 seconds
                                } catch (InterruptedException ex) {
                                    ex.printStackTrace();
                                }
                                embeddedMediaPlayerComponent.mediaPlayer().controls().stop();

                                // Close dialog & trigger BPM detection
                                SwingUtilities.invokeLater(() -> {
                                    tapDialog.dispatchEvent(new WindowEvent(tapDialog, WindowEvent.WINDOW_CLOSING));
                                });
                            }).start();
                        }
                    });

                } else {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, "Failed to get stream URL!", "Error", JOptionPane.ERROR_MESSAGE);
                    });
                }
            }).start();
        
            //lisiter for counting key presses and displaying
            tapDialog.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                        taps.add(System.currentTimeMillis());
                        info.setText("Taps recorded: " + taps.size());
                    }
                }
            });
            
            //to display the bpm and Suggested mood after the keyPress dialog closed
            tapDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                //show a dialog box with out any button 
                String mood = bpmDetector.calculateMoodFromTaps(taps);
                JDialog resultDialog = new JDialog((Frame) null, true);
                resultDialog.setUndecorated(true);
                resultDialog.setSize(350, 150);
                resultDialog.setLocationRelativeTo(tapDialog);

                JLabel label = new JLabel("Moodify suggests: " + mood, SwingConstants.CENTER);
                label.setFont(new Font("SansSerif", Font.BOLD, 18));
                label.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
                resultDialog.add(label);

                // Still block keys if needed
                resultDialog.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        e.consume();
                    }
                });

                resultDialog.setFocusable(true);
                
                new javax.swing.Timer(3000, evt -> resultDialog.dispose()).start();
                resultDialog.setVisible(true);
            }
        });
            
        tapDialog.setVisible(true);
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
    //panel.add(addButton, gbc);

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

        // Store reference to favorites button
        favoritesButton = new JButton("⭐ Favorites");
        favoritesButton.addActionListener(e -> showFavorites());
        searchPanel.add(favoritesButton);

        panel.add(searchPanel, BorderLayout.NORTH);

        searchButton.addActionListener(e -> searchSongs());
        searchPanel.add(searchButton);
        panel.add(searchPanel, BorderLayout.NORTH);

        listModel = new DefaultListModel<>();
        playListJList = new JList<>(listModel);

        // Remove the complex listener and use a simpler approach
        // The button text will be updated when needed, not on every list change

        playListJList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if(e.getClickCount() == 1){ //single click for song play
                    int index = playListJList.locationToIndex(e.getPoint());
                    if (index >= 0){
                        playSelectedSong(index);
                    }
                }else if (e.getClickCount() == 2){ //double click for make song as favorite
                    int index = playListJList.locationToIndex(e.getPoint());
                    if (index >= 0){
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
    //add new method for play selected song
    private void playSelectedSong(int index) {
        if (playlist == null || playlist.head == null) {
            JOptionPane.showMessageDialog(this, "Playlist is empty!", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Check if we're in favorites view
        boolean inFavoritesView = isInFavoritesView();

        // Get node at the selected index
        Node selectedNode;
        if (inFavoritesView) {
            selectedNode = getFavoriteNodeAtIndex(index);
        } else {
            selectedNode = getNodeAtIndex(index);
        }

        if (selectedNode == null) {
            JOptionPane.showMessageDialog(this, "Could not find the selected song", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Rest of the method remains the same...
        currentNode = selectedNode;
        isPlaying = true;
        remainingSeconds = currentNode.getDuration();

        if (songDurationTimer != null) {
            songDurationTimer.restart();
        }

        updateThemeBasedOnMood();

        new Thread(() -> {
            streamUrl = YouTubeUrlHelper.getStreamLinkFromYouTube(currentNode.songPath);

            if (streamUrl != null) {
                if (embeddedMediaPlayerComponent != null && embeddedMediaPlayerComponent.mediaPlayer() != null) {
                    embeddedMediaPlayerComponent.mediaPlayer().controls().stop();
                }
                embeddedMediaPlayerComponent.mediaPlayer().media().play(streamUrl);
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Now Playing: " + currentNode.songName + " - " + currentNode.artistName);
                    updatePlayListDisplay();
                });
            } else {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Failed to get stream URL!", "Error", JOptionPane.ERROR_MESSAGE);
                    updatePlayListDisplay();
                });
            }
        }).start();
        updatePlayListDisplay();
    }
    
    
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setName("controlPanel");
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(150, 150, 200), 2), 
            "Playlist Controls"
        ));
        panel.setOpaque(true); // Ensure panel is opaque
        panel.setBackground(new Color(250, 250, 250, 230)); // Semi-transparent white
        
        //volume control
        JPanel volumePanel = createVolumeControl();
    
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
        sortButton.addActionListener(e -> perfromSort());
    
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
        panel.add(volumePanel);
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
    
    //add vloume slider function
    private JPanel createVolumeControl() {
        JPanel volumePanel = new JPanel(new BorderLayout(5, 0));
        volumePanel.setOpaque(false);

        //volume icon
        JLabel volumeIcon = new JLabel("🔊");
        volumeIcon.setToolTipText("Volume");
        volumePanel.add(volumeIcon, BorderLayout.WEST);

        //volume slider
        JSlider volumeSlider = new JSlider(0, 100, 50);
        volumeSlider.setPreferredSize(new Dimension(60, 20));
        volumeSlider.setOpaque(false);
        volumeSlider.setToolTipText("Adjust volume");

        //set initial volume
        if (embeddedMediaPlayerComponent != null && embeddedMediaPlayerComponent.mediaPlayer() != null) {
            embeddedMediaPlayerComponent.mediaPlayer().audio().setVolume(50);
        }

        // Add change listener
        volumeSlider.addChangeListener(e -> {
            if (!volumeSlider.getValueIsAdjusting() && embeddedMediaPlayerComponent != null && 
                embeddedMediaPlayerComponent.mediaPlayer() != null) {
                int volume = volumeSlider.getValue();
                embeddedMediaPlayerComponent.mediaPlayer().audio().setVolume(volume);

                // Update icon based on volume level
                if (volume == 0) {
                    volumeIcon.setText("🔇");
                } else if (volume < 30) {
                    volumeIcon.setText("🔈");
                } else if (volume < 70) {
                    volumeIcon.setText("🔉");
                } else {
                    volumeIcon.setText("🔊");
                }
            }
        });

        volumePanel.add(volumeSlider, BorderLayout.CENTER);

        return volumePanel;
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
        Color[] targetColors;
    
        if (currentNode != null && isPlaying) {
            switch(currentNode.getMoodScore()) {
                case 1: 
                    targetColors = CALM_COLORS;
                    break;
                case 2: 
                    targetColors = NEUTRAL_COLORS;
                    break;
                case 3: 
                    targetColors = ENERGETIC_COLORS;
                    break;
                default:
                    targetColors = DEFAULT_COLORS;
            }
        } else {
            targetColors = DEFAULT_COLORS;
        }
        currentColorIndex = (currentColorIndex + 1) % targetColors.length;
        startColorTransition(targetColors[currentColorIndex]);
    }
    private void startColorTransition(Color targetColor) {
        if (colorTransitionTimer.isRunning()) {
            colorTransitionTimer.stop();
        }

        targetBackgroundColor = targetColor;
        transitionStep = 0;
        colorTransitionTimer.start();
    }
    private void updateColorTransition() {
        if (transitionStep >= TRANSITION_STEPS) {
            colorTransitionTimer.stop();
            currentBackgroundColor = targetBackgroundColor;
            return;
        }

        float ratio = (float) transitionStep / TRANSITION_STEPS;

        int red = (int) (currentBackgroundColor.getRed() * (1 - ratio) + targetBackgroundColor.getRed() * ratio);
        int green = (int) (currentBackgroundColor.getGreen() * (1 - ratio) + targetBackgroundColor.getGreen() * ratio);
        int blue = (int) (currentBackgroundColor.getBlue() * (1 - ratio) + targetBackgroundColor.getBlue() * ratio);

        Color intermediateColor = new Color(red, green, blue);
        applyBackgroundColor(intermediateColor);

        transitionStep++;
    }
    private void applyBackgroundColor(Color color) {
        currentBackgroundColor = color;
        getContentPane().setBackground(color);
        updateAllPanelColors(color);
        repaint();
    }
    
    private void updateAllPanelColors(Color themeColor) {
        Component[] components = getContentPane().getComponents();
        for (Component comp : components) {
            updateComponentColor(comp, themeColor);
        }
    }

    private void updateComponentColor(Component comp, Color themeColor) {
        if (comp instanceof JPanel) {
            JPanel panel = (JPanel) comp;

            // Don't change background of panels that should stay visible
            if (shouldKeepOriginalBackground(panel)) {
                // Use a slightly darker, less transparent version of the theme color for panels
                Color panelColor = new Color(
                    Math.max(0, themeColor.getRed() - 20),
                    Math.max(0, themeColor.getGreen() - 20), 
                    Math.max(0, themeColor.getBlue() - 20),
                    230 // Less transparent
                );
                panel.setBackground(panelColor);
                panel.setOpaque(true);
            }

            // Update child components
            for (Component child : panel.getComponents()) {
                updateComponentColor(child, themeColor);
            }
        } else if (comp instanceof JList) {
            // Keep list background white for readability but make selection color match theme
            comp.setBackground(Color.WHITE);
            if (comp instanceof JList) {
                JList<?> list = (JList<?>) comp;
                list.setSelectionBackground(themeColor.darker());
                list.setSelectionForeground(Color.WHITE);
            }
        } else if (comp instanceof JButton) {
            // For buttons, use a solid version of the theme color that provides good contrast
            JButton button = (JButton) comp;
                Color buttonColor = new Color(
                    Math.max(0, themeColor.getRed() - 5),
                    Math.max(0, themeColor.getGreen() - 5), 
                    Math.max(0, themeColor.getBlue() - 5),
                    100 // Less transparent
                );

            button.setBackground(buttonColor);
            button.setForeground(getContrastColor(buttonColor)); // Ensure text is readable
            button.setOpaque(true);
            button.setBorderPainted(false);

        } else if (comp instanceof JTextField || comp instanceof JComboBox) {
            // Keep form elements with white background for better usability
            comp.setBackground(Color.WHITE);
            comp.setForeground(Color.BLACK);
        } else if (comp instanceof JLabel) {
            // Adjust label colors for better readability against the theme
            JLabel label = (JLabel) comp;
            label.setForeground(getContrastColor(themeColor));
        } else {
            // For other components, use a more opaque version of the theme color
            Color semiTransparent = new Color(
                themeColor.getRed(), 
                themeColor.getGreen(), 
                themeColor.getBlue(), 
                220 // More opaque than before
            );
            comp.setBackground(semiTransparent);
        }
    }

    // Helper method to get a contrasting text color for any background
    private Color getContrastColor(Color color) {
        // Calculate luminance (perceived brightness)
        double luminance = (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue()) / 255;
        return luminance > 0.5 ? Color.BLACK : Color.WHITE;
    }

    private boolean shouldKeepOriginalBackground(JPanel panel) {
        // Check if this panel should keep its original background
        // (e.g., control panels, input panels for better readability)
        String panelName = panel.getName();
        return panelName != null && (
            panelName.contains("control") || 
            panelName.contains("input") || 
            panelName.contains("volume")
        );
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
        // Store whether we're currently in favorites view
        boolean wasInFavoritesView = isInFavoritesView();

        listModel.clear();

        if (playlist != null && playlist.head != null) {
            Node current = playlist.head;
            int index = 0;

            while (current != null) {
                // Only show favorite songs if we were in favorites view
                if (!wasInFavoritesView || current.isFavorite()) {
                    String songInfo = current.songName + " - " + current.artistName +
                            " [ " + current.getMoodScore() + " ] " + " - "
                            + playlistSorter.formatDuration(current.getDuration());

                    if (current == currentNode && isPlaying) {
                        songInfo = "▶ " + songInfo;
                    }
                    // Add favorite star
                    if (current.isFavorite()) {
                        songInfo = "⭐ " + songInfo;
                    }

                    listModel.addElement(songInfo);
                }
                current = current.nextNode;
                index++;
            }

            // Highlight current playing song in playlist
            if (currentNode != null) {
                int currentIndex = getIndexOfNodeInDisplay(currentNode, wasInFavoritesView);
                if (currentIndex >= 0) {
                    playListJList.setSelectedIndex(currentIndex);
                    playListJList.ensureIndexIsVisible(currentIndex);
                }
            }
        }
    }

    // Helper method to get index of node in the current display view
    private int getIndexOfNodeInDisplay(Node targetNode, boolean favoritesOnly) {
        if (playlist == null || playlist.head == null || targetNode == null) return -1;

        Node current = playlist.head;
        int displayIndex = 0;

        while (current != null) {
            // Only count nodes that should be displayed
            if (!favoritesOnly || current.isFavorite()) {
                if (current == targetNode) {
                    return displayIndex;
                }
                displayIndex++;
            }
            current = current.nextNode;
        }

        return -1;
    }
    //helper method for get index of node
    private int getIndexOfNode(Node targetNode) {
        if (playlist == null || playlist.head == null || targetNode == null) return -1;

        Node current = playlist.head;
        int index = 0;

        while (current != null) {
            if (current == targetNode) {
                return index;
            }
            current = current.nextNode;
            index++;
        }

        return -1;
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
    private void playSong() {
        playSongWithoutChangingView();
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

    private void nextSong() {
        if (currentNode != null) {
            Node nextNode;

            if (showingFavorites) {
                // In favorites view - find next favorite song
                nextNode = findNextFavoriteNode(currentNode);
            } else {
                // In normal view - use regular next node
                nextNode = currentNode.nextNode;
            }

            if (nextNode != null) {
                currentNode = nextNode;
                resetTimerForCurrentSong();
                updateThemeBasedOnMood();
                playSongWithoutChangingView(); // Use new method that doesn't change view
            } else {
                JOptionPane.showMessageDialog(this, "No Next Song Available!", "Info", JOptionPane.INFORMATION_MESSAGE);
                isPlaying = false;
                songDurationTimer.stop();
                updatePlayListDisplay();
            }
        }
    }

    private void previousSong() {
        if (currentNode != null) {
            Node previousNode;

            if (showingFavorites) {
                // In favorites view - find previous favorite song
                previousNode = findPreviousFavoriteNode(currentNode);
            } else {
                // In normal view - use regular previous node
                previousNode = currentNode.previousNode;
            }

            if (previousNode != null) {
                currentNode = previousNode;
                resetTimerForCurrentSong();
                updateThemeBasedOnMood();
                playSongWithoutChangingView(); // Use new method that doesn't change view
            } else {
                JOptionPane.showMessageDialog(this, "No Previous Song Available", "Error", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    // Helper method to find first favorite node
    private Node findFirstFavoriteNode() {
        if (playlist == null || playlist.head == null) return null;

        Node current = playlist.head;
        while (current != null) {
            if (current.isFavorite()) {
                return current;
            }
            current = current.nextNode;
        }
        return null;
    }

    // Helper method to find next favorite node
    private Node findNextFavoriteNode(Node startNode) {
        if (playlist == null || startNode == null) return null;

        Node current = startNode.nextNode;
        while (current != null) {
            if (current.isFavorite()) {
                return current;
            }
            current = current.nextNode;
        }
        return null;
    }

    // Helper method to find previous favorite node
    private Node findPreviousFavoriteNode(Node startNode) {
        if (playlist == null || startNode == null) return null;

        Node current = startNode.previousNode;
        while (current != null) {
            if (current.isFavorite()) {
                return current;
            }
            current = current.previousNode;
        }
        return null;
    }

    // Helper method to reset timer for current song
    private void resetTimerForCurrentSong() {
        remainingSeconds = currentNode.getDuration();
        if (songDurationTimer != null) {
            songDurationTimer.restart();
        }
    }

    private void clearPlaylist() {
        if (playlist != null) {
            playlist.clear();
            currentNode = null;
            isPlaying = false;
            showingFavorites = false; // Exit favorites view
            updatePlayListDisplay();
            JOptionPane.showMessageDialog(this, "Playlist Cleared!");
        }
    }


    private void skipForward() {
        if (currentNode != null && isPlaying) {
            try{
            //skip 10secs
            remainingSeconds = Math.max(0, remainingSeconds - 10);
            
            // get teh current time stamp from media player
            currentTime = embeddedMediaPlayerComponent.mediaPlayer().status().time();
            newTime = currentTime + 10000;  //10s forward
            
            //get the total duration from the media player
            totalDuration = embeddedMediaPlayerComponent.mediaPlayer().status().length();
            
            if (newTime >= totalDuration){
                nextSong(); //if we skip past the end, then go to next song
            } else {
                
                embeddedMediaPlayerComponent.mediaPlayer().controls().setTime(newTime);
                
                // Update remaining seconds based on new position
                remainingSeconds = (int) ((totalDuration - newTime) / 1000);
                AutoClosingDialog.show(this, "Skipped forward 10 seconds", "Message", 1, 1000);
            }
            }catch(Exception e){
                JOptionPane.showMessageDialog(this, "Error skipping forward: " 
                        + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private void skipBackward(){
        if (currentNode != null && isPlaying) {
            try{
            //skip 10 secs
            
            //get the current time stamp
            currentTime = embeddedMediaPlayerComponent.mediaPlayer().status().time();
            newTime = Math.max(0, currentTime - 10000); // 10 seconds backward, but not before 0
            
            embeddedMediaPlayerComponent.mediaPlayer().controls().setTime(newTime);
            
            // Update remaining seconds based on new position
            long totalDuration = embeddedMediaPlayerComponent.mediaPlayer().status().length();
            remainingSeconds = (int) ((totalDuration - newTime) / 1000);
            
            
            remainingSeconds = Math.min(currentNode.getDuration(), remainingSeconds + 10);
            AutoClosingDialog.show(this, "Skipped backward 10 seconds", "Message", 1, 1000);
            
            }catch(Exception e){
                 JOptionPane.showMessageDialog(this, "Error skipping backward: " 
                         + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    

                //  REMOVE SONG FUNCTIONALITY

    private void addRightClickMenu() {
        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem removeItem = new JMenuItem("Remove Song");
        removeItem.addActionListener(e -> removeSelectedSong());
        popupMenu.add(removeItem);

        // Add favorite toggle menu item
        JMenuItem favoriteItem = new JMenuItem("Toggle Favorite");
        favoriteItem.addActionListener(e -> toggleFavoriteSelectedSong());
        popupMenu.add(favoriteItem);

        playListJList.setComponentPopupMenu(popupMenu);

        playListJList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int index = playListJList.locationToIndex(e.getPoint());
                    if (index != -1) {
                        playListJList.setSelectedIndex(index);

                        // Update the favorite menu item text based on current status
                        Node selectedNode = getNodeAtIndex(index);
                        if (selectedNode != null) {
                            if (selectedNode.isFavorite()) {
                                favoriteItem.setText("Remove from Favorites");
                            } else {
                                favoriteItem.setText("Add to Favorites");
                            }
                        }
                    }
                }
            }
        });
    }
    private void toggleFavoriteSelectedSong() {
        int selectedIndex = playListJList.getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, "Please select a song first!", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String selectedValue = playListJList.getSelectedValue();
        String songName = selectedValue.split(" - ")[0].trim();

        // Remove the "▶ " prefix if present (for currently playing song)
        if (songName.startsWith("▶ ")) {
            songName = songName.substring(2);
        }
        // Remove the "⭐ " prefix if present (already favorite)
        if (songName.startsWith("⭐ ")) {
            songName = songName.substring(2);
        }

        AddfavSong favController = new AddfavSong(playlist);
        boolean success = favController.toggleFavorite(songName);

        if (success) {
            Node selectedNode = getNodeAtIndex(selectedIndex);
            String message = selectedNode.isFavorite() ?
                    "Added to favorites: " + songName :
                    "Removed from favorites: " + songName;

            JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
            updatePlayListDisplay();
            PlaylistSaveHelper.savePlaylistToFile(playlist);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update favorite status: " + songName, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void removeSelectedSong() {
    int selectedIndex = playListJList.getSelectedIndex();
    if (selectedIndex == -1) {
        JOptionPane.showMessageDialog(this, "Please select a song to remove!", "Error", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Get song name from display
    String selectedValue = playListJList.getSelectedValue();
    
    // Remove all possible prefixes from the display text
    String songName = selectedValue.split(" - ")[0].trim();
    
    // Remove playing indicator if present
    if (songName.startsWith("▶ ")) {
        songName = songName.substring(2);
    }
    // Remove favorite indicator if present
    if (songName.startsWith("⭐ ")) {
        songName = songName.substring(2);
    }

    // Show confirmation dialog
    int confirm = JOptionPane.showConfirmDialog(this, 
        "Are you sure you want to remove this song?\n\n" +
        "Song: " + songName + "\n" +
        "Note: Song will be removed from playlist",
        "Confirm Remove Song",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.WARNING_MESSAGE);

    if (confirm != JOptionPane.YES_OPTION) {
        return;
    }

    // Use RemoveSong controller to remove
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

    java.util.List<Node> foundNodes = new java.util.ArrayList<>();
    Node current = playlist.head;

    while (current != null) {
        if (current.songName.toLowerCase().contains(searchTerm.toLowerCase()) ||
            current.artistName.toLowerCase().contains(searchTerm.toLowerCase())) {

            foundNodes.add(current);
        }
        current = current.nextNode;
    }

    if (foundNodes.isEmpty()) {
        JOptionPane.showMessageDialog(this,
            "No songs found with: '" + searchTerm + "'",
            "Search Results",
            JOptionPane.INFORMATION_MESSAGE);
    } else {
        // Build list model
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (Node song : foundNodes) {
            String songInfo = song.songName + " - " + song.artistName +
                    " [ " + song.getMoodScore() + " ] " + " - " +
                    playlistSorter.formatDuration(song.getDuration());
            listModel.addElement(songInfo);
        }

        // Create JList
        JList<String> resultList = new JList<>(listModel);
        resultList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // put items in a scroll pane
        JScrollPane scrollPane = new JScrollPane(resultList);
        scrollPane.setPreferredSize(new Dimension(400, 200));

        int option = JOptionPane.showConfirmDialog(this, scrollPane,
                "Search Results for: '" + searchTerm + "'",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION && !resultList.isSelectionEmpty()) {
            int selectedIndex = resultList.getSelectedIndex();
            Node selectedSong = foundNodes.get(selectedIndex);

            //selected song
            JOptionPane.showMessageDialog(this,
                    "Loading selected:\n" + selectedSong.songName + " - " + selectedSong.artistName,
                    "Song Selected",
                    JOptionPane.INFORMATION_MESSAGE);
            
            
            new Thread(()->{
               try{
                   String url = YouTubeUrlHelper.getStreamLinkFromYouTube(selectedSong.songPath);

                    if (url != null) {
                        if (embeddedMediaPlayerComponent != null && embeddedMediaPlayerComponent.mediaPlayer() != null) {
                            embeddedMediaPlayerComponent.mediaPlayer().controls().stop();
                        }
                        embeddedMediaPlayerComponent.mediaPlayer().media().play(url);
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(this, "Now Playing: " + selectedSong.songName + " - " + selectedSong.artistName);
                            updatePlayListDisplay();
                        });
                    } else {
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(this, "Failed to get stream URL!", "Error", JOptionPane.ERROR_MESSAGE);
                            updatePlayListDisplay();
                        });
                    }
                    

               }catch(Exception e){
                   JOptionPane.showMessageDialog(this,"Unexpected Error happend while trying to play selected song");
               }
            }).start();
            updatePlayListDisplay();
        }
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
        if (showingFavorites) {
            // If already in favorites view, return to normal view
            exitFavoritesView();
            return;
        }

        if (playlist == null || playlist.head == null) {
            JOptionPane.showMessageDialog(this, "Playlist is empty!", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        showingFavorites = true;
        listModel.clear();
        Node current = playlist.head;
        boolean hasFavorites = false;

        while (current != null) {
            if (current.isFavorite()) {
                hasFavorites = true;
                String songInfo = current.songName + " - " + current.artistName +
                        " [ " + current.getMoodScore() + " ] " + " - " +
                        playlistSorter.formatDuration(current.getDuration());

                if (current == currentNode && isPlaying) {
                    songInfo = "▶ " + songInfo;
                }

                // Add favorite star
                songInfo = "⭐ " + songInfo;

                listModel.addElement(songInfo);
            }
            current = current.nextNode;
        }

        if (!hasFavorites) {
            JOptionPane.showMessageDialog(this,
                    "No favorite songs found!\nRight-click on a song and select 'Add to Favorites'.",
                    "No Favorites",
                    JOptionPane.INFORMATION_MESSAGE);
            showingFavorites = false;
            updatePlayListDisplay(); // Return to normal view
        } else {
            // Highlight current playing song if it's a favorite
            if (currentNode != null && currentNode.isFavorite()) {
                int currentIndex = getIndexOfFavoriteNode(currentNode);
                if (currentIndex >= 0) {
                    playListJList.setSelectedIndex(currentIndex);
                    playListJList.ensureIndexIsVisible(currentIndex);
                }
            }
        }

        updateFavoritesButtonText(); // Update the button text when entering favorites view
    }
    private void exitFavoritesView() {
        showingFavorites = false;
        updatePlayListDisplay();
    }
    // Helper method to get index of a favorite node in the favorites view
    private int getIndexOfFavoriteNode(Node targetNode) {
        if (playlist == null || playlist.head == null || targetNode == null || !targetNode.isFavorite()) return -1;

        Node current = playlist.head;
        int index = 0;

        while (current != null) {
            if (current.isFavorite()) {
                if (current == targetNode) {
                    return index;
                }
                index++;
            }
            current = current.nextNode;
        }
        return -1;
    }
    private boolean isInFavoritesView() {
        return showingFavorites;
    }
    private Node getFavoriteNodeAtIndex(int index) {
        if (playlist == null || index < 0) return null;

        Node current = playlist.head;
        int currentIndex = 0;

        while (current != null) {
            if (current.isFavorite()) {
                if (currentIndex == index) {
                    return current;
                }
                currentIndex++;
            }
            current = current.nextNode;
        }
        return null;
    }
    // New method that plays song without changing the view
    private void playSongWithoutChangingView() {
        if (playlist == null || playlist.head == null) {
            JOptionPane.showMessageDialog(this, "Playlist is empty!", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Check if we're in favorites view and current node is no longer a favorite
        if (showingFavorites && (currentNode == null || !currentNode.isFavorite())) {
            // Find the first favorite song
            currentNode = findFirstFavoriteNode();
            if (currentNode == null) {
                JOptionPane.showMessageDialog(this, "No favorite songs available!", "Error", JOptionPane.WARNING_MESSAGE);
                showingFavorites = false;
                updatePlayListDisplay();
                return;
            }
        }

        if (currentNode == null) {
            currentNode = playlist.head;
        }

        isPlaying = true;
        remainingSeconds = currentNode.getDuration();
        if (songDurationTimer != null) {
            songDurationTimer.start();
        }

        updateThemeBasedOnMood();

        new Thread(() -> {
            streamUrl = YouTubeUrlHelper.getStreamLinkFromYouTube(currentNode.songPath);

            if (streamUrl != null) {
                if (embeddedMediaPlayerComponent != null && embeddedMediaPlayerComponent.mediaPlayer() != null) {
                    embeddedMediaPlayerComponent.mediaPlayer().controls().stop();
                }
                embeddedMediaPlayerComponent.mediaPlayer().media().play(streamUrl);
                SwingUtilities.invokeLater(() -> {
                    AutoClosingDialog.show(this, "Now Playing: " + currentNode.songName + " - " + currentNode.artistName, "Message", 1, 2000);
                    // Update display without changing view mode
                    updatePlayListDisplay();
                });
            } else {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Failed to get stream URL!", "Error", JOptionPane.ERROR_MESSAGE);
                    updatePlayListDisplay();
                });
            }
        }).start();

        AutoClosingDialog.show(this, "Fetching your song... just a moment!", "Message", 1, 5000);
        updatePlayListDisplay();
    }

    private void updateFavoritesButtonText() {
        if (favoritesButton != null) {
            if (showingFavorites) {
                favoritesButton.setText("📋 All Songs");
                favoritesButton.setToolTipText("Click to show all songs");
            } else {
                favoritesButton.setText("⭐ Favorites");
                favoritesButton.setToolTipText("Click to show favorite songs only");
            }
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
            showingFavorites = false; // Exit favorites view
            updatePlayListDisplay();
            JOptionPane.showMessageDialog(this, "Playlist imported successfully!");
        }
    }
    //method for initialize the color transisition
    private void initializeColorTransition() {
        currentBackgroundColor = DEFAULT_COLORS[0];
        targetBackgroundColor = DEFAULT_COLORS[0];

        colorTransitionTimer = new Timer(TRANSITION_DELAY, e -> updateColorTransition());
        colorTransitionTimer.setRepeats(true);

        // Set initial background
        applyBackgroundColor(currentBackgroundColor);
    }
    private void startMoodColorCycling() {
    Timer moodCycleTimer = new Timer(5000, e -> { // Change color every 5 seconds
        if (currentNode != null && isPlaying) {
            updateThemeBasedOnMood();
        }
    });
    moodCycleTimer.start();
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
