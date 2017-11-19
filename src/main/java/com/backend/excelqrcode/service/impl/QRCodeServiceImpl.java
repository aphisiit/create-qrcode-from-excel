package com.backend.excelqrcode.service.impl;

import com.backend.excelqrcode.service.QRCodeService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

@Service
public class QRCodeServiceImpl implements QRCodeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(QRCodeServiceImpl.class);

    @Override
    public byte[] generateQRCode(String text, int width, int height) {
        Assert.hasText(text,"Text Must not empty");
        Assert.isTrue(width > 0, "width must greater than zero");
        Assert.isTrue(height > 0, "height must greater than zero");

        LOGGER.info("Will generate image text=[{}],width=[{}],height=[{}]",text,width,height);


        ByteArrayOutputStream baos;

        Map<EncodeHintType,Object> hints = null;

        try {

            hints = new EnumMap<EncodeHintType,Object>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            baos = new ByteArrayOutputStream();



//            BitMatrix matrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE,width,height);
            BitMatrix matrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE,width,height,hints);
            MatrixToImageWriter.writeToStream(matrix, MediaType.IMAGE_PNG.getSubtype(),baos,new MatrixToImageConfig());

            return baos.toByteArray();
        }catch (WriterException ex){
            LOGGER.info("WriterException {}",ex);
            return null;
        }catch (IOException ex){
            LOGGER.info("WriterException {}",ex);
            return null;
        }catch (Exception ex){
            LOGGER.info("WriterException {}",ex);
            return null;
        }

    }

}

