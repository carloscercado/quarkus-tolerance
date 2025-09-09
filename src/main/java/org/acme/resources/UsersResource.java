package org.acme.resources;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.services.UsersService;

@Path("/external")
public class UsersResource {

    @Inject
    UsersService phoneService;

    @GET
    @Path("/peoples")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsers() {
        return Response.ok(phoneService.getUsers())
                .build();
    }
}
