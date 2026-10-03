package com.urlplatform.apigateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class GatewayRoutingTest {

    @Autowired
    private RouteLocator routeLocator;

    @Test
    void contextLoads() {
        // Verifies Spring context starts with gateway configuration
    }

    @Test
    void gatewayHasExpectedRoutes() {
        long routeCount = routeLocator.getRoutes()
                .filter(r -> r.getId().startsWith("user-service")
                          || r.getId().startsWith("url-service"))
                .count()
                .block();

        assertThat(routeCount).isEqualTo(3);
    }
}
