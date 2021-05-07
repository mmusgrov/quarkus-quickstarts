package org.acme.lra;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;

import javax.ws.rs.core.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class TxnResourceTest {
//    @Test
    public void testSane() {
        given()
        .when()
            .get("/txns/report")
        .then()
            .statusCode(200);
    }

    @Test
    public void testWithTransaction() {
        given()
        .when()
            .post("/txns/tx")
        .then()
            .statusCode(200);
    }

//    @Test
    public void testNoJaxrs() {
        Response r1 = RestAssured.get("http://localhost:8080/non-jaxrs-participant/start-lra");
        Response r2 = RestAssured.get("http://localhost:8080/non-jaxrs-participant/test");
        given()
                .when()
                .get(NonJAXRSParticipant.SIMPLE_PARTICIPANT_RESOURCE_PATH)
                .then()
                .statusCode(200);
    }
}
