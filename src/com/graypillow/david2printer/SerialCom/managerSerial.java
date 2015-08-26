/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.graypillow.david2printer.SerialCom;

import com.graypillow.david2printer.DavidScanner.David_3_10_4;
import static com.graypillow.david2printer.SerialCom.SerialCom.portList;
import com.graypillow.david2printer.main.David2Printer;
import com.graypillow.david2printer.util.messagePrinter;
import gnu.io.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.List;
import java.util.TooManyListenersException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.Timer;

/**
 *
 * @author Dark
 */
public class managerSerial {

    private David2Printer parent;
    private JComboBox davidSerialBox;
    private JComboBox printerSerialBox;
    private JComboBox portrateBox;

    static CommPortIdentifier portId; //
    static Enumeration portList; //定义串口对象的枚举器

    private SerialCom serialDavid;
    private SerialCom serialPrinter;

    private int speed = 2000;

    private volatile boolean isAutoRun = false;

    private boolean hasSendMessage = false;

    public void setIsAutoRun(boolean isAutoRun) {
        this.isAutoRun = isAutoRun;
    }

    public managerSerial(JComboBox davidSerialBox, JComboBox printerSerialBox,
            JComboBox portrateBox, David2Printer parent) {
        this.parent = parent;
        this.davidSerialBox = davidSerialBox;
        this.printerSerialBox = printerSerialBox;
        this.portrateBox = portrateBox;
        //初始化波特率
        portrateBox.addItem("9600");
        portrateBox.addItem("14400");
        portrateBox.addItem("19200");
        portrateBox.addItem("28800");
        portrateBox.addItem("38400");
        portrateBox.addItem("57600");
        portrateBox.addItem("115200");
        portrateBox.addItem("250000");
        portrateBox.setSelectedItem("250000");

        //更新串口
        serialDavid = new SerialCom();
        serialPrinter = new SerialCom();

        portList = CommPortIdentifier.getPortIdentifiers();
        while (portList.hasMoreElements()) {
            portId = (CommPortIdentifier) portList.nextElement();
            String portname = portId.getName();
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                davidSerialBox.addItem(portname);
                printerSerialBox.addItem(portname);
            }
        }

        David_3_10_4.setDavidComInterface(serialDavid);
        David_3_10_4.setPrinterComInterface(serialPrinter);

    }

    public boolean openDavidSerial() {
        serialDavid.setPortRate(Integer.valueOf((String) portrateBox.getSelectedItem()));
        serialDavid.setPort((String) davidSerialBox.getSelectedItem());
        serialDavid.open();
        if (!serialDavid.isOpen()) {
            JOptionPane.showMessageDialog(parent, "串口打开失败");
            return false;
        }
        return true;
    }

    public boolean openPrinterSerial() {
        serialPrinter.setPortRate(Integer.valueOf((String) portrateBox.getSelectedItem()));
        serialPrinter.setPort((String) printerSerialBox.getSelectedItem());
        serialPrinter.open();
        if (!serialPrinter.isOpen()) {
            JOptionPane.showMessageDialog(parent, "串口打开失败");
            return false;
        }
        //添加端口监听
        try {
            serialPrinter.getSerialPort().addEventListener(serialPrinter);
        } catch (TooManyListenersException ex) {
            Logger.getLogger(managerSerial.class.getName()).log(Level.SEVERE, null, ex);
        }

        return true;
    }

    public void closeDavidSerial() {
        if (serialDavid.isOpen()) {
            serialDavid.close();
        }
    }

    public void closePrinterSerial() {
        if (serialPrinter.isOpen()) {
            serialPrinter.close();
            //去掉端口监听
            serialPrinter.getSerialPort().removeEventListener();
        }
    }

    public void SendMessageToDavid(String sendDavid) {
        if (serialDavid.isOpen()) {
            serialDavid.send(sendDavid);
        }
    }

    public void SendMessageToPrinter(String sendPrinter) {
        if (serialPrinter.isOpen()) {
            serialPrinter.send(sendPrinter);
        }
    }

    public void transmitMessage() {
        //创建一个转发进程
        //线程安全性有待解决
        final SwingWorker<String, String> mySwingWorker = new SwingWorker<String, String>() {
            @Override
            protected String doInBackground() {
                String outputString = "";
                String input = "";
                String printerString = "";

                while (true) {
                    if (serialDavid.isOpen() & serialPrinter.isOpen()) {
                        if (!hasSendMessage) {
                            input = serialDavid.receive();
                        }

                        printerString = serialPrinter.receive();
                        if (!printerString.equals("")) {
                            System.out.println(printerString);
                            if (printerString.equals("ok")) {
                                hasSendMessage = false;
                            }
                        }
                        printerString = "";
                        if (!input.equals("")) {
                            messagePrinter.addMessage(input, "David");
                            //优先判断m语句和设置起点的命令
                            if (input.equals(David_3_10_4.SETINITPOINT)) {
                                David_3_10_4.initThePosition(speed);
                                messagePrinter.addMessage("回到初始位置", "Printer");
                            } else if (David_3_10_4.ismCmdMatches(input) & isAutoRun) {
                                David_3_10_4.sendMoveCmd(input, speed);
                                if (David_3_10_4.getLocaton() >= David_3_10_4.getMaxLocation()) {
                                    David_3_10_4.AutoScanControl.saveScan();
                                }
                                messagePrinter.addMessage(David_3_10_4.mCmd2Gcode(input, speed), "Printer");
                            } else if (parent.PM.getD2pProperties().containsKey(input)) {
                                outputString = parent.PM.getD2pProperties().getProperty(input);
                            }
                        }
                        publish(outputString);
                        input = "";
                        outputString = "";
                        try {
                            Thread.currentThread().sleep(10);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(managerSerial.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                }

            }

            @Override

            protected void process(List<String> chunks) {
                for (String line : chunks) {
                    if (!line.equals("")) {
                        messagePrinter.addMessage(line, "Printer");
                        serialPrinter.send(line);
                        hasSendMessage = true;
                    }
                }
            }
        };
        mySwingWorker.execute();

//        Timer myTimer = new Timer(1000, new ActionListener() {
//
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                System.out.println("SwingWorker 的状态 是否被取消 "
//                        + mySwingWorker.isCancelled() + "SwingWorker 的状态 是否完成" + mySwingWorker.isDone());
//                if (mySwingWorker.isCancelled() | mySwingWorker.isDone()) {
//                    mySwingWorker.execute();
//                }
//            }
//        });
//        myTimer.setRepeats(true);
//        myTimer.start();
    }

}
