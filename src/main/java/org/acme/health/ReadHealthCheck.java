package org.acme.health;

import jakarta.inject.Inject;
import org.acme.resources.external.UsersClient;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.URI;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

@Readiness
public class ReadHealthCheck implements HealthCheck {

    @Inject
    @RestClient
    UsersClient usersClient;

    @Override
    public HealthCheckResponse call() {

        try{
            usersClient.getUsers();
        }catch (Exception e){
            return HealthCheckResponse.named("No esta preparado")
                    .withData("userApi", e.getMessage())
                    .down()
                    .build();
        }

        return HealthCheckResponse.named("Esta preparado")
                .withData("userApi", true)
                .up()
                .build();
    }

    public static SSLContext createInsecureSSLContext() throws Exception {
        TrustManager[] trustAll = new TrustManager[]{
                new X509TrustManager() {
                    public void checkClientTrusted(X509Certificate[] chain, String authType) {}
                    public void checkServerTrusted(X509Certificate[] chain, String authType) {}
                    public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                }
        };

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAll, new SecureRandom());
        return sslContext;
    }
}
