package com.tools.svn.remote.file;

import com.tools.svn.bean.SVNLocalFile;
import com.tools.svn.bean.remote.SVNRevisionLog;
import com.tools.svn.remote.log.SVNLogGenerator;
import org.tmatesoft.svn.core.SVNLogEntry;

import java.util.ArrayList;
import java.util.List;

public class SVNRemoteFileGenerator {


    public List<SVNLocalFile> list() {
        SVNRevisionLog svnRevisionLog = SVNLogGenerator.fetch();
        List<SVNLocalFile> localFiles = new ArrayList<>();
        List<SVNLogEntry> logEntities = svnRevisionLog.getLogEntities();
        for (SVNLogEntry logEntry: logEntities) {

        }
        return localFiles;
    }
}
