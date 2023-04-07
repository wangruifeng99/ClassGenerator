package com.tools.svn.bean;

public class SVNDeployFile {
    private String localFile;
    private String remoteFile;
    private String backupFile;

    public SVNDeployFile(String localFile, String remoteFile, String backupFile) {
        this.localFile = localFile;
        this.remoteFile = remoteFile;
        this.backupFile = backupFile;
    }

    public String getLocalFile() {
        return localFile;
    }

    public void setLocalFile(String localFile) {
        this.localFile = localFile;
    }

    public String getRemoteFile() {
        return remoteFile;
    }

    public void setRemoteFile(String remoteFile) {
        this.remoteFile = remoteFile;
    }

    public String getBackupFile() {
        return backupFile;
    }

    public void setBackupFile(String backupFile) {
        this.backupFile = backupFile;
    }

    @Override
    public String toString() {
        return "SVNDeployFile{" +
                "localFile='" + localFile + '\'' +
                ", remoteFile='" + remoteFile + '\'' +
                ", backupFile='" + backupFile + '\'' +
                '}';
    }
}
