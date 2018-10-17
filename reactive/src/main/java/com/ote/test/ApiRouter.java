package com.ote.test;

/*
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class ApiRouter {

    @Autowired
    private TestService service;

    @Bean
    RouterFunction<?> mainRouterFunction() {
        return nest(path("/test"), route(GET("/{id}"), this::call));
    }

    Mono<ServerResponse> call(final ServerRequest request) {
        Integer index = Integer.parseInt(request.pathVariable("id"));
        return Mono.just(service.call(index)).transform(str -> buildResponse(str));
    }

    private Mono<ServerResponse> buildResponse(final Mono<String> str) {
        return str.flatMap(s -> ServerResponse.ok().body(str, String.class));
    }

}*/
