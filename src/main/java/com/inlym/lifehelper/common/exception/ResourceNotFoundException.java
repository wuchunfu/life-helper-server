package com.inlym.lifehelper.common.exception;

/**
 * 资源未找到异常
 *
 * <h2>触发条件
 * <p>使用主键 ID 查找资源时，未找到资源（可能是不存在或者已被删除）。
 *
 * <h2>使用说明
 * <p>不要直接使用这个类，而是在各个模块内继承这个类，抛出子类。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @date 2024/2/16
 * @since 2.1.0
 **/
public class ResourceNotFoundException extends RuntimeException {}
