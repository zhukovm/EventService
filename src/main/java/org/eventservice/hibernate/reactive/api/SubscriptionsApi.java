package org.eventservice.hibernate.reactive.api;

import io.smallrye.mutiny.Uni;
import org.eventservice.hibernate.reactive.entities.Event;
import org.eventservice.hibernate.reactive.entities.Subscription;
import org.eventservice.hibernate.reactive.service.SubscriptionService;
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
    SubscriptionService subscriptionService;

    @Path("{id}")
    public Uni<Subscription> get(String id) {
        return subscriptionService.getSubscription(id);
    }

    @GET
    public Uni<List<Subscription>> list() {
        return subscriptionService.list();
    }

    @POST
    public Uni<Response> createSubscription(Subscription subscription) {
        return subscriptionService.createSubscription(subscription)
                .replaceWith(Response.ok(subscription).status(CREATED)::build);
    }
}
