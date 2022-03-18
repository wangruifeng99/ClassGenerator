package com.tools.svn.prop;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class MSVNProperties {
    public static String repository;
    public static String srcBaseDir = "/branches/dev210118/src/";
    public static String confBaseDir = "/branches/dev210118/conf/";
    public static String libBaseDir = "/branches/dev210118/lib/";
    public static String compilerDir;
    public static String outputDir;
    public static String username;
    public static String password;
    public static String outputCodeDir;
    public static Long[] revisions;

    public static void init() {
        loadSvnProp();
        loadRevisions();
    }

    private static void loadSvnProp() {
        Properties prop = new Properties();
        InputStream in = MSVNProperties.class.getClassLoader().getResourceAsStream("svn.properties");
        try {
            String userDir = System.getProperty("user.dir");
            if(in == null) {
                File configFile = new File(userDir + "\\svn.properties");
                in = new FileInputStream(configFile);
            }
            prop.load(in);
            repository = prop.getProperty("repository");
            if(repository == null || repository.length() == 0) {
                System.out.println("repositoryŒ¥≈‰÷√");
                System.exit(0);
            }
            String branchBase = "/branches" + repository.substring(repository.lastIndexOf("/"));
            srcBaseDir = prop.getProperty("srcBaseDir");
            if(srcBaseDir == null || srcBaseDir.length() == 0) {
                srcBaseDir = branchBase + "/src/";
            }
            confBaseDir = prop.getProperty("confBaseDir");
            if(confBaseDir == null || confBaseDir.length() == 0) {
                confBaseDir = branchBase + "/conf/";
            }
            libBaseDir = prop.getProperty("libBaseDir");
            if(libBaseDir == null || libBaseDir.length() == 0) {
                libBaseDir = branchBase + "/lib/";
            }
            compilerDir = prop.getProperty("classDir");
            outputDir = prop.getProperty("outputDir");
            if(outputDir == null || outputDir.length() == 0) {
                outputCodeDir = userDir + "\\code";
            } else {
                outputCodeDir = outputDir + "\\code";
            }
        } catch (Exception e) {
            System.out.println("≈‰÷√º”‘ÿ ß∞‹:svn.properties");
            System.exit(0);
        }
    }

    private static void loadRevisions() {
        InputStream in = MSVNProperties.class.getClassLoader().getResourceAsStream("revisions");
        if(in == null) {
            String userDir = System.getProperty("user.dir");
            File configFile = new File(userDir + "\\revisions");
            try {
                in = new FileInputStream(configFile);
            } catch (FileNotFoundException e) {
                System.out.println("≈‰÷√º”‘ÿ ß∞‹£∫revisions");
                System.exit(0);
            }
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();
        try {
            while(true) {
                String line = br.readLine();
                if(line == null) {
                    break;
                }
                sb.append(line);
            }
        } catch (Exception e) {
            System.out.println("º”‘ÿrevisions ß∞‹");
            e.printStackTrace();
            System.exit(0);
        }
        if(sb.length() == 0) {
            System.out.println("revisionsŒ¥÷∏∂®");
            System.exit(0);
        }
        String rawRevisions = sb.toString();
        String[] rawRevisionArr = rawRevisions.split("[,£¨\\s*]");
        List<Long> realRevisions = new ArrayList<>();
        for (String s : rawRevisionArr) {
            String realRevision = s.trim();
            if (realRevision.length() == 0) {
                continue;
            }
            realRevisions.add(Long.parseLong(realRevision));
        }
        revisions = realRevisions.toArray(new Long[0]);
    }
}
