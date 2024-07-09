package com.lvwj.halo.common.utils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Base64工具
 *
 * @author L.cm
 */
public class Base64Util {

  /**
   * 编码
   *
   * @param value 字符串
   * @return {String}
   */
  public static String encode(String value) {
    return encode(value, StandardCharsets.UTF_8);
  }

  /**
   * 编码
   *
   * @param value   字符串
   * @param charset 字符集
   * @return {String}
   */
  public static String encode(String value, java.nio.charset.Charset charset) {
    byte[] val = value.getBytes(charset);
    return new String(encode(val), charset);
  }

  /**
   * 编码URL安全
   *
   * @param value 字符串
   * @return {String}
   */
  public static String encodeUrlSafe(String value) {
    return encodeUrlSafe(value, StandardCharsets.UTF_8);
  }

  /**
   * 编码URL安全
   *
   * @param value   字符串
   * @param charset 字符集
   * @return {String}
   */
  public static String encodeUrlSafe(String value, java.nio.charset.Charset charset) {
    byte[] val = value.getBytes(charset);
    return new String(encodeUrlSafe(val), charset);
  }

  /**
   * 解码
   *
   * @param value 字符串
   * @return {String}
   */
  public static String decode(String value) {
    return decode(value, StandardCharsets.UTF_8);
  }

  /**
   * 解码
   *
   * @param value   字符串
   * @param charset 字符集
   * @return {String}
   */
  public static String decode(String value, java.nio.charset.Charset charset) {
    byte[] val = value.getBytes(charset);
    return new String(decode(val), charset);
  }

  /**
   * 解码URL安全
   *
   * @param value 字符串
   * @return {String}
   */
  public static String decodeUrlSafe(String value) {
    return decodeUrlSafe(value, StandardCharsets.UTF_8);
  }

  /**
   * 解码URL安全
   *
   * @param value   字符串
   * @param charset 字符集
   * @return {String}
   */
  public static String decodeUrlSafe(String value, java.nio.charset.Charset charset) {
    byte[] val = value.getBytes(charset);
    return new String(decodeUrlSafe(val), charset);
  }

  public static byte[] encode(byte[] src) {
    if (src.length == 0) {
      return src;
    }
    return Base64.getEncoder().encode(src);
  }

  /**
   * Base64-decode the given byte array.
   *
   * @param src the encoded byte array
   * @return the original byte array
   */
  public static byte[] decode(byte[] src) {
    if (src.length == 0) {
      return src;
    }
    return Base64.getDecoder().decode(src);
  }

  /**
   * Base64-encode the given byte array using the RFC 4648
   * "URL and Filename Safe Alphabet".
   *
   * @param src the original byte array
   * @return the encoded byte array
   * @since 4.2.4
   */
  public static byte[] encodeUrlSafe(byte[] src) {
    if (src.length == 0) {
      return src;
    }
    return Base64.getUrlEncoder().encode(src);
  }

  /**
   * Base64-decode the given byte array using the RFC 4648
   * "URL and Filename Safe Alphabet".
   *
   * @param src the encoded byte array
   * @return the original byte array
   * @since 4.2.4
   */
  public static byte[] decodeUrlSafe(byte[] src) {
    if (src.length == 0) {
      return src;
    }
    return Base64.getUrlDecoder().decode(src);
  }

  /**
   * Base64-encode the given byte array to a String.
   *
   * @param src the original byte array
   * @return the encoded byte array as a UTF-8 String
   */
  public static String encodeToString(byte[] src) {
    if (src.length == 0) {
      return "";
    }
    return Base64.getEncoder().encodeToString(src);
  }

  /**
   * Base64-decode the given byte array from a UTF-8 String.
   *
   * @param src the encoded UTF-8 String
   * @return the original byte array
   */
  public static byte[] decodeFromString(String src) {
    if (src.isEmpty()) {
      return new byte[0];
    }
    return Base64.getDecoder().decode(src);
  }

  /**
   * Base64-encode the given byte array to a String using the RFC 4648
   * "URL and Filename Safe Alphabet".
   *
   * @param src the original byte array
   * @return the encoded byte array as a UTF-8 String
   */
  public static String encodeToUrlSafeString(byte[] src) {
    return Base64.getUrlEncoder().encodeToString(src);
  }

  /**
   * Base64-decode the given byte array from a UTF-8 String using the RFC 4648
   * "URL and Filename Safe Alphabet".
   *
   * @param src the encoded UTF-8 String
   * @return the original byte array
   */
  public static byte[] decodeFromUrlSafeString(String src) {
    return Base64.getUrlDecoder().decode(src);
  }

}
