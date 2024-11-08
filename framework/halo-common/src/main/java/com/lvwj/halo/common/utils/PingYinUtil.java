package com.lvwj.halo.common.utils;

import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class PingYinUtil {

  private PingYinUtil() {
  }

  /**
   * 返回首字母
   */
  public static String getPyIndexStr(String strChinese, boolean bUpCase) {
    try {
      StringBuilder buffer = new StringBuilder();
      // 把中文转化成byte数组
      byte[] chineseBytes = strChinese.getBytes("GBK");
      for (int i = 0; i < chineseBytes.length; i++) {
        if ((chineseBytes[i] & 255) > 128) {
          int char1 = chineseBytes[i++] & 255;
          // 左移运算符用“<<”表示，是将运算符左边的对象，向左移动运算符右边指定的位数，并且在低位补零。其实，向左移n位，就相当于乘上2的n次方
          char1 <<= 8;
          int chart = char1 + (chineseBytes[i] & 255);
          buffer.append(getPyIndexChar((char) chart, bUpCase));
          continue;
        }
        char c = (char) chineseBytes[i];
        // 确定指定字符是否可以是 Java
        if (!Character.isJavaIdentifierPart(c)) {
          // 标识符中首字符以外的部分。
          c = 'A';
        }
        buffer.append(c);
      }
      return buffer.toString();
    } catch (Exception e) {
    }
    return null;
  }

  /**
   * 得到首字母
   */
  private static char getPyIndexChar(char charGbk, boolean bUpCase) {
    char result;
    if (charGbk >= 45217 && charGbk <= 45252) {
      result = 'A';
    } else if (charGbk >= 45253 && charGbk <= 45760) {
      result = 'B';
    } else if (charGbk >= 45761 && charGbk <= 46317) {
      result = 'C';
    } else if (charGbk >= 46318 && charGbk <= 46825) {
      result = 'D';
    } else if (charGbk >= 46826 && charGbk <= 47009) {
      result = 'E';
    } else if (charGbk >= 47010 && charGbk <= 47296) {
      result = 'F';
    } else if (charGbk >= 47297 && charGbk <= 47613) {
      result = 'G';
    } else if (charGbk >= 47614 && charGbk <= 48118) {
      result = 'H';
    } else if (charGbk >= 48119 && charGbk <= 49061) {
      result = 'J';
    } else if (charGbk >= 49062 && charGbk <= 49323) {
      result = 'K';
    } else if (charGbk >= 49324 && charGbk <= 49895) {
      result = 'L';
    } else if (charGbk >= 49896 && charGbk <= 50370) {
      result = 'M';
    } else if (charGbk >= 50371 && charGbk <= 50613) {
      result = 'N';
    } else if (charGbk >= 50614 && charGbk <= 50621) {
      result = 'O';
    } else if (charGbk >= 50622 && charGbk <= 50905) {
      result = 'P';
    } else if (charGbk >= 50906 && charGbk <= 51386) {
      result = 'Q';
    } else if (charGbk >= 51387 && charGbk <= 51445) {
      result = 'R';
    } else if (charGbk >= 51446 && charGbk <= 52217) {
      result = 'S';
    } else if (charGbk >= 52218 && charGbk <= 52697) {
      result = 'T';
    } else if (charGbk >= 52698 && charGbk <= 52979) {
      result = 'W';
    } else if (charGbk >= 52980 && charGbk <= 53688) {
      result = 'X';
    } else if (charGbk >= 53689 && charGbk <= 54480) {
      result = 'Y';
    } else if (charGbk >= 54481 && charGbk <= 55289) {
      result = 'Z';
    } else {
      result = (char) (65 + RandomUtil.randomInt(25));
    }

    if (!bUpCase) {
      result = Character.toLowerCase(result);
    }
    return result;
  }
}
