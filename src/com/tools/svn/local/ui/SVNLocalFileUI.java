package com.tools.svn.local.ui;

import com.tools.svn.bean.SVNDeployFile;
import com.tools.svn.bean.SVNLocalBinaryFile;
import com.tools.svn.bean.SVNLocalFile;
import com.tools.svn.bean.ServerHost;
import com.tools.svn.local.binary.SVLLocalBinaryFileGenerator;
import com.tools.uploader.linux.LinuxFileUploader;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class SVNLocalFileUI extends JFrame{

    List<SVNLocalFile> files;

    List<ServerHost> hosts;

    JTextArea textArea;

    boolean isRemote;

    public SVNLocalFileUI(List<SVNLocalFile> files, List<ServerHost> hosts) {
        this.files = files;
        this.hosts = hosts;
    }

    public SVNLocalFileUI(List<SVNLocalFile> files, List<ServerHost> hosts, boolean isRemote) {
        this.files = files;
        this.hosts = hosts;
        this.isRemote = isRemote;
    }

    public void display() {
        if (files == null) {
            files = new ArrayList<>();
        }
        if (hosts == null) {
            hosts = new ArrayList<>();
        }
        setTitle("AppServer文件发布工具");
        setSize(1300, 1000);
        setResizable(true);
        setLocationRelativeTo(null);
        try {
            setIconImage(Toolkit.getDefaultToolkit().getImage(SVNLocalFileUI.class.getResource("logo.PNG")));
        } catch (Exception e) {
            e.printStackTrace();
        }

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setLayout(new GridLayout(3, 1, 0, 10));

        // 创建表格模型，并添加表头
        DefaultTableModel model = new DefaultTableModel();
        Object[] title;
        if (isRemote) {
            title = new String[]{"选择", "文件", "状态", "提交时间", "最后修改时间", "已编译"};
        } else {
            title = new String[]{"选择", "文件", "状态", "最后修改时间", "已编译"};
        }
        model.setColumnIdentifiers(title);
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
        table.getColumnModel().getColumn(3).setMinWidth(120); // 限制第三列的最小宽度
        if (isRemote) {
            table.getColumnModel().getColumn(4).setMinWidth(120); // 限制第四列的最小宽度
        }
        table.setDefaultEditor(Object.class, null);

        table.setRowHeight(22);
        Map<String, Long> binaryTimeMap = genBinaryTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (SVNLocalFile file : files) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(file.getLastModifyTime());
            String lastModifyTime = file.getLastModifyTime() == 0 ? "0" : sdf.format(calendar.getTime());
            calendar.setTimeInMillis(file.getCommittedTime());
            String committedTime = file.getCommittedTime() == 0 ? "0" : sdf.format(calendar.getTime());
            Long binaryModifyTimeL = binaryTimeMap.get(file.getAbsFileName());
            String binaryCompiled = "否";
            if (binaryModifyTimeL != null && binaryModifyTimeL > file.getLastModifyTime() || !file.getAbsFileName().endsWith(".java")) {
                binaryCompiled = "是";
            }
            if (isRemote) {
                model.addRow(new Object[]{false, file.getAbsFileName(), file.getStatus().toString(), committedTime, lastModifyTime, binaryCompiled});
            } else {
                model.addRow(new Object[]{false, file.getAbsFileName(), file.getStatus().toString(), lastModifyTime, binaryCompiled});
            }

        }
        // 创建一个 JPanel 容器组件，并添加表格和按钮到其中
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(table));

        // 将 JPanel 添加到 JFrame 中
//        add(panel);
        mainPanel.add(panel);

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
        hostTable.setRowHeight(22);
        for (ServerHost host : hosts) {
            hostModel.addRow(new Object[]{false, host.getName(), host.getIp(), host.getUser()});
        }
        // 创建一个 JPanel 容器组件，并添加表格和按钮到其中
        JPanel hostPanel = new JPanel(new BorderLayout());
        hostPanel.setLayout(new BorderLayout());
        hostPanel.add(new JScrollPane(hostTable), BorderLayout.CENTER);
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
            SVNLocalBinaryFile binaryFiles = new SVLLocalBinaryFileGenerator(deployFiles).list();
            System.out.println(binaryFiles);

            List<ServerHost> deployHosts = new ArrayList<>();
            int hostRowCount = hostModel.getRowCount();
            for (int i = 0; i < hostRowCount; i ++) {
                Boolean checked = (Boolean) hostModel.getValueAt(i, 0);
                if (checked) {
                    deployHosts.add(hosts.get(i));
                }
            }
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

//        add(hostPanel);
        mainPanel.add(hostPanel);
        mainPanel.add(new JScrollPane(textArea));

        add(mainPanel);
        // 设置窗口关闭操作和可见性
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static class MyTableCellRenderer extends DefaultTableCellRenderer {

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            Color color;
            if (column == 2) { // 最后一列
                String status = (String) value;
                if (status == null) {
                    c.setForeground(table.getForeground());
                } else if (status.equalsIgnoreCase("modified")) {
                    color = new Color(50, 50, 180);
                    c.setForeground(color);
                } else if (status.equalsIgnoreCase("delete") || status.equalsIgnoreCase("missing")) {
                    color = new Color(180, 50, 50);
                    c.setForeground(color);
                } else if (status.equalsIgnoreCase("added")){
                    color = new Color(227, 65, 214);
                    c.setForeground(color);
                } else {
                    c.setForeground(table.getForeground());
                }
            } else if (column == 4) {
                if (value.equals("否")) {
                    color = new Color(255, 50, 50);
                    c.setForeground(color);
                }
            } else {
                c.setForeground(table.getForeground());
            }
            return c;
        }
    }

    private Map<String, Long> genBinaryTime() {
        Map<String, Long> result = new HashMap<>();
        SVNLocalBinaryFile allBinaryFiles = new SVLLocalBinaryFileGenerator(files).list();
        List<SVNDeployFile> modifyFiles = allBinaryFiles.getModifyFiles();
        for (SVNDeployFile deployFile: modifyFiles) {
            File file = new File(deployFile.getLocalFile());
            if (file.exists()) {
                result.put(deployFile.getSourceFile(), file.lastModified());
            }
        }
        return result;
    }

}
