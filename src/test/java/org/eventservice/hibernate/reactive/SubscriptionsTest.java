package org.eventservice.hibernate.reactive;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import org.eventservice.hibernate.reactive.entities.Event;
import org.eventservice.hibernate.reactive.entities.Subscription;
import org.eventservice.hibernate.reactive.entities.User;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.eventservice.hibernate.reactive.AllTests.eventUUID1;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Order(3)
public class SubscriptionsTest {

/*    @Test
    @Order(1)
    public void createSubscription() {

        Event event = new Event();

        event.setId(eventUUID1);

        Subscription s = Subscription.builder()
                .event(event)
                .user(User.builder().id(1).build())
                .build();


        Response response = given()
                .when()
                .body(s)
                .contentType("application/json")
                .post("/subscriptions")
                .then()
                .statusCode(201)
                .extract().response();

        Assertions.assertNotNull(response.jsonPath().get("id"));
    }

    @Test
    @Order(2)
    public void listAllSubscriptions() {
        Response response = given()
                .when()
                .get("/subscriptions")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .extract().response();

        System.out.println(response.asString());

        Assertions.assertEquals(1, response.jsonPath().getList("id").size());
        assertThat(response.jsonPath().getList("user.firstName")).containsExactlyInAnyOrder("Mikhail");
        assertThat(response.jsonPath().getList("event.name")).containsExactlyInAnyOrder("Test event 1 Modified");
    }*/
}