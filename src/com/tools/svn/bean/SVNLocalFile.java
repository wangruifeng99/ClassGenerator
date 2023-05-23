package com.tools.svn.bean;

import org.tmatesoft.svn.core.wc.SVNStatusType;

public class SVNLocalFile {
    String absFileName;

    long lastModifyTime;

    SVNStatusType status;

    public SVNLocalFile() {
    }

    public SVNLocalFile(String absFileName, SVNStatusType status) {
        this.absFileName = absFileName;
        this.status = status;
    }

    public String getAbsFileName() {
        return absFileName;
    }

    public void setAbsFileName(String absFileName) {
        this.absFileName = absFileName;
    }

    public long getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(long lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public SVNStatusType getStatus() {
        return status;
    }

    public void setStatus(SVNStatusType status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "SVNLocalFile{" +
                "absFileName='" + absFileName + '\'' +
                ", lastModifyTime='" + lastModifyTime + '\'' +
                ", status=" + status +
                '}';
    }
}
