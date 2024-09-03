package com.lvwj.halo.dubbo.crypto.annotation.decrypt;

import com.lvwj.halo.dubbo.crypto.CryptoType;

import java.lang.annotation.*;

/**
 * rsa 解密
 *
 * @author licoy.cn
 * @see ApiDecrypt
 */
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiDecrypt(CryptoType.RSA)
public @interface ApiDecryptRsa {
}
