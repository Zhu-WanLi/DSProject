package com.zhu.mall.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;

/**
 * 描述：      生成二维码的工具
 */
public class QRCodeGenerator {
    public static void generateQRCode(String text, int width, int height, String filePath)
            throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        //拿到一个比特矩阵                                 格式
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
        //拿到图片存储地址
        Path path = FileSystems.getDefault().getPath(filePath);
        //从矩阵变成图片
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
    }

    public static void main(String[] args) {
        try {
            //不能使用常量类里的地址，那个使用spring注入的，这里测试时没有使用spring
            //要指定具体生成的文件
            generateQRCode("Hello World",350,350,"D:/BaiduNetdiskDownload/DS/QRTest.png");
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
