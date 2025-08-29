package com.tool.control;

import com.tool.model.DoublyLinkedList;
import com.tool.model.Node;
import java.util.ArrayList;
import java.util.List;

public class PlaylistSorter {
   
    public void sortByMood(DoublyLinkedList currentPlaylist,String mood){
        switch(mood){
            case "Calm":
                sort(currentPlaylist, true);
                break;
            case "Neutral":
                sortNeutral(currentPlaylist);
                break;
                
            case "Energetic":
                sort(currentPlaylist, false);
                break;
            default:
                return;
        }
    }
    
    public void sortByTime(DoublyLinkedList currentPlaylist,boolean byAscedingOrder){
        if(currentPlaylist.head == null){return;} // empty playlist
        boolean swaped ;
        
        do{
            swaped = false;
            Node currentNode = currentPlaylist.head;
            
            //loop-throught all the nodes in the linkedList
            while(currentNode.nextNode != null){
             
                //compare currentNodes mood socre with next Nodes moodScore
                if(byAscedingOrder && currentNode.getDuration()> currentNode.nextNode.getDuration()){
                    swapData(currentNode,currentNode.nextNode);
                    swaped = true;
                }
                
                else if(!byAscedingOrder && currentNode.getDuration()< currentNode.nextNode.getDuration()){
                    swapData(currentNode.nextNode, currentNode);
                    swaped = true;
                }
                
                //increment
                currentNode = currentNode.nextNode;
            }
        }while(swaped);        
    }
        
    private void sort(DoublyLinkedList playlist,boolean ascending){
        
        if(playlist.head == null){return;} // empty playlist
        boolean swaped ;
        
        do{
            swaped = false;
            Node currentNode = playlist.head;
            
            //loop-throught all the nodes in the linkedList
            while(currentNode.nextNode != null){
             
                //compare currentNodes mood socre with next Nodes moodScore
                if(ascending && currentNode.getMoodScore() > currentNode.nextNode.getMoodScore()){
                    swapData(currentNode,currentNode.nextNode);
                    swaped = true;
                }
                
                else if(!ascending && currentNode.getMoodScore()< currentNode.nextNode.getMoodScore()){
                    swapData(currentNode.nextNode, currentNode);
                    swaped = true;
                }
                
                //increment
                currentNode = currentNode.nextNode;
            }
        }while(swaped);
    }
    
    //swap firstNode data with secondNode data
    private void swapData(Node firstNode, Node secondNode){
  
        //store firstNode values temprorly
        String tempSongName = firstNode.songName;
        String tempSongPath = firstNode.songPath;
        String tempArtistName = firstNode.artistName;
        int tempMoodScore = firstNode.getMoodScore();
        int tempDuratoin = firstNode.getDuration();
        
        
        //apply second node values to firstNode
        firstNode.songName = secondNode.songName;
        firstNode.songPath = secondNode.songPath;
        firstNode.artistName = secondNode.artistName;
        firstNode.setMoodScore(secondNode.getMoodScore());
        firstNode.setDuration(secondNode.getDuration());
        
        
        //apply first node values to secondNode
        secondNode.songName = tempSongName;
        secondNode.songPath = tempSongPath;
        secondNode.artistName = tempArtistName;
        secondNode.setMoodScore(tempMoodScore);
        secondNode.setDuration(tempDuratoin);
    }
    
    private void sortNeutral(DoublyLinkedList playList){
        if(playList.head == null){return;} // empty playlist
        boolean swaped ;
        
        do{
            swaped = false;
            Node currentNode = playList.head;
            
            //loop-throught all the nodes in the linkedList
            while(currentNode.nextNode != null){
             
                //Math.abs use to get the absolute value
                //compare distens between currentNodes mood socre to 5 and nextNode mood score to 5 swap if there is less distance
                if(Math.abs(currentNode.getMoodScore()-3) > Math.abs(currentNode.nextNode.getMoodScore() -3)){
                    swapData(currentNode, currentNode.nextNode);
                    swaped = true;
                }
                //increment
                currentNode = currentNode.nextNode;
            }
        }while(swaped);
    }
    
    //get time converted back to a string
    public String formatDuration(int totalSeconds){
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        
        return String.format("%d:%02d", minutes,seconds);
    }
}
