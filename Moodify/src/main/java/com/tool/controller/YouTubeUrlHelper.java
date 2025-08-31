/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tool.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Paths;

/**
 *
 * @author ASUS
 */
public class YouTubeUrlHelper {
    
    public static String getStreamLinkFromYouTube(String youtubeUrl) {
    try {
        // Remove any surrounding quotes first
        String cleanedInput = youtubeUrl.replaceAll("^\"|\"$", "");
        
        // Check if the input is a file path instead of a YouTube URL
        if (isFilePath(cleanedInput)) {
            System.out.println("Input is a file path, returning: " + cleanedInput);
            return cleanedInput;
        }
        
        System.out.println("Current Working Directory: " + System.getProperty("user.dir"));

        String yt_dl_filePath = Paths.get("src", "main", "resources", "yt-dlp.exe").toString();
        System.out.println("Resolved Path: " + yt_dl_filePath);

        System.out.println("Getting stream link from: " + youtubeUrl);

        ProcessBuilder processBuilder = new ProcessBuilder(
            yt_dl_filePath,
            "-g",
            //"-f", "bestaudio[ext=m4a]/bestaudio/best", //use this to only get the audio
            "-f best[ext=mp4]/best",
            youtubeUrl
        );
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        String streamUrl = null;

        //get the line what start from the http (which is the streaming link)
        while ((line = reader.readLine()) != null) {
            System.out.println("YT-DLP Output: " + line);
            if (line.startsWith("http")) {
                streamUrl = line;
            }
        }

        int exitCode = process.waitFor();
        if (exitCode == 0 && streamUrl != null && !streamUrl.isEmpty()) {
            System.out.println("Stream URL: " + streamUrl);
            return streamUrl.trim();
        } else {
            System.err.println("yt-dlp did not return a valid stream URL");
        }
    } catch (Exception e) {
        System.out.println("Error: " + e.getMessage());
    }
    return null;
}

// Helper method to check if a string is a file path
private static boolean isFilePath(String input) {
    // Check for common file path indicators
    if (input == null || input.trim().isEmpty()) {
        return false;
    }
    
    // Remove any surrounding quotes for the check
    String cleanedInput = input.replaceAll("^\"|\"$", "");
    
    // Check if it starts with common file path patterns
    if (cleanedInput.startsWith("/") || // Unix absolute path
        cleanedInput.startsWith("~/") || // Unix home directory
        cleanedInput.matches("^[A-Za-z]:\\\\") || // Windows drive letter
        cleanedInput.startsWith(".\\") || // Windows relative path
        cleanedInput.startsWith("./") || // Unix relative path
        cleanedInput.contains("\\") || // Contains backslash (Windows)
        cleanedInput.contains("/")) {  // Contains forward slash (Unix)
        
        // Additional check to exclude URLs that might contain slashes
        if (cleanedInput.startsWith("http://") || 
            cleanedInput.startsWith("https://") ||
            cleanedInput.startsWith("www.") ||
            cleanedInput.startsWith("youtube.com") ||
            cleanedInput.startsWith("youtu.be")) {
            return false;
        }
        return true;
    }
    
    // Check for file extension patterns
    if (cleanedInput.matches(".*\\.(mp4|avi|mov|wmv|flv|mkv|mp3|wav|m4a|wma)$")) {
        return true;
    }
    
    return false;
}
}
