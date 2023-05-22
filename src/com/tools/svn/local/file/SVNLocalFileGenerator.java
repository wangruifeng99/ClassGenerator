package com.tools.svn.local.file;


import com.tools.svn.bean.SVNLocalFile;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNStatusClient;
import org.tmatesoft.svn.core.wc.SVNStatusType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SVNLocalFileGenerator {
    protected List<SVNStatusType> statusTypeList;

    protected String path;

    public SVNLocalFileGenerator(String localPath) {
        this.path = localPath;
    }

    public void setStatusTypeList(List<SVNStatusType> statusTypeList) {
        this.statusTypeList = statusTypeList;
    }

    public List<SVNLocalFile> list() {
        List<SVNLocalFile> list = new ArrayList<>();
        SVNClientManager clientManager = SVNClientManager.newInstance();
        SVNStatusClient statusClient = clientManager.getStatusClient();
        try {
            statusClient.doStatus(new File(path), SVNRevision.WORKING, SVNDepth.INFINITY,
                    false, true, false, false, status -> {
                SVNStatusType nodeStatus = status.getNodeStatus();
                if (this.statusTypeList == null || this.statusTypeList.contains(nodeStatus)) {
                    SVNLocalFile localFile = new SVNLocalFile();
                    String absFileName = status.getFile().getAbsolutePath();
                    localFile.setAbsFileName(absFileName);
                    localFile.setStatus(nodeStatus);
                    list.add(localFile);
                }
            }, null);
        } catch (SVNException e) {
            e.printStackTrace();
        }
        return list;
    }
}
