package com.zhu.mall.utils;

import com.zhu.mall.common.Constant;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 * 描述：      图片工具类
 */
public class ImageUtil {
    public static void main(String[] args) throws IOException {
        
        String path = "D:/BaiduNetdiskDownload/DS/";

        //裁剪
        Thumbnails.of(new File(path + "05f686de-5229-49d7-8801-123cc59f1172.jpg"))
                .sourceRegion(Positions.BOTTOM_RIGHT,160,160).size(160, 160)
                .toFile(new File(path + "thumbnail.jpg"));

        //缩放
         //按比例缩放
        Thumbnails.of(new File(path + "05f686de-5229-49d7-8801-123cc59f1172.jpg"))
                .scale(0.7).toFile(path + "0.7倍.jpg");
        Thumbnails.of(new File(path + "05f686de-5229-49d7-8801-123cc59f1172.jpg"))
                .scale(1.5).toFile(path + "1.5倍.jpg");
         //指定大小
        Thumbnails.of(new File(path + "05f686de-5229-49d7-8801-123cc59f1172.jpg"))
                .size(500,500).keepAspectRatio(false).toFile(path + "不保持比例500大小.jpg");
        Thumbnails.of(new File(path + "05f686de-5229-49d7-8801-123cc59f1172.jpg"))
                .size(500,500).keepAspectRatio(true).toFile(path + "保持比例500大小.jpg");
        //旋转
        Thumbnails.of(path + "05f686de-5229-49d7-8801-123cc59f1172.jpg").size(500,500)
                .keepAspectRatio(true).rotate(90).toFile(path + "rotate(90).jpg");

        Thumbnails.of(new File(path + "725ae727-d2a4-4076-8a75-f2674ad0c4cd.jpg")).size(500, 500).watermark(Positions.CENTER,
                ImageIO.read(new File(path + "watermark.jpg")),
                Constant.IMAGE_OPACITY).toFile(path + "ma.jpg");
    }
}
