package com.lvwj.halo.common.constants;

public class ValidateConstant {

  public static final String REG_EXP_BANK_CARD = "^\\d{11,23}$";
  public static final String REG_EXP_ID_CARD = "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([012]\\d)|3[0-1])\\d{3}(\\d|x|X)$";
  public static final String REG_EXP_PHONE = "^1[3-9][0-9]\\d{8}$";
  public static final String REG_EXP_SOCIAL_CODE = "^[^_IOZSVa-z\\W]{2}\\d{6}[^_IOZSVa-z\\W]{10}$";

  public static final String MSG_BANK_CARD = "银行卡号格式错误";
  public static final String MSG_ID_CARD = "身份证号格式错误";
  public static final String MSG_PHONE = "手机号码格式错误";
  public static final String MSG_SOCIAL_CODE = "统一社会信用代码格式错误";
}
