package com.backend.excelqrcode.controller;

import com.backend.excelqrcode.service.QRCodeService;
import com.backend.excelqrcode.service.ZipService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.*;
import java.util.Iterator;

@Controller
public class ReadExcelController {

    private static Logger LOGGER = LoggerFactory.getLogger(ReadExcelController.class);

    private static String OS = System.getProperty("os.name").toLowerCase();

    @Autowired
    private QRCodeService qrCodeService;

    @Autowired
    private ZipService zipService;

    private String OUTPUT_ZIP_FILE;
    private String SOURCE_FOLDER;


    @GetMapping("/readExcel")
    public ResponseEntity<String> readExcel(){
        if (isWindows()) {
            OUTPUT_ZIP_FILE = "C:\\Users\\aphisit\\Desktop\\qrzip.zip";
            SOURCE_FOLDER = "C:\\Users\\aphisit\\Desktop\\qrzip"; // SourceFolder path
        } else {
            OUTPUT_ZIP_FILE = "/mnt/c/Users/aphisit/Desktop/qrzip.zip";
            SOURCE_FOLDER = "/mnt/c/Users/aphisit/Desktop/qrzip"; // SourceFolder path
        }

        HttpHeaders headers = new HttpHeaders();
//        headers.add("Content-Type","image/png");
        FileInputStream fileInputStream;
        FileOutputStream fileOutputStream;
        XSSFWorkbook workbook;
        XSSFSheet sheet;
        String filename;

        int numberRow = 0;

        try {
            if (isWindows()) {
                fileInputStream = new FileInputStream(new File("C:\\Users\\aphisit\\Desktop\\FLListforGenerateQRCode_10M1.xlsx"));
                filename = new File("C:\\Users\\aphisit\\Desktop\\FLListforGenerateQRCode_10M1.xlsx").getName();
            }else{
                fileInputStream = new FileInputStream(new File("/mnt/c/Users/aphisit/Desktop/FLListforGenerateQRCode_10M1.xlsx"));
                filename = new File("/mnt/c/Users/aphisit/Desktop/FLListforGenerateQRCode_10M1.xlsx").getName();
            }

            LOGGER.info("fileName : {}" ,new File("/mnt/c/Users/aphisit/Desktop/FLListforGenerateQRCode_10M1.xlsx").getName());

            workbook = new XSSFWorkbook(fileInputStream);

            sheet = workbook.getSheetAt(0);

            Iterator<Row> iterator = sheet.iterator();
            String dataTemp;

            new File(SOURCE_FOLDER).mkdir();

            while (iterator.hasNext()){
                dataTemp = "";
                Row currentRow = iterator.next();
                for(Cell cell : currentRow){
                    if(cell.getCellTypeEnum() == CellType.STRING){
                        dataTemp += "-" + cell.getRichStringCellValue().toString();
                    }else if(cell.getCellTypeEnum() == CellType.NUMERIC){
                        dataTemp += "-" + cell.getNumericCellValue();
                    }
                }

                dataTemp = dataTemp.substring(1);
                dataTemp = new String(dataTemp.getBytes("UTF-8"),"UTF-8");

                byte[] qrcode = qrCodeService.generateQRCode(dataTemp,512,512);

                if(isWindows()){
                    fileOutputStream = new FileOutputStream(SOURCE_FOLDER + "\\" + (currentRow.getRowNum() + 1) + ".png");
                }else{
                    fileOutputStream = new FileOutputStream(SOURCE_FOLDER + "/" + (currentRow.getRowNum() + 1) + ".png");
                }


                fileOutputStream.write(qrcode);
//                fileOutputStream = new FileOutputStream("C:\\Users\\aphisit\\Desktop\\qrzip\\" + (currentRow.getRowNum() + 1) + ".txt");
//                dataTemp = "ทดสอบภาษาไทย - " + dataTemp;
//                fileOutputStream.write(dataTemp.getBytes());

                fileOutputStream.close();

                LOGGER.info("Create file {} successfully",(currentRow.getRowNum() + 1));
            }


            fileInputStream.close();

            zipService.generateFileZip(SOURCE_FOLDER,OUTPUT_ZIP_FILE,SOURCE_FOLDER);

            FileUtils.cleanDirectory(new File(SOURCE_FOLDER));
            FileUtils.deleteDirectory(new File(SOURCE_FOLDER));

            return new ResponseEntity<String>("getPhysicalNumberOfRows : " + sheet.getPhysicalNumberOfRows(),headers, HttpStatus.OK);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<String>(e.getMessage(),headers, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<String>(e.getMessage(),headers, HttpStatus.OK);
        }
    }

    public static boolean isWindows() {
        return (OS.indexOf("win") >= 0);
    }

    public static boolean isMac() {
        return (OS.indexOf("mac") >= 0);
    }

    public static boolean isUnix() {
        return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 );
    }

    public static boolean isSolaris() {
        return (OS.indexOf("sunos") >= 0);
    }
}
