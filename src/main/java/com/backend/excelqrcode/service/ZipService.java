package com.backend.excelqrcode.service;

import java.io.File;

public interface ZipService {

    public void generateFileZip(String node,String output,String source_folder);
    public void generateFileList(File node);
    public String generateZipEntry(String file);
    public void zipFile();
}
