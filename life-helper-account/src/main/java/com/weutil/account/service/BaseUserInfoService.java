package com.weutil.account.service;

import com.weutil.account.entity.User;
import com.weutil.account.mapper.UserMapper;
import com.weutil.account.model.BaseUserInfo;
import com.weutil.oss.service.OssService;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 基础用户信息服务
 *
 * <h2>说明
 * <p>将昵称和头像这两个用户信息单独封装处理。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @date 2024/7/22
 * @since 3.0.0
 **/
@Service
@RequiredArgsConstructor
public class BaseUserInfoService {
    private final UserMapper userMapper;

    private final OssService ossService;

    /**
     * 更新用户基础信息
     *
     * @param userId 用户 ID
     * @param info   用户基础信息
     *
     * @date 2024/6/9
     * @since 2.3.0
     */
    public void update(long userId, @Nonnull BaseUserInfo info) {
        User updated = User.builder().id(userId).build();

        // 处理昵称
        if (info.getNickName() != null) {
            updated.setNickName(info.getNickName());
        }

        // 处理头像
        if (info.getAvatarUrl() != null) {
            String ossKey = "";
            if (info.getAvatarUrl().startsWith("http")) {
                // [TODO] 图片是外部地址，则转储至主仓库
                // ossKey = ossService.dumpExternalImage(OssDir.AVATAR, info.getAvatarUrl());
            } else {
                // [TODO] 图片已上传至内部 OSS 空间，复制到主仓库
                //  ossKey = ossService.copyImage(OssDir.AVATAR, info.getAvatarUrl());
            }
            updated.setAvatarPath(ossKey);
        }

        userMapper.update(updated);
    }

    /**
     * 获取用户基础信息
     *
     * @param userId 用户 ID
     *
     * @date 2024/6/9
     * @since 2.3.0
     */
    public BaseUserInfo get(long userId) {
        User user = userMapper.selectOneById(userId);
        return BaseUserInfo.builder().nickName(user.getNickName()).avatarUrl(user.getAvatarPath()).build();
    }
}
