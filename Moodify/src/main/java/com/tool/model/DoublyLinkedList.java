package com.tool.model;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DoublyLinkedList implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
        public Node head;
        public Node tail;

        public final void insertBeginning(String songName, String artistName, String songPath, int duration){
        Node newNode = new Node(songName, artistName, songPath, duration);
        if(head == null){
            head = newNode;
            tail = newNode;
        }
        else{
            head.previousNode = newNode;
            newNode.nextNode = head;
            head = newNode;
        }
    }
        //new overloaded method(Sa)
       public final void insertBeginning(String songName, String artistName, String songPath, int duration, int moodScore){
        Node newNode = new Node(songName, artistName, songPath, duration, moodScore);
        if (head == null){
            head = newNode;
            tail = newNode;
        }
        else{
            head.previousNode = newNode;
            newNode.nextNode = head;
            head = newNode;
        }
    }


        public final void insertEnd(String songName, String artistName, String songPath, int duration){
        Node newNode = new Node(songName, artistName, songPath, duration);
        if(tail == null ){
            head = newNode;
            tail = newNode;
        }
        else{
            tail.nextNode = newNode;
            newNode.previousNode = tail;
            tail = newNode;
        }
    }
        //new overloaded method(Sa)
        public final void insertEnd(String songName, String artistName, String songPath, int duration, int moodScore){
        Node newNode = new Node(songName, artistName, songPath, duration, moodScore);
        if(tail == null){
            head = newNode;
            tail = newNode;
        }
        else{
            tail.nextNode = newNode;
            newNode.previousNode = tail;
            tail = newNode;
        }
    }
        public final void insertSong(String songName, String artistName, int duration, String songPath, int moodScore) {
        insertEnd(songName, artistName, songPath, duration, moodScore);
    }

        public final void deleteBegin(){
                if(head == null){
                        System.out.println("List is Empty");
                        return;
                }
                else if(head == tail) {
                        System.out.println("deleted:"+head.songName);
                        head = null;
                        tail = null;
                }
                else{
                        System.out.println("deleted:"+head.songName);
                        head = head.nextNode;
                        head.previousNode = null;
                }
        }

        public final void deleteEnd(){
                if(head == null){
                        System.out.println("List is Empty");
                        return;
                }
                else if(head == tail){
                        System.out.println("deleted:"+tail.songName);
                        head = null;
                        tail = null;
                }
                else{
                        System.out.println("deleted:"+tail.songName);
                        tail = tail.previousNode;
                        tail.nextNode = null;
                }
        }
        //Updated -- added return type
        public List<Node> printForward(){
                List<Node> playList = new ArrayList<>();

                Node current_node = head;
                while(current_node != null){
                        System.out.println(current_node);
                        playList.add(current_node);
                        current_node = current_node.nextNode;
                }
                return playList;
        }

        //Updated -- added return type
        public List<Node> printBackward(){
                List<Node> playList = new ArrayList<>();
                Node current_node = tail;
                while(current_node != null){
                        System.out.println(current_node);
                        playList.add(current_node);
                        current_node = current_node.previousNode;
                }
                return playList;
        }

        public final int length(){
                int count = 0;
                Node current_node = head;

                while (current_node != null){
                        count ++;
                        current_node = current_node.nextNode;
                }
                return count;
        }

        public void clear(){
                head = null;
                tail = null;
                System.out.println("List Cleared");
        }


        public void searchBySongName(String songName){
                if(head != null){
                        Node current_node = head;
                        while (current_node != null){
                                if(current_node.songName == songName){
                                        System.out.println("Found song node:"+current_node);
                                        return;
                                }
                                current_node = current_node.nextNode;
                        }
                        return;
                }
        }
        //add new methods(Sa)
        public Node getNodeAt(int index) {
                if (index < 0 || index >= length()) return null;

                Node current = head;
                for (int i = 0; i < index; i++) {
                        current = current.nextNode;
                }
                return current;
        }
        //add getAllNodes method(Sa)
        public List<Node> getAllNodes(){
                List<Node> nodes = new ArrayList<>();
                Node current = head;
                while (current != null){
                        nodes.add(current);
                        current = current.nextNode;
                }
                return nodes;
        }

}
