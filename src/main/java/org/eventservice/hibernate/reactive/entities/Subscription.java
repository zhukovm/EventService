package org.eventservice.hibernate.reactive.entities;

import lombok.*;

import jakarta.persistence.*;

@Entity
@Table(name = "subscriptions")
@NamedQuery(name = "Subscriptions.findAll", query = "SELECT s FROM Subscription s LEFT JOIN FETCH s.user LEFT JOIN FETCH s.event")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Subscription {
    @Id
    //@SequenceGenerator(name = "subscriptionsSequence", sequenceName = "subscriptions_id_seq", allocationSize = 1, initialValue = 1)
   // @GeneratedValue(generator = "subscriptionsSequence")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(optional = false)
    private User user;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Event event;
}
