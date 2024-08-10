package com.lvwj.halo.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 通用异常枚举码
 *
 * @author lvweijie
 * @date 2024年06月09日 16:01
 */
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public enum BaseErrorEnum implements IErrorEnum{

    SUCCESS(0, "操作成功"),
    FAILURE(500, "操作失败"),

    UNAUTHORIZED(401, "请求未授权"),
    CLIENT_UNAUTHORIZED(401, "客户端请求未授权"),
    REQUEST_REJECT(403, "请求被拒绝"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "404-该请求接口不存在"),

    METHOD_NOT_SUPPORTED(405, "不支持当前请求方法"),
    MEDIA_TYPE_NOT_SUPPORTED(415, "不支持当前媒体类型"),


    PARAM_MISS_ERROR(400, "缺少必要的请求参数"),
    PARAM_TYPE_ERROR(400, "请求参数类型错误"),
    PARAM_BIND_ERROR(400, "请求参数绑定错误"),
    PARAM_VALID_ERROR(400, "参数校验失败"),
    PARAM_EMPTY_ERROR(400, "参数[{0}]不能为空"),
    NULL_POINT_ERROR(499, "空指针异常"),
    INTERNAL_SERVER_ERROR(500, "服务未知异常"),
    NOT_EXISTS_ERROR(500, "数据不存在！"),

    IDEMPOTENT_ERROR(503, "重复操作，系统已拦截，请{0}再试!"),
    RATE_LIMIT_ERROR(504, "您访问太频繁触发了限流, 请稍后再试! 限定速率[{0}次/{1}秒]"),

    SQL_ERROR(600, "运行SQL出现异常"),
    RPC_FAILED(601, "RPC接口请求失败 {0}"),
    REQUIRED_FILE_PARAM_ERROR(610, "请求中必须至少包含一个有效文件"),
    ILLEGAL_ARGUMENT_ERROR(611, "无效参数异常"),

    INSTANCE_TYPE_ERROR(622, "[{0}] isn't instance of type[{1}]",false),
    REG_EXP_ERROR(623, "[{0}] doesn't match pattern[{1}]",false),

    CACHE_NULL_ERROR(700, "缓存NULL值错误"),

    ;

    /**
     * code编码
     */
    private final Integer code;
    /**
     * 中文信息描述
     */
    private final String description;

    private Boolean displayable = true;
}
