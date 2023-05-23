package com.tools.test;

import com.jcraft.jsch.*;

import java.io.*;

public class TestLinuxCommand {
    public static void main(String[] args) throws IOException, JSchException {
        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession("cplapp", "10.110.80.132", 22);
            session.setPassword("cplapp1");
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            // 创建SSH通道
            Channel channel = session.openChannel("exec");
//            channel.connect();
            ChannelExec exec = (ChannelExec) channel;
            exec.setCommand("ls -l /a");
            exec.setErrStream(System.out);
//            exec.setOutputStream(System.out);
            exec.connect();
            exec.setCommand("ls -l /");

            exec.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(exec.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            reader.close();
            exec.disconnect();
            session.disconnect();
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }
}
