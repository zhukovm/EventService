package org.eventservice.hibernate.reactive.api;

import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eventservice.hibernate.reactive.entities.Group;
import org.eventservice.hibernate.reactive.entities.User;
import org.hibernate.reactive.mutiny.Mutiny;

import java.util.List;

import static jakarta.ws.rs.core.Response.Status.CREATED;

@Path("groupes")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class GroupesApi {
    @Inject
    Mutiny.SessionFactory sf;

    @GET
    public Uni<List<User>> list() {
        return sf.withTransaction((s, t) -> s
                .createNamedQuery("Group.findAll", User.class)
                .getResultList()
        );
    }

    @POST
    public Uni<Response> createGroup(Group group) {
        return sf.withTransaction((s, t) -> s.persist(group))
                .replaceWith(Response.ok(group).status(CREATED)::build);
    }
}
