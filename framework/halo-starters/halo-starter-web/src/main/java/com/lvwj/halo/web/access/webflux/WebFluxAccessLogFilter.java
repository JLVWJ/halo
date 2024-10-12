package com.lvwj.halo.web.access.webflux;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;


@ConditionalOnWebApplication(type = Type.REACTIVE)
public class WebFluxAccessLogFilter implements WebFilter {

  @Override
  public reactor.core.publisher.Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    WebFluxAccessLogger accessLogger = WebFluxAccessLogger.newInstance(exchange);

    ServerWebExchange mutateExchange = exchange.mutate()
        .request(new CacheRequestBodyDecorator(exchange)).build();

    return chain.filter(mutateExchange)
        .doFinally(signal -> writeAccessLog(accessLogger, mutateExchange));
  }

  private void writeAccessLog(WebFluxAccessLogger accessLogger, ServerWebExchange exchange) {
    accessLogger.log(exchange.getRequest(), exchange.getResponse());
  }
}