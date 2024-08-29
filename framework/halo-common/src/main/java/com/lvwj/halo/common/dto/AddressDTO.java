package com.lvwj.halo.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 地址：省市区详细地址
 *
 * @author lvwj
 * @date 2023-01-16 15:23
 */
@Data
@AllArgsConstructor
public class AddressDTO implements Serializable {

  private String province;

  private String city;

  private String county;

  private String detailAddress;
}
