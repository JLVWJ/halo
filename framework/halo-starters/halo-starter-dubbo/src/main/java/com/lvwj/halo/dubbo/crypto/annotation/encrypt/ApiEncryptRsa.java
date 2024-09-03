package com.lvwj.halo.dubbo.crypto.annotation.encrypt;

import com.lvwj.halo.dubbo.crypto.CryptoType;

import java.lang.annotation.*;

/**
 * rsa 加密
 *
 * @author licoy.cn, L.cm
 * @see ApiEncrypt
 */
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiEncrypt(CryptoType.RSA)
public @interface ApiEncryptRsa {
}
