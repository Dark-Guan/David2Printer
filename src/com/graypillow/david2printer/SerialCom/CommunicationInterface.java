/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.graypillow.david2printer.SerialCom;

import java.util.Observable;
import java.io.*;
/**
 *通讯接口方法
 * @author Dark
 */
public interface CommunicationInterface {
    
    /**
     *通讯接口的打开方法
     */
    public abstract boolean open();

    /**
     *通讯接口的参数设置
     */
    public abstract void setParameter();
    /**
     *通讯接口的关闭方法
     */
    public abstract boolean close();
    
    /**
     *通讯接口的发送方法，发送字符串
     * @param input
     */
    public abstract void send(String input);
    
    /**
     *通讯接口的接收方法
     * @return
     */
    public abstract String receive();
    
    /**
     *中断，取消发送命令的方法
     */
    public void sendBreak();
}
