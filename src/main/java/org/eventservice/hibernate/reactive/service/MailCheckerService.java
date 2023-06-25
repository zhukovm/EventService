package org.eventservice.hibernate.reactive.service;

import io.quarkus.mailer.MockMailbox;
import io.quarkus.scheduler.Scheduled;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.mail.*;
import javax.mail.search.BodyTerm;
import javax.mail.search.FlagTerm;
import javax.mail.search.SubjectTerm;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
@Data
@Slf4j
public class MailCheckerService {
    @ConfigProperty(name = "quarkus.mailer.username")
    String username;
    @ConfigProperty(name = "quarkus.mailer.password")
    String password;
    @ConfigProperty(name = "quarkus.mailer.imapHost")
    String imapHost;
    @ConfigProperty(name = "quarkus.mailer.imapPort")
    int imapPort;

    @ConfigProperty(name = "quarkus.mailer.mock")
    boolean isMockingEnabled;
    @Inject
    MockMailbox mailbox;
    @Inject
    RegistrationService registrationService;

    Pattern selectEventUuidPattern = Pattern.compile("uuid:\"(.*?)\"");
    Pattern selectPreferredUserNamePattern = Pattern.compile("\\((.*?)\\)");

    @Scheduled(every = "30s")
    public Uni<Void> checkMails() throws MessagingException, IOException {
        List<Uni<?>> unis = getMessageContentStream()
                .filter(messageText -> messageText.contains(NotificationsService.REGISTRATION_CHECK_IDENTIFYING_EXPRESSION))
                .map(mailMessage -> tryToFindAndUpdateRegistration(mailMessage))
                .collect(Collectors.toList());

        return unis.isEmpty() ? Uni.createFrom().nullItem() : Uni.combine().all().unis(unis).discardItems();
    }

    private Uni<?> tryToFindAndUpdateRegistration(String messageText) {
        Optional<String> eventUuid = selectEventUuidPattern.matcher(messageText).results().map(matchResult -> matchResult.group(1)).findFirst();
        Optional<String> preferredUserName = selectPreferredUserNamePattern.matcher(messageText).results().map(matchResult -> matchResult.group(1)).findFirst();

        if (preferredUserName.isPresent() && eventUuid.isPresent()) {
            return registrationService.getRegistrationByEventAndUser(preferredUserName.get(), eventUuid.get())
                    .flatMap(registration -> registrationService.confirmRegistration(registration));
        }
        return Uni.createFrom().nullItem();
    }

    private Stream<String> getMessageContentStream() throws MessagingException, IOException {
        if (isMockingEnabled) {
            return mailbox.getMailMessagesSentTo("EventServiceUser@yandex.ru")
                    .stream().map(mailMessage -> mailMessage.getText());
        } else {
            Session session = Session.getDefaultInstance(new Properties());
            Store store = session.getStore("imaps");
            store.connect(imapHost, imapPort, username, password);
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);

            // Fetch unseen messages from inbox folder
            Message[] messages = inbox.search(
                    new FlagTerm(new Flags(Flags.Flag.SEEN)
                            , false));

            log.debug("initial message count " + messages.length);

            Message[] filteredMessages = inbox.search(
                    new SubjectTerm(SenderService.MAIL_SUBJECT)
                    , messages);

            filteredMessages = inbox.search(
                    new BodyTerm("+")
                    , filteredMessages);

            log.debug("after theme filtration message count " + messages.length);

            sortMessagesByDate(filteredMessages);

            printMessages(filteredMessages);

            try {
                return Arrays.stream(filteredMessages)
                        .map(mailMessage -> {
                            try {
                                return mailMessage.getContent().toString();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            } catch (MessagingException e) {
                                throw new RuntimeException(e);
                            }
                        });
            } finally {
                inbox.setFlags(messages, new Flags(Flags.Flag.SEEN), true);
                log.debug("MARK messages as RED " + messages.length);
            }
        }
    }

    private static void printMessages(Message[] messages) throws MessagingException, IOException {
        for (Message message : messages) {
            System.out.println(
                    "[MAIL CHECKER] sendDate:\n" + message.getSentDate()
                            + " subject:\n" + message.getSubject()
                            + " message:\n" + message.getContent()

            );
        }
    }

    private static void sortMessagesByDate(Message[] messages) {
        Arrays.sort(messages, (m1, m2) -> {
                    try {
                        return m2.getSentDate().compareTo(m1.getSentDate());
                    } catch (MessagingException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }

}
