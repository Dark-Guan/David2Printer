package com.graypillow.david2printer.DavidScanner;

import com.graypillow.david2printer.SerialCom.CommunicationInterface;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Dark
 */
public class David_3_10_4 {

    public static String ADDTOLIST = "A";
    public static String ERASESCAN = "E";
    public static String EREASETEXTURE = "F";
    public static String GRABTEXTURE = "G";
    public static String MODESCAN = "2";
    public static String MODETEXTURE = "3";
    public static String MODESL = "4";
    public static String SAVESCAN = "Z";
    public static String STARTSCAN = "S";
    public static String STARTSCANREPETION = "R";
    public static String STOPLASERSCAN = "T";

    public static String SETINITPOINT = "0";

    public static int SLEEPTIME = 4000;

    private static final String reg1 = "m\\+\\d+";
    private static final String reg2 = "m\\-\\d+";
    private static final String reg3 = "m\\d+";

    public final static int MOVEZERO = 0;
    public final static int MOVEUP = 1;
    public final static int MOVEUDOWN = -1;

    private volatile static long locaton = 0;

    private volatile static long maxLocation = 20000;
    private volatile static int scanAngle = 30;

    private static int scanTime = 360 / scanAngle + 1;
    private static int subdivide = 100;

    private static CommunicationInterface printerComInterface = null;
    private static CommunicationInterface davidComInterface = null;

    public static void initScanTime() {
        David_3_10_4.scanTime = 360 / scanAngle + 1;
    }

    public static void setSubdivide(int subdivide) {
        David_3_10_4.subdivide = subdivide;
    }

    public static int getSubdivide() {
        return subdivide;
    }

    public static void setMaxLocation(long maxLocation) {
        David_3_10_4.maxLocation = maxLocation;
    }

    public static long getMaxLocation() {
        return maxLocation;
    }

    public static void setScanAngle(int scanAngle) {
        David_3_10_4.scanAngle = scanAngle;
        scanTime = 360 / scanAngle + 1;
    }

    public static int getScanAngle() {
        return scanAngle;
    }

    public static void setPrinterComInterface(CommunicationInterface myComInterface) {
        David_3_10_4.printerComInterface = myComInterface;
    }

    public static void setDavidComInterface(CommunicationInterface davidComInterface) {
        David_3_10_4.davidComInterface = davidComInterface;
    }

    public static boolean ismCmdMatches(String cmdString) {
        if (cmdString.matches(reg3)) {
            return true;
        } else if (cmdString.matches(reg1)) {
            return true;
        } else if (cmdString.matches(reg2)) {
            return true;
        }
        return false;
    }

    /**
     * output steps with sign
     *
     * @param cmdString
     * @return
     */
    public static int cmdToStep(String cmdString) {
        int steps = 0;
        String midString = "";
        if (ismCmdMatches(cmdString)) {
            midString = cmdString.replaceFirst("m", "");
//            midString = cmdString.replaceFirst("\\+", "");
//            midString = cmdString.replaceFirst("\\-", "");
        }
        if (midString.matches("\\d+")) {
            steps = Integer.parseInt(midString);
        }
        return steps;
    }

    public static long getLocaton() {
        return locaton;
    }

    public static void setLocaton(long locaton) {
        David_3_10_4.locaton = locaton;
    }

    public static String mCmd2Gcode(String cmdString, int speed) {
        String gCodeString = "";
        if (cmdString.matches(reg3)) {
            gCodeString = "G91\nGO Z" + (0.01 * cmdToStep(cmdString)) + " F" + speed;
        } else if (cmdString.matches(reg1)) {
            gCodeString = "G91\nGO Z" + (0.01 * cmdToStep(cmdString)) + " F" + speed;
        } else if (cmdString.matches(reg2)) {
            gCodeString = "G91\nGO Z" + (0.01 * cmdToStep(cmdString)) + " F" + speed;
        }
        return gCodeString;
    }

    public static void moveToMaxPointAndBack(int speed) {
        if (printerComInterface != null) {
            String cmdString = "G91\nGO Z" + (0.01 * (maxLocation)) + " F" + speed;
            printerComInterface.send(cmdString);
            cmdString = "G91\nGO Z" + (0.01 * (-maxLocation)) + " F" + speed;
            printerComInterface.send(cmdString);
        }
    }

