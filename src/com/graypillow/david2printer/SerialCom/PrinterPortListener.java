/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.graypillow.david2printer.SerialCom;

import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

/**
 *
 * @author Dark
 */
public class PrinterPortListener implements SerialPortEventListener {

    private SerialCom mySerialCom;

     PrinterPortListener(SerialCom mySerialCom) {
        this.mySerialCom = mySerialCom;
    }

    @Override
    public void serialEvent(SerialPortEvent spe) {
        if (spe.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            String receiveString = mySerialCom.receive();
            while (!receiveString.equals("")) {
                System.out.println(receiveString);
                receiveString = mySerialCom.receive();
            }
            
        }
    }

}
