package vn.fs.service;


public interface QrCodeGeneratorService {
    boolean generateQRCode(String qrCodeContent, String filePath, int width, int height);
}