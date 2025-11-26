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
public enum BaseErrorEnum implements IErrorEnum {

    SUCCESS(0, "操作成功"),
    FAILURE(500, "操作失败"),

    PARAM_MISS_ERROR(400, "缺少必要的请求参数"),
    PARAM_TYPE_ERROR(400, "请求参数类型错误"),
    PARAM_BIND_ERROR(400, "请求参数绑定错误"),
    PARAM_VALID_ERROR(400, "参数校验失败"),
    PARAM_EMPTY_ERROR(400, "参数[{0}]不能为空"),
    PARAM_ILLEGAL_ERROR(400, "非法参数异常"),
    MULTIPART_PARAM_ERROR(400, "请求参数中必须至少包含一个有效文件"),
    MEDIA_TYPE_NOT_SUPPORTED(400, "不支持当前媒体类型"),
    UNAUTHORIZED(401, "请求未授权"),
    CLIENT_UNAUTHORIZED(401, "客户端请求未授权"),
    REQUEST_REJECT(403, "请求被拒绝"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "404-该请求接口不存在"),
    METHOD_NOT_SUPPORTED(405, "不支持当前请求方法"),

    INTERNAL_SERVER_ERROR(500, "服务未知异常"),

    NULL_POINT_ERROR(600, "空指针异常"),
    NOT_EXISTS_ERROR(600, "业务数据不存在"),
    SQL_ERROR(610, "运行SQL出现异常"),
    RPC_FAILED(611, "API接口请求异常: {0}"),
    HTTP_REQUEST_ERROR(611, "HTTP接口请求异常: {0}"),
    SERIALIZE_ERROR(612, "{0}序列化失败"),
    DESERIALIZE_ERROR(612, "{0}反序列化失败"),

    INSTANCE_TYPE_ERROR(622, "[{0}]不是[{1}]的实例"),
    REG_EXP_ERROR(623, "[{0}]不匹配正则[{1}]"),

    TRANSITION_ERROR(666, "状态迁移异常: {0}"),
    CACHE_NULL_ERROR(700, "缓存NULL值异常: 不允许缓存NULL值"),

    IDEMPOTENT_ERROR(710, "重复操作，系统已拦截，请{0}再试!"),
    RATE_LIMIT_ERROR(711, "您访问太频繁触发了限流, 请稍后再试! 限定速率[{0}次/{1}秒]"),

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
