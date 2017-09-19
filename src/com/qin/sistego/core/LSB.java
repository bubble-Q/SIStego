package com.qin.sistego.core;

import com.qin.sistego.exception.FileOperationException;
import com.qin.sistego.exception.OutOfSizeException;
import com.qin.sistego.util.ByteUtil;
import com.qin.sistego.util.FileUtil;
import com.qin.sistego.util.ImageUtil;

import java.io.*;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class LSB {

    public static String hideFile(String image, String file) throws OutOfSizeException {
        byte[] pixelsBytes = ImageUtil.pixels2Bytes(image);
        byte[] fileBytes = FileUtil.file2InfoBytes(file);
        int fileLength = FileUtil.getInfoBytesContentLength(fileBytes);

        if (!checkLength(pixelsBytes, fileBytes))
            throw new OutOfSizeException("文件大小不应超过 " + fileLength + " 字节");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd_HHmmss");
        String suffix = FileUtil.getSuffix(image);
        String output = image.substring(0, image.lastIndexOf(".")) +
                "_" + dateFormat.format(new Date()) +
                "." + suffix; // 输出的文件名

        pixelsBytes = sequenceMixBits(pixelsBytes, fileBytes);
        FileUtil.copyFile(image, output);
        ImageUtil.bytes2Pixels(output, pixelsBytes);

        return output;
    }

    public static String hideString(String image, String message) throws OutOfSizeException {
        byte[] pixelsBytes = ImageUtil.pixels2Bytes(image);
        byte[] stringBytes = message.getBytes();
        byte[] stringLengthBytes = ByteUtil.int2Bytes(stringBytes.length);
        stringBytes = ByteUtil.concatBytes(stringLengthBytes, stringBytes);

        if (!checkLength(pixelsBytes, stringBytes))
            throw new OutOfSizeException("字符串的大小不应超过 " + (stringBytes.length - 4) + " 字节");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd_HHmmss");
        String suffix = FileUtil.getSuffix(image);
        String output = image.substring(0, image.lastIndexOf(".")) +
                "_" + dateFormat.format(new Date()) +
                "." + suffix; // 输出的文件名

        pixelsBytes = sequenceMixBits(pixelsBytes, stringBytes);
        FileUtil.copyFile(image, output);
        ImageUtil.bytes2Pixels(output, pixelsBytes);

        return output;
    }

    public static String extractFile(String image) {
        byte[] pixelsBytes = ImageUtil.pixels2Bytes(image);
        byte[] fileBytes = deSequenceMix(pixelsBytes);
        String fileName = FileUtil.getInfoBytesName(fileBytes);
        fileBytes = FileUtil.getInfoBytesContent(fileBytes);

        File output = new File(new File(image).getParent(), fileName);
        BufferedOutputStream bos = null;

        try {
            bos = new BufferedOutputStream(new FileOutputStream(output));
            bos.write(fileBytes);
        } catch (FileNotFoundException e) {
            throw new FileOperationException("没有找到指定的文件");
        } catch (IOException e) {
            throw new FileOperationException("提取LSB隐写的文件时出错");
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return output.getAbsolutePath();
    }

    public static String extractString(String image) {
        byte[] pixelsBytes = ImageUtil.pixels2Bytes(image);
        byte[] infoBytes = deSequenceMix(pixelsBytes);
        int infoLength = ByteUtil.bytes2Int(Arrays.copyOfRange(infoBytes, 0, 4));
        infoBytes = Arrays.copyOfRange(infoBytes, 4, infoLength + 4);

        return new String(infoBytes);
    }

    private static boolean checkLength(byte[] imageBytes, byte[] contentBytes) {
        return contentBytes.length * 8 <= imageBytes.length;
    }

    private static byte[] sequenceMixBits(byte[] pixelsBytes, byte[] infoBytes) {
        int pixelsBytesIndex = 0, infoBytesIndex = 0;

        for (; infoBytesIndex < infoBytes.length; infoBytesIndex++) {
            byte data = infoBytes[infoBytesIndex];
            byte[] bits = new byte[8];
            bits[7] = (byte) ((data >> 7) & 0x01);
            bits[6] = (byte) ((data >> 6) & 0x01);
            bits[5] = (byte) ((data >> 5) & 0x01);
            bits[4] = (byte) ((data >> 4) & 0x01);
            bits[3] = (byte) ((data >> 3) & 0x01);
            bits[2] = (byte) ((data >> 2) & 0x01);
            bits[1] = (byte) ((data >> 1) & 0x01);
            bits[0] = (byte) (data & 0x01);

            int bitIndex = 7;
            for (; pixelsBytesIndex < pixelsBytes.length && bitIndex >= 0; pixelsBytesIndex++) {
                byte mask = (byte) 254;
                pixelsBytes[pixelsBytesIndex] = (byte) (pixelsBytes[pixelsBytesIndex] & mask | bits[bitIndex]);
                bitIndex--;
            }
        }

        return pixelsBytes;
    }

    private static byte[] deSequenceMix(byte[] pixelsBytes) {
        ByteBuffer buffer = ByteBuffer.allocate(pixelsBytes.length / 8);
        byte bits;

        for (int i = 0; i < pixelsBytes.length; ) {
            bits = (byte) (((pixelsBytes[i] & 0x01) << 7)
                    | ((pixelsBytes[i + 1] & 0x01) << 6)
                    | ((pixelsBytes[i + 2] & 0x01) << 5)
                    | ((pixelsBytes[i + 3] & 0x01) << 4)
                    | ((pixelsBytes[i + 4] & 0x01) << 3)
                    | ((pixelsBytes[i + 5] & 0x01) << 2)
                    | ((pixelsBytes[i + 6] & 0x01) << 1)
                    | ((pixelsBytes[i + 7] & 0x01)));
            buffer.put(bits);
            i += 8;
        }

        return buffer.array();
    }
}
