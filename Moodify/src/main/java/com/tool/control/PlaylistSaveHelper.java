/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tool.control;

import com.tool.model.DoublyLinkedList;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author ASUS
 */
public class PlaylistSaveHelper {
    
    
// Save playlist to file automatically on exit
    public static void savePlaylistToFile(DoublyLinkedList playlist) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("playlist.dat"))) {
            oos.writeObject(playlist);
            System.out.println("Playlist automatically saved to playlist.dat");
        } catch (IOException ex) {
            System.out.println("Error saving playlist: " + ex.getMessage());
        }
    }
    
        // Load playlist from file automatically on startup
    public static DoublyLinkedList loadPlaylistFromFile() {
        File file = new File("playlist.dat");
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                System.out.println("Playlist loaded from file successfully!");
                return (DoublyLinkedList) ois.readObject();
                
            } catch (IOException | ClassNotFoundException ex) {
                System.out.println("Error loading playlist: " + ex.getMessage());
                return new DoublyLinkedList(); // Create new if load fails
            }
        } 
        
        return new DoublyLinkedList(); 
    }
    
    public static DoublyLinkedList loadPlaylistManual(JFrame parent){
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load Playlist");
        
        if (fileChooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                JOptionPane.showMessageDialog(parent, "Playlist loaded successfully!");
                return (DoublyLinkedList) ois.readObject();
//                updatePlayListDisplay();

            } catch (IOException | ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(parent, "Error loading playlist: " + ex.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
                return new DoublyLinkedList();
            }
        }
        return new DoublyLinkedList();
    }
    
    public static void savePlaylistmanual(JFrame parent,DoublyLinkedList playlist){
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Playlist");
        
        if (fileChooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(playlist);
                JOptionPane.showMessageDialog(parent, "Playlist saved successfully!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(parent, "Error saving playlist: " + ex.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
