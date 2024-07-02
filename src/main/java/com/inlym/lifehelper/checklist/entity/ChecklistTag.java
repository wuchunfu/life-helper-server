package com.inlym.lifehelper.checklist.entity;

import com.inlym.lifehelper.checklist.constant.Color;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 待办任务标签
 *
 * <h2>说明
 * <p>给任务（{@code ChecklistTask}）打标签，两者之间是「多对多」关系。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @date 2024/6/11
 * @since 2.3.0
 */
@Table("checklist_tag")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChecklistTag {
    // ============================ 通用字段 ============================

    /** 主键 ID */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 删除时间（逻辑删除标志） */
    @Column(isLogicDelete = true)
    private LocalDateTime deleteTime;

    // ============================ 业务字段 ============================

    /** 所属用户 ID */
    private Long userId;

    /** 标签名称 */
    private String name;

    /**
     * 标记颜色（枚举值）
     *
     * <h3>说明
     * <p>用于标记项目名称旁的圆点颜色。
     */
    private Color color;

    /**
     * 收藏时间
     *
     * <h3>说明
     * <p>使用“时间”字段代替布尔值，在收藏列表排序时，以收藏时间作为排序依据。
     */
    private LocalDateTime favoriteTime;

    /**
     * （在排序中的）前一个 ID
     *
     * <h3>说明
     * <p>该字段仅用于组内排序使用，排在第1个（最前面）的该字段赋值为 {@code 0}
     */
    private Long prevId;
}
