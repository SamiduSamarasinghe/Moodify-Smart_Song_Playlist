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
    
    public static String getStreamLinkFromYouTube(String youtubeUrl){
    try{
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
            else if(line.startsWith("https")){
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
}
