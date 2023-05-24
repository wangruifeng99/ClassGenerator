package com.tools.svn.bean;

public class ServerHost {
    private String name;
    private String ip;
    private String user;
    private String password;
    private String deployBaseDir;
    private String deployBackupDir;

    public ServerHost(String name, String ip, String user, String password, String deployBaseDir, String deployBackupDir) {
        this.name = name;
        this.ip = ip;
        this.user = user;
        this.password = password;
        this.deployBaseDir = deployBaseDir;
        this.deployBackupDir = deployBackupDir;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDeployBaseDir() {
        return deployBaseDir;
    }

    public void setDeployBaseDir(String deployBaseDir) {
        this.deployBaseDir = deployBaseDir;
    }

    public String getDeployBackupDir() {
        return deployBackupDir;
    }

    public void setDeployBackupDir(String deployBackupDir) {
        this.deployBackupDir = deployBackupDir;
    }

    @Override
    public String toString() {
        return "ServerHost{" +
                "name='" + name + '\'' +
                ", ip='" + ip + '\'' +
                ", user='" + user + '\'' +
                ", password='" + "******" + '\'' +
                ", deployBaseDir='" + deployBaseDir + '\'' +
                ", deployBackupDir='" + deployBackupDir + '\'' +
                '}';
    }
}
