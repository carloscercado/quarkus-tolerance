package org.acme;

import com.github.tomakehurst.wiremock.stubbing.Scenario;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

import org.acme.mocks.UsersApiMock;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.MediaType;


import static org.hamcrest.Matchers.*;

import static io.restassured.RestAssured.given;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

@QuarkusTest
@QuarkusTestResource(UsersApiMock.class)
class UsersResourceTest {

    @Test
    @DisplayName("Validando que devuelva 200 y con array de json sean 10")
    public void testGetusersSuccess() {
        given()
                .when().get("/external/peoples")
                .then()
                .statusCode(200)
                .body("", hasSize(10));
    }

    @Test
    @DisplayName("Validando comportamiento por timeout")
    public void testGetusersTimeout() {
        UsersApiMock.getWireMockServer().stubFor(get(urlEqualTo("/users"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
                        .withFixedDelay(3100)
                        .withBodyFile("users.json")
                        .withStatus(200)));

        given()
                .when().get("/external/peoples")
                .then()
                .statusCode(500)
                .body("message", Matchers.contains("Servicio externo no disponible, intente más tarde"));
    }

    @Test
    @DisplayName("Validando logica de reintentos")
    public void testGetusersRetrySuccess() {
        // Primeras 2 llamadas fallan con 500, la tercera responde bien
        UsersApiMock.getWireMockServer().stubFor(get(urlEqualTo("/users"))
                .inScenario("reintentos")
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(aResponse()
                        .withFixedDelay(3200)
                        .withBodyFile("users.json")
                        .withStatus(200))
                .willSetStateTo("segundo intento"));

        UsersApiMock.getWireMockServer().stubFor(get(urlEqualTo("/users"))
                .inScenario("reintentos")
                .whenScenarioStateIs("segundo intento")
                .willReturn(aResponse()
                        .withFixedDelay(3200)
                        .withBodyFile("users.json")
                        .withStatus(200))
                .willSetStateTo("tercer intento"));

        UsersApiMock.getWireMockServer().stubFor(get(urlEqualTo("/users"))
                .inScenario("reintentos")
                .whenScenarioStateIs("tercer intento")
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
                        .withBodyFile("users.json")
                        .withStatus(200)));

        given()
                .when().get("/external/peoples")
                .then()
                .statusCode(200)
                .body("", hasSize(10));
    }

    @Test
    @DisplayName("Validando logica de de fallback")
    public void testGetusersFallback() {
        UsersApiMock.getWireMockServer().stubFor(get(urlEqualTo("/users"))
                .willReturn(aResponse()
                        .withBody("Internal Server Error")
                        .withStatus(500)));

        given()
                .when().get("/external/peoples")
                .then()
                .statusCode(500)
                .body("message", Matchers.contains("Servicio externo no disponible, intente más tarde"));
    }

}