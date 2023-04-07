package com.tools.test;

import com.tools.svn.bean.SVNLocalFile;
import com.tools.svn.bean.ServerHost;
import com.tools.svn.local.file.SVNLocalFileGenerator;
import com.tools.svn.local.file.SVNLocalModifiedFileGenerator;
import com.tools.svn.local.ui.SVNLocalFileUI;

import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        SVNLocalFileGenerator generator = new SVNLocalModifiedFileGenerator();
        // ���������������
        List<SVNLocalFile> files = generator.list();
        List<ServerHost> hosts = getHosts();
        new SVNLocalFileUI(files, hosts).display();

    }

    public static List<ServerHost> getHosts() {
        List<ServerHost> hosts = new ArrayList<>();
        hosts.add(new ServerHost("ȫ���APP1", "10.95.128.61", "testapp", "testapp1"));
        hosts.add(new ServerHost("ȫ���APP2", "10.95.128.62", "testapp", "testapp1"));
        hosts.add(new ServerHost("ȫ���ر�", "10.95.128.63", "testapp", "testapp1"));
        hosts.add(new ServerHost("ȫ���Algo", "10.95.128.64", "testapp", "testapp1"));
        hosts.add(new ServerHost("ȫ���Ϲ�", "10.95.128.65", "testapp", "testapp1"));
        return hosts;
    }
}
