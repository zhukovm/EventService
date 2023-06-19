package org.eventservice.hibernate.reactive.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "registrations")
@NamedQuery(name = "Registrations.findAll", query = "SELECT s FROM Registration s LEFT JOIN FETCH s.user LEFT JOIN FETCH s.event")
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
