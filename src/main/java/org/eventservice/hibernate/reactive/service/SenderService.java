package org.eventservice.hibernate.reactive.service;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.reactive.ReactiveMailer;
import io.quarkus.scheduler.Scheduled;
import io.smallrye.mutiny.Uni;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.eventservice.hibernate.reactive.entities.User;
import org.eventservice.hibernate.reactive.enums.NotificationStatus;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.stream.Collectors;

@ApplicationScoped
@Slf4j
@Getter
public class SenderService {

    @Inject
    NotificationsService notificationsService;

    @Inject
    ReactiveMailer reactiveMailer;

    private int processedMessagedCounter = 0;

    @Scheduled(every = "1s")
    Uni<Void> processNotifications() {
        log.info("processNotifications started");

        return notificationsService.list()
                .map(notifications -> notifications.stream()
                        .filter(n -> NotificationStatus.CREATED.equals(n.getStatus()) || NotificationStatus.RETRY.equals(n.getStatus()))
                        .collect(Collectors.toList())
                ).map(unsentNotifications -> unsentNotifications.stream().map(n -> {
                                    User u = n.getSubscription().getUser();
                                    // return String.format("Sending notification to %s %s about %s, to email %s", u.getFirstName(), u.getLastName(), n.getUserMessage(), u.getEmail());
                                    return Mail.withText(u.getEmail(), "Notification from NetCracker Event System", String.format("Dear %s %s, we inform you about %s", u.getFirstName(), u.getLastName(), n.getUserMessage()));
                                })
                                .collect(Collectors.toList())
                )
                .call(mails -> {
                    if (!mails.isEmpty()) {
                        return reactiveMailer.send(mails.toArray(new Mail[0]));
                    } else {
                        return Uni.createFrom().item(mails);
                    }
                })
                .invoke(mails -> incrementProcessMessagedCount(mails.size()))
                .invoke(mails -> log.info(String.format("generated mails %s", mails)))
                .invoke(() -> log.info("processNotifications ended"))
                .flatMap(mails -> Uni.createFrom().voidItem());
    }

    private synchronized void incrementProcessMessagedCount(int newAmount) {
        processedMessagedCounter += newAmount;
    }

/*    @Scheduled(every = "10s")
    Uni<Void> processMails() {
        reactiveMailer.
    }*/
}
