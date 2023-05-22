package com.tools.svn.event;

import com.tools.svn.bean.remote.SVNRevisionLog;
import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNLogEntry;

public class SVNLogResultHandler  implements ISVNLogEntryHandler {

    SVNRevisionLog svnRevisionLog;

    public SVNLogResultHandler(SVNRevisionLog svnRevisionLog) {
        this.svnRevisionLog = svnRevisionLog;
    }

    @Override
    public void handleLogEntry(SVNLogEntry svnLogEntry) {
        this.svnRevisionLog.addLogEntry(svnLogEntry.getRevision(), svnLogEntry);
    }
}
