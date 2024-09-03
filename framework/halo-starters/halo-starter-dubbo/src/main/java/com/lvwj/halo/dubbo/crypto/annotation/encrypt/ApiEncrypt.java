package com.lvwj.halo.dubbo.crypto.annotation.encrypt;


import com.lvwj.halo.dubbo.crypto.CryptoType;

import java.lang.annotation.*;

/**
 * <p>加密</p>
 *
 * @author licoy.cn, L.cm
 */
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ApiEncrypt {

	/**
	 * 加密类型
	 *
	 * @return 类型
	 */
	CryptoType value();

	/**
	 * 私钥，用于某些需要单独配置私钥的方法，没有时读取全局配置的私钥
	 *
	 * @return 私钥
	 */
	String secretKey() default "";

}
