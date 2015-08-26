package com.graypillow.david2printer.SerialCom;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import gnu.io.*;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Dark
 */
public class SerialCom implements CommunicationInterface, Runnable, SerialPortEventListener, CommPortOwnershipListener {

    //定义串口通讯不要的对象
    static CommPortIdentifier portId; //
    static CommPort commPort;
    static Enumeration portList; //定义串口对象的枚举器

    private volatile int portRate = 250000;//只用许修改波特率，跟arduino通讯只需要选择波特率
    private InputStream inputStream; //定义输入字节流
    BufferedReader myBR;
    private OutputStream outputStream; //定义输入字节流
    private SerialPort serialPort;//串口对象
    private Thread readThread; //读取进程，发送时实时读取
    private Thread writeThread;//写进程，发送

    private StringBuffer readBuilder = new StringBuffer();//存储读取
    private StringBuffer writeBuilder = new StringBuffer();//存储写入

    private StringBuffer middleString = new StringBuffer();

    private boolean isOpen = false;

    private String portNameString;

    SerialCom() {
        portList = CommPortIdentifier.getPortIdentifiers();
    }

    public SerialPort getSerialPort() {
        return serialPort;
    }

    public boolean isOpen() {
        return isOpen;
    }

    //获取系统中可用的串口
    public Enumeration getPorts() {
        portList = CommPortIdentifier.getPortIdentifiers();
        return portList;
    }

    //
    public void setPort(String comString) {

        try {
            //        while (portList.hasMoreElements()) {
//            portId = (CommPortIdentifier) portList.nextElement();
//            String portname = portId.getName();
//            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
//                if (portname.equals(comString)) {
//                    // System.out.println(comString + " start!");
//                    portNameString = comString;
//                    break;
//                }
//            }
//        }
            portId = CommPortIdentifier.getPortIdentifier(comString);

        } catch (NoSuchPortException ex) {
            Logger.getLogger(SerialCom.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public boolean open() {

//        开启串口
        try {

            serialPort = (SerialPort) portId.open("David2Printer", 2000);
            //设置波特率
            setParameter();
        } catch (PortInUseException ex) {
            Logger.getLogger(SerialCom.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
//        获取串口对象的输入流
        try {
            inputStream = serialPort.getInputStream();
            myBR = new BufferedReader(new InputStreamReader(inputStream));
        } catch (IOException ex) {
            serialPort.close();
            Logger.getLogger(SerialCom.class.getName()).log(Level.SEVERE, null, ex);
            return false;

        }
//        获取串口对象的输出流
        try {
            outputStream = serialPort.getOutputStream();
        } catch (IOException ex1) {
            serialPort.close();
            Logger.getLogger(SerialCom.class.getName()).log(Level.SEVERE, null, ex1);
            return false;
        }

        portId.addPortOwnershipListener(this);

        isOpen = true;
        return true;
    }

    public void setPortRate(int portRate) {
        this.portRate = portRate;

    }

    @Override
    //只允许修改波特率
    public void setParameter() {
        try {
            serialPort.setSerialPortParams(portRate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
        } catch (UnsupportedCommOperationException ex) {
            Logger.getLogger(SerialCom.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public boolean close() {
        if (!isOpen) {
            return false;
        }
        //关闭串口
        if (!(serialPort == null)) {
            try {
                inputStream.close();
                outputStream.close();
            } catch (IOException ex) {
                Logger.getLogger(SerialCom.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
            serialPort.close();
            portId.removePortOwnershipListener(this);
        }
        isOpen = false;
        return true;
    }

    @Override
    public void send(String input) {
        input = input + "\n";
        try {
            outputStream.write(input.getBytes());

        } catch (IOException ex) {
            Logger.getLogger(SerialCom.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String receive() {

        try {
            if (inputStream.available() >= 2) {
                return myBR.readLine();
            }
        } catch (IOException ex) {
            Logger.getLogger(SerialCom.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    @Override
    public void sendBreak() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    //提供直接开启进程的功能
    public void run() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

//    public static void main(String[] args) {
//        SerialCom SC = new SerialCom();
//        try {
//            SC.setPort("COM1");
//            SC.setPortRate(250000);
//            if (SC.open()) {
//                SC.send("hallo");
//            }
//        } finally {
//            if (!(SC == null)) {
//                SC.close();
//            }
//        }
//    }
    public void serialEvent(SerialPortEvent spe) {
        switch (spe.getEventType()) {
            case SerialPortEvent.BI:
            case SerialPortEvent.OE:
            case SerialPortEvent.FE:
            case SerialPortEvent.PE:
            case SerialPortEvent.CD:
            case SerialPortEvent.CTS:
            case SerialPortEvent.DSR:
            case SerialPortEvent.RI:
            case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
                break;
            case SerialPortEvent.DATA_AVAILABLE:
                byte[] readBuffer = new byte[20];
                try {
                    while (inputStream.available() > 0) {
                        int numBytes = inputStream.read(readBuffer);
                    }
                    System.out.println(new String(readBuffer));
                } catch (IOException e) {
                    System.out.println(e);
                }
                break;
        }
    }

    public void ownershipChange(int type) {
        if (type == CommPortOwnershipListener.PORT_OWNERSHIP_REQUESTED) {
            int cmd = JOptionPane.showConfirmDialog(null,
                    "当前串口" + portNameString + "被其他使用者请求\n" + "是：放弃改串口；否 ：不理睬", "提示",
                    JOptionPane.WARNING_MESSAGE);
            if (cmd == JOptionPane.YES_OPTION) {
                close();
            }
        }
    }
}
