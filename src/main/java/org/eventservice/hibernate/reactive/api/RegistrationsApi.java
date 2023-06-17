package org.eventservice.hibernate.reactive.api;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.eventservice.hibernate.reactive.entities.Registration;
import org.hibernate.reactive.mutiny.Mutiny;

import java.util.List;

import static jakarta.ws.rs.core.Response.Status.CREATED;

@Path("registrations")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class RegistrationsApi {
    @Inject
    Mutiny.SessionFactory sf;

    @GET
    public Uni<List<Registration>> list() {
        return sf.withSession(s -> s
                .createNamedQuery("Registrations.findAll", Registration.class)
                .getResultList()
        );
    }

    @POST
    public Uni<Response> createRegistration(Registration registration) {
        return sf.withTransaction((s, t) -> s.persist(registration))
                .replaceWith(Response.ok(registration).status(CREATED)::build);
    }
}
