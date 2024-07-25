-- --------------------------------------------------------
-- 手机号（短信验证码）登录日志表
-- 对应实体: [com.weutil.account.login.phone.entity.PhoneSmsLoginLog]
-- --------------------------------------------------------

CREATE TABLE `login_phone_sms_log`
(
    /* 下方是通用字段 */
    `id`               BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
    `create_time`      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `delete_time`      DATETIME                 DEFAULT NULL COMMENT '删除时间（逻辑删除标志）',

    /* 下方为业务字段 */
    `phone`            CHAR(11)        NOT NULL DEFAULT '' COMMENT '手机号',
    `phone_account_id` BIGINT UNSIGNED NOT NULL COMMENT '关联的用户手机号账户表 ID',
    `user_id`          BIGINT UNSIGNED NOT NULL COMMENT '对应的用户 ID',
    `token`            CHAR(32)        NOT NULL DEFAULT '' COMMENT '发放的鉴权令牌',
    `ip`               CHAR(15)        NOT NULL DEFAULT '' COMMENT '客户端 IP 地址',
    `login_time`       DATETIME        NOT NULL COMMENT '登录时间',
    `code`             CHAR(6)         NOT NULL DEFAULT '' COMMENT '短信验证码',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8mb4
    COMMENT ='手机号（短信验证码）登录日志';
