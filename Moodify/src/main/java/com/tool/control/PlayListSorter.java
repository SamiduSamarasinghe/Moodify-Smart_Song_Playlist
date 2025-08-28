package com.tool.control;

import com.tool.model.DoublyLinkedList;
import com.tool.model.Node;
import java.util.ArrayList;
import java.util.List;

public class PlayListSorter {

    public void sortByMood(DoublyLinkedList currentPlaylist,String mood){
        
        if(currentPlaylist != null){
            switch(mood){
                case "Calm":
                    sort(currentPlaylist, 1);
                    break;

                case "Neutral":
                    sort(currentPlaylist, 2);
                    break;

                case "Energetic":
                    sort(currentPlaylist, 3);                    
                    break;

                default:
                    break;
            }
        }
    }
    
    private void sort(DoublyLinkedList playList,int moodValue){
        
        Node calmHead = null , calmTail = null;
        Node neutralHead = null , neutralTail = null;
        Node energeticHead = null, energeticTail = null; 
        
        Node currentNode = playList.head;
        
        while(currentNode != null){
            
            Node nextNode = currentNode.nextNode;
            //breake the link
            currentNode.nextNode = null;
            currentNode.previousNode = null;
            
            // go through each mood
            switch(currentNode.getMoodScore()){
                
                case 1: //calm
                    if(calmHead == null){
                        calmHead = currentNode;
                        calmTail = currentNode;
                    }
                    else{ // add to the end Node & swap the links
                        calmTail.nextNode = currentNode;        
                        currentNode.previousNode = calmTail;
                        calmTail = currentNode;
                    }
                    break;
            }
            
            
            
            currentNode = currentNode.nextNode;
        }
        
        
    }
}
