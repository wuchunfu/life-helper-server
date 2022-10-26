package com.inlym.lifehelper.external.heweather;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;

/**
 * 和风天气通用工具类
 *
 * <h2>主要用途
 * <p>将在服务类中多处用到的通用方法封装在当前类中。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @date 2022/10/26
 * @since 1.5.0
 **/
public abstract class HeUtils {
    /** 资源根地址 */
    private static final String RESOURCE_BASE_URL = "https://static.lifehelper.com.cn";

    /**
     * 获取天气 icon 图片的 URL 地址
     *
     * @param iconId icon 字段数据
     *
     * @since 1.5.0
     */
    public static String getIconUrl(String iconId) {
        return RESOURCE_BASE_URL + "/qweather/icon/" + iconId + ".png";
    }

    /**
     * 获取天气生活指数图片的 URL 地址
     *
     * @param typeId 生活指数类型 ID
     *
     * @since 1.5.0
     */
    public static String getLiveImageUrl(String typeId) {
        return RESOURCE_BASE_URL + "/qweather/live/" + typeId + ".svg";
    }

    /**
     * 获取天气预警图片的 URL 地址
     *
     * <h2>图片地址示例
     * <p>{@code https://static.lifehelper.com.cn/qweather/warning/1001-Blue.png}
     *
     * @param type          预警类型 ID
     * @param severityColor 预警严重等级颜色
     *
     * @since 1.5.0
     */
    public static String getWarningImageUrl(String type, String severityColor) {
        return RESOURCE_BASE_URL + "/qweather/warning/" + type + "-" + severityColor + ".png";
    }

    /**
     * 获取天气预警图片的 URL 地址
     *
     * <h2>图片地址示例
     * <p>{@code https://static.lifehelper.com.cn/qweather/warning/1001-Blue.png}
     *
     * @param type          预警类型 ID
     * @param severityColor 预警严重等级颜色
     *
     * @since 1.5.0
     */
    public static String getWarningIconUrl(String type, String severityColor) {
        return RESOURCE_BASE_URL + "/qweather/warning/" + type + "-" + severityColor + ".png";
    }

    /**
     * 解析时间文本
     *
     * @param timeText 时间文本，格式示例："2022-10-26T21:16+08:00"
     *
     * @since 1.5.0
     */
    public static LocalDateTime parseTime(String timeText) {
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(DateTimeFormatter.ISO_LOCAL_DATE)
            .appendLiteral('T')
            .appendValue(HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(MINUTE_OF_HOUR, 2)
            .parseLenient()
            .appendOffsetId()
            .parseStrict()
            .toFormatter();

        return LocalDateTime.parse(timeText, formatter);
    }
}
