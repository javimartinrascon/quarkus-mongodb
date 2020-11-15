package com.quarkus.mongodb.api;

import com.quarkus.mongodb.model.User;
import com.quarkus.mongodb.repository.UserRepository;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Objects;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    UserRepository userRepository;

    @POST
    public Response add(User user) {
        userRepository.add(user);

        return Response.accepted(user).build();
    }

    @GET
    public Response list() {
        return Response.ok(userRepository.list()).build();
    }

    @GET
    @Path("/{userId}")
    public Response fetchById(@PathParam("userId") String userId) {
        User user = userRepository.findById(userId);
        if (Objects.isNull(user)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(user).build();
    }

    @DELETE
    @Path("/{userId}")
    public Response delete(@PathParam("userId") String userId) {
        if (userRepository.delete(userId) > 0) {
            return Response.ok().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
