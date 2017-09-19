package com.qin.sistego.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class ByteUtil {

    /**
     * 将一个 int 型数转化为 byte 型数<br/>
     * <strong>注意</strong>：被转化的数大于 255 时会造成精度丢失，但比特序列是相同的
     *
     * @param integer 需要进行转化的 int 型数
     * @return 转换后的 byte 型数
     */
    public static byte int2Byte(int integer) {
        return (byte) integer;
    }

    /**
     * 将一个 32 位的 int 型数转化为 4 个 8 位的 byte 型数
     *
     * @param integer 需要进行转化的 int 型数
     * @return 转换后的 byte 型数数组
     */
    public static byte[] int2Bytes(int integer) {
        ByteBuffer buffer = ByteBuffer.allocate(4).putInt(integer);

        return buffer.array();
    }

    /**
     * 将一个长度为 4 的 byte 型数组转换为一个 32 位的 int 型数
     *
     * @param bytes 长度为 4 的 byte 型数组
     * @return 转换后的 int 型数
     */
    public static int bytes2Int(byte[] bytes) {
        if (bytes.length != 4) throw new IllegalArgumentException("byte 数组长度不为4");

        int integer = ((bytes[0] & 0xff) << 24)
                | ((bytes[1] & 0xff) << 16)
                | ((bytes[2] & 0xff) << 8)
                | (bytes[3] & 0xff);

        return integer;
    }

    /**
     * 将一个 byte 数组转化为 int 数组<br/>
     * <strong>注意</strong>：转换时会4个 byte 一组的将其合并为 int，因此 byte 数组的长度需要是4的倍数
     *
     * @param bytes 需要被转换的 byte 数组
     * @return 转换后的 int 数组
     */
    public static int[] bytes2Ints(byte[] bytes) {
        if (bytes.length % 4 != 0) throw new IllegalArgumentException("byte 数组的长度不是4的倍数");

        IntBuffer buffer = IntBuffer.allocate(bytes.length / 4);

        for (int index = 0; index < bytes.length; ) {
            int integer = ((bytes[index] & 0xff) << 24)
                    | ((bytes[index + 1] & 0xff) << 16)
                    | ((bytes[index + 2] & 0xff) << 8)
                    | (bytes[index + 3] & 0xff);
            buffer.put(integer);
            index += 4;
        }

        return buffer.array();
    }

    /**
     * 将一个 int 数组转化为 byte 数组，转换时将每个 int 型数拆分为 4 个 byte 型数
     *
     * @param ints 需要转换的 int 数组
     * @return 转换后的 byte 数组
     */
    public static byte[] ints2Bytes(int[] ints) {
        ByteBuffer buffer = ByteBuffer.allocate(ints.length * 4);

        for (int i = 0; i < ints.length; i++) {
            buffer.putInt(ints[i]);
        }

        return buffer.array();
    }

    /**
     * 拼接两个 byte 数组
     *
     * @param first  第一个 byte 数组
     * @param second 第二个 byte 数组
     * @return 返回拼接后的 byte 数组
     */
    public static byte[] concatBytes(byte[] first, byte[] second) {
        byte[] out = new byte[first.length + second.length];
        System.arraycopy(first, 0, out, 0, first.length);
        System.arraycopy(second, 0, out, first.length, second.length);

        return out;
    }

    /**
     * 将多个 byte 数组拼接
     *
     * @param arrays byte 数组
     * @return 拼接后的 byte 数组
     */
    public static byte[] concatBytes(byte[]... arrays) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (byte[] array : arrays) {
            try {
                baos.write(array);
            } catch (IOException e) {
                throw new RuntimeException("拼接 byte 数组时出错");
            }
        }

        return baos.toByteArray();
    }

    /**
     * 在源数组中搜索目标数组
     *
     * @param src 源数组
     * @param target 目标 数组
     * @return 找到目标数组则返回它的开始位置的索引，未找到则返回 -1
     */
    public static int searchSubList(byte[] src, byte[] target) {
        boolean flag = false;
        int endIndex = -1;

        for (int i = 0, j; i < src.length; i++) {
            for (j = 0; j < target.length; j++) {
                if ((i + j) >= src.length) {
                    flag = true;
                    break;
                }
                if (src[i + j] != target[j]) {
                    break;
                }
            }
            if (flag)
                break;
            if (j == target.length) {
                endIndex = i;
                break;
            }
        }

        return endIndex;
    }

    /**
     * 翻转 byte 数组
     *
     * @param bytes 需要进行翻转的 byte 数组
     */
    public static void reverseBytes(byte[] bytes) {
        for (int i = 0, j = bytes.length - 1; i <= j; i++, j--) {
            byte temp = bytes[i];
            bytes[i] = bytes[j];
            bytes[j] = temp;
        }
    }
}
