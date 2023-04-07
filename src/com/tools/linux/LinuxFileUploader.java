package com.tools.linux;

import com.jcraft.jsch.*;
import com.tools.svn.bean.SVNDeployFile;
import com.tools.svn.bean.SVNLocalBinaryFile;
import com.tools.svn.bean.SVNLocalFile;
import com.tools.svn.bean.ServerHost;
import com.tools.uploader.FileUploader;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

public class LinuxFileUploader implements FileUploader {

    private final SVNLocalBinaryFile binaryFiles;

    private final List<ServerHost> serverHosts;

    private JTextArea textArea;

    private JButton button;

    private static final ThreadPoolExecutor pool;

    private int counter;

    static {
        pool = new ThreadPoolExecutor(5, 10, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<>(20));
    }

    public LinuxFileUploader(SVNLocalBinaryFile binaryFiles, List<ServerHost> serverHosts) {
        this.binaryFiles = binaryFiles;
        this.serverHosts = serverHosts;
        counter = 0;
    }

    @Override
    public void upload() {
        startCounter();
        String password = "";
        int port = 22; // SSH端口号，默认是22

        JSch jsch = new JSch();
        for (ServerHost serverHost: serverHosts) {
            pool.execute(() -> {
                try {
                    // 创建SSH会话
                    Session session = jsch.getSession(serverHost.getUser(), serverHost.getIp(), port);
                    session.setPassword(password);
                    session.setConfig("StrictHostKeyChecking", "no");
                    session.connect();
                    // 创建SSH通道
                    Channel channel = session.openChannel("sftp");
                    channel.connect();
                    ChannelSftp sftp = (ChannelSftp) channel;
                    flushToTextArea(serverHost.getName() + " 开始备份\n");
                    for (SVNDeployFile file: binaryFiles.getModifyFiles()) {
                        String remoteFilePath = file.getRemoteFile();
                        String backupPath = file.getBackupFile();
                        flushToTextArea(serverHost.getName() + " " + remoteFilePath + "-> " + backupPath + "\n");
                        // 创建目录
                        try {
                            sftp.cd(backupPath.substring(0, backupPath.lastIndexOf("/")));
                        } catch (SftpException e) {
                            // 目录不存在，创建目录
                            String[] dirs = backupPath.split("/");
                            String path = "";
                            for (String dir : dirs) {
                                if (dir.length() > 0 && !dir.contains(".")) {
                                    path += "/" + dir;
                                    try {
                                        sftp.cd(path);
                                    } catch (SftpException ex) {
                                        sftp.mkdir(path);
                                        sftp.cd(path);
                                    }
                                }
                            }
                        }
                        try {
                            sftp.stat(remoteFilePath);
//                            sftp.rm();
                            // 文件已存在，备份到指定目录
                            sftp.rename(remoteFilePath, backupPath);
                        } catch (SftpException e) {
                            // 文件不存在，直接上传
                            flushToTextArea(remoteFilePath + " 无须备份\n");
                        }
                    }
                    for (SVNDeployFile file: binaryFiles.getModifyFiles()) {
                        String remoteFilePath = file.getRemoteFile();
                        // 创建目录
                        try {
                            sftp.cd(remoteFilePath.substring(0, remoteFilePath.lastIndexOf("/")));
                        } catch (SftpException e) {
                            // 目录不存在，创建目录
                            String[] dirs = remoteFilePath.split("/");
                            String path = "";
                            for (String dir : dirs) {
                                if (dir.length() > 0 && !dir.contains(".")) {
                                    path += "/" + dir;
                                    try {
                                        sftp.cd(path);
                                    } catch (SftpException ex) {
                                        sftp.mkdir(path);
                                        sftp.cd(path);
                                    }
                                }
                            }
                        }
                        // 上传文件
                        File local = new File(file.getLocalFile());
                        if (local.isFile()) {
                            flushToTextArea(serverHost.getName() + " " + file.getLocalFile() + " 开始上传\n");
                            sftp.put(new FileInputStream(local), file.getRemoteFile());
                            flushToTextArea(serverHost.getName() + " " + file.getLocalFile() + " 上传成功\n");
                        } else {
                            flushToTextArea(serverHost.getName() + " " + file.getLocalFile() + " 本地文件不存在或不是文件\n");
                        }
                    }
                    // 关闭通道和会话
                    sftp.exit();
                    channel.disconnect();
                    session.disconnect();
                } catch (JSchException | FileNotFoundException | SftpException e) {
                    throw new RuntimeException(e);
                } finally {
                    counter ++;
                }
            });
        }

    }

    public void setTextArea(JTextArea textArea) {
        this.textArea = textArea;
    }

    public void setButton(JButton button) {
        this.button = button;
    }

    public void flushToTextArea(String msg) {
        pool.execute(() -> {
            if (textArea != null) {
                textArea.append(msg);
            } else {
                System.out.println(msg);
            }
            textArea.setCaretPosition(textArea.getDocument().getLength());
        });
    }

    public void startCounter() {
        pool.execute(() -> {
            while (true) {
                if (counter == serverHosts.size()) {
                    if (button != null) {
                        button.setEnabled(true);
                    }
                    if (textArea != null) {
                        textArea.append("发布成功");
                    }
                    break;
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
