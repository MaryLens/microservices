package com.example.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;

@SpringBootApplication
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

    @Bean
    public RouterFunction<ServerResponse> gatewayRoutes() {
        return
                // user-service
                route("user_service")
                        .route(request -> request.path().startsWith("/api/users")
                                        || request.path().startsWith("/api/auth"),
                                http())
                        // Для Docker:
                         .before(uri("http://user-service:8081"))
                        // Для Intelli:
                        //.before(uri("http://localhost:8081"))
                        .build()

                        // product-service
                        .and(route("product_service")
                                .route(request -> request.path().startsWith("/api/products")
                                                || request.path().startsWith("/api/categories"),
                                        http())
                                // Для Docker:
                                 .before(uri("http://product-service:8082"))
                                // Для IntelliJ:
                                //.before(uri("http://localhost:8082"))
                                .build())

                        // cart + wishlist-service
                        .and(route("cart_service")
                                .route(request -> request.path().startsWith("/api/cart"),
                                        http())
                                // Docker:
                                 .before(uri("http://cart-service:8083"))
                                // IntelliJ:
                                //.before(uri("http://localhost:8083"))
                                .build())

                        .and(route("wishlist_service")
                                .route(request -> request.path().startsWith("/api/wishlist"),
                                        http())
                                // Docker:
                                 .before(uri("http://wishlist-service:8085"))
                                // IntelliJ:
                                //.before(uri("http://localhost:8085"))
                                .build())

                        // orders
                        .and(route("order_service")
                                .route(request -> request.path().startsWith("/api/orders"),
                                        http())
                                // Docker:
                                 .before(uri("http://order-service:8084"))
                                // IntelliJ:
                                //.before(uri("http://localhost:8084"))
                                .build())

                        // notification-service (email)
                        .and(route("notification_service")
                                .route(request -> request.path().startsWith("/api/notifications"),
                                        http())
                                // Docker:
                                 .before(uri("http://notification-service:8086"))
                                // IntelliJ:
                                //.before(uri("http://localhost:8086"))
                                .build());
    }
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:3000")
                        .allowedMethods("*")
                        .allowedHeaders("*");
            }
        };
    }
}
