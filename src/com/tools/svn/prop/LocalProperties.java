package com.tools.svn.prop;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Properties;

public class LocalProperties {
    public static String baseDir;
    public static String compilerDir;

    public static String linuxBase;

    public static String backupDir;

    public static void init() {
        loadLocalProp();
    }

    private static void loadLocalProp() {
        Properties prop = new Properties();
        InputStream in = MSVNProperties.class.getClassLoader().getResourceAsStream("local.properties");
        try {
            String userDir = System.getProperty("user.dir");
            if(in == null) {
                File configFile = new File(userDir + "\\local.properties");
                in = Files.newInputStream(configFile.toPath());
            }
            prop.load(in);
            baseDir = prop.getProperty("baseDir");
            if(baseDir == null || baseDir.length() == 0) {
                System.out.println("baseDirδ����");
                System.exit(0);
            }
            compilerDir = prop.getProperty("classDir");
            if(compilerDir == null || compilerDir.length() == 0) {
                System.out.println("compilerDirδ����");
                System.exit(0);
            }
            linuxBase = prop.getProperty("linuxPath");
            if(linuxBase == null || linuxBase.length() == 0) {
                System.out.println("linuxBaseδ����");
                System.exit(0);
            }
            backupDir = prop.getProperty("backupDir");
            if(backupDir == null || backupDir.length() == 0) {
                System.out.println("backupDirδ����");
                System.exit(0);
            }
        } catch (Exception e) {
            System.out.println("���ü���ʧ��:local.properties");
            System.exit(0);
        }
    }
}
