/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tool.controller;

import java.awt.Button;
import java.awt.Component;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 *
 * @author ASUS
 */
public class AutoClosingDialog {
    
    public static void show(Component parent,String message,String title,int messageType,int timeOutMiliSec){
        Object[] options = {"OK"};
        JDialog jDialog = new JOptionPane(
                message,
                messageType,
                JOptionPane.DEFAULT_OPTION,
                null,
                options, //pass an empty object so no buttons will show up
                options[0]
        ).createDialog(parent, title);
        
        //run a timer to tigger Dialog.dispose
        Timer timer = new Timer(timeOutMiliSec,e-> jDialog.dispose());
        timer.setRepeats(false);
        timer.start();
        
        jDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        jDialog.setVisible(true);
    }
    
}
