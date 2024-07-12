package com.lvwj.halo.httpclient.util;

import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.Map;

/**
 * @author lvweijie
 * @date 2024年07月12日 15:14
 */
@Slf4j
public class OkHttpClientUtil {

    private OkHttpClientUtil() {

    }

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final MediaType XML = MediaType.parse("application/xml; charset=utf-8");
    private static volatile OkHttpClient okHttpClient;

    public static OkHttpClient okHttpClient() {
        if (null == okHttpClient) {
            synchronized (OkHttpClientUtil.class) {
                if (null == okHttpClient) {
                    okHttpClient = SpringUtil.getBean(OkHttpClient.class);
                }
            }
        }
        return okHttpClient;
    }

    /**
     * get 请求
     *
     * @param url 请求url地址
     * @return string
     */
    public static String doGet(String url) {
        return doGet(url, null, null);
    }

    public static byte[] doGetByte(String url) {
        return doGetByte(url, null, null);
    }

    public static String doPost(String url) {
        return doPost(url, null, null);
    }

    /**
     * get 请求
     *
     * @param url    请求url地址
     * @param params 请求参数 map
     * @return string
     */
    public static String doGetWithParams(String url, Map<String, String> params) {
        return doGet(url, params, null);
    }

    /**
     * get 请求
     *
     * @param url     请求url地址
     * @param headers 请求头字段 {k1, v1 k2, v2, ...}
     * @return string
     */
    public static String doGetWithHeaders(String url, Map<String, String> headers) {
        return doGet(url, null, headers);
    }

    /**
     * get 请求
     *
     * @param url     请求url地址
     * @param params  请求参数 map
     * @param headers 请求头字段 {k1, v1 k2, v2, ...}
     * @return string
     */
    public static String doGet(String url, Map<String, String> params, Map<String, String> headers) {
        StringBuilder sb = new StringBuilder(url);
        if (params != null && !params.keySet().isEmpty()) {
            boolean firstFlag = true;
            for (String key : params.keySet()) {
                if (firstFlag) {
                    sb.append("?").append(key).append("=").append(params.get(key));
                    firstFlag = false;
                } else {
                    sb.append("&").append(key).append("=").append(params.get(key));
                }
            }
        }
        Request.Builder builder = new Request.Builder();
        if (headers != null && !headers.isEmpty()) {
            for (String header : headers.keySet()) {
                builder.addHeader(header, headers.get(header));
            }
        }
        Request request = builder.url(sb.toString()).build();
        return executeBody(request);
    }

    public static byte[] doGetByte(String url, Map<String, String> params, Map<String, String> headers) {
        StringBuilder sb = new StringBuilder(url);
        if (params != null && !params.keySet().isEmpty()) {
            boolean firstFlag = true;
            for (String key : params.keySet()) {
                if (firstFlag) {
                    sb.append("?").append(key).append("=").append(params.get(key));
                    firstFlag = false;
                } else {
                    sb.append("&").append(key).append("=").append(params.get(key));
                }
            }
        }
        Request.Builder builder = new Request.Builder();
        if (headers != null && !headers.isEmpty()) {
            for (String header : headers.keySet()) {
                builder.addHeader(header, headers.get(header));
            }
        }
        Request request = builder.url(sb.toString()).build();
        return executeByte(request);
    }

    /**
     * post 请求
     *
     * @param url    请求url地址
     * @param params 请求参数 map
     * @return string
     */
    public static String doPostForm(String url, Map<String, String> params) {
        FormBody.Builder builder = new FormBody.Builder();
        if (params != null && !params.keySet().isEmpty()) {
            for (String key : params.keySet()) {
                builder.add(key, params.get(key));
            }
        }
        Request request = new Request.Builder().url(url).post(builder.build()).build();
        return execute(request);
    }

    /**
     * post 请求
     *
     * @param url     请求url地址
     * @param params  请求参数 map
     * @param headers 请求头字段 {k1:v1, k2: v2, ...}
     * @return string
     */
    public static String doPost(String url, Map<String, String> params, Map<String, String> headers) {
        FormBody.Builder builder = new FormBody.Builder();
        if (params != null && !params.keySet().isEmpty()) {
            for (String key : params.keySet()) {
                builder.add(key, params.get(key));
            }
        }
        Request.Builder requestBuilder = new Request.Builder();
        if (headers != null && !headers.isEmpty()) {
            for (String header : headers.keySet()) {
                requestBuilder.addHeader(header, headers.get(header));
            }
        }
        Request request = requestBuilder.url(url).post(builder.build()).build();
        return execute(request);
    }

    /**
     * post 请求, 请求数据为 json 的字符串
     *
     * @param url  请求url地址
     * @param json 请求数据, json 字符串
     * @return string
     */
    public static String doPostJson(String url, String json) {
        return executePost(url, json, JSON);
    }

    /**
     * post 请求, 请求数据为 json 的字符串
     *
     * @param url     请求url地址
     * @param json    请求数据, json 字符串
     * @param headers 请求头字段 {k1, v1 k2, v2, ...}
     * @return string
     */
    public static String doPostJson(String url, String json, Map<String, String> headers) {
        RequestBody requestBody = RequestBody.create(json, JSON);
        Request.Builder builder = new Request.Builder();
        if (headers != null && !headers.isEmpty()) {
            for (String header : headers.keySet()) {
                builder.addHeader(header, headers.get(header));
            }
        }
        Request request = builder.url(url).post(requestBody).build();
        return execute(request);
    }

    /**
     * post 请求, 请求数据为 xml 的字符串
     *
     * @param url 请求url地址
     * @param xml 请求数据, xml 字符串
     * @return string
     */
    public static String doPostXml(String url, String xml) {
        return executePost(url, xml, XML);
    }

    private static String executePost(String url, String data, MediaType contentType) {
        RequestBody requestBody = RequestBody.create(data, contentType);
        Request request = new Request.Builder().url(url).post(requestBody).build();
        return execute(request);
    }

    private static String execute(Request request) {
        try (Response response = okHttpClient().newCall(request).execute()) {
            if (response.isSuccessful() && null != response.body()) {
                return response.body().string();
            }
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return "";
    }

    private static String executeBody(Request request) {
        try (Response response = okHttpClient().newCall(request).execute()) {
            if (response.body() != null) {
                return response.body().string();
            }
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return "";
    }

    private static byte[] executeByte(Request request) {
        try (Response response = okHttpClient().newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return response.body().bytes();
            }
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return null;
    }
}
