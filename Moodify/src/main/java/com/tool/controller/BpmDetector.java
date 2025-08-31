/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tool.controller;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ASUS
 */
public class BpmDetector {
    
    private List<Long> intervals = new ArrayList<>();
    
    public String calculateMoodFromTaps(List<Long> taps) {
        if (taps.size() < 2) {
            return "Not enough taps";
        }

        //find out gap between each time stamps
        for (int i = 1; i < taps.size(); i++) {
            intervals.add(taps.get(i) - taps.get(i - 1));
        }
        
        //get the total average from the intervales
        double avg = intervals.stream().mapToLong(v -> v).average().orElse(0);
        double bpm = 60000.0 / avg; //devide the average by the total miliseconds in a minute

        if (bpm < 70) return "Calm (" + (int)bpm + " BPM)";
        else if (bpm < 110) return "Neutral (" + (int)bpm + " BPM)";
        else return "Energetic (" + (int)bpm + " BPM)";
    }
}
