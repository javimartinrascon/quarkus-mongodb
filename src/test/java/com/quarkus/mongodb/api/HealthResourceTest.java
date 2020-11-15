package com.quarkus.mongodb.api;

import com.quarkus.mongodb.EmbeddedMongoConfiguration;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;

@QuarkusTest
@QuarkusTestResource(EmbeddedMongoConfiguration.class)
class HealthResourceTest {

    @Test
    public void health() {
        RestAssured.when().get("/health/ready").then()
                .body("status", is("UP"),
                        "checks.data", containsInAnyOrder(hasKey("default")),
                        "checks.status", containsInAnyOrder("UP"),
                        "checks.name", containsInAnyOrder("MongoDB connection health check"));
    }
}