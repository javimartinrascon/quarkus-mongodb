package com.quarkus.mongodb.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quarkus.mongodb.EmbeddedMongoConfiguration;
import com.quarkus.mongodb.model.Address;
import com.quarkus.mongodb.model.User;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.MediaType;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.containsInAnyOrder;

@QuarkusTest
@QuarkusTestResource(EmbeddedMongoConfiguration.class)
class UserResourceTest {

    ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void list() throws Exception {

        createResources();

        RestAssured
                .given()
                .header("Content-Type", MediaType.APPLICATION_JSON)
                .when()
                .get("/users")
                .then()
                .statusCode(200)
                .body("", hasSize(2),
                        "[0].id", is(notNullValue()),
                        "[0].name", is("Name1"),
                        "[0].surname", is("Surname1"),
                        "[0].birthDate", is("01/01/1970"),
                        "[0].address", is(notNullValue()),
                        "[0].address.type", is("St."),
                        "[0].address.streetAddress", is("Street, 1"),
                        "[0].address.city", is("City"),
                        "[0].address.state", is("State"),
                        "[0].address.zipCode", is("12345"),
                        "[1].id", is(notNullValue()),
                        "[1].name", is("Name2"),
                        "[1].surname", is("Surname2"),
                        "[1].birthDate", is("02/02/1970"),
                        "[1].address", is(notNullValue()),
                        "[1].address.type", is("St."),
                        "[1].address.streetAddress", is("Street, 2"),
                        "[1].address.city", is("City"),
                        "[1].address.state", is("State"),
                        "[1].address.zipCode", is("12345"));
    }

    @Test
    void add() throws Exception {

        Address userAddress = new Address("St.", "Street, 1", "City", "State", "12345");
        User user = new User("Name", "Surname", "01/01/1970", userAddress);

        Response response = RestAssured
                .given()
                .header("Content-Type", MediaType.APPLICATION_JSON)
                .body(mapper.writeValueAsString(user))
                .post("/users")
                .andReturn();

        assertThat(response, is(notNullValue()));
        assertThat(response.statusCode(), is(202));
        User savedUser = mapper.readValue(response.getBody().print(), User.class);
        assertThat(savedUser.getId(), is(notNullValue()));
        assertThat(savedUser.getName(), is("Name"));
        assertThat(savedUser.getSurname(), is("Surname"));
        assertThat(savedUser.getBirthDate(), is("01/01/1970"));
        assertThat(savedUser.getAddress().getType(), is("St."));
        assertThat(savedUser.getAddress().getStreetAddress(), is("Street, 1"));
        assertThat(savedUser.getAddress().getCity(), is("City"));
        assertThat(savedUser.getAddress().getState(), is("State"));
        assertThat(savedUser.getAddress().getZipCode(), is("12345"));
    }

    @Test
    void findById() throws Exception {

        String userId = getStoredUserId();

        Response response = RestAssured
                .given()
                .header("Content-Type", MediaType.APPLICATION_JSON)
                .get("/users/" + userId)
                .andReturn();

        assertThat(response, is(notNullValue()));
        assertThat(response.getStatusCode(), is(200));
        User fetchedUser = mapper.readValue(response.getBody().print(), User.class);
        assertThat(fetchedUser.getId(), is(notNullValue()));
    }

    @Test
    public void health() {
        RestAssured.when().get("/health/ready").then()
                .body("status", is("UP"),
                        "checks.data", containsInAnyOrder(hasKey("default")),
                        "checks.status", containsInAnyOrder("UP"),
                        "checks.name", containsInAnyOrder("MongoDB connection health check"));
    }

    private void createResources() throws JsonProcessingException {

        Address userAddress1 = new Address("St.", "Street, 1", "City", "State", "12345");
        User user1 = new User("Name1", "Surname1", "01/01/1970", userAddress1);

        Response responsePost = RestAssured
                .given()
                .header("Content-Type", MediaType.APPLICATION_JSON)
                .body(mapper.writeValueAsString(user1))
                .post("/users")
                .andReturn();
        assertThat(responsePost, is(notNullValue()));
        assertThat(responsePost.statusCode(), is(202));

        Address userAddress2 = new Address("St.", "Street, 2", "City", "State", "12345");
        User user2 = new User("Name2", "Surname2", "02/02/1970", userAddress2);
        responsePost = RestAssured
                .given()
                .header("Content-Type", MediaType.APPLICATION_JSON)
                .body(mapper.writeValueAsString(user2))
                .post("/users")
                .andReturn();

        assertThat(responsePost, is(notNullValue()));
        assertThat(responsePost.statusCode(), is(202));
    }

    private String getStoredUserId() throws JsonProcessingException {
        Address userAddress = new Address("St.", "Street, 1", "City", "State", "12345");
        User user = new User("Name", "Surname", "01/01/1970", userAddress);

        Response responsePost = RestAssured
                .given()
                .header("Content-Type", MediaType.APPLICATION_JSON)
                .body(mapper.writeValueAsString(user))
                .post("/users")
                .andReturn();

        assertThat(responsePost.statusCode(), is(202));

        User savedUser = mapper.readValue(responsePost.getBody().print(), User.class);
        assertThat(savedUser, is(notNullValue()));

        String userId = savedUser.getId();
        assertThat(userId, is(notNullValue()));

        return userId;
    }

}