package com.tools.svn.local.file;

import org.tmatesoft.svn.core.wc.SVNStatusType;

import java.util.ArrayList;
import java.util.List;

public class SVNLocalModifiedFileGenerator extends SVNLocalFileGenerator{

    public SVNLocalModifiedFileGenerator(String localPath) {
        super(localPath);
        List<SVNStatusType> list = new ArrayList<>();
        list.add(SVNStatusType.STATUS_MODIFIED);
        list.add(SVNStatusType.STATUS_UNVERSIONED);
        list.add(SVNStatusType.STATUS_DELETED);
        list.add(SVNStatusType.STATUS_MISSING);
        list.add(SVNStatusType.STATUS_ADDED);
        setStatusTypeList(list);
    }
}
