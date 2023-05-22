package com.tools.svn.local.binary;

import com.tools.svn.bean.SVNDeployFile;
import com.tools.svn.bean.SVNLocalBinaryFile;
import com.tools.svn.bean.SVNLocalFile;
import org.tmatesoft.svn.core.wc.SVNStatusType;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

public class SVLLocalBinaryFileGenerator {

    public static Map<String, String> format = new HashMap<>();
    public static String linuxPath = "/export/home/${hostName}";

    public static String backupPath = "/export/home/${hostName}/logs/backup";
    public static String localBaseDir = "D:\\work\\svn_repository\\AppServer";
    public static String localBinaryDir = "D:\\work\\svn_repository\\AppServer\\bin";

    static {
        format.put(".java", ".class");
    }

    /**
     * Դ�ļ�
     */
    private List<SVNLocalFile> localFiles;

    /**
     * ��ĿĿ¼
     */
    private final String sourceBaseDir;

    /**
     * ����Ŀ¼
     */
    private final String binaryBaseDir;

    public SVLLocalBinaryFileGenerator(List<SVNLocalFile> localFiles) {
        this.localFiles = localFiles;
        this.sourceBaseDir = "D:\\work\\svn_repository\\AppServer\\src";;
        this.binaryBaseDir = "D:\\work\\svn_repository\\AppServer\\bin";
    }

    public SVNLocalBinaryFile list() {
        if (localFiles == null) {
            localFiles = new ArrayList<>();
        }
        String username = System.getenv("USERNAME");
        SVNLocalBinaryFile binaryFiles = new SVNLocalBinaryFile();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String time = sdf.format(new Date());
        for (SVNLocalFile file: localFiles) {
            // �����ļ�·��
            String absFileName = file.getAbsFileName();
            if (new File(absFileName).isDirectory()) {
                continue;
            }
            // ��ȡ�ļ���
            int index = Math.max(absFileName.lastIndexOf("."), 0);
            // Դ�ļ���ʽ
            String sourceFormat = absFileName.substring(index);
            // ������ļ���ʽ
            String binaryFormat = format.get(sourceFormat);
            // �����ļ���jar�ļ�����Ҫ�ӱ���Ŀ¼��ȡ
            if (binaryFormat == null) {
                binaryFormat = sourceFormat;
            }
            // ������ļ�����·��
            String binaryFile = absFileName.replace(sourceBaseDir, binaryBaseDir);
            binaryFile = binaryFile.replace(sourceFormat, "");
            // �ļ���
            String binaryFileName = binaryFile.substring(binaryFile.lastIndexOf("\\") + 1);
            // ������ļ����ļ�Ŀ¼
            String binaryPath = binaryFile.substring(0, binaryFile.lastIndexOf("\\"));
            File binaryDir = new File(binaryPath);
            // ���ڿ��ܴ����ڲ��࣬�����б���Ҫ�ӱ�����ļ�����Ŀ¼������ȡ
            if (binaryDir.isDirectory()) {
                // ����Ŀ¼�������ļ�
                String[] dirFiles = binaryDir.list();
                if (dirFiles == null) {
                    System.out.println(binaryDir + binaryFormat + "δ�ҵ�");
                    continue;
                }
                for (String dirFile : dirFiles) {
                    // ����ļ�����Ҫ�������ļ���һ�»����ļ���ΪҪ�����ļ����ڲ����������뵽�������б�
                    if (dirFile.equals(binaryFileName + binaryFormat) || dirFile.contains(binaryFileName + "$")) {
                        // �����ļ���=�ļ�Ŀ¼+�ļ���
                        String deployLocalFile = binaryDir + "\\" + dirFile;
                        String deployRemoteFile = deployLocalFile.replace(localBinaryDir, linuxPath);
                        deployRemoteFile = deployRemoteFile.replace(localBaseDir, linuxPath);
                        deployRemoteFile = deployRemoteFile.replace("\\", "/");
                        System.out.println("�����ļ� " + deployRemoteFile);
                        String backupFile = deployRemoteFile.replace(linuxPath, backupPath + "/" + username + "/" + time);
                        SVNDeployFile svnDeployFile = new SVNDeployFile(deployLocalFile, deployRemoteFile, backupFile);
                        // ɾ��
                        if (SVNStatusType.STATUS_MISSING.equals(file.getStatus()) || SVNStatusType.STATUS_DELETED.equals(file.getStatus())) {
                            binaryFiles.addDeleteFile(svnDeployFile);
                        } else {
                            binaryFiles.addModifyFiles(svnDeployFile);
                        }
                    }
                }
            }

        }
        return binaryFiles;
    }
}
