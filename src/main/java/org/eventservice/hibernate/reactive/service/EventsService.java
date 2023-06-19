package org.eventservice.hibernate.reactive.service;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eventservice.hibernate.reactive.entities.Event;
import org.eventservice.hibernate.reactive.entities.UUIDContainer;
import org.eventservice.hibernate.reactive.enums.NotificationType;
import org.hibernate.reactive.mutiny.Mutiny;

import java.util.List;

@ApplicationScoped
@Slf4j
public class EventsService {
    @Inject
    NotificationsService notificationsService;
    @Inject
    Mutiny.SessionFactory sf;

    public Uni<Event> getEvent(String id) {
        return sf.withSession(s -> s.find(Event.class, id)
                .call(e -> s.fetch(e.getSubscriptions()))
                .call(e -> s.fetch(e.getRegistrations()))
                .call(e -> s.fetch(e.getComments()))
                .call(e -> s.fetch(e.getCreator()))
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
                .flatMap(e -> getEvent(event.getId()))
                .flatMap(e -> notificationsService.generateNotificationsForSubscriptions(event, NotificationType.EVENT_CREATED))
                .flatMap(notifications -> s.persistAll(notifications.toArray()))
        );
        /*.replaceWith(sf.withSession(s-> s.refresh(Event.builder().id(event.getId()).build())));*/
    }

    public Uni<Event> modifyEvent(Event event) {
        log.info("[EVENT] modify with " + event);
        return sf.withTransaction((s, t) -> s.merge(event)
                .call(e -> s.refresh(e))
                .call(e -> notificationsService.generateNotificationsForSubscriptions(e, NotificationType.DESCRIPTION_CHANGED)
                        .flatMap(notifications -> s.persistAll(notifications.toArray()))
                ))
                ;
    }

    public Uni<Void> registrationCheck(UUIDContainer eventDescription) {
        return sf.withTransaction((s, t) ->
                getEvent(eventDescription.getUuid())
                        .call(e -> s.refresh(e))
                        .call(e -> notificationsService.generateNotificationsForRegistrations(e, NotificationType.REGISTRATION_CHECK)
                                .flatMap(notifications -> s.persistAll(notifications.toArray()))
                        )).flatMap(event -> Uni.createFrom().voidItem());
    }
}
