package com.tools.svn.prop;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Properties;

public class LocalProperties {
    public static String baseDir;
    public static String compilerDir;


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
        } catch (Exception e) {
            System.out.println("���ü���ʧ��:local.properties");
            System.exit(0);
        }
    }
}
