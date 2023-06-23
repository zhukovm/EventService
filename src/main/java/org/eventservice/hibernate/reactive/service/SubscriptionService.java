package org.eventservice.hibernate.reactive.service;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.eventservice.hibernate.reactive.entities.Event;
import org.eventservice.hibernate.reactive.entities.Subscription;
import org.hibernate.reactive.mutiny.Mutiny;

import java.util.List;

import static jakarta.ws.rs.core.Response.Status.CREATED;

@ApplicationScoped
@Slf4j
public class SubscriptionService {
    @Inject
    Mutiny.SessionFactory sf;

    public Uni<Subscription> getSubscription(String id) {
        return sf.withSession(s -> s.find(Subscription.class, id));
    }

    public Uni<Subscription> findSubscriptionByUserAndEvent(String userUuid, String eventUuid) {
        return sf.withSession(s -> s
                .createNamedQuery("Subscriptions.findSubscriptionByEventAndUser", Subscription.class)
                .setParameter("userUuid", userUuid)
                .setParameter("eventUuid", eventUuid)
                .getSingleResultOrNull()
        );
    }

    public Uni<List<Subscription>> list() {
        return sf.withSession(s -> s
                .createNamedQuery("Subscriptions.findAll", Subscription.class)
                .getResultList()
        );
    }

    public Uni<Void> createSubscription(Subscription subscription) {
        return sf.withTransaction((s, t) -> s.persist(subscription));
    }
}
