package org.eventservice.hibernate.reactive.entities;

import lombok.Data;

import jakarta.persistence.*;

/*@Entity
@Table(name = "roles")*/
@Data
public class Role {
    @Id
    //@SequenceGenerator(name = "rolesSequence", sequenceName = "roles_id_seq", allocationSize = 1, initialValue = 1)
   // @GeneratedValue(generator = "rolesSequence")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(length = 40, unique = true)
    private String name;
}
