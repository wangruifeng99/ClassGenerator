package com.tools.svn;


import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.*;

import java.io.File;

public class ClassChangeListGenerator {
    public static void main(String[] args) throws SVNException {
        String path = "D:\\work\\svn_repository\\AppServer"; // 要查看已删除文件的本地工作副本路径

        SVNClientManager clientManager = SVNClientManager.newInstance();
        SVNStatusClient statusClient = clientManager.getStatusClient();

        try {
            statusClient.doStatus(new File(path), SVNRevision.WORKING, SVNDepth.INFINITY, false, true, false, false, new ISVNStatusHandler() {
                public void handleStatus(SVNStatus status) throws SVNException {
                    System.out.println(status.getFile().getAbsolutePath() + "-" + status.getNodeStatus());
                }
            }, null);
        } catch (SVNException e) {
            e.printStackTrace();
        }
    }
}
