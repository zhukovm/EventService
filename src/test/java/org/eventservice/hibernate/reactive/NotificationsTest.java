package org.eventservice.hibernate.reactive;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import org.eventservice.hibernate.reactive.entities.Event;
import org.eventservice.hibernate.reactive.service.EventsService;
import org.junit.jupiter.api.*;

import jakarta.inject.Inject;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.eventservice.hibernate.reactive.AllTests.eventUUID1;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Order(4)
public class NotificationsTest {

 /*   @Inject
    EventsService eventsService;
    @Test
    @Order(1)
    public void checkNotificationsAfterEventModification() {
        String newStatus = "Test event 1 Modified to trigger notification";

        Event eventForModification = eventsService.get(eventUUID1).await().indefinitely();

        eventForModification.setName(newStatus);

        Response response = given()
                .when()
                .body(eventForModification)
                .contentType("application/json")
                .put("/events")
                .then()
                .statusCode(200)
                .extract().response();

        assertThat(response.jsonPath().get("name").equals(newStatus));

        *//*Response notificationResponse = given()
                .when()
                .get("/notifications")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .extract().response();

        System.out.println(notificationResponse.asString());*//*

        //Assertions.assertEquals("DESCRIPTION_CHANGED", response.jsonPath().get("notificationType"));

//        assertThat(response.jsonPath().getList("userMessage")).containsExactlyInAnyOrder("Mikhail");
    }

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

    }*/

}
