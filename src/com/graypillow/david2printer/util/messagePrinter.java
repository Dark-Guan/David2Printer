/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.graypillow.david2printer.util;

import com.sun.org.apache.bcel.internal.generic.InstructionConstants;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import sun.security.jca.GetInstance;

/**
 * 一个单例设计模式的信息打印框，用于打印不同格式的信息到一个Jtable
 *
 * @author Dark
 */
public class messagePrinter {

    private static JTextArea staitcjTextArea;
    private static messagePrinter myMessagePrinter;

    private static messagePrinter msgPrinter;

    private messagePrinter() {

    }

    public static messagePrinter getInstance() {

        return InstanceFactory.myMessagePrinter;
    }

    private static class InstanceFactory {

        private static messagePrinter myMessagePrinter = new messagePrinter();
    }

    public static void addMessage(String message, String ownName) {
        if (staitcjTextArea != null) {
            staitcjTextArea.append(ownName + ">>" + message);
            staitcjTextArea.append("\n");
            //限制staitcjTextArea中的字符个数
            if (staitcjTextArea.getDocument().getLength() > 2048) {
//                try {
                    
//                    String text = staitcjTextArea.getDocument().
//                            getText(staitcjTextArea.getDocument().getLength() - 2048,
//                                    staitcjTextArea.getDocument().getLength());
                    staitcjTextArea.setText("");
                    //staitcjTextArea.setText(text);
                    
//                } catch (BadLocationException ex) {
//                    Logger.getLogger(messagePrinter.class.getName()).log(Level.SEVERE, null, ex);
//                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "未指定JTextArea", "警告！", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void setJTextArea(JTextArea jTextArea) {
        staitcjTextArea = jTextArea;
        if (!staitcjTextArea.equals(null)) {
            //给TestArea添加自动滚动
            staitcjTextArea.getDocument().addDocumentListener(new DocumentListener() {

                @Override
                public void insertUpdate(DocumentEvent e) {
                    staitcjTextArea.setCaretPosition(staitcjTextArea.getDocument().getLength());
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                }
            });
        }
    }

}
