package org.acme.resources.external;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;
import java.util.Map;

@Path("/users")
@RegisterRestClient(configKey = "users-api")
@Produces(MediaType.APPLICATION_JSON)
public interface UsersClient {

    @GET
    List<Map<String, Object>> getUsers();

}
