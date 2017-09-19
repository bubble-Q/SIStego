package com.qin.sistego.core;

import com.qin.sistego.exception.FileOperationException;
import com.qin.sistego.util.ByteUtil;
import com.qin.sistego.util.FileUtil;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;

public class Tail {

    private static final byte[] JPEG_END = {(byte) 0xff, (byte) 0xd9};
    private static final byte[] PNG_END = {0x00, 0x00, 0x00, 0x00, 0x49, 0x45, 0x4e, 0x44, (byte) 0xae, 0x42, 0x60, (byte) 0x82};

    public static String hideFile(String image, String file) {
        byte[] fileBytes = FileUtil.file2InfoBytes(file);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd_HHmmss");
        String suffix = FileUtil.getSuffix(image);
        String output = image.substring(0, image.lastIndexOf(".")) +
                "_" + dateFormat.format(new Date()) +
                "." + suffix; // 输出的文件名

        if (suffix.toUpperCase().equals("GIF")) fileBytes = Base64.getEncoder().encode(fileBytes);

        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        try {
            bis = new BufferedInputStream(new FileInputStream(image));
            bos = new BufferedOutputStream(new FileOutputStream(output));

            int data = bis.read();
            while (data != -1) {
                bos.write(data);
                data = bis.read();
            }
            bos.write(fileBytes);
        } catch (FileNotFoundException e) {
            throw new FileOperationException("没有找到指定的文件");
        } catch (IOException e) {
            throw new FileOperationException("隐藏文件时出错");
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return output;
    }

    public static String hideString(String image, String message) {
        byte[] messageBytes = message.getBytes();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd_HHmmss");
        String suffix = FileUtil.getSuffix(image);
        String output = image.substring(0, image.lastIndexOf(".")) +
                "_" + dateFormat.format(new Date()) +
                "." + suffix; // 输出的文件名

        if (suffix.toUpperCase().equals("GIF")) messageBytes = Base64.getEncoder().encode(messageBytes);

        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        try {
            bis = new BufferedInputStream(new FileInputStream(image));
            bos = new BufferedOutputStream(new FileOutputStream(output));

            int data = bis.read();
            while (data != -1) {
                bos.write(data);
                data = bis.read();
            }
            bos.write(messageBytes);
        } catch (FileNotFoundException e) {
            throw new FileOperationException("没有找到指定的文件");
        } catch (IOException e) {
            throw new FileOperationException("隐藏字符串时出错");
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return output;
    }

    public static String extractFile(String image) {
        byte[] infoBytes;
        String suffix = FileUtil.getSuffix(image);
        String output;

        infoBytes = getTail(FileUtil.file2Bytes(image), suffix);
        output = FileUtil.getInfoBytesName(infoBytes);
        byte[] contentBytes = FileUtil.getInfoBytesContent(infoBytes);

        File file = new File(new File(image).getParent(), output);
        BufferedOutputStream bos = null;

        try {
            bos = new BufferedOutputStream(new FileOutputStream(file));
            bos.write(contentBytes);
        } catch (FileNotFoundException e) {
            throw new FileOperationException("没有找到指定的文件");
        } catch (IOException e) {
            throw new FileOperationException("提取文件时出错");
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return file.getAbsolutePath();
    }

    public static String extractString(String image) {
        byte[] infoBytes;
        String suffix = FileUtil.getSuffix(image);

        infoBytes = getTail(FileUtil.file2Bytes(image), suffix);

        return new String(infoBytes);
    }

    private static byte[] getTail(byte[] imageBytes, String suffix) {
        byte[] tail;

        if (suffix.toUpperCase().equals("JPEG") | suffix.toUpperCase().equals("JPG"))
            tail = getJpegTail(imageBytes);
        else if (suffix.toUpperCase().equals("PNG"))
            tail = getPngTail(imageBytes);
        else if (suffix.toUpperCase().equals("GIF"))
            tail = getGifTail(imageBytes);
        else if (suffix.toUpperCase().equals("BMP"))
            tail = getBmpTail(imageBytes);
        else throw new RuntimeException("不支持后缀为" + suffix + "的文件的隐写");

        return tail;
    }

    private static byte[] getJpegTail(byte[] imageBytes) {
        int from = ByteUtil.searchSubList(imageBytes, JPEG_END) + JPEG_END.length;

        return Arrays.copyOfRange(imageBytes, from, imageBytes.length);
    }

    private static byte[] getPngTail(byte[] imageBytes) {
        int from = ByteUtil.searchSubList(imageBytes, PNG_END) + PNG_END.length;

        return Arrays.copyOfRange(imageBytes, from, imageBytes.length);
    }

    private static byte[] getGifTail(byte[] imageBytes) {
        String str = new String(imageBytes);
        int from = str.lastIndexOf(";") + 1;
        String base64 = str.substring(from, str.length());

        return Base64.getDecoder().decode(base64);
    }

    private static byte[] getBmpTail(byte[] imageBytes) {
        byte[] fileLengthBytes = Arrays.copyOfRange(imageBytes, 2, 6); // 描述文件大小的比特串
        ByteUtil.reverseBytes(fileLengthBytes); // 比特串是按“小端方式”存储的，需要进行复原
        int fileLength = ByteUtil.bytes2Int(fileLengthBytes);

        return Arrays.copyOfRange(imageBytes, fileLength, imageBytes.length);
    }
}
