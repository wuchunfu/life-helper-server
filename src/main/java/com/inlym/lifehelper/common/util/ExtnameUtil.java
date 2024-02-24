package com.inlym.lifehelper.common.util;

/**
 * 文件后缀名工具集
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @date 2024/2/24
 * @since 2.2.0
 **/
public abstract class ExtnameUtil {
    /**
     * 检测图片格式
     *
     * @param bytes 图片字节内容
     *
     * @since 2.2.0
     */
    public static String detectImageFormat(byte[] bytes) {
        if (bytes.length >= 2 && bytes[0] == (byte) 0xFF && bytes[1] == (byte) 0xD8) {
            return "jpg";
        } else if (bytes.length >= 8 && bytes[0] == (byte) 0x89 && bytes[1] == (byte) 0x50 && bytes[2] == (byte) 0x4E && bytes[3] == (byte) 0x47 && bytes[4] == (byte) 0x0D && bytes[5] == (byte) 0x0A && bytes[6] == (byte) 0x1A && bytes[7] == (byte) 0x0A) {
            return "png";
        } else if (bytes.length >= 6 && bytes[0] == (byte) 0x47 && bytes[1] == (byte) 0x49 && bytes[2] == (byte) 0x46 && bytes[3] == (byte) 0x38 && bytes[4] == (byte) 0x39 && bytes[5] == (byte) 0x61) {
            return "gif";
        } else if (bytes.length >= 2 && bytes[0] == (byte) 0x42 && bytes[1] == (byte) 0x4D) {
            return "bmp";
        } else {
            return "";
        }
    }
}
