package com.tools.svn.local.ui;

import com.tools.svn.bean.SVNLocalFile;
import com.tools.svn.local.file.SVNLocalFileGenerator;
import com.tools.svn.local.file.SVNLocalModifiedFileGenerator;
import org.tmatesoft.svn.core.wc.SVNStatusType;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SVNLocalFileUI extends JFrame{

    List<SVNLocalFile> files;

    public SVNLocalFileUI(List<SVNLocalFile> files) {
        this.files = files;
    }

    public void display() {
        if (files == null) {
            files = new ArrayList<>();
        }
        setTitle("AppServer文件发布工具");
        setSize(1200, 400);
        setResizable(true);
        setLocationRelativeTo(null);

        // 创建表格模型，并添加表头
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new Object[]{false, "文件", "状态"});
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
        table.getColumnModel().getColumn(0).setMaxWidth(50); // 限制第一列的最大宽度
        table.getColumnModel().getColumn(0).setCellEditor(table.getDefaultEditor(Boolean.class));
//        table.getColumnModel().getColumn(0).setCellRenderer(table.getDefaultRenderer(Boolean.class));

        table.getColumnModel().getColumn(1).setMinWidth(800); // 限制第一列的最大宽度
        table.setDefaultEditor(Object.class, null);
        for (SVNLocalFile file : files) {
            model.addRow(new Object[]{false, file.getAbsFileName(), file.getStatus().toString()});
        }
//        model.addTableModelListener(e -> {
//            if (e.getColumn() == 0) {
//                int row = e.getFirstRow();
//                System.out.println(row);
//                Boolean checked = (Boolean) model.getValueAt(row, 0);
//                System.out.println(checked);
//            }
//        });
        // 创建一个 JPanel 容器组件，并添加表格和按钮到其中
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
//        JButton button = new JButton("确定");
//        panel.add(button, BorderLayout.SOUTH);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton button = new JButton("发布");
        button.addActionListener(e -> {
            List<SVNLocalFile> deployFiles = new ArrayList<>();
            int rowCount = model.getRowCount();
            for (int i = 0; i < rowCount; i ++) {
                Boolean checked = (Boolean) model.getValueAt(i, 0);
                if (checked) {
                    deployFiles.add(files.get(i));
                }
            }
            System.out.println(deployFiles);
        });
        buttonPanel.add(button);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        add(panel);

        // 将 JPanel 添加到 JFrame 中
        add(panel);

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

    static class CopyableTableCellRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            return this;
        }
    }


    public static void main(String[] args) {
        // 向表格中添加数据行
        List<SVNLocalFile> files = getSVNLocalFiles();

        new SVNLocalFileUI(files).display();
    }

    private static List<SVNLocalFile> getSVNLocalFiles() {

        SVNLocalFileGenerator generator = new SVNLocalModifiedFileGenerator();
        return generator.list();
    }

}
