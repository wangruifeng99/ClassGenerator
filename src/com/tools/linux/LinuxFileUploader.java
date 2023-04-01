package com.tools.linux;

import com.tools.svn.prop.MSVNProperties;
import com.tools.uploader.FileUploader;

public class LinuxFileUploader implements FileUploader {
    @Override
    public void upload() {
        System.out.println("aaa");
        System.out.println("Ba");
        MSVNProperties.init();

    }
}
