package org.eventservice.hibernate.reactive.service;

import io.quarkus.mailer.MockMailbox;
import io.smallrye.config.ConfigMapping;
import io.vertx.ext.mail.MailMessage;
import jakarta.inject.Inject;
import lombok.Data;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;
import javax.mail.*;
import javax.mail.search.FlagTerm;
import java.io.IOException;
import java.util.*;

@ApplicationScoped
@Data
public class MailCheckerService {
    @ConfigProperty(name = "quarkus.mailer.username")
    String username;
    @ConfigProperty(name = "quarkus.mailer.password")
    String password;
    @ConfigProperty(name = "quarkus.mailer.imapHost")
    String imapHost;
    @ConfigProperty(name = "quarkus.mailer.imapPort")
    int imapPort;
    @Inject
    MockMailbox mailbox;

    public void checkMails() throws MessagingException, IOException {


        if(mailbox!=null) {
          mailbox.getMailMessagesSentTo("zhumix@yandex.ru")
                  .stream().filter(mailMessage -> mailMessage.getText().contains(NotificationsService.REGISTRATION_CHECK_IDENTIFYING_EXPRESSION))
                  .filter(mailMessage -> mailMessage)
          ;

        }


        Session session = Session.getDefaultInstance(new Properties());
        Store store = session.getStore("imaps");
        store.connect(imapHost, imapPort, username, password);
        Folder inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_ONLY);

        // Fetch unseen messages from inbox folder
        Message[] messages = inbox.search(
                new FlagTerm(new Flags(Flags.Flag.SEEN), false));

        sortMessagesByDate(messages);

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
