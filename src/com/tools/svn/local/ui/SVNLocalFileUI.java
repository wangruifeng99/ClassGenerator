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
        setTitle("AppServer�ļ���������");
        setSize(1300, 1000);
        setResizable(true);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // �������ģ�ͣ�����ӱ�ͷ
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new Object[]{"ѡ��", "�ļ�", "״̬", "����޸�ʱ��", "�ѱ���"});
        JTable table = new JTable(model) {
            public TableCellRenderer getCellRenderer(int row, int column) {
                if (column == 0) { // ��һ��ʹ��CheckBox
                    return getDefaultRenderer(Boolean.class);
                } else {
                    return new MyTableCellRenderer();
                }
            }
        };
        table.getTableHeader().setVisible(true);
        table.getTableHeader().setReorderingAllowed(false); // ��ֹ�϶���ͷ����
        table.getColumnModel().getColumn(0).setMaxWidth(100); // ���Ƶ�һ�е������
        table.getColumnModel().getColumn(0).setCellEditor(table.getDefaultEditor(Boolean.class));
//        table.getColumnModel().getColumn(0).setCellRenderer(table.getDefaultRenderer(Boolean.class));

        table.getColumnModel().getColumn(1).setMinWidth(800); // ���Ƶ�һ�е���С���
        table.getColumnModel().getColumn(3).setMinWidth(120); // ���Ƶ����е���С���
        table.setDefaultEditor(Object.class, null);
        // TODO
        Map<String, Long> binaryTimeMap = genBinaryTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (SVNLocalFile file : files) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(file.getLastModifyTime());
            String lastModifyTime = file.getLastModifyTime() == 0 ? "0" : sdf.format(calendar.getTime());
            Long binaryModifyTimeL = binaryTimeMap.get(file.getAbsFileName());
            String binaryCompiled = "��";
            if (binaryModifyTimeL != null && binaryModifyTimeL > file.getLastModifyTime() || !file.getAbsFileName().endsWith(".java")) {
                binaryCompiled = "��";
            }
            model.addRow(new Object[]{false, file.getAbsFileName(), file.getStatus().toString(), lastModifyTime, binaryCompiled});
        }
        // ����һ�� JPanel �������������ӱ��Ͱ�ť������
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        // �� JPanel ��ӵ� JFrame ��
//        add(panel);
        mainPanel.add(panel, BorderLayout.NORTH);

        // �������ģ�ͣ�����ӱ�ͷ
        DefaultTableModel hostModel = new DefaultTableModel();
        hostModel.setColumnIdentifiers(new Object[]{"ѡ�������", "������", "IP", "�û���"});
        JTable hostTable = new JTable(hostModel) {
            public TableCellRenderer getCellRenderer(int row, int column) {
                if (column == 0) { // ��һ��ʹ��CheckBox
                    return getDefaultRenderer(Boolean.class);
                } else {
                    return new MyTableCellRenderer();
                }
            }
        };
        hostTable.getTableHeader().setVisible(true);
        hostTable.getTableHeader().setReorderingAllowed(false); // ��ֹ�϶���ͷ����
        hostTable.getColumnModel().getColumn(0).setMaxWidth(100); // ���Ƶ�һ�е������
        hostTable.getColumnModel().getColumn(0).setCellEditor(hostTable.getDefaultEditor(Boolean.class));
//        table.getColumnModel().getColumn(0).setCellRenderer(table.getDefaultRenderer(Boolean.class));

        hostTable.getColumnModel().getColumn(0).setMinWidth(500); // ���Ƶ�һ�е������
        hostTable.setDefaultEditor(Object.class, null);
        for (ServerHost host : hosts) {
            hostModel.addRow(new Object[]{false, host.getName(), host.getIp(), host.getUser()});
        }
        // ����һ�� JPanel �������������ӱ��Ͱ�ť������
        JPanel hostPanel = new JPanel(new BorderLayout());
        hostPanel.add(new JScrollPane(hostTable), BorderLayout.NORTH);
//        JButton button = new JButton("ȷ��");
//        panel.add(button, BorderLayout.SOUTH);
        JPanel deployButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton deployButton = new JButton("����");
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
        mainPanel.add(new JScrollPane(textArea), BorderLayout.CENTER);
        // �� JPanel ��ӵ� JFrame ��
//        add(hostPanel);
        mainPanel.add(hostPanel, BorderLayout.SOUTH);

        add(mainPanel);
        // ���ô��ڹرղ����Ϳɼ���
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static class MyTableCellRenderer extends DefaultTableCellRenderer {

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (column == 2) { // ���һ��
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
                } else if (status.equalsIgnoreCase("added")){
                    color = new Color(227, 65, 214);
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
