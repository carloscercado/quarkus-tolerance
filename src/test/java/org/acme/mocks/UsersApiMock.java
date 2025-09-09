package org.acme.mocks;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.junit.jupiter.api.extension.MediaType;

import java.util.Collections;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;

public class UsersApiMock implements QuarkusTestResourceLifecycleManager {

    private static WireMockServer wireMockServer;

    @Override
    public Map<String, String> start() {
        wireMockServer = new WireMockServer(WireMockConfiguration.options().port(9090));
        wireMockServer.start();

        wireMockServer.stubFor(get(urlEqualTo("/users"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
                        .withBodyFile("users.json")
                        .withStatus(200)));

        return Collections.singletonMap("users-api/mp-rest/url", wireMockServer.baseUrl());
    }

    @Override
    public void stop() {

        if(wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    public static WireMockServer getWireMockServer() {
        return wireMockServer;
    }
}
