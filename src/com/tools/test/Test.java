package com.tools.test;

import com.jcraft.jsch.*;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLOutput;
import com.tools.svn.bean.SVNLocalFile;
import com.tools.svn.bean.ServerHost;
import com.tools.svn.local.file.SVNLocalFileGenerator;
import com.tools.svn.local.file.SVNLocalModifiedFileGenerator;
import com.tools.svn.local.ui.SVNLocalFileUI;
import com.tools.svn.security.RSAUtils;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class Test {
    public static void main(String[] args) throws IOException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, JSchException, InterruptedException {
        SVNLocalFileGenerator generator = new SVNLocalModifiedFileGenerator("D:\\work\\svn_repository\\AppServer");
        // 向表格中添加数据行
        List<SVNLocalFile> files = generator.list();
        List<ServerHost> hosts = getHosts();
        new SVNLocalFileUI(files, hosts).display();
//        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
//        keyGenerator.init(16);
//        SecretKey secretKey = keyGenerator.generateKey();
//        System.out.println(secretKey.getEncoded());
//        String testuser = RSAUtils.encrypt("testuser1");
//        System.out.println(testuser);
//        String decrypt = RSAUtils.decrypt(testuser);
//        System.out.println(decrypt);
//        JSch jsch = new JSch();
//        Session session = jsch.getSession("cplapp", "10.110.80.132", 22);
//        session.setPassword("cplapp1");
//        session.setConfig("StrictHostKeyChecking", "no");
//        session.connect();
//        // 创建SSH通道
//        Channel channel = session.openChannel("exec");
//        channel.connect();
//        ChannelExec exec = (ChannelExec) channel;
//        exec.setOutputStream(System.out);
//        exec.setErrStream(System.out);
//        exec.setCommand("ls -l /");
//        InputStream inputStream = channel.getInputStream();
//        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//        channel.connect();
//        String line;
//        while ((line = reader.readLine()) != null) {
//            System.out.println(line);
//        }
//        reader.close();
//        channel.disconnect();
//        session.disconnect();


    }

    public static List<ServerHost> getHosts() {
        List<ServerHost> hosts = new ArrayList<>();

        return hosts;
    }
}
