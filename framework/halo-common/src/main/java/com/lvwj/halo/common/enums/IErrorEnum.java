package com.lvwj.halo.common.enums;

/**
 * 异常枚举接口
 *
 * @author lvweijie
 * @date 2024年06月09日 15:50
 */
public interface IErrorEnum extends IEnum<Integer> {

    /**
     * 异常信息对用户是否可见 (如：支付收款账号金额不足， 这种商业机密信息不可告诉用户)
     */
    Boolean getDisplayable();
}
