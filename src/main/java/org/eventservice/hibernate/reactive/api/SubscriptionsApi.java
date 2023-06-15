package org.eventservice.hibernate.reactive.api;

import io.smallrye.mutiny.Uni;
import org.eventservice.hibernate.reactive.entities.Subscription;
import org.hibernate.reactive.mutiny.Mutiny;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import java.util.List;

import static jakarta.ws.rs.core.Response.Status.CREATED;

@Path("subscriptions")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class SubscriptionsApi {
    @Inject
    Mutiny.SessionFactory sf;

    @GET
    public Uni<List<Subscription>> list() {
        return sf.withSession(s -> s
                .createNamedQuery("Subscriptions.findAll", Subscription.class)
                .getResultList()
        );
    }

    @POST
    public Uni<Response> createSubscription(Subscription subscription) {
        return sf.withTransaction((s, t) -> s.persist(subscription))
                .replaceWith(Response.ok(subscription).status(CREATED)::build);
    }
}
