package com.tools.svn;


import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.*;

import java.io.File;

public class ClassChangeListGenerator {
    public static void main(String[] args) throws SVNException {
        String path = "D:\\work\\svn_repository\\AppServer"; // Ҫ�鿴��ɾ���ļ��ı��ع�������·��

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
