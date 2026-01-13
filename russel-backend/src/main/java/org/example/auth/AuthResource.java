package org.example.auth;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {

    @POST
    @Path("/login")
    public Response login(LoginRequest req) {

        String hash = PasswordUtil.hash(req.password());

        Integer userId = UserDAO.login(req.email(), hash);
        if (userId == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        return Response.ok(new LoginResponse(userId)).build();
    }

    @POST
    @Path("/register")
    public Response register(LoginRequest req) {

        if (UserDAO.emailExists(req.email())) {
            return Response.status(Response.Status.CONFLICT).build();
        }

        String hash = PasswordUtil.hash(req.password());
        UserDAO.register(req.email(), hash);

        Integer userId = UserDAO.login(req.email(), hash);
        return Response.ok(new LoginResponse(userId)).build();
    }
}
