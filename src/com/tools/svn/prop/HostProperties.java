package com.tools.svn.prop;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class HostProperties {
    public static List<String> hostNameList;
    public static List<String> hostIPList;

    public static List<String> hostUserList;

    public static List<String> hostPwdList;

    public static void init() {
        loadLocalProp();
    }

    private static void loadLocalProp() {
        Properties prop = new Properties();
        InputStream in = MSVNProperties.class.getClassLoader().getResourceAsStream("host.properties");
        try {
            String userDir = System.getProperty("user.dir");
            if(in == null) {
                File configFile = new File(userDir + "\\host.properties");
                in = Files.newInputStream(configFile.toPath());
            }
            prop.load(in);
            String hostName = prop.getProperty("host.name");
            if(hostName == null || hostName.length() == 0) {
                System.out.println("host.nameŒ¥≈‰÷√");
                System.exit(0);
            }
            String hostIP = prop.getProperty("host.ip");
            if(hostIP == null || hostIP.length() == 0) {
                System.out.println("host.ipŒ¥≈‰÷√");
                System.exit(0);
            }
            String hostUser = prop.getProperty("host.user");
            if(hostUser == null || hostUser.length() == 0) {
                System.out.println("host.userŒ¥≈‰÷√");
                System.exit(0);
            }
            String hostPwd = prop.getProperty("host.pwd");
            if(hostPwd == null || hostPwd.length() == 0) {
                System.out.println("host.pwdŒ¥≈‰÷√");
                System.exit(0);
            }
            hostName = new String(hostName.getBytes(StandardCharsets.ISO_8859_1), "GBK");
            String[] hostNames = hostName.split(",");
            String[] hostIps = hostIP.split(",");
            String[] hostUsers = hostUser.split(",");
            String[] hostPwds = hostPwd.split(",");
            int length = hostNames.length;
            if (hostIps.length < length || hostUsers.length < length || hostPwds.length < length) {
                System.out.println("host.properties–≈œ¢≥§∂»≤ª“ª÷¬");
            }
            hostNameList = Arrays.asList(hostNames);
            hostIPList = Arrays.asList(hostIps);
            hostUserList = Arrays.asList(hostUsers);
            hostPwdList = Arrays.asList(hostPwds);

        } catch (Exception e) {
            System.out.println("≈‰÷√º”‘ÿ ß∞‹:host.properties");
            System.exit(0);
        }
    }
}
