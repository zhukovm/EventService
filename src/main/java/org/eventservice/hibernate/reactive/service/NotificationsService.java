package org.eventservice.hibernate.reactive.service;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eventservice.hibernate.reactive.common.DateUtils;
import org.eventservice.hibernate.reactive.entities.Event;
import org.eventservice.hibernate.reactive.entities.Notification;
import org.eventservice.hibernate.reactive.entities.Subscription;
import org.eventservice.hibernate.reactive.enums.NotificationStatus;
import org.eventservice.hibernate.reactive.enums.NotificationType;
import org.hibernate.reactive.mutiny.Mutiny;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class NotificationsService {
    public static String REGISTRATION_CHECK_IDENTIFYING_EXPRESSION = "Please confirm presence at";

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

    public Uni<List<Notification>> generateNotificationsForSubscriptions(Event event, NotificationType notificationType) {
        return eventsService.getEvent(event.getId())
                .map(e -> {
                    List<Notification> eventSubscriptionNotifications = generateCreateNotificationsForSubscriptions(notificationType, e, e.getSubscriptions());
                    List<Notification> eventGroupNotifications = generateCreateNotificationsForSubscriptions(notificationType, e, e.getGroup().getSubscriptions());

                    Set<Notification> result = new HashSet<Notification>(eventSubscriptionNotifications);
                    result.addAll(eventGroupNotifications);

                    return result.stream().toList();
                });
    }

    private List<Notification> generateCreateNotificationsForSubscriptions(NotificationType notificationType, Event e, List<Subscription> subscriptions) {
        return subscriptions.stream()
                .map(s -> Notification.builder()
                        .notificationType(notificationType)
                        .status(NotificationStatus.CREATED)
                        .subscription(s)
                        .createdAt(DateUtils.getCurrentUtcDate())
                        .userMessage(buildNotificationMessage(e, notificationType))
                        .build()
                ).collect(Collectors.toList());
    }

    private Notification buildCreateEventNotification(NotificationType notificationType, Event e, Subscription s) {
        return Notification.builder()
                .notificationType(notificationType)
                .status(NotificationStatus.CREATED)
                .subscription(s)
                .createdAt(DateUtils.getCurrentUtcDate())
                .userMessage(buildNotificationMessage(e, notificationType))
                .build();
    }

    public Uni<List<Notification>> generateNotificationsForRegistrations(Event event, NotificationType notificationType) {
        return eventsService.getEvent(event.getId())
                .map(e -> e.getRegistrations().stream().map(registration ->
                        Notification.builder()
                                .notificationType(notificationType)
                                .status(NotificationStatus.CREATED)
                                .registration(registration)
                                .createdAt(DateUtils.getCurrentUtcDate())
                                .userMessage(buildNotificationMessage(e, notificationType))
                                .build()
                ).collect(Collectors.toList()));
    }

    public Uni<Void> changeStatusToDelivered(Uni<List<Notification>> sentNotifications) {
        return sentNotifications.map(notifications ->
                        notifications.stream().map(n -> {
                            n.setStatus(NotificationStatus.SENT);
                            return n;
                        }).collect(Collectors.toList()))
                .flatMap(notifications ->
                        sf.withTransaction((s, t) -> s.mergeAll(notifications.toArray()))
                );
    }

    private String buildNotificationMessage(Event event, NotificationType notificationType) {
        switch (notificationType) {
            case REGISTRATION_CHECK:
                return String.format(REGISTRATION_CHECK_IDENTIFYING_EXPRESSION + " %s, held at %s \n the valid answer is \"+\" in reply to this mail\n uuid:\"%s\"", event.getName(), event.getPlannedDateTime(), event.getId());
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
