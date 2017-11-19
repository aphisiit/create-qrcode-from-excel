package com.backend.excelqrcode.service;

public interface QRCodeService {
    public byte[] generateQRCode(String text, int width, int height);
//    public void purgeCache();
}
