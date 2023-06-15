package org.eventservice.hibernate.reactive.service;

import io.smallrye.mutiny.Uni;
import org.eventservice.hibernate.reactive.common.DateUtils;
import org.eventservice.hibernate.reactive.entities.Event;
import org.eventservice.hibernate.reactive.entities.Notification;
import org.eventservice.hibernate.reactive.enums.NotificationStatus;
import org.eventservice.hibernate.reactive.enums.NotificationType;
import org.hibernate.reactive.mutiny.Mutiny;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class NotificationsService {
    @Inject
    Mutiny.SessionFactory sf;

    @Inject
    EventsService eventsService;

    public Uni<List<Notification>> list() {
        return sf.withTransaction((s, t) -> s
                .createNamedQuery("Notifications.findAll", Notification.class)
                .getResultList()
        );
    }

    public Uni<List<Notification>> generateNotifications(Event event, NotificationType notificationType) {
        return eventsService.get(event.getId())
                .map(e -> e.getSubscriptions().stream().map(s ->
                        Notification.builder()
                                .notificationType(notificationType)
                                .status(NotificationStatus.CREATED)
                                .subscription(s)
                                .createdAt(DateUtils.getCurrentUtcDate())
                                .userMessage(buildNotificationMessage(e, notificationType))
                                .build()
                ).collect(Collectors.toList()));
    }

    private String buildNotificationMessage(Event event, NotificationType notificationType) {
        switch (notificationType) {
            case EVENT_CREATED:
                return event.getName() + " was created";
            case ASSIGNED_DATE_CHANGED:
                return event.getName() + " was re-scheduled to " + event.getPlannedDateTime();
            case DESCRIPTION_CHANGED:
                return event.getName() + " has new description: " + event.getDescription();
            case EVENT_CANCELLED:
                return event.getName() + " was cancelled";
        }
        return "default message";
    }
}
