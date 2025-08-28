package com.tool.control;

import com.tool.model.DoublyLinkedList;
import com.tool.model.Node;
import java.util.ArrayList;
import java.util.List;

public class PlayListSorter {
   
    public void sortByMood(DoublyLinkedList currentPlayList,String mood){
        switch(mood){
            case "Calm":
                sort(currentPlayList, true);
                break;
            case "Neutral":
                sortNeutral(currentPlayList);
                break;
                
            case "Energetic":
                sort(currentPlayList, false);
                break;
            default:
                return;
        }
    }
        
    private void sort(DoublyLinkedList playList,boolean ascending){
        
        if(playList.head == null){return;} // empty playlist
        boolean swaped ;
        
        do{
            swaped = false;
            Node currentNode = playList.head;
            
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
        
        
        //apply second node values to firstNode
        firstNode.songName = secondNode.songName;
        firstNode.songPath = secondNode.songPath;
        firstNode.artistName = secondNode.artistName;
        firstNode.setMoodScore(secondNode.getMoodScore());

        
        //apply first node values to secondNode
        secondNode.songName = tempSongName;
        secondNode.songPath = tempSongPath;
        secondNode.artistName = tempArtistName;
        secondNode.setMoodScore(tempMoodScore);
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
                if(Math.abs(currentNode.getMoodScore()-5) > Math.abs(currentNode.nextNode.getMoodScore() -5)){
                    swapData(currentNode, currentNode.nextNode);
                    swaped = true;
                }
                //increment
                currentNode = currentNode.nextNode;
            }
        }while(swaped);
    }
}
