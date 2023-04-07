package com.tools.svn.bean;

public class ServerHost {
    private String name;
    private String ip;
    private String user;
    private String password;

    public ServerHost(String name, String ip, String user, String password) {
        this.name = name;
        this.ip = ip;
        this.user = user;
        this.password = password;
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

    @Override
    public String toString() {
        return "ServerHost{" +
                "name='" + name + '\'' +
                ", ip='" + ip + '\'' +
                ", user='" + user + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
