package org.eventservice.hibernate.reactive.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "registrations")
@NamedQuery(name = "Registrations.findAll", query = "SELECT r FROM Registration r LEFT JOIN FETCH r.user LEFT JOIN FETCH r.event")
@NamedQuery(name = "Registrations.findRegistrationByEventAndUser", query = "SELECT r FROM Registration r LEFT JOIN FETCH r.user LEFT JOIN FETCH r.event WHERE r.user.preferredUserName=:preferredUserName AND r.event.id=:eventUuid")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Registration {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @ManyToOne(optional = false)
    private User user;
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Event event;
    @Column
    private boolean isConfirmed;
}
