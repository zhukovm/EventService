package org.eventservice.hibernate.reactive;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import org.eventservice.hibernate.reactive.entities.Event;
import org.eventservice.hibernate.reactive.entities.User;
import org.junit.jupiter.api.*;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;


@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Order(2)
public class EventsTest {

    private static String plannedDateTimeAsText = "11/06/1989 12:12:12";

 /*   @Test
    @Order(1)
    public void createEvent() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        java.util.Date plannedDateTime = sdf.parse(plannedDateTimeAsText);

        Event e1 = buildEvent(1, plannedDateTime);
        Event e2 = buildEvent(2, plannedDateTime);


        createEvent(e1);
        createEvent(e2);
    }

    private static void createEvent(Event e1) {
        Response response = given()
                .when()
                .body(e1)
                .contentType("application/json")
                .post("/events")
                .then()
                .statusCode(201)
                .extract().response();

        Assertions.assertNotNull(response.jsonPath().get("id"));
    }

    private static Event buildEvent(int id, java.util.Date plannedDateTime) {
        Event e = new Event();
        e.setCreatedAt(new Date(System.currentTimeMillis()));
        e.setPlannedDateTime(plannedDateTime);
        e.setCreator(User.builder().id(1).build());
        e.setName("Test event " + id);
        e.setDescription("Test descr " + id);
        e.setShortDescription("Test short descr " + id);
        e.setStatus("Created");
        return e;
    }

    @Test
    @Order(2)
    public void listAllEvents() {
        Response response = given()
                .when()
                .get("/events")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .extract().response();

        Assertions.assertEquals(2, response.jsonPath().getList("id").size());
        assertThat(response.jsonPath().getList("name")).containsExactlyInAnyOrder("Test event 1", "Test event 2");
        assertThat(response.jsonPath().getList("plannedDateTime")).contains(plannedDateTimeAsText);
        // assertThat(response.jsonPath().getList("subscriptions.event.name")).containsExactlyInAnyOrder(plannedDateTimeAsText);
    }

    @Test
    @Order(3)
    public Event getEvent() {
        Response response = given()
                .when()
                .get("/events/1")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .extract().response();

        assertThat(response.jsonPath().get("name").equals("Test event 1"));

        return response.as(Event.class);
    }

    @Test
    @Order(4)
    public void modifyEvent() {
        String newStatus = "Test event 1 Modified";

        Event eventForModification = getEvent();
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
    }*/

}