package com.backend.excelqrcode.controller;


import com.backend.excelqrcode.service.QRCodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
@EnableAsync
@EnableCaching
@EnableScheduling
public class QRCodeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(QRCodeController.class);


    @Autowired
    QRCodeService qrCodeService;

    @GetMapping(value = "/qrcode")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@RequestParam("text") String text) {

        LOGGER.info("RequestParam : {}",text);
        LOGGER.info("subString Text : {}",text.substring(text.lastIndexOf("/") + 1));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type","image/png");
        headers.add("Content-Disposition","attachment; filename*=UTF8''" + text.substring(text.lastIndexOf("/") + 1) + ".png");

        try{
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new ByteArrayResource(qrCodeService.generateQRCode(text, 512, 512)));
        }catch (Exception ex){
            throw new InternalServerError("Error While generating QRCode {}",ex);
        }
    }

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public class InternalServerError extends RuntimeException{
        private static final long serialVersionUID = 1L;
        public InternalServerError(final String message,final Throwable cause){
            super(message);
            LOGGER.info("INTERNAL_SERVER_ERROR {}",message);
        }
    }
}
