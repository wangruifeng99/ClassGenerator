package com.tools.svn.remote.log;

import com.tools.svn.bean.remote.SVNRevisionLog;
import com.tools.svn.event.SVNLogEntryHandler;
import com.tools.svn.event.SVNLogResultHandler;
import com.tools.svn.prop.MSVNProperties;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.wc.*;

import java.util.ArrayList;
import java.util.Collection;

public class SVNLogGenerator {

    static {
        MSVNProperties.init();
        SVNLogEntryHandler.init();
        FSRepositoryFactory.setup();
    }

    public static SVNRevisionLog fetch() {
        SVNURL repositoryURL = null;
        try {
            repositoryURL = SVNURL.parseURIEncoded(MSVNProperties.repository);
        } catch (SVNException e) {
            e.printStackTrace();
            System.exit(0);
        }
        DefaultSVNOptions options = SVNWCUtil.createDefaultOptions(true);
        SVNClientManager clientManager = SVNClientManager.newInstance(options, MSVNProperties.username, MSVNProperties.password);
        Collection<SVNRevisionRange> revisionList = new ArrayList<>();
        Long[] revisions = MSVNProperties.revisions;
        for(Long revision: revisions) {
            if(revision == null || revision == 0) {
                continue;
            }
            SVNRevision startRevision = SVNRevision.create(revision);
            SVNRevision endRevision = SVNRevision.create(revision);
            SVNRevisionRange revisionRange = new SVNRevisionRange(startRevision, endRevision);
            revisionList.add(revisionRange);
        }
        SVNLogClient logClient = clientManager.getLogClient();
        SVNRevisionLog revisionLog = new SVNRevisionLog();
        try {
            logClient.doLog(repositoryURL, null, null, revisionList,
                    false, true,  false,
                    0, null,  new SVNLogResultHandler(revisionLog));
        } catch (SVNException e) {
            throw new RuntimeException(e);
        }
        return revisionLog;
    }
}
