package com.tools.svn.bean.remote;

import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.internal.wc.admin.SVNLog;

import java.util.*;

public class SVNRevisionLog {

    Map<Long, SVNLogEntry> revisionLog;

    public SVNRevisionLog() {
        this.revisionLog = new HashMap<>();
    }

    public SVNLogEntry getLogEntry(Long revision) {
        return revisionLog.get(revision);
    }

    public void addLogEntry(Long revision, SVNLogEntry logEntry) {
        revisionLog.put(revision, logEntry);
    }

    public Set<Long> getRevisions() {
        return revisionLog.keySet();
    }

    public List<SVNLogEntry> getLogEntities() {
        List<SVNLogEntry> svnLogEntries = new ArrayList<>();
        List<Long> revisions = new ArrayList<>(getRevisions());
        Collections.sort(revisions);
        for (long revision: revisions) {
            svnLogEntries.add(getLogEntry(revision));
        }
        return svnLogEntries;
    }

    @Override
    public String toString() {
        return "SVNRevisionLog{" +
                "revisionLog=" + revisionLog +
                '}';
    }
}
