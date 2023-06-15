package org.eventservice.hibernate.reactive;

import io.quarkus.mailer.MockMailbox;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jakarta.inject.Inject;
import org.eventservice.hibernate.reactive.common.DateUtils;
import org.eventservice.hibernate.reactive.entities.Event;
import org.eventservice.hibernate.reactive.entities.Role;
import org.eventservice.hibernate.reactive.entities.Subscription;
import org.eventservice.hibernate.reactive.entities.User;
import org.eventservice.hibernate.reactive.service.EventsService;
import org.eventservice.hibernate.reactive.service.MailCheckerService;
import org.eventservice.hibernate.reactive.service.SenderService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.text.ParseException;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@QuarkusTest
//@Disabled
public class AllTests {

    private static String plannedDateTimeAsText = "11/06/1989 12:12:12";
    @Inject
    EventsService eventsService;
    @Inject
    SenderService senderService;
    @Inject
    MockMailbox mailbox;
    @Inject
    MailCheckerService mailCheckerService;

    public static String eventUUID1;
    public static String userUuid = "10f1a89d-193c-4f9b-b420-55c7d2aaf710";
    public static String administratorRoleUuid = "10f1a89d-193c-4f9b-b420-55c7d2aaf708";

    public String adminUserToken = "";

    @Test
    public void performTests() throws Exception {
        getAdminUserToken();

        createUser();

        getActiveUser();

        listAllUsers();

        createEvent();
        listAllEvents();

        createSubscription();
        listAllSubscriptions();

        getEvent();

        modifyEvent();

        checkSenderServiceWork();

        checkIncomingMails();
        //checkNotificationsAfterEventModification();
    }

    private void getAdminUserToken() {
        Response response = given()
                .when()
                .contentType(ContentType.URLENC)
                .param("password",1)
                .param("username","eksi")
                .param("grant_type","password")
                .param("client_id","event-service-ui")
                .post("http://localhost:8080/realms/master/protocol/openid-connect/token")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .extract().response();

        adminUserToken = response.jsonPath().getString("access_token");
    }

    private void checkIncomingMails() throws Exception {
        mailCheckerService.checkMails();
    }

    private void checkSenderServiceWork() {
        //await().atMost(2, SECONDS).until(() -> senderService.getProcessedMessagedCounter() == 1);
        await().atMost(10, SECONDS).until(() -> mailbox.getTotalMessagesSent() > 0);
    }

    public void listAllUsers() {
        Response response = given()
                .when()
                .get("/users")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .extract().response();
        assertThat(response.jsonPath().getList("firstName")).contains("Mikhail");
        assertThat(response.jsonPath().getList("role.name")).contains("Administrator");
    }
    public void getActiveUser() throws ParseException {
        Response response = given()
                .when()
                .header("authorization", "Bearer " + adminUserToken)
                .get("/users/activeUser")
                .then()
                .statusCode(200)
                .extract().response();

        Assertions.assertNotNull(response.jsonPath().get("id"));
    }
    public void createUser() throws ParseException {
        /*SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));*/

        Role role = new Role();
        role.setId(administratorRoleUuid);

        User user = User.builder().build();
        user.setFirstName("Ekaterina");
        user.setLastName("Sidorenkova");
        user.setPatronymic("Sidorovna");
        user.setEmail("k@fifa.com");
        user.setBirthDate(DateUtils.getDateFormat().parse(getBirthDate()));
        user.setPassword("p");
        user.setPhone("1111111111");
        user.setRole(role);

        Response response = given()
                .when()
                .header("authorization", "Bearer " + adminUserToken)
                .body(user)
                .contentType("application/json")
                .post("/users")
                .then()
                .statusCode(201)
                .extract().response();

        Assertions.assertNotNull(response.jsonPath().get("id"));
        Assertions.assertEquals(getBirthDate(), response.jsonPath().get("birthDate"));
    }

    private static String getBirthDate() {
        return "20/04/2001";
    }

    public void createEvent() throws ParseException {
        java.util.Date plannedDateTime = DateUtils.getDateTimeFormat().parse(plannedDateTimeAsText);

        Event e1 = buildEvent(1, plannedDateTime);
        Event e2 = buildEvent(2, plannedDateTime);


        eventUUID1 = createEvent(e1);
        createEvent(e2);
    }

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

    public Event getEvent() {
        Response response = given()
                .when()
                .get("/events/" + eventUUID1)
                .then()
                .statusCode(200)
                .contentType("application/json")
                .extract().response();

        assertThat(response.jsonPath().get("name").equals("Test event 1"));
        // cause of JsonIgnore assertThat(response.jsonPath().getList("subscriptions.id")).containsExactlyInAnyOrder("1");

        return response.as(Event.class);
    }

    private static String createEvent(Event e1) {
        Response response = given()
                .when()
                .body(e1)
                .contentType("application/json")
                .post("/events")
                .then()
                .statusCode(201)
                .extract().response();

        Assertions.assertNotNull(response.jsonPath().get("id"));

        return response.jsonPath().get("id");
    }

    private static Event buildEvent(int id, java.util.Date plannedDateTime) {
        Event e = new Event();
        e.setCreatedAt(new Date(System.currentTimeMillis()));
        e.setPlannedDateTime(plannedDateTime);
        e.setCreator(User.builder().id(userUuid).build());
        e.setName("Test event " + id);
        e.setDescription("Test descr " + id);
        e.setShortDescription("Test short descr " + id);
        e.setStatus("Created");
        return e;
    }


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
    }

    public void createSubscription() {

        Event event = new Event();

        event.setId(eventUUID1);

        Subscription s = Subscription.builder()
                .event(event)
                .user(User.builder().id(userUuid).build())
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
        assertThat(response.jsonPath().getList("event.name")).containsExactlyInAnyOrder("Test event 1");
    }

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

        /*Response notificationResponse = given()
                .when()
                .get("/notifications")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .extract().response();

        System.out.println(notificationResponse.asString());*/

        //Assertions.assertEquals("DESCRIPTION_CHANGED", response.jsonPath().get("notificationType"));

//        assertThat(response.jsonPath().getList("userMessage")).containsExactlyInAnyOrder("Mikhail");
    }
}
