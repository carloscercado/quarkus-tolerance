package org.acme.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.acme.resources.external.UsersClient;
import org.eclipse.microprofile.faulttolerance.exceptions.TimeoutException;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;

import java.util.*;

@ApplicationScoped
public class UsersService {

    @Inject
    @RestClient
    UsersClient userClient;

    @Timeout(3000) // 3 segundos máximo
    @Retry(retryOn = TimeoutException.class, maxRetries = 3, delay = 500, jitter = 25) // reintenta hasta 3 veces con 1 segundo de espera
    @Fallback(fallbackMethod = "getUsersFallback")
    public List<Map<String, Object>> getUsers() {
        return userClient.getUsers();
    }

    public List<Map<String, Object>> getUsersFallback() {

        Map<String, Object> map = new HashMap<>();
        map.put("message", "Servicio externo no disponible, intente más tarde");
        map.put("data", "[]");

        List<Map<String, Object>> list = new ArrayList<>();
        list.add(map);

        throw new WebApplicationException(
                Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(list)
                        .build()
        );
    }
}
