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
            exec.setOutputStream(System.out);
            exec.setErrStream(System.out);
            exec.setCommand("mv /export/home/cplapp/com/stock/businesslogic/reckoning/optimization/test/Test.class.bak /export/home/cplapp/com/stock/businesslogic/reckoning/optimization/test/Test.class");
            OutputStream outputStream = channel.getOutputStream();
            System.out.println(outputStream);
            BufferedReader reader = new BufferedReader(new InputStreamReader(channel.getInputStream()));
            channel.connect();
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            reader.close();
            channel.disconnect();
            session.disconnect();
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }
}
