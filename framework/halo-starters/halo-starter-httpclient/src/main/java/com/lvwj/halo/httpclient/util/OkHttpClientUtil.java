package com.lvwj.halo.httpclient.util;

import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.lvwj.halo.common.enums.BaseErrorEnum;
import com.lvwj.halo.common.exceptions.BusinessException;
import com.lvwj.halo.common.utils.Func;
import com.lvwj.halo.common.utils.JsonUtil;
import com.lvwj.halo.common.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

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
    private static final MediaType MULTIPART = MediaType.parse("multipart/form-data; charset=utf-8");

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
        return doGet(url, null);
    }

    public static byte[] doGetByte(String url) {
        return doGetByte(url, null, null);
    }

    public static InputStream doGetStream(String url) throws IOException {
        return doGetStream(url, null, null);
    }

    public static <T> T doGet(String url, Map<String, String> params, Class<T> clazz) {
        String resp = doGet(url, params);
        if (StringUtil.isBlank(resp)) return null;
        return JsonUtil.parse(resp, clazz);
    }

    public static <T> T doGet(String url, Map<String, String> params, TypeReference<T> type) {
        String resp = doGet(url, params);
        if (StringUtil.isBlank(resp)) return null;
        return JsonUtil.parse(resp, type);
    }

    public static <T> T doGet(String url, Map<String, String> params, Map<String, String> headers, Class<T> clazz) {
        String resp = doGet(url, params, headers);
        if (StringUtil.isBlank(resp)) return null;
        return JsonUtil.parse(resp, clazz);
    }

    public static <T> T doGet(String url, Map<String, String> params, Map<String, String> headers, TypeReference<T> type) {
        String resp = doGet(url, params, headers);
        if (StringUtil.isBlank(resp)) return null;
        return JsonUtil.parse(resp, type);
    }

    /**
     * get 请求
     *
     * @param url    请求url地址
     * @param params 请求参数 map
     * @return string
     */
    public static String doGet(String url, Map<String, String> params) {
        return execute(getGetRequest(url, params, null));
    }

    /**
     * 异步get 请求
     *
     * @param url    请求url地址
     * @param params 请求参数 map
     * @return string
     */
    public static CompletableFuture<String> doGetAsync(String url, Map<String, String> params) {
        return doGetAsync(url, params, null);
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
        return execute(getGetRequest(url, params, headers));
    }

    /**
     * 异步get 请求
     *
     * @param url     请求url地址
     * @param params  请求参数 map
     * @param headers 请求头字段 {k1, v1 k2, v2, ...}
     * @return string
     */
    public static CompletableFuture<String> doGetAsync(String url, Map<String, String> params, Map<String, String> headers) {
        return executeAsync(getGetRequest(url, params, headers));
    }

    public static byte[] doGetByte(String url, Map<String, String> params, Map<String, String> headers) {
        return executeByte(getGetRequest(url, params, headers));
    }

    public static InputStream doGetStream(String url, Map<String, String> params, Map<String, String> headers) throws IOException {
        return executeStream(getGetRequest(url, params, headers));
    }

    public static <T> T doPostForm(String url, Map<String, String> params, Class<T> clazz) {
        String resp = doPostForm(url, params);
        if (StringUtil.isBlank(resp)) return null;
        return JsonUtil.parse(resp, clazz);
    }

    public static <T> T doPostForm(String url, Map<String, String> params, TypeReference<T> type) {
        String resp = doPostForm(url, params);
        if (StringUtil.isBlank(resp)) return null;
        return JsonUtil.parse(resp, type);
    }

    public static <T> T doPostForm(String url, Map<String, String> params, Map<String, String> headers, Class<T> clazz) {
        String resp = doPostForm(url, params, headers);
        if (StringUtil.isBlank(resp)) return null;
        return JsonUtil.parse(resp, clazz);
    }

    public static <T> T doPostForm(String url, Map<String, String> params, Map<String, String> headers, TypeReference<T> type) {
        String resp = doPostForm(url, params, headers);
        if (StringUtil.isBlank(resp)) return null;
        return JsonUtil.parse(resp, type);
    }

    /**
     * post 请求
     *
     * @param url    请求url地址
     * @param params 请求参数 map
     * @return string
     */
    public static String doPostForm(String url, Map<String, String> params) {
        return execute(getFormBodyPostRequest(url, params, null));
    }

    /**
     * post 请求
     *
     * @param url     请求url地址
     * @param params  请求参数 map
     * @param headers 请求头字段 {k1:v1, k2: v2, ...}
     * @return string
     */
    public static String doPostForm(String url, Map<String, String> params, Map<String, String> headers) {
        return execute(getFormBodyPostRequest(url, params, headers));
    }

    public static <T> T doPostJson(String url, String json, Class<T> clazz) {
        String resp = doPostJson(url, json);
        if (StringUtil.isBlank(resp)) return null;
        return JsonUtil.parse(resp, clazz);
    }

    public static <T> T doPostJson(String url, String json, TypeReference<T> type) {
        String resp = doPostJson(url, json);
        if (StringUtil.isBlank(resp)) return null;
        return JsonUtil.parse(resp, type);
    }

    public static <T> T doPostJson(String url, String json, Map<String, String> headers, Class<T> clazz) {
        String resp = doPostJson(url, json, headers);
        if (StringUtil.isBlank(resp)) return null;
        return JsonUtil.parse(resp, clazz);
    }

    public static <T> T doPostJson(String url, String json, Map<String, String> headers, TypeReference<T> type) {
        String resp = doPostJson(url, json, headers);
        if (StringUtil.isBlank(resp)) return null;
        return JsonUtil.parse(resp, type);
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
     * 异步post请求，请求体为json格式
     *
     * @param url  请求url地址
     * @param json 请求数据, json 字符串
     * @return java.util.concurrent.CompletableFuture<java.lang.String>
     * @author lvweijie
     * @date 2024/7/26 16:13
     */
    public static CompletableFuture<String> doPostJsonAsync(String url, String json) {
        return doPostJsonAsync(url, json, null);
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
        Request request = getRequestBodyPostRequest(url, json, headers, JSON);
        return execute(request);
    }

    /**
     * 异步post请求，请求体为json格式
     *
     * @param url     请求url地址
     * @param json    请求数据, json 字符串
     * @param headers 请求头字段
     * @return java.util.concurrent.CompletableFuture<java.lang.String>
     * @author lvweijie
     * @date 2024/7/26 16:13
     */
    public static CompletableFuture<String> doPostJsonAsync(String url, String json, Map<String, String> headers) {
        Request request = getRequestBodyPostRequest(url, json, headers, JSON);
        return executeAsync(request);
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
        Request request = getRequestBodyPostRequest(url, data, null, contentType);
        return execute(request);
    }

    private static String execute(Request request) {
        String method = request.method();
        String url = request.url().toString();
        try (Response response = okHttpClient().newCall(request).execute()) {
            String resp = response.body().string();
            if (response.isSuccessful()) {
                logResponse(method, resp);
            } else {
                String message = String.format("[%s]请求失败, code:%d, message:%s, body:%s", url, response.code(), response.message(), resp);
                logError(method, message, null);
            }
            return resp;
        } catch (Exception e) {
            logError(method, e.getMessage(), e);
            if (e instanceof BusinessException) {
                throw (BusinessException) e;
            } else {
                throw new BusinessException(BaseErrorEnum.HTTP_REQUEST_ERROR, e.getMessage());
            }
        }
    }

    private static byte[] executeByte(Request request) {
        String method = request.method();
        String url = request.url().toString();
        try (Response response = okHttpClient().newCall(request).execute()) {
            String resp = response.body().string();
            if (response.isSuccessful()) {
                logResponse(method, resp);
            } else {
                String message = String.format("[%s]请求失败, code:%d, message:%s, body:%s", url, response.code(), response.message(), resp);
                logError(method, message, null);
            }
            return response.body().bytes();
        } catch (Exception e) {
            logError(method, e.getMessage(), e);
            if (e instanceof BusinessException) {
                throw (BusinessException) e;
            } else {
                throw new BusinessException(BaseErrorEnum.HTTP_REQUEST_ERROR, e.getMessage());
            }
        }
    }

    private static InputStream executeStream(Request request) throws IOException {
        Response response = okHttpClient().newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().byteStream();
        }
        return null;
    }

    private static CompletableFuture<String> executeAsync(Request request) {
        String method = request.method();
        String url = request.url().toString();
        CompletableFuture<String> cf = new CompletableFuture<>();
        okHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                logError(method, e.getMessage(), e);
                cf.completeExceptionally(new BusinessException(BaseErrorEnum.HTTP_REQUEST_ERROR, e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                String body = Optional.of(response.body()).map(Object::toString).orElse("");
                if (response.isSuccessful()) {
                    logResponse(method, body);
                } else {
                    String message = String.format("[%s]请求失败, code:%d, message:%s, body:%s", url, response.code(), response.message(), body);
                    logError(method, message, null);
                }
                cf.complete(body);
            }
        });
        return cf;
    }

    private static Request getFormBodyPostRequest(String url, Map<String, String> params, Map<String, String> headers) {
        FormBody.Builder builder = new FormBody.Builder();
        if (Func.isNotEmpty(params)) {
            for (String key : params.keySet()) {
                builder.add(key, params.get(key));
            }
        }
        Request.Builder requestBuilder = new Request.Builder();
        if (Func.isNotEmpty(headers)) {
            for (String header : headers.keySet()) {
                requestBuilder.addHeader(header, headers.get(header));
            }
        }
        logRequest(true, url, null, params, headers);
        return requestBuilder.url(url).post(builder.build()).build();
    }

    private static Request getRequestBodyPostRequest(String url, String data, Map<String, String> headers, MediaType contentType) {
        RequestBody requestBody = RequestBody.create(data, contentType);
        Request.Builder requestBuilder = new Request.Builder();
        if (Func.isNotEmpty(headers)) {
            for (String header : headers.keySet()) {
                requestBuilder.addHeader(header, headers.get(header));
            }
        }
        logRequest(true, url, data, null, headers);
        return requestBuilder.url(url).post(requestBody).build();
    }

    private static Request getGetRequest(String url, Map<String, String> params, Map<String, String> headers) {
        StringBuilder sb = new StringBuilder(url);
        if (Func.isNotEmpty(params)) {
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
        Request.Builder requestBuilder = new Request.Builder();
        if (Func.isNotEmpty(headers)) {
            for (String header : headers.keySet()) {
                requestBuilder.addHeader(header, headers.get(header));
            }
        }
        logRequest(false, url, null, params, headers);
        return requestBuilder.url(sb.toString()).build();
    }

    private static void logRequest(boolean post, String url, String data, Map<String, String> params, Map<String, String> headers) {
        String method = post ? "POST" : "GET";
        log.info("[{} REQUEST] Url ==> {}", method, url);
        if (Func.isNotEmpty(headers)) {
            log.info("[{} REQUEST] Header ==> {}", method, JsonUtil.toJson(headers));
        }
        if (Func.isNotEmpty(params)) {
            log.info("[{} REQUEST] Param ==> {}", method, JsonUtil.toJson(params));
        }
        if (Func.isNotEmpty(data)) {
            log.info("[{} REQUEST] Body ==> {}", method, data);
        }
    }

    private static void logResponse(String method, String response) {
        log.info("[{} RESPONSE] Resp ==> {}", method.toUpperCase(), response);
    }

    private static void logError(String method, String error, Exception e) {
        log.error("[{} RESPONSE] Error ==> {}", method.toUpperCase(), error, e);
    }
}
