package com.lvwj.halo.dubbo.crypto.annotation.crypto;

import com.lvwj.halo.dubbo.crypto.CryptoType;
import com.lvwj.halo.dubbo.crypto.annotation.decrypt.ApiDecrypt;
import com.lvwj.halo.dubbo.crypto.annotation.encrypt.ApiEncrypt;

import java.lang.annotation.*;

/**
 * <p>DES加密解密</p>
 *
 * @author Chill
 */
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ApiEncrypt(CryptoType.DES)
@ApiDecrypt(CryptoType.DES)
public @interface ApiCryptoDes {

}
