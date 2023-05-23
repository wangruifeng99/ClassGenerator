package com.tools.svn;

import com.tools.svn.bean.SVNLocalFile;
import com.tools.svn.bean.ServerHost;
import com.tools.svn.bean.remote.SVNRevisionLog;
import com.tools.svn.event.SVNLogEntryHandler;
import com.tools.svn.event.SVNLogResultHandler;
import com.tools.svn.local.file.SVNLocalFileGenerator;
import com.tools.svn.local.file.SVNLocalModifiedFileGenerator;
import com.tools.svn.local.host.HostInfoGenerator;
import com.tools.svn.local.ui.SVNLocalFileUI;
import com.tools.svn.prop.LocalProperties;
import com.tools.svn.prop.MSVNProperties;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.wc.*;

import java.util.*;

public class ClassGenerator {

    public static void main( String[] args ) throws SVNException {
        genLocal();
    }

    public static void genRemote() throws SVNException {
        MSVNProperties.init();
        SVNLogEntryHandler.init();
        FSRepositoryFactory.setup();

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
        SVNLogEntryHandler.total = revisionList.size();
        SVNLogClient logClient = clientManager.getLogClient();
        SVNRevisionLog revisionLog = new SVNRevisionLog();
        logClient.doLog(repositoryURL, null, null, revisionList,
                false, true,  false,
                0, null,  new SVNLogResultHandler(revisionLog));
//        System.out.println(revisionLog);
        List<SVNLogEntry> logEntities = revisionLog.getLogEntities();
        for (SVNLogEntry logEntry: logEntities) {
            Map<String, SVNLogEntryPath> changedPaths = logEntry.getChangedPaths();
            Set<Map.Entry<String, SVNLogEntryPath>> entries = changedPaths.entrySet();
            for (Map.Entry<String, SVNLogEntryPath> logPathEntry: entries) {
                SVNLogEntryPath entryPath = logPathEntry.getValue();
                char type = entryPath.getType();
                System.out.println(entryPath.getPath() + "   " + type);

            }
        }

    }

    public static void genLocal() {
        LocalProperties.init();
        SVNLocalFileGenerator generator = new SVNLocalModifiedFileGenerator(LocalProperties.baseDir);
        // 向表格中添加数据行
        List<SVNLocalFile> files = generator.list();
        files.sort((file1, file2) -> Long.compare(file2.getLastModifyTime(), file1.getLastModifyTime()));
        List<ServerHost> hosts = new HostInfoGenerator().list();
        new SVNLocalFileUI(files, hosts).display();
    }

}
