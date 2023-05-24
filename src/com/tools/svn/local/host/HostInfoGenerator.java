package com.tools.svn.local.host;

import com.tools.svn.bean.ServerHost;
import com.tools.svn.prop.HostProperties;
import com.tools.svn.security.RSAUtils;

import java.util.ArrayList;
import java.util.List;

public class HostInfoGenerator {

    public HostInfoGenerator() {
    }

    public List<ServerHost> list() {
        HostProperties.init();
        List<String> hostNameList = HostProperties.hostNameList;
        List<String> hostIPList = HostProperties.hostIPList;
        List<String> hostUserList = HostProperties.hostUserList;
        List<String> hostPwdList = HostProperties.hostPwdList;
        List<String> deployBaseDirList = HostProperties.hostDeployBaseDirList;
        List<String> deployBackupDirList = HostProperties.hostDeployBackupDirList;
        List<ServerHost> serverHostList = new ArrayList<>();
        for (int i = 0; i < hostNameList.size(); i ++) {
            String hostName = hostNameList.get(i);
            String hostIp = hostIPList.get(i);
            String hostUser = hostUserList.get(i);
            String hostPwd = hostPwdList.get(i);
            String deployBaseDir = deployBaseDirList.get(i);
            deployBaseDir = deployBaseDir.replace("${hostUser}", hostUser);
            String deployBackupDir = deployBackupDirList.get(i);
            deployBackupDir = deployBackupDir.replace("${hostUser}", hostUser);
            try {
                hostPwd = RSAUtils.decrypt(hostPwd);
            } catch (Exception e) {
                e.printStackTrace();
            }
            serverHostList.add(new ServerHost(hostName, hostIp, hostUser, hostPwd, deployBaseDir, deployBackupDir));
        }
        return serverHostList;
    }
}
