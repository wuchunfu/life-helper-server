package com.inlym.lifehelper.checklist.mapper;

import com.inlym.lifehelper.checklist.entity.ChecklistProject;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 待办清单存储库
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @date 2024/7/1
 * @since 2.3.0
 */
@Mapper
public interface ChecklistProjectMapper extends BaseMapper<ChecklistProject> {}
