package com.lvwj.halo.httpclient.config;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.Proxy;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

/**
 * @author lvweijie
 * @date 2024年03月21日 15:50
 */
@AutoConfiguration
public class HttpClientConfiguration {

    @Value("${halo.http.client.okHttp3.readTimeout:120}")
    private Integer readTimeout;

    @Value("${halo.http.client.okHttp3.writeTimeout:120}")
    private Integer writeTimeout;

    @Value("${halo.http.client.okHttp3.connectTimeout:120}")
    private Integer connectTimeout;

    @Value("${halo.http.client.okHttp3.maxIdleConnections:20}")
    private Integer maxIdleConnections;

    @Value("${halo.http.client.okHttp3.keepAliveDuration:120}")
    private Integer keepAliveDuration;

    @Value("${halo.http.client.okHttp3.maxRequestsPerHost:200}")
    private Integer maxRequestsPerHost;

    @Value("${halo.http.client.okHttp3.maxRequests:200}")
    private Integer maxRequests;

    @Value("${halo.http.client.okHttp3.retryOnConnectionFailure:false}")
    private Boolean retryOnConnectionFailure;

/*    @Bean    spring web6.1版本以上 已过时
    public RestTemplate restTemplate() {
        OkHttp3ClientHttpRequestFactory okHttp3ClientHttpRequestFactory = new OkHttp3ClientHttpRequestFactory();
        okHttp3ClientHttpRequestFactory.setReadTimeout(readTimeout);
        okHttp3ClientHttpRequestFactory.setWriteTimeout(writeTimeout);
        okHttp3ClientHttpRequestFactory.setConnectTimeout(connectTimeout);
        RestTemplate restTemplate = new RestTemplate(okHttp3ClientHttpRequestFactory);
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        return restTemplate;
    }*/

    @Bean
    public OkHttpClient okHttpClient() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .sslSocketFactory(sslSocketFactory(), x509TrustManager())
                .retryOnConnectionFailure(retryOnConnectionFailure)
                .connectionPool(pool())
                .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                .readTimeout(readTimeout, TimeUnit.SECONDS)
                .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                .hostnameVerifier((hostname, session) -> true)
                // 设置代理
                .proxy(Proxy.NO_PROXY)
                // .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 8888)))
                // 拦截器
                // .addInterceptor()
                .build();
        okHttpClient.dispatcher().setMaxRequestsPerHost(maxRequestsPerHost);
        okHttpClient.dispatcher().setMaxRequests(maxRequests);
        return okHttpClient;
    }

    @Bean
    public X509TrustManager x509TrustManager() {
        return new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
    }

    @Bean
    public SSLSocketFactory sslSocketFactory() {
        try {
            // 信任任何链接
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{x509TrustManager()}, new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            //e.printStackTrace();
        }
        return null;
    }

    @Bean
    public ConnectionPool pool() {
        return new ConnectionPool(maxIdleConnections, keepAliveDuration, TimeUnit.SECONDS);
    }
}
