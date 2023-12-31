package org.eventservice.hibernate.reactive.api;

import io.quarkus.oidc.UserInfo;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.RolesAllowed;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eventservice.hibernate.reactive.entities.User;
import org.hibernate.reactive.mutiny.Mutiny;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.util.List;

import static jakarta.ws.rs.core.Response.Status.CREATED;

@Path("users")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class UsersApi {
    @Inject
    Mutiny.SessionFactory sf;

    @Inject
    JsonWebToken jwt;

    @GET
    @Path("activeUser")
    public Uni<User> getActiveUser() {
        String preferredUserName = jwt.getClaim("preferred_username");
        return sf.withSession(s ->
                        s.createNamedQuery("Users.findByPreferredUserName", User.class)
                                .setParameter("preferredUserName", preferredUserName)
                                .getSingleResultOrNull()
        );
    }

    @GET
    public Uni<List<User>> list() {
        return sf.withTransaction((s, t) -> s
                // LEFT JOIN FETCH u.role
                .createNamedQuery("Users.findAll", User.class)
                .getResultList()
        );
    }

    @POST
    @RolesAllowed("event_service_admin")
    public Uni<Response> createUser(User user) {
        return sf.withTransaction((s, t) -> s.persist(user))
                .replaceWith(Response.ok(user).status(CREATED)::build);
    }
}
