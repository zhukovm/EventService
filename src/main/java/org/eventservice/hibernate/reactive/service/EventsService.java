package org.eventservice.hibernate.reactive.service;

import io.smallrye.mutiny.Uni;
import lombok.extern.slf4j.Slf4j;
import org.eventservice.hibernate.reactive.entities.Event;
import org.eventservice.hibernate.reactive.enums.NotificationType;
import org.hibernate.reactive.mutiny.Mutiny;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
@Slf4j
public class EventsService {
    @Inject
    NotificationsService notificationsService;
    @Inject
    Mutiny.SessionFactory sf;

    public Uni<Event> get(String id) {
        return sf.withSession(s -> s.find(Event.class, id)
                .call(e -> s.fetch(e.getSubscriptions()))
        );
    }

    public Uni<List<Event>> listEvents() {
        return sf.withTransaction((s, t) -> s
                .createNamedQuery("Events.findAll", Event.class)
                .getResultList()
        );
    }

    public Uni<Void> createEvent(Event event) {
        return sf.withTransaction((s, t) -> s.persist(event)
                        .flatMap(e -> get(event.getId()))
                        .flatMap(e -> notificationsService.generateNotifications(event, NotificationType.EVENT_CREATED))
                        .flatMap(notifications -> s.persistAll(notifications.toArray()))
        );
    }

    public Uni<Event> modifyEvent(Event event) {
        log.info("[EVENT] modify with " + event);
        return sf.withTransaction((s, t) -> s.merge(event)
                .call(e -> s.refresh(e))
                .call(e -> notificationsService.generateNotifications(e, NotificationType.DESCRIPTION_CHANGED)
                        .flatMap(notifications -> s.persistAll(notifications.toArray()))
                ))
                ;
    }
}
