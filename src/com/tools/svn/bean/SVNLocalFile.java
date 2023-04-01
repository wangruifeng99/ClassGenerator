package com.tools.svn.bean;

import org.tmatesoft.svn.core.wc.SVNStatusType;

public class SVNLocalFile {
    String absFileName;
    SVNStatusType status;

    public String getAbsFileName() {
        return absFileName;
    }

    public void setAbsFileName(String absFileName) {
        this.absFileName = absFileName;
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
                ", status=" + status +
                '}';
    }
}
