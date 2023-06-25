package org.eventservice.hibernate.reactive;

import io.quarkus.mailer.MockMailbox;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jakarta.inject.Inject;
import org.eventservice.hibernate.reactive.common.DateUtils;
import org.eventservice.hibernate.reactive.entities.*;
import org.eventservice.hibernate.reactive.enums.EventStatus;
import org.eventservice.hibernate.reactive.enums.NotificationStatus;
import org.eventservice.hibernate.reactive.service.EventsService;
import org.eventservice.hibernate.reactive.service.MailCheckerService;
import org.eventservice.hibernate.reactive.service.SenderService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.text.ParseException;
import java.util.Arrays;

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

    private static final int secsTimeout = 65;

    public static String eventUUID1;
    public static String groupUUID1;
    public static String groupTypeUUID1;
    public static String userUuid = "10f1a89d-193c-4f9b-b420-55c7d2aaf710";
    public static String userUuid2;
    //public static String administratorRoleUuid = "10f1a89d-193c-4f9b-b420-55c7d2aaf708";

    public String adminUserToken = "";

    @Test
    public void performTests() throws Exception {
        getAdminUserToken();

        createUser();

        getActiveUser();

        listAllUsers();

        createGroupType();
        createGroup();

        createSubscriptionForGroup();

        createEvent();
        listAllEvents();

        createComments();
        listAllCommentsByEventUuid();

        createSubscriptionForEvent();
        createSubscriptionForGroupType();

        listAllSubscriptions();

        createRegistrationWithExistingSubscription();
        createRegistrationWithoutSubscription();
        listAllRegistrations();

        getEvent();

        modifyEvent();

        checkSenderServiceWork();

        registrationCheck();

        checkNotifications();

        checkIncomingMailsWithRegistrationsConfirmed();


        //todo
        // 1. проверка готовности-done
        // 2. комменты done
        // 3. Корректные нотификации

        //checkNotificationsAfterEventModification();
    }




    private void createComments() {
        Comment c = Comment.builder()
                .event(Event.builder().id(eventUUID1).build())
                .createdAt(new Date(System.currentTimeMillis()))
                .text("text1")
                .build();
        Comment c2 = Comment.builder()
                .event(Event.builder().id(eventUUID1).build())
                .createdAt(new Date(System.currentTimeMillis()))
                .text("text2")
                .build();

        createAndCheckComment(c);
        createAndCheckComment(c2);
    }

    private static void createAndCheckComment(Comment c) {
        Response response = given()
                .when()
                .body(c)
                .contentType("application/json")
                .post("/comments")
                .then()
                .statusCode(201)
                .extract().response();

        Assertions.assertNotNull(response.jsonPath().get("id"));
        Assertions.assertNotNull(response.jsonPath().get("event.id"));
    }

    private void listAllCommentsByEventUuid() {
        Response response = given()
                .when()
                .contentType("application/json")
                .param("event.uuid", eventUUID1)
                .get("/comments")
                .then()
                .statusCode(200)
                .extract().response();

        Assertions.assertEquals(2, response.jsonPath().getList("id").size());
        Assertions.assertEquals(2, response.jsonPath().getList("event.name").size());
    }

    private void checkNotifications() {
        await().atMost(secsTimeout, SECONDS).until(() ->
                getCheckNotifiactionsResponse()
                        .jsonPath()
                        .getList("status")
                        .equals(Arrays.asList("SENT", "SENT"))
        );

        Response response = getCheckNotifiactionsResponse();

        Assertions.assertEquals(2, response.jsonPath().getList("id").size());
        assertThat(response.jsonPath().getList("status")).containsExactly(NotificationStatus.SENT.toString(), NotificationStatus.SENT.toString());

        /*
        Condition<Object> emptyRowCondition = new Condition<>(cs -> StringUtils.isNotEmpty(cs.toString()), "is empty!");

        assertThat(response.jsonPath().getList("registration.name")).areNot(emptyRowCondition);
        assertThat(response.jsonPath().getList("subscription.name")).areNot(emptyRowCondition);
        */
    }

    private static Response getCheckNotifiactionsResponse() {
        Response response = given()
                .when()
                .get("/notifications")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .extract().response();
        return response;
    }

    private static Response getCheckRegistrationsResponse() {
        Response response = given()
                .when()
                .get("/registrations")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .extract().response();
        return response;
    }

    private void registrationCheck() {
        UUIDContainer eventUUID = UUIDContainer.builder()
                .uuid(eventUUID1)
                .build();

        Response response = given()
                .when()
                .body(eventUUID)
                .contentType("application/json")
                .post("/events/registrationCheck")
                .then()
                .statusCode(200)
                .extract().response();
    }

    private void createRegistrationWithExistingSubscription() {
        createRegistrationAndCheckForUser(userUuid);
    }

    private void createRegistrationWithoutSubscription() {
        createRegistrationAndCheckForUser(userUuid2);
    }

    private static void createRegistrationAndCheckForUser(String userUuid) {
        Event event = new Event();

        event.setId(eventUUID1);

        Registration r = Registration.builder()
                .event(event)
                .user(User.builder().id(userUuid).build())
                .build();


        Response response = given()
                .when()
                .body(r)
                .contentType("application/json")
                .post("/registrations")
                .then()
                .statusCode(201)
                .extract().response();

        Assertions.assertNotNull(response.jsonPath().get("id"));
    }

    private void listAllRegistrations() {
        Response response = given()
                .when()
                .get("/registrations")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .extract().response();

        System.out.println(response.asString());

        Assertions.assertEquals(2, response.jsonPath().getList("id").size());
        assertThat(response.jsonPath().getList("user.firstName")).contains("EventServiceUser");
        assertThat(response.jsonPath().getList("event.name")).contains("Test event 1");
    }

    private void createGroupType() {
        GroupType gt = GroupType.builder()
                .name("Learning")
                .build();

        Response response = given()
                .when()
                .header("authorization", "Bearer " + adminUserToken)
                .body(gt)
                .contentType("application/json")
                .post("/grouptype")
                .then()
                .statusCode(201)
                .extract().response();

        groupTypeUUID1 = response.jsonPath().get("id");
        Assertions.assertNotNull(groupTypeUUID1);
    }

    private void createGroup() {
        Group g = Group.builder()
                .groupType(GroupType.builder().id(groupTypeUUID1).build())
                .name("English lovers")
                .shortDescription("We love to learn English!!!")
                .description("Welcome to EnglishClub, a free website to help you learn (and teach) English. It's your club, where you can:\n" +
                        "\n" +
                        "make your own English page with blogs, photos, videos, music, groups and friends\n" +
                        "test your level in English and get help with English grammar\n" +
                        "study English grammar, vocabulary and pronunciation\n" +
                        "play English games and do English quizzes online\n" +
                        "chat in English with other students and teachers\n" +
                        "find schools where you can learn English at home or abroad\n" +
                        "EnglishClub is divided into various main sections that you can navigate easily.")
                .build();

        Response response = given()
                .when()
                .header("authorization", "Bearer " + adminUserToken)
                .body(g)
                .contentType("application/json")
                .post("/groupes")
                .then()
                .statusCode(201)
                .extract().response();

        groupUUID1 = response.jsonPath().get("id");
        Assertions.assertNotNull(groupUUID1);
    }

    private void getAdminUserToken() {
        Response response = given()
                .when()
                .contentType(ContentType.URLENC)
                .param("password", 1)
                .param("username", "eksi")
                .param("grant_type", "password")
                .param("client_id", "event-service-ui")
                .post("http://localhost:8080/realms/master/protocol/openid-connect/token")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .extract().response();

        adminUserToken = response.jsonPath().getString("access_token");
    }

    private void checkIncomingMailsWithRegistrationsConfirmed() throws Exception {
        await().atMost(1500, SECONDS).until(() ->
                getCheckRegistrationsResponse()
                        .jsonPath()
                        .getList("confirmed")
                        .equals(Arrays.asList(true))
        );
    }

    private void checkSenderServiceWork() {
        if (mailCheckerService.isMockingEnabled()) {
            //await().atMost(2, SECONDS).until(() -> senderService.getProcessedMessagedCounter() == 1);
            await().atMost(secsTimeout, SECONDS).until(() -> mailbox.getTotalMessagesSent() > 0);
        }
    }

    public void listAllUsers() {
        Response response = given()
                .when()
                .get("/users")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .extract().response();
        assertThat(response.jsonPath().getList("firstName")).contains("EventServiceUser");
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
/*        Role role = new Role();
        role.setId(administratorRoleUuid);*/

        User user = User.builder().build();
        user.setFirstName("Ekaterina");
        user.setLastName("Sidorenkova");
        user.setPatronymic("Sidorovna");
        user.setEmail("k@fifa.com");
        user.setBirthDate(DateUtils.getDateFormat().parse(getBirthDate()));
        //user.setPassword("p");
        user.setPhone("1111111111");
        //user.setRole(role);
        user.setPreferredUserName("eksi");

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
        userUuid2 = response.jsonPath().get("id");
    }

    private static String getBirthDate() {
        return "20/04/2001";
    }

    public void createEvent() throws ParseException {
        java.util.Date plannedDateTime = DateUtils.getDateTimeFormat().parse(plannedDateTimeAsText);

        Event e1 = buildEvent(1, groupUUID1, plannedDateTime);
        Event e2 = buildEvent(2, groupUUID1, plannedDateTime);


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
        assertThat(response.jsonPath().getList("creator.id")).containsOnly(userUuid);
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
//        assertThat(response.jsonPath().getList("subscriptions")).isNotEmpty();

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
        Assertions.assertNotNull(response.jsonPath().get("group.id"));
        Assertions.assertNotNull(response.jsonPath().get("creator.id"));
        Assertions.assertEquals(false, Boolean.valueOf(response.jsonPath().get("isConfirmedByAdministrator")));

        return response.jsonPath().get("id");
    }

    private static Event buildEvent(int id, String groupId, java.util.Date plannedDateTime) {
        Event e = new Event();
        e.setCreatedAt(new Date(System.currentTimeMillis()));
        e.setPlannedDateTime(plannedDateTime);
        e.setCreator(User.builder().id(userUuid).build());
        e.setName("Test event " + id);
        e.setDescription("Test descr " + id);
        e.setShortDescription("Test short descr " + id);
        e.setStatus(EventStatus.OPEN);
        e.setGroup(Group.builder()
                .id(groupId)
                .build());
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

    public void createSubscriptionForEvent() {
        Event event = new Event();

        event.setId(eventUUID1);

        Subscription s = Subscription.builder()
                .event(event)
                .user(User.builder().id(userUuid).build())
                .build();

        postAndCheckSubscription(s);
    }

    private static void postAndCheckSubscription(Subscription s) {
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

    private void createSubscriptionForGroupType() {
        GroupType groupType = new GroupType();
        groupType.setId(groupTypeUUID1);

        Subscription s = Subscription.builder()
                .groupType(groupType)
                .user(User.builder().id(userUuid).build())
                .build();

        postAndCheckSubscription(s);
    }

    private void createSubscriptionForGroup() {
        Group group = new Group();
        group.setId(groupUUID1);

        Subscription s = Subscription.builder()
                .group(group)
                .user(User.builder().id(userUuid).build())
                .build();

        postAndCheckSubscription(s);
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

        Assertions.assertEquals(3, response.jsonPath().getList("id").size());
        assertThat(response.jsonPath().getList("user.firstName")).contains("EventServiceUser");
        assertThat(response.jsonPath().getList("event.name")).contains("Test event 1");
    }

    public void checkNotificationsAfterEventModification() {
        String newStatus = "Test event 1 Modified to trigger notification";

        Event eventForModification = eventsService.getEvent(eventUUID1).await().indefinitely();

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
