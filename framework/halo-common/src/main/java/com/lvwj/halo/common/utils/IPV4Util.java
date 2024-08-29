package com.lvwj.halo.common.utils;

public class IPV4Util {

  public static final String DEFAULT_SUBNET_MASK_A = "255.0.0.0";
  public static final String DEFAULT_SUBNET_MASK_B = "255.255.0.0";
  public static final String DEFAULT_SUBNET_MASK_C = "255.255.255.0";

  public static final String TYPE_IP_A = "A";
  public static final String TYPE_IP_B = "B";
  public static final String TYPE_IP_C = "C";
  public static final String TYPE_IP_D = "D";
  public static final String TYPE_IP_LOCATE = "locate";

  public static boolean isSameAddress(String resourceIp, String requestIp) {
    if (getIpType(resourceIp).equals(getIpType(requestIp))) {
      return isSameAddress(resourceIp, requestIp, getIpDefaultMask(getIpType(resourceIp)));
    }
    return false;
  }

  public static boolean isSameAddress(String resourceIp, String requestIp, String subnetMask) {
    String resourceAddr = getAddrIp(resourceIp, subnetMask);
    String requestAddr = getAddrIp(requestIp, subnetMask);
    return resourceAddr.equals(requestAddr);
  }

  private static String getIpDefaultMask(String ipType) {
    return switch (ipType) {
      case TYPE_IP_A -> DEFAULT_SUBNET_MASK_A;
      case TYPE_IP_B -> DEFAULT_SUBNET_MASK_B;
      case TYPE_IP_C -> DEFAULT_SUBNET_MASK_C;
      default -> throw new IllegalArgumentException("not found mask address");
    };
  }

  private static String getBinaryIp(String data) {
    String[] dataArray = data.split("\\.");
    StringBuilder binaryIp = new StringBuilder();
    for (String ipStr : dataArray) {
      long signIp = Long.parseLong(ipStr);
      String binary = Long.toBinaryString(signIp);
      long binaryInt = Long.parseLong(binary);
      binaryIp.append(String.format("%08d", binaryInt));
    }

    return binaryIp.toString();
  }

  private static String getAddrIp(String ip, String subnetMask) {
    StringBuilder addrIp = new StringBuilder();
    String binaryIp = getBinaryIp(ip);
    String binarySubnetMask = getBinaryIp(subnetMask);
    for (int i = 0; i < 32; i++) {
      byte ipByte = Byte.parseByte(String.valueOf(binaryIp.charAt(i)));
      byte subnetMaskByte = Byte.parseByte(String.valueOf(binarySubnetMask.charAt(i)));
      addrIp.append(ipByte & subnetMaskByte);
    }
    return addrIp.toString();
  }

  public static String getIpType(String ip) {
    String binaryIp = getBinaryIp(ip);
    if (binaryIp.startsWith("127")) {
      return TYPE_IP_LOCATE;
    }
    if (binaryIp.startsWith("0")) {
      return TYPE_IP_A;
    }
    if (binaryIp.startsWith("10")) {
      return TYPE_IP_B;
    }
    if (binaryIp.startsWith("110")) {
      return TYPE_IP_C;
    }
    if (binaryIp.startsWith("1110")) {
      return TYPE_IP_D;
    }
    throw new IllegalArgumentException("invalid ip address");
  }
}
