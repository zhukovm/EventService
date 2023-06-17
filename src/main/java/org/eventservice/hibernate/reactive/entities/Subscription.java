package org.eventservice.hibernate.reactive.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "subscriptions")
@NamedQuery(name = "Subscriptions.findAll", query = "SELECT s FROM Subscription s LEFT JOIN FETCH s.user LEFT JOIN FETCH s.event")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(optional = false)
    private User user;

    //todo make check that event | group | groupType should exist
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    private Event event;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    private Group group;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    private GroupType groupType;

}
