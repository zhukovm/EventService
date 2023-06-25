package org.eventservice.hibernate.reactive.api;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.eventservice.hibernate.reactive.entities.Registration;
import org.eventservice.hibernate.reactive.entities.Subscription;
import org.eventservice.hibernate.reactive.service.SubscriptionService;
import org.hibernate.reactive.mutiny.Mutiny;

import java.util.List;

import static jakarta.ws.rs.core.Response.Status.CREATED;

@Path("registrations")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
@Slf4j
public class RegistrationsApi {
    @Inject
    Mutiny.SessionFactory sf;

    @Inject
    SubscriptionService subscriptionService;

    @GET
    @Path("{id}")
    public Uni<Registration> get(String id) {
        return sf.withTransaction((s, t) -> s.find(Registration.class, id));
    }

    @GET
    public Uni<List<Registration>> list() {
        return sf.withSession(s -> s
                .createNamedQuery("Registrations.findAll", Registration.class)
                .getResultList()
        );
    }

    @POST
    public Uni<Response> createRegistration(Registration registration) {
        Uni<Subscription> subscriptionUni = subscriptionService.findSubscriptionByUserAndEvent(registration.getUser().getId(), registration.getEvent().getId());

        return sf.withTransaction(
                (s, t) -> subscriptionUni.flatMap(subscription -> {
                            Uni<Void> registrationUni = s.persist(registration);
                            Uni<Void> createSubscriptionUni;
                            if (subscription == null) {
                                log.debug("subscription was not found");
                                createSubscriptionUni = subscriptionService.createSubscription(Subscription.builder()
                                        .event(registration.getEvent())
                                        .user(registration.getUser())
                                        .build());
                            } else {
                                log.debug("subscription found " + subscription);

                                createSubscriptionUni = Uni.createFrom().nullItem();
                            }
                            return Uni.combine().all().unis(registrationUni, createSubscriptionUni).discardItems();
                        }
                )
        ).replaceWith(Response.ok(registration).status(CREATED)::build);
    }
}
