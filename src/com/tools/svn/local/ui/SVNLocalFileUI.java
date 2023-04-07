package com.tools.svn.local.ui;

import com.tools.linux.LinuxFileUploader;
import com.tools.svn.bean.SVNLocalBinaryFile;
import com.tools.svn.bean.SVNLocalFile;
import com.tools.svn.bean.ServerHost;
import com.tools.svn.local.binary.SVLLocalBinaryFileGenerator;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SVNLocalFileUI extends JFrame{

    List<SVNLocalFile> files;

    List<ServerHost> hosts;

    JTextArea textArea;

    public SVNLocalFileUI(List<SVNLocalFile> files, List<ServerHost> hosts) {
        this.files = files;
        this.hosts = hosts;
    }

    public void display() {
        if (files == null) {
            files = new ArrayList<>();
        }
        if (hosts == null) {
            hosts = new ArrayList<>();
        }
        setTitle("AppServer文件发布工具");
        setSize(1200, 1000);
        setResizable(true);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // 创建表格模型，并添加表头
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new Object[]{"选择", "文件", "状态"});
        JTable table = new JTable(model) {
            public TableCellRenderer getCellRenderer(int row, int column) {
                if (column == 0) { // 第一列使用CheckBox
                    return getDefaultRenderer(Boolean.class);
                } else {
                    return new MyTableCellRenderer();
                }
            }
        };
        table.getTableHeader().setVisible(true);
        table.getTableHeader().setReorderingAllowed(false); // 禁止拖动表头排序
        table.getColumnModel().getColumn(0).setMaxWidth(100); // 限制第一列的最大宽度
        table.getColumnModel().getColumn(0).setCellEditor(table.getDefaultEditor(Boolean.class));
//        table.getColumnModel().getColumn(0).setCellRenderer(table.getDefaultRenderer(Boolean.class));

        table.getColumnModel().getColumn(1).setMinWidth(800); // 限制第一列的最小宽度
        table.setDefaultEditor(Object.class, null);
        for (SVNLocalFile file : files) {
            model.addRow(new Object[]{false, file.getAbsFileName(), file.getStatus().toString()});
        }
        // 创建一个 JPanel 容器组件，并添加表格和按钮到其中
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        // 将 JPanel 添加到 JFrame 中
//        add(panel);
        mainPanel.add(panel, BorderLayout.NORTH);

        // 创建表格模型，并添加表头
        DefaultTableModel hostModel = new DefaultTableModel();
        hostModel.setColumnIdentifiers(new Object[]{"选择服务器", "服务器", "IP", "用户名"});
        JTable hostTable = new JTable(hostModel) {
            public TableCellRenderer getCellRenderer(int row, int column) {
                if (column == 0) { // 第一列使用CheckBox
                    return getDefaultRenderer(Boolean.class);
                } else {
                    return new MyTableCellRenderer();
                }
            }
        };
        hostTable.getTableHeader().setVisible(true);
        hostTable.getTableHeader().setReorderingAllowed(false); // 禁止拖动表头排序
        hostTable.getColumnModel().getColumn(0).setMaxWidth(100); // 限制第一列的最大宽度
        hostTable.getColumnModel().getColumn(0).setCellEditor(hostTable.getDefaultEditor(Boolean.class));
//        table.getColumnModel().getColumn(0).setCellRenderer(table.getDefaultRenderer(Boolean.class));

        hostTable.getColumnModel().getColumn(0).setMinWidth(500); // 限制第一列的最大宽度
        hostTable.setDefaultEditor(Object.class, null);
        for (ServerHost host : hosts) {
            hostModel.addRow(new Object[]{false, host.getName(), host.getIp(), host.getUser()});
        }
        // 创建一个 JPanel 容器组件，并添加表格和按钮到其中
        JPanel hostPanel = new JPanel(new BorderLayout());
        hostPanel.add(new JScrollPane(hostTable), BorderLayout.NORTH);
//        JButton button = new JButton("确定");
//        panel.add(button, BorderLayout.SOUTH);
        JPanel deployButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton deployButton = new JButton("发布");
        deployButton.addActionListener(e -> {
            deployButton.setEnabled(false);
            List<SVNLocalFile> deployFiles = new ArrayList<>();
            int rowCount = model.getRowCount();
            for (int i = 0; i < rowCount; i ++) {
                Boolean checked = (Boolean) model.getValueAt(i, 0);
                if (checked) {
                    deployFiles.add(files.get(i));
                }
            }
            String baseDir = "D:\\work\\svn_repository\\AppServer\\src";
            String binaryDir = "D:\\work\\svn_repository\\AppServer\\bin";
            SVNLocalBinaryFile binaryFiles = new SVLLocalBinaryFileGenerator(deployFiles, baseDir, binaryDir).list();
            System.out.println(binaryFiles);

            List<ServerHost> deployHosts = new ArrayList<>();
            int hostRowCount = hostModel.getRowCount();
            for (int i = 0; i < hostRowCount; i ++) {
                Boolean checked = (Boolean) hostModel.getValueAt(i, 0);
                if (checked) {
                    deployHosts.add(hosts.get(i));
                }
            }
            System.out.println(deployHosts);
            LinuxFileUploader uploader = new LinuxFileUploader(binaryFiles, deployHosts);
            uploader.setTextArea(textArea);
            uploader.setButton(deployButton);
            textArea.setText("");
            uploader.upload();
//            deployButton.setEnabled(true);
        });
        deployButtonPanel.add(deployButton);
        hostPanel.add(deployButtonPanel, BorderLayout.SOUTH);
//        add(panel);
        textArea = new JTextArea();
        textArea.setEditable(false);
        hostPanel.add(new JScrollPane(textArea), BorderLayout.CENTER);
        // 将 JPanel 添加到 JFrame 中
//        add(hostPanel);
        mainPanel.add(hostPanel, BorderLayout.CENTER);

        add(mainPanel);
        // 设置窗口关闭操作和可见性
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static class MyTableCellRenderer extends DefaultTableCellRenderer {

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (column == 2) { // 最后一列
                String status = (String) value;
                Color color;
                if (status == null) {
                    c.setForeground(table.getForeground());
                } else if (status.equalsIgnoreCase("modified")) {
                    color = new Color(50, 50, 180);
                    c.setForeground(color);
                } else if (status.equalsIgnoreCase("delete") || status.equalsIgnoreCase("missing")) {
                    color = new Color(180, 50, 50);
                    c.setForeground(color);
                } else {
                    c.setForeground(table.getForeground());
                }
            } else {
                c.setForeground(table.getForeground());
            }
            return c;
        }
    }

}
