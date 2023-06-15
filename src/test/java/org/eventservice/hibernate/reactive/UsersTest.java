package org.eventservice.hibernate.reactive;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import org.eventservice.hibernate.reactive.entities.Role;
import org.eventservice.hibernate.reactive.entities.User;
import org.junit.jupiter.api.*;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Order(1)
public class UsersTest {
 /*   @Test
    public void listAllUsers() {
        //List all, should have all 3 fruits the database has initially:
        Response response = given()
                .when()
                .get("/users")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .extract().response();
        assertThat(response.jsonPath().getList("firstName")).containsExactlyInAnyOrder("Mikhail");
        assertThat(response.jsonPath().getList("role.name")).containsExactlyInAnyOrder("Administrator");
    }

    @Test
    public void createUser() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        Role role = new Role();
        role.setId(1);

        User user = User.builder().build();
        user.setFirstName("Katya");
        user.setLastName("Sidorenkova");
        user.setPatronymic("Sidorovna");
        user.setEmail("k@fifa.com");
        user.setBirthDate(new Date(sdf.parse("20/04/2001").getTime()));
        user.setPassword("p");
        user.setPhone("1111111111");
        user.setRole(role);

        Response response = given()
                .when()
                .body(user)
                .contentType("application/json")
                .post("/users")
                .then()
                .statusCode(201)
                .extract().response();

        Assertions.assertNotNull(response.jsonPath().get("id"));
        Assertions.assertEquals("20/04/2001", response.jsonPath().get("birthDate"));
    }*/
}