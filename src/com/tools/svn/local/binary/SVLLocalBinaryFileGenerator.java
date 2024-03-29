package com.tools.svn.local.binary;

import com.tools.svn.bean.SVNDeployFile;
import com.tools.svn.bean.SVNLocalBinaryFile;
import com.tools.svn.bean.SVNLocalFile;
import com.tools.svn.prop.LocalProperties;
import org.tmatesoft.svn.core.wc.SVNStatusType;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

public class SVLLocalBinaryFileGenerator {

    public static Map<String, String> format = new HashMap<>();
    public static String localBaseDir;
    public static String localBinaryDir;

    static {
        format.put(".java", ".class");
    }

    /**
     * 源文件
     */
    private List<SVNLocalFile> localFiles;

    /**
     * 项目目录
     */
    private final String sourceBaseDir;

    /**
     * 编译目录
     */
    private final String binaryBaseDir;

    static {
        LocalProperties.init();
        localBinaryDir = LocalProperties.compilerDir;
        localBaseDir = LocalProperties.baseDir;
    }

    public SVLLocalBinaryFileGenerator(List<SVNLocalFile> localFiles) {
        this.localFiles = localFiles;
        this.sourceBaseDir = LocalProperties.baseDir + "\\src";
        this.binaryBaseDir = LocalProperties.compilerDir;
    }

    public SVNLocalBinaryFile list() {
        if (localFiles == null) {
            localFiles = new ArrayList<>();
        }
        String username = System.getenv("USERNAME");
        SVNLocalBinaryFile binaryFiles = new SVNLocalBinaryFile();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd/HHmmssSSS");
        String time = sdf.format(new Date());
        for (SVNLocalFile file: localFiles) {
            // 本地文件路径
            String absFileName = file.getAbsFileName();
            if (new File(absFileName).isDirectory()) {
                continue;
            }
            // 截取文件名
            int index = Math.max(absFileName.lastIndexOf("."), 0);
            // 源文件格式
            String sourceFormat = absFileName.substring(index);
            if (sourceFormat.equals(absFileName)) {
                sourceFormat = "";
            }
            // 编译后文件格式
            String binaryFormat = format.get(sourceFormat);
            // 配置文件、jar文件不需要从编译目录获取
            if (binaryFormat == null) {
                binaryFormat = sourceFormat;
            }
            // 编译后文件绝对路径
            String binaryFile = absFileName.replace(sourceBaseDir, binaryBaseDir);
            binaryFile = binaryFile.replace(sourceFormat, "");
            // 文件名
            String binaryFileName = binaryFile.substring(binaryFile.lastIndexOf("\\") + 1);
            // 编译后文件的文件目录
            String binaryPath = binaryFile.substring(0, binaryFile.lastIndexOf("\\"));
            File binaryDir = new File(binaryPath);
            // 由于可能存在内部类，发布列表需要从编译后文件所在目录遍历获取
            if (binaryDir.isDirectory()) {
                // 查找目录下所有文件
                String[] dirFiles = binaryDir.list();
                if (dirFiles == null) {
                    System.out.println(binaryDir + binaryFormat + "未找到");
                    continue;
                }
                for (String dirFile : dirFiles) {
                    // 如果文件名与要发布的文件名一致或者文件名为要发布文件的内部类名，加入到待发布列表
                    if (dirFile.equals(binaryFileName + binaryFormat) || dirFile.contains(binaryFileName + "$")) {
                        // 发布文件名=文件目录+文件名
                        String deployLocalFile = binaryDir + "\\" + dirFile;
                        String deployRemoteFile = deployLocalFile.replace(localBinaryDir, "");
                        deployRemoteFile = deployRemoteFile.replace(localBaseDir, "");
                        deployRemoteFile = deployRemoteFile.replace("\\", "/");
//                        System.out.println("发布文件 " + deployRemoteFile);
                        String backupFile = "/" + username + "/" + time + deployRemoteFile;
                        SVNDeployFile svnDeployFile = new SVNDeployFile(deployLocalFile, deployRemoteFile, backupFile);
                        svnDeployFile.setSourceFile(absFileName);
                        // 删除
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
