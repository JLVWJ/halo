package com.lvwj.halo.common.utils;

import com.lvwj.halo.common.dto.AddressDTO;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 国内地址解析工具
 *
 * @author lvwj
 * @date 2023-01-16 15:19
 */
public class AddressUtil {

  public static final String regex = "(?<province>[^省]+省|.+自治区|[^台湾]+台湾|[^澳门]+澳门|[^香港]+香港|[^市]+市)?(?<city>[^自治州]+自治州|[^特别行政区]+特别行政区|[^市]+市|.*?地区|.*?行政单位|.+盟|市辖区|[^县]+县)(?<county>[^县]+县|[^市]+市|[^镇]+镇|[^区]+区|[^乡]+乡|.+场|.+旗|.+海域|.+岛)?(?<address>.*)";

  public static final Pattern pattern = Pattern.compile(regex);

  /**
   * 解析地址
   *
   * @param address 具体地址：省市区详细地址
   * @return AddressDTO
   * @author lvwj
   * @date 2023-01-16 21:30
   */
  public static AddressDTO parse(String address) {
    if (Func.isBlank(address)) {
      return null;
    }
    String province = null;
    String city = null;
    String county = null;
    String detailAddress = null;
    final Matcher matcher = pattern.matcher(address);
    while (matcher.find()) {
      province = Func.toStr(matcher.group("province")).trim();
      city = Func.toStr(matcher.group("city")).trim();
      county = Func.toStr(matcher.group("county")).trim();
      detailAddress = Func.toStr(matcher.group("address")).trim();
    }
    if (Func.isAllEmpty(province, city, county, detailAddress)) {
      return null;
    }
    return new AddressDTO(province, city, county, detailAddress);
  }
}
