package com.lvwj.halo.log.access.webflux;


import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;

import java.nio.charset.StandardCharsets;

@SuppressWarnings("PMD")
public class CacheRequestBodyDecorator extends ServerHttpRequestDecorator {

  private final ServerWebExchange exchange;
  private StringBuffer cacheBodyAppender;

  private static final String REQUEST_BODY_CACHE = ServerHttpRequest.class.getName() + ".body";

  public CacheRequestBodyDecorator(ServerWebExchange exchange) {
    super(exchange.getRequest());

    this.exchange = exchange;
  }

  @Override
  public Flux<DataBuffer> getBody() {
    // 数据可能是分几次收到，意味着doOnNext会回调多次，因此通过append的方式追加
    return super.getBody().doOnNext(this::appendBody);
  }

  public void appendBody(DataBuffer dataBuffer) {
    if (cacheBodyAppender == null) {
      cacheBodyAppender = new StringBuffer();
      exchange.getAttributes().put(REQUEST_BODY_CACHE, cacheBodyAppender);
    }

    String data = StandardCharsets.UTF_8.decode(dataBuffer.asByteBuffer()).toString();
    cacheBodyAppender.append(data);
  }
}
