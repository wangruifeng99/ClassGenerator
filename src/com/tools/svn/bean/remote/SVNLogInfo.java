package com.tools.svn.bean.remote;

import com.tools.svn.bean.SVNLocalFile;

import java.util.List;

public class SVNLogInfo {
    long revision;
    String[] actions;
    String author;
    String date;
    String message;
    List<SVNLocalFile> svnLocalFiles;

    public SVNLogInfo(long revision, String[] actions, String author, String date, String message, List<SVNLocalFile> svnLocalFiles) {
        this.revision = revision;
        this.actions = actions;
        this.author = author;
        this.date = date;
        this.message = message;
        this.svnLocalFiles = svnLocalFiles;
    }

    public long getRevision() {
        return revision;
    }

    public void setRevision(long revision) {
        this.revision = revision;
    }

    public String[] getActions() {
        return actions;
    }

    public void setActions(String[] actions) {
        this.actions = actions;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<SVNLocalFile> getSvnLocalFiles() {
        return svnLocalFiles;
    }

    public void setSvnLocalFiles(List<SVNLocalFile> svnLocalFiles) {
        this.svnLocalFiles = svnLocalFiles;
    }
}