    public static void sendMoveCmd(String cmdString, int speed) {
        if (printerComInterface != null) {
            printerComInterface.send(mCmd2Gcode(cmdString, speed));
            locaton = locaton + cmdToStep(cmdString);
        }
    }

    public static void initThePosition(int speed) {
        if (printerComInterface != null) {
            String cmdString = "G91\nG0 Z" + (0.01 * (-locaton)) + " F" + speed;
            printerComInterface.send(cmdString);
            locaton = 0;
        }
    }

    public static class AutoScanControl {

        public static final int STATE_SCANNING = 1;

        public static final int STATE_SAVINGSCAN = 2;

        public static final int STATE_PREPARE = 3;
        public static final int STATE_STOP = 4;

        private volatile static int currentState = STATE_PREPARE;

        public static int getCurrentState() {
            return currentState;
        }

        public static void setCurrentState(int currentState) {
            AutoScanControl.currentState = currentState;
        }

        public static void startScan() {
            if (davidComInterface != null & (currentState == STATE_PREPARE | currentState == STATE_SCANNING | currentState == STATE_SAVINGSCAN)) {
                //设置到扫描模式
                davidComInterface.send(MODESCAN);
                try {
                    Thread.currentThread().sleep(2000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(David_3_10_4.class.getName()).log(Level.SEVERE, null, ex);
                }
                //回零点
                initThePosition(2000);
                //开始扫描
                davidComInterface.send(STARTSCAN);
            }
        }

        public static void saveScan() {
            if (davidComInterface != null & printerComInterface != null & currentState != STATE_STOP) {
                setCurrentState(STATE_SAVINGSCAN);
                //停止扫描
                davidComInterface.send(STOPLASERSCAN);
                try {
                    Thread.currentThread().sleep(SLEEPTIME);
                } catch (InterruptedException ex) {
                    Logger.getLogger(David_3_10_4.class.getName()).log(Level.SEVERE, null, ex);
                }
                //进入纹理捕捉模式
                davidComInterface.send(MODETEXTURE);
                try {
                    Thread.currentThread().sleep(SLEEPTIME);
                } catch (InterruptedException ex) {
                    Logger.getLogger(David_3_10_4.class.getName()).log(Level.SEVERE, null, ex);
                }
                //捕捉纹理
                try {
                    printerComInterface.send("M107");
                    Thread.currentThread().sleep(1000);
                    davidComInterface.send(GRABTEXTURE);
                    Thread.currentThread().sleep(1000);
                    printerComInterface.send("M106 S255");

                    Thread.currentThread().sleep(SLEEPTIME);
                } catch (InterruptedException ex) {
                    Logger.getLogger(David_3_10_4.class.getName()).log(Level.SEVERE, null, ex);
                }
                //扫描加入队列
                davidComInterface.send(ADDTOLIST);
                try {
                    Thread.currentThread().sleep(SLEEPTIME);
                } catch (InterruptedException ex) {
                    Logger.getLogger(David_3_10_4.class.getName()).log(Level.SEVERE, null, ex);
                }
                //删除当前扫描
                davidComInterface.send(ERASESCAN);
                try {
                    Thread.currentThread().sleep(SLEEPTIME);
                } catch (InterruptedException ex) {
                    Logger.getLogger(David_3_10_4.class.getName()).log(Level.SEVERE, null, ex);
                }
                //转动一定角度
                String turn = "G91\nG0 E" + (float) ((3200.0 / subdivide) / (360.0 / scanAngle)) + " F60";
                System.out.println("转动角度" + turn);
                printerComInterface.send(turn);
                try {
                    Thread.currentThread().sleep(SLEEPTIME);
                } catch (InterruptedException ex) {
                    Logger.getLogger(David_3_10_4.class.getName()).log(Level.SEVERE, null, ex);
                }
                scanTime = scanTime - 1;
                //重新开始扫描
                if (scanTime >= 0) {
                    startScan();
                    setCurrentState(STATE_SCANNING);
                    System.out.println("重新开始扫描！" + "还剩" + scanTime + "次");
                } else {
                    setCurrentState(STATE_STOP);
                    System.out.println("停止扫描！");
                    return;
                }
            }
        }
    }
}
