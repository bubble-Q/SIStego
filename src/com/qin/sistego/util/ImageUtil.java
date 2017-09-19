package com.qin.sistego.util;

import com.qin.sistego.exception.FileOperationException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ImageUtil {

    public static byte[] pixels2Bytes(String imagePath) {
        BufferedImage image;

        try {
            image = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            throw new FileOperationException("读取图片文件时出错");
        }

        int size = image.getWidth() * image.getHeight();
        ByteBuffer buffer = ByteBuffer.allocate(size * 4);

        for (int x = image.getMinX(); x < image.getWidth(); x++) {
            for (int y = image.getMinY(); y < image.getHeight(); y++) {
                int data = image.getRGB(x, y);
                buffer.putInt(data);
            }
        }

        return buffer.array();
    }

    public static void bytes2Pixels(String imagePath, byte[] bytes) {
        int[] ints = ByteUtil.bytes2Ints(bytes);
        String suffix = FileUtil.getSuffix(imagePath).toLowerCase();
        BufferedImage img;

        try {
            img = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            throw new FileOperationException("将数据写入图片文件时出错");
        }

        for (int x = img.getMinX(), i = 0; x < img.getWidth(); x++) {
            for (int y = img.getMinY(); y < img.getHeight(); y++) {
                img.setRGB(x, y, ints[i]);
                i++;
            }
        }

        try {
            ImageIO.write(img, suffix, new File(imagePath));
        } catch (IOException e) {
            throw new FileOperationException("将数据写入图片文件时出错");
        }
    }
}
