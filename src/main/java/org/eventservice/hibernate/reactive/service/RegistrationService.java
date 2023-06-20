package org.eventservice.hibernate.reactive.service;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eventservice.hibernate.reactive.entities.Registration;
import org.hibernate.reactive.mutiny.Mutiny;

@ApplicationScoped
public class RegistrationService {
    @Inject
    Mutiny.SessionFactory sf;

    public Uni<Registration> getRegistrationByEventAndUser(String userPreferredUserName, String eventUuid) {
        return sf.withSession(s ->
                s.createNamedQuery("Registrations.findRegistrationByEventAndUser", Registration.class)
                        .setParameter("preferredUserName", userPreferredUserName)
                        .setParameter("eventUuid", eventUuid)
                        .getSingleResultOrNull()
        );
    }

    public Uni<Registration> confirmRegistration(Registration registration) {
        registration.setConfirmed(true);

        return sf.withTransaction((s, t) -> s.merge(registration));
    }

}
