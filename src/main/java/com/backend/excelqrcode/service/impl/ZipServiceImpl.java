package com.backend.excelqrcode.service.impl;

import com.backend.excelqrcode.service.ZipService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service("ZipService")
public class ZipServiceImpl implements ZipService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZipServiceImpl.class);

    private List<String> fileList;
    private String OUTPUT_ZIP_FILE;
    private String SOURCE_FOLDER; //= "C:\\Users\\aphisit\\Desktop\\qrzip"; // SourceFolder path

    public void generateFileZip(String node,String output,String source_folder){
        fileList = new ArrayList<>();
        OUTPUT_ZIP_FILE = output;
        SOURCE_FOLDER = source_folder;

        generateFileList(new File(node));
        zipFile();
    }

    @Override
    public void generateFileList(File node) {
        if(node.isFile()){
            fileList.add(generateZipEntry(node.toString()));
        }

        if(node.isDirectory()){
            String[] subNote = node.list();
            for(String fileName : subNote){
                generateFileList(new File(node, fileName));
            }
        }
    }

    @Override
    public String generateZipEntry(String file) {
        return file.substring(SOURCE_FOLDER.length() + 1, file.length());
    }

    @Override
    public void zipFile() {
        byte[] buffer = new byte[1024];
        String source = new File(SOURCE_FOLDER).getName();
        FileOutputStream fileOutputStream = null;
        ZipOutputStream zipOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(OUTPUT_ZIP_FILE);
            zipOutputStream = new ZipOutputStream(fileOutputStream);

            FileInputStream fileInputStream = null;

            for(String file : this.fileList){
                LOGGER.info("File added : {}",file);
                ZipEntry zipEntry = new ZipEntry(source + File.separator + file);
                zipOutputStream.putNextEntry(zipEntry);
                try {
                    fileInputStream = new FileInputStream(SOURCE_FOLDER + File.separator + file);
                    int len;
                    while((len = fileInputStream .read(buffer)) > 0){
                        zipOutputStream.write(buffer,0,len);
                    }
                }finally {
                    fileInputStream.close();
                }
            }

            zipOutputStream.closeEntry();
            LOGGER.info("Folder successfully compressed");

        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                zipOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
