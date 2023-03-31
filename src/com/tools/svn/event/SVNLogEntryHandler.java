package com.tools.svn.event;

import com.tools.svn.prop.MSVNProperties;
import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNNodeKind;

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
        // ɾ���ϴ����ɵ��ļ�
        if(counter.getAndIncrement() == 0) {
            deleteCodeDir();
        }
        // ��ȡ���µ��ļ��б�
        Set<String> pathList = generateChangeList(logEntry);
        // ���ݸ��µ��ļ��б������ļ�
        generateFile(pathList);
        synchronized(SVNLogEntryHandler.class) {
            // ����ִ�н����󣬴�ӡ�ļ��嵥
            if(counter.get() >= total) {
                printFileList();
            }
            // ����ִ�н����󣬴�ӡʧ���嵥
            if(!errorList.isEmpty() && counter.get() >= total) {
                System.out.println("�����ļ���ȡʧ�ܣ�");
                for (String errPath: errorList) {
                    System.out.println(errPath);
                }
            }
        }

    }

    /**
     * ɾ����������ļ���
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
            //�ݹ�ɾ��Ŀ¼�е���Ŀ¼��
            for (String child : children) {
                boolean success = deleteCodeDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
        }
        // Ŀ¼��ʱΪ�գ�����ɾ��
        return dir.delete();
    }

    private Set<String> generateChangeList(SVNLogEntry logEntry) {
        // ��ȡ�ļ��嵥
        Map<String, SVNLogEntryPath> changedPaths = logEntry.getChangedPaths();
        Set<String> filePathList = new HashSet<>();
        for(SVNLogEntryPath path: changedPaths.values()) {
            char type = path.getType();
            // �����ɾ�����ļ���������������
            if(type == SVNLogEntryPath.TYPE_DELETED) {
                continue;
            }
            // ������ļ��У��򲻴���
            if(SVNNodeKind.DIR.equals(path.getKind())) {
                continue;
            }
            String filePath = path.getPath();
            // ����java�ļ��������ļ���jar�ļ�
            if(filePath.startsWith(srcBaseDir)) {
                filePath = filePath.replace(srcBaseDir, "");
            } else if(filePath.startsWith(confBaseDir)) {
                filePath = filePath.replace(confBaseDir, "");
            } else if(filePath.startsWith(libBaseDir)) {
                filePath = filePath.replace(libBaseDir, "");
            }

            // �����.java�ļ������׺����Ϊ.class�ļ������ڲ��ұ���·����class�ļ�
            filePath = filePath.replace(".java", ".class");
            filePathList.add(filePath);
            System.out.println("׼���ļ���" + filePath);
        }
        return filePathList;
    }

    private void generateFile(Set<String> pathList) {
        for(String path: pathList) {
            // ���ұ�����class�ļ��������ļ���jar�ļ�
            File sourceFile = getSourceFile(path);
            if(sourceFile == null) {
                // �ļ�û�ҵ�����ӵ������б�
                synchronized (SVNLogEntryHandler.class) {
                    errorList.add(path);
                }
                continue;
            }
            try {
                // ��ȡĿ���ļ� outputDir + �ļ�·�� + �ļ���
                File destFile = getDestFile(path);
                // ����ļ��Ѿ����ڣ�ֱ������������Ҫ��������
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
     * ���ұ���Ŀ¼�µ�Դ�ļ�
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
     * ��ȡĿ��·��
     */
    private File getDestFile(String filePath) throws IOException {
        if(!outputCodeDir.endsWith("\\")) {
            outputCodeDir = outputCodeDir + "\\";
        }
        String destFilePath = outputCodeDir + filePath;
        System.out.println("���������ļ���" + destFilePath);
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
            System.out.println("�ļ��嵥��");
            Process exec = Runtime.getRuntime().exec(String.format("cmd /c tree /f \"%s\"", outputCodeDir));
            InputStream inputStream = exec.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream,"GBK"));
            String s;
            while ((s = br.readLine()) != null) {
                System.out.println(s);
            }
            exec.waitFor();
        } catch (IOException | InterruptedException e) {
            System.out.println("�ļ��б����ʧ��");
        }
    }

}
