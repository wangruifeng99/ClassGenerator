package com.tools.svn.bean;

import org.tmatesoft.svn.core.wc.SVNStatusType;

import java.util.Objects;

public class SVNLocalFile {
    String absFileName;

    long lastModifyTime;

    long committedTime;

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

    public long getCommittedTime() {
        return committedTime;
    }

    public void setCommittedTime(long committedTime) {
        this.committedTime = committedTime;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SVNLocalFile localFile = (SVNLocalFile) o;
        return absFileName.equals(localFile.absFileName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(absFileName);
    }
}
