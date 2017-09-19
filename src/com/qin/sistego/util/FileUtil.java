package com.qin.sistego.util;

import com.qin.sistego.exception.FileOperationException;

import java.io.*;
import java.util.Arrays;

public class FileUtil {

    /**
     * 获取文件的后缀名
     *
     * @param file 文件路径
     * @return 文件后缀名
     */
    public static String getSuffix(String file) {
        File f = new File(file);
        String name = f.getName();

        return name.substring(name.lastIndexOf(".") + 1, name.length());
    }

    /**
     * 将文件转化为 byte 数组
     *
     * @param file 文件所在路径
     * @return 转换后的 byte 数组
     */
    public static byte[] file2Bytes(String file) {
        BufferedInputStream bis = null;
        ByteArrayOutputStream baos;

        try {
            bis = new BufferedInputStream(new FileInputStream(file));
            baos = new ByteArrayOutputStream();

            int data = bis.read();

            while (data != -1) {
                baos.write(data);
                data = bis.read();
            }
        } catch (FileNotFoundException e) {
            throw new FileOperationException("没有找到指定的文件");
        } catch (IOException e) {
            throw new FileOperationException("文件转化为 byte 数组时出错");
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return baos.toByteArray();
    }

    /**
     * 将文件转化为一个字符串
     *
     * @param file 文件路径
     * @return 转化后的字符串
     */
    public static String file2String(String file) {
        byte[] fileBytes = file2Bytes(file);

        return new String(fileBytes);
    }

    /**
     * 将文件的信息（文件名 + 文件内容）转化为 byte 数组<br/>
     * 此方法用于在隐写时将文件的必要信息全部转化为 byte 数组表示
     *
     * @param file 文件所在路径
     * @return byte 数组
     */
    public static byte[] file2InfoBytes(String file) {
        byte[] fileNameBytes = new File(file).getName().getBytes();
        byte[] fileNameLengthBytes = ByteUtil.int2Bytes(fileNameBytes.length);
        byte[] fileBytes = file2Bytes(file);
        byte[] fileLengthBytes = ByteUtil.int2Bytes(fileBytes.length);

        return ByteUtil.concatBytes(fileNameLengthBytes, fileNameBytes, fileLengthBytes, fileBytes);
    }

    /**
     * 此方法用于获取 <code>file2InfoBytes</code> 方法隐藏的文件名
     *
     * @param infoBytes 包含文件名、文件内容的 byte 数组
     * @return 文件名
     */
    public static String getInfoBytesName(byte[] infoBytes) {
        int fileNameLength = ByteUtil.bytes2Int(Arrays.copyOfRange(infoBytes, 0, 4));
        String fileName = new String(Arrays.copyOfRange(infoBytes, 4, fileNameLength + 4));

        return fileName;
    }

    /**
     * 此方法用于获取 <code>file2InfoBytes</code> 方法隐藏的文件内容的字节数
     *
     * @param infoBytes 包含文件名、文件内容的 byte 数组
     * @return 文件内容的字节数
     */
    public static int getInfoBytesContentLength(byte[] infoBytes) {
        int fileNameLength = ByteUtil.bytes2Int(Arrays.copyOfRange(infoBytes, 0, 4));
        int contentLength = ByteUtil.bytes2Int(Arrays.copyOfRange(infoBytes, fileNameLength + 4, fileNameLength + 8));

        return contentLength;
    }

    /**
     * 此方法用于获取 <code>file2InfoBytes</code> 方法隐藏的文件内容
     *
     * @param infoBytes 包含文件名、文件内容的 byte 数组
     * @return 代表文件内容的 byte 数组
     */
    public static byte[] getInfoBytesContent(byte[] infoBytes) {
        int fileNameLength = ByteUtil.bytes2Int(Arrays.copyOfRange(infoBytes, 0, 4));
        int contentLength = ByteUtil.bytes2Int(Arrays.copyOfRange(infoBytes, fileNameLength + 4, fileNameLength + 8));

        return Arrays.copyOfRange(infoBytes, fileNameLength + 8, fileNameLength + 8 + contentLength);
    }

    /**
     * 将指定的文件复制到指定的位置
     *
     * @param src 源文件位置
     * @param dest 复制的文件的位置
     */
    public static void copyFile(String src, String dest) {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        try {
            bis = new BufferedInputStream(new FileInputStream(src));
            bos = new BufferedOutputStream(new FileOutputStream(dest));

            int data = bis.read();
            while (data != -1) {
                bos.write(data);
                data = bis.read();
            }
        } catch (FileNotFoundException e) {
            throw new FileOperationException("没有找到指定的文件");
        } catch (IOException e) {
            throw new FileOperationException("复制文件时出错");
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
    }
}
