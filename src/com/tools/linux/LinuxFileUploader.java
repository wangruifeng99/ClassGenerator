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
        int port = 22; // SSH�˿ںţ�Ĭ����22

        JSch jsch = new JSch();
        for (ServerHost serverHost: serverHosts) {
            pool.execute(() -> {
                try {
                    // ����SSH�Ự
                    Session session = jsch.getSession(serverHost.getUser(), serverHost.getIp(), port);
                    session.setPassword(password);
                    session.setConfig("StrictHostKeyChecking", "no");
                    session.connect();
                    // ����SSHͨ��
                    Channel channel = session.openChannel("sftp");
                    channel.connect();
                    ChannelSftp sftp = (ChannelSftp) channel;
                    flushToTextArea(serverHost.getName() + " ��ʼ����\n");
                    for (SVNDeployFile file: binaryFiles.getModifyFiles()) {
                        String remoteFilePath = file.getRemoteFile();
                        String backupPath = file.getBackupFile();
                        flushToTextArea(serverHost.getName() + " " + remoteFilePath + "-> " + backupPath + "\n");
                        // ����Ŀ¼
                        try {
                            sftp.cd(backupPath.substring(0, backupPath.lastIndexOf("/")));
                        } catch (SftpException e) {
                            // Ŀ¼�����ڣ�����Ŀ¼
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
                            // �ļ��Ѵ��ڣ����ݵ�ָ��Ŀ¼
                            sftp.rename(remoteFilePath, backupPath);
                        } catch (SftpException e) {
                            // �ļ������ڣ�ֱ���ϴ�
                            flushToTextArea(remoteFilePath + " ���뱸��\n");
                        }
                    }
                    for (SVNDeployFile file: binaryFiles.getModifyFiles()) {
                        String remoteFilePath = file.getRemoteFile();
                        // ����Ŀ¼
                        try {
                            sftp.cd(remoteFilePath.substring(0, remoteFilePath.lastIndexOf("/")));
                        } catch (SftpException e) {
                            // Ŀ¼�����ڣ�����Ŀ¼
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
                        // �ϴ��ļ�
                        File local = new File(file.getLocalFile());
                        if (local.isFile()) {
                            flushToTextArea(serverHost.getName() + " " + file.getLocalFile() + " ��ʼ�ϴ�\n");
                            sftp.put(new FileInputStream(local), file.getRemoteFile());
                            flushToTextArea(serverHost.getName() + " " + file.getLocalFile() + " �ϴ��ɹ�\n");
                        } else {
                            flushToTextArea(serverHost.getName() + " " + file.getLocalFile() + " �����ļ������ڻ����ļ�\n");
                        }
                    }
                    // �ر�ͨ���ͻỰ
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
                        textArea.append("�����ɹ�");
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
