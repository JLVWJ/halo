package com.lvwj.halo.common.utils;

/**
 * @author lvweijie
 * @date 2024年03月04日 11:45
 */
public class GeoPositionUtil {

    /**
     * 地球平均半径（单位：米）
     */
    private static final double EARTH_AVG_RADIUS = 6371000;

    /**
     * 经纬度转化为弧度(rad)
     *
     * @param d 经度/纬度
     */
    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     * （基于googleMap中的算法得到两经纬度之间的距离,计算精度与谷歌地图的距离精度差不多。）
     *
     * @param longitude1 第一点的经度
     * @param latitude1  第一点的纬度
     * @param longitude2 第二点的经度
     * @param latitude2  第二点的纬度
     * @return 返回的距离，单位m
     */
    public static double getDistance(double longitude1, double latitude1, double longitude2, double latitude2) {
        double radLat1 = rad(latitude1);
        double radLat2 = rad(latitude2);
        double a = radLat1 - radLat2;
        double b = rad(longitude1) - rad(longitude2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_AVG_RADIUS;
        s = Math.round(s * 10000d) / 10000d;
        return s;
    }

    public static double getDistance(String location1, String location2) {
        String[] split1 = location1.split(",");
        String[] split2 = location2.split(",");
        double longitude1 = Double.parseDouble(split1[0]);
        double latitude1 = Double.parseDouble(split1[1]);
        double longitude2 = Double.parseDouble(split2[0]);
        double latitude2 = Double.parseDouble(split2[1]);
        return getDistance(longitude1, latitude1, longitude2, latitude2);
    }
}


