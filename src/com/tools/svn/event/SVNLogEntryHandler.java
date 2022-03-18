package com.tools.svn.event;

import com.tools.svn.prop.MSVNProperties;
import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SVNLogEntryHandler implements ISVNLogEntryHandler {

    private static String srcBaseDir = "/branches/dev210118/src/";
    private static String confBaseDir = "/branches/dev210118/conf/";
    private static String libBaseDir = "/branches/dev210118/lib/";
    private static String compilerDir = "D:\\work\\eclipse_workspace\\AppServer\\bin";
    private static String outputDir = "D:\\temp";
    private static String outputCodeDir = outputDir + "\\code";
    private static List<String> errorList;
    private static final AtomicInteger counter = new AtomicInteger();
    public static int total = 0;

    public static void init() {
        srcBaseDir = MSVNProperties.srcBaseDir;
        confBaseDir = MSVNProperties.confBaseDir;
        libBaseDir = MSVNProperties.libBaseDir;
        compilerDir = MSVNProperties.compilerDir;
        outputDir = MSVNProperties.outputDir;
        outputCodeDir = MSVNProperties.outputCodeDir;
        errorList = new ArrayList<>();
    }

    @Override
    public void handleLogEntry(SVNLogEntry logEntry) {
        // 删除上次生成的文件
        if(counter.getAndIncrement() == 0) {
            deleteCodeDir();
        }
        // 获取更新的文件列表
        Set<String> pathList = generateChangeList(logEntry);
        // 根据更新的文件列表生成文件
        generateFile(pathList);
        synchronized(SVNLogEntryHandler.class) {
            // 任务执行结束后，打印文件清单
            if(counter.get() >= total) {
                printFileList();
            }
            // 任务执行结束后，打印失败清单
            if(!errorList.isEmpty() && counter.get() >= total) {
                System.out.println("以下文件获取失败：");
                for (String errPath: errorList) {
                    System.out.println(errPath);
                }
            }
        }

    }

    /**
     * 删除代码输出文件夹
     */
    private void deleteCodeDir() {
        File codeDir = new File(outputCodeDir);
        deleteCodeDir(codeDir);
    }

    private static boolean deleteCodeDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            if(children == null) {
                return true;
            }
            //递归删除目录中的子目录下
            for (String child : children) {
                boolean success = deleteCodeDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

    private Set<String> generateChangeList(SVNLogEntry logEntry) {
        Map<String, SVNLogEntryPath> changedPaths = logEntry.getChangedPaths();
        Set<String> filePathList = new HashSet<>();
        for(SVNLogEntryPath path: changedPaths.values()) {
            char type = path.getType();
            if(type == SVNLogEntryPath.TYPE_DELETED) {
                continue;
            }
            String filePath = path.getPath();
            if(filePath.startsWith(srcBaseDir)) {
                filePath = filePath.replace(srcBaseDir, "");
            } else if(filePath.startsWith(confBaseDir)) {
                filePath = filePath.replace(confBaseDir, "");
            } else if(filePath.startsWith(libBaseDir)) {
                filePath = filePath.replace(libBaseDir, "");
            }

            filePath = filePath.replace(".java", ".class");
            filePathList.add(filePath);
            System.out.println("准备文件：" + filePath);
        }
        return filePathList;
    }

    private void generateFile(Set<String> pathList) {
        for(String path: pathList) {
            File sourceFile = getSourceFile(path);
            if(sourceFile == null) {
                synchronized (SVNLogEntryHandler.class) {
                    errorList.add(path);
                }
                continue;
            }
            try {
                File destFile = getDestFile(path);
                if(destFile.exists()) {
                    continue;
                }
                Files.copy(sourceFile.toPath(), destFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
                synchronized (SVNLogEntryHandler.class) {
                    errorList.add(path);
                }
            }
        }

    }

    /**
     * 查找编译目录下的源文件
     */
    private File getSourceFile(String filePath) {
        String fullPath = compilerDir + "\\" + filePath;
        File file = new File(fullPath);
        if(file.exists()) {
            return file;
        }
        fullPath = compilerDir + "\\lib\\" + filePath;
        file = new File(fullPath);
        if(file.exists()) {
            return file;
        }
        fullPath = compilerDir + "\\conf\\" + filePath;
        file = new File(fullPath);
        if(file.exists()) {
            return file;
        }
        return null;
    }

    /**
     * 获取目标路径
     */
    private File getDestFile(String filePath) throws IOException {
        if(!outputCodeDir.endsWith("\\")) {
            outputCodeDir = outputCodeDir + "\\";
        }
        String destFilePath = outputCodeDir + filePath;
        System.out.println("正在生成文件：" + destFilePath);
        int dest = destFilePath.lastIndexOf("/");
        if(dest == -1) {
            dest = destFilePath.lastIndexOf("\\");
        }
        String destDir = destFilePath.substring(0, dest);
        File dir = new File(destDir);
        if(!dir.exists()) {
            dir.mkdirs();
        }
        return new File(destFilePath);
    }

    private static void printFileList() {
        try {
            System.out.println("------------------------------------------------------------------------------------");
            System.out.println("文件清单：");
            Process exec = Runtime.getRuntime().exec(String.format("cmd /c tree /f \"%s\"", outputCodeDir));
            InputStream inputStream = exec.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream,"GBK"));
            String s;
            while ((s = br.readLine()) != null) {
                System.out.println(s);
            }
            exec.waitFor();
        } catch (IOException | InterruptedException e) {
            System.out.println("文件列表输出失败");
        }
    }

}
