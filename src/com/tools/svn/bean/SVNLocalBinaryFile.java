package com.tools.svn.bean;

import java.util.ArrayList;
import java.util.List;

public class SVNLocalBinaryFile {
    private final List<SVNDeployFile> modifyFiles;
    private final List<SVNDeployFile> deleteFiles;

    public SVNLocalBinaryFile() {
        this.modifyFiles = new ArrayList<>();
        this.deleteFiles = new ArrayList<>();
    }

    public void addModifyFiles(SVNDeployFile file) {
        this.modifyFiles.add(file);
    }

    public void addDeleteFile(SVNDeployFile file) {
        this.deleteFiles.add(file);
    }

    public List<SVNDeployFile> getModifyFiles() {
        return modifyFiles;
    }

    public List<SVNDeployFile> getDeleteFiles() {
        return deleteFiles;
    }

    @Override
    public String toString() {
        return "SVNLocalBinaryFile{" +
                "modifyFiles=" + modifyFiles +
                ", deleteFiles=" + deleteFiles +
                '}';
    }
}
