package com.inlym.lifehelper.weather.data.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 天气灾害预警信息
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @date 2022/10/26
 * @see <a href="https://dev.qweather.com/docs/api/warning/weather-warning/">天气灾害预警</a>
 * @since 1.5.0
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarningNow {
    // =============================  数据处理后新增的字段  =============================

    /** 图片的 URL 地址 */
    private String imageUrl;

    // =============================  和风天气原有的字段  ==============================

    /** 当前 API 的最近更新时间 */
    private LocalDateTime updateTime;

    /** 本条预警的唯一标识，可判断本条预警是否已经存在 */
    private String id;

    /** 预警发布时间 */
    private LocalDateTime pubTime;

    /** 预警信息标题 */
    private String title;

    /** 预警信息的发布状态 */
    private String status;

    /** 预警严重等级颜色，可能为空 */
    private String severityColor;

    /** 预警类型ID */
    private String type;

    /** 预警类型名称 */
    private String typeName;

    /** 预警详细文字描述 */
    private String text;
}
