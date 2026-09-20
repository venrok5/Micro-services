package com.example.gateway_api;

import static org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions.lb;
import static org.springframework.cloud.gateway.server.mvc.filter.CircuitBreakerFilterFunctions.circuitBreaker;
import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.stripPrefix;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

@Configuration
public class RouteConfiguration {

    @Bean
    public RouterFunction<ServerResponse> gatewayRoutes() {
        return route("bank-rest")
                .GET("/gateway/**", http())
                .before(stripPrefix(1)) // удалить из маршрута чтобы gteway не приходил в user-service 
                .filter(lb("BANK_REST")) // сервис находится через Eureka
                .filter(circuitBreaker("bankRestCircuitBreaker"))
                .build();
    }
}