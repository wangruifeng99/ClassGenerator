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
import com.tools.svn.security.RSAUtils;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.wc.*;

import java.io.File;
import java.util.*;

public class ClassGenerator {

    public static void main( String[] args ) throws Exception {
        try {
            if (args.length == 0 || "local".equals(args[0])) {
                genLocal();
            }
            if ("remote".equals(args[0])) {
                genRemote();
            }
            if ("sign".equals(args[0])) {
                if (args.length < 2) {
                    System.out.println("请输入要加密的文本，多个文本使用空格分隔");
                    return;
                }
                for (int i = 1; i < args.length; i ++) {
                    String encrypt = RSAUtils.encrypt(args[i]);
                    System.out.println(args[i] + "   " + encrypt);
                }

            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public static void genRemote() throws Exception {
        MSVNProperties.init();
        LocalProperties.init();
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
        SVNClientManager clientManager = SVNClientManager.newInstance(options, MSVNProperties.username, RSAUtils.decrypt(MSVNProperties.password));
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
        List<SVNLocalFile> svnLocalFileList = new ArrayList<>();
//        System.out.println(revisionLog);
        List<SVNLogEntry> logEntities = revisionLog.getLogEntities();

        long modifyTime;
        for (SVNLogEntry logEntry: logEntities) {
            long committedTime = logEntry.getDate().getTime();
            modifyTime = logEntry.getDate().getTime();
            Map<String, SVNLogEntryPath> changedPaths = logEntry.getChangedPaths();
            Set<Map.Entry<String, SVNLogEntryPath>> entries = changedPaths.entrySet();
            for (Map.Entry<String, SVNLogEntryPath> logPathEntry: entries) {
                SVNLogEntryPath entryPath = logPathEntry.getValue();
                if (entryPath.getKind().equals(SVNNodeKind.DIR)) {
                    continue;
                }
                SVNLocalFile localFile = new SVNLocalFile();
                char type = entryPath.getType();
                if (type == SVNLogEntryPath.TYPE_DELETED) {
                    localFile.setStatus(SVNStatusType.STATUS_DELETED);
                } else {
                    localFile.setStatus(SVNStatusType.STATUS_MODIFIED);
                }
                String absFileName = entryPath.getPath();
                int index = MSVNProperties.repository.indexOf("/branches");
                String branch = MSVNProperties.repository.substring(index);
                localFile.setAbsFileName(absFileName.replace(branch, LocalProperties.baseDir).replace("/", "\\"));
                File file = new File(localFile.getAbsFileName());
                if (file.exists()) {
                    modifyTime = file.lastModified();
                }
                localFile.setLastModifyTime(modifyTime);
                localFile.setCommittedTime(committedTime);
                if (svnLocalFileList.contains(localFile)) {
                    int i = svnLocalFileList.indexOf(localFile);
                    svnLocalFileList.remove(localFile);
                    svnLocalFileList.add(i, localFile);
                } else {
                    svnLocalFileList.add(localFile);
                }
            }
        }
        svnLocalFileList.sort((file1, file2) -> Long.compare(file2.getLastModifyTime(), file1.getLastModifyTime()));
        List<ServerHost> hosts = new HostInfoGenerator().list();
        new SVNLocalFileUI(svnLocalFileList, hosts, true).display();
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
