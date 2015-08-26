/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.graypillow.david2printer.DavidScanner;

import java.awt.Frame;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Dark
 */
public class PropertyManage {

    private volatile Properties d2pProperties;
    private Frame parentFrame;
    private JTable myJTable;

    private myTableModel myTM;

    public PropertyManage(JTable myJTable, Frame parentFrame) {
        this.d2pProperties = new Properties();
        this.myJTable = myJTable;
        this.parentFrame = parentFrame;
        myTM = new myTableModel(d2pProperties);
    }

    public Properties getD2pProperties() {
        return d2pProperties;
    }

    public void openProperty() {
        JFileChooser myJfc = new JFileChooser();
        File dir = new File(System.getProperty("user.dir"));
        myJfc.setCurrentDirectory(dir);
        int v = myJfc.showOpenDialog(parentFrame);
        if (v == JFileChooser.APPROVE_OPTION) {
            File file = myJfc.getSelectedFile();
            d2pProperties.clear();
            loadProperty(file.getAbsolutePath());
            myTM.refreshData();
        }
    }

    public void saveProperty() {
        JFileChooser myJfc = new JFileChooser();
        File dir = new File(System.getProperty("user.dir"));
        myJfc.setCurrentDirectory(dir);
        int v = myJfc.showOpenDialog(parentFrame);
        FileOutputStream FOP = null;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String cmdString = df.format(new Date());
        if (v == JFileChooser.APPROVE_OPTION) {
            File file = myJfc.getSelectedFile();
            if (file.exists()) {
                int c = JOptionPane.showConfirmDialog(parentFrame, "该文件已经存在，是否覆盖？");
                if (c == JOptionPane.YES_OPTION) {
                    try {
                        FOP = new FileOutputStream(file);
                        d2pProperties.store(FOP,cmdString);
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(PropertyManage.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(PropertyManage.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else {
                try {
                    FOP = new FileOutputStream(file);
                    d2pProperties.store(FOP, cmdString);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(PropertyManage.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(PropertyManage.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public boolean loadProperty(String proDir) {
        FileInputStream fis = null;
        try {
            File f = new File(proDir);
            fis = new FileInputStream(f);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(PropertyManage.class.getName()).log(Level.SEVERE,
                    null, ex);
            JOptionPane.showMessageDialog(parentFrame, "未找到配置文件", "警告？",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        try {
            if (fis != null) {
//                d2pProperties= new Properties();
                d2pProperties.load(fis);
                fis.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(PropertyManage.class.getName()).log(Level.SEVERE,
                    null, ex);
            JOptionPane.showMessageDialog(parentFrame, "配置文件加载错误", "警告？",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    public void addAProperty() {
        String inputString = "";
        inputString = JOptionPane.showInputDialog(parentFrame, "请输入键值对 (例如 l=M07)",
                "添加项", JOptionPane.QUESTION_MESSAGE);
        String reg = ".*=.*";
        if (inputString.matches(reg)) {
            System.out.println(inputString);
            String[] cmd = inputString.split("=");

            d2pProperties.put(cmd[0], cmd[1]);
            myTM.refreshData();
        }

    }

    public void deletePreperty() {

        String selected = (String) myTM.getValueAt(myJTable.getSelectedRow(),
                0);
        if (d2pProperties.size() > 1) {
            d2pProperties.remove(selected);
        } else {
            JOptionPane.showMessageDialog(parentFrame, "不能将设置全部删除！", "警告！", JOptionPane.WARNING_MESSAGE);
        }
        myTM.refreshData();
    }

    public String getCmd() {
        String selected = (String) myTM.getValueAt(myJTable.getSelectedRow(),
                1);
        return selected;
    }

    public void refreshingProperties() {
        if (d2pProperties.isEmpty()) {
            String[][] modelStringses = new String[][]{{""}, {""}};
            myJTable.setModel(
                    new DefaultTableModel(
                            modelStringses, new String[]{
                                "David指令", "打印机指令"
                            }));
        } else {

        }

    }

    public void showPropertyInJtable() {
        myTM.refreshData();
    }

    /**
     *
     */
    public myTableModel getMyTablemodel() {
        return myTM;
    }

    public class myTableModel extends AbstractTableModel {

        private final String[] columnNames = {
            "David指令", "打印机指令"
        };
        private Object[][] data;
        Properties d2pProperties;

        myTableModel(Properties d2pProperties) {
            this.d2pProperties = d2pProperties;

        }

        public void refreshData() {
            if (!d2pProperties.isEmpty()) {
                int propertiesCount = d2pProperties.size();
                data = new String[propertiesCount][2];

                Set<Object> keySet = d2pProperties.keySet();
                Iterator iter = keySet.iterator();
                for (int i = 0; i < d2pProperties.size() & iter.hasNext(); i++) {
                    Object next = iter.next();
                    data[i][0] = next.toString();
                    data[i][1] = d2pProperties.getProperty(next.toString());
                }
            }

            myJTable.setModel(myTM);
            myJTable.updateUI();
        }

        @Override
        public int getRowCount() {
            return data.length;
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public String getColumnName(int col) {
            return columnNames[col];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return data[rowIndex][columnIndex];
        }

        public boolean isCellEditable(int row, int col) {
            //Note that the data/cell address is constant,
            //no matter where the cell appears onscreen.
            return true;
        }
    }

}
