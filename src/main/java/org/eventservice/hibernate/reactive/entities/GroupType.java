package org.eventservice.hibernate.reactive.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "group_types")
@NamedQuery(name = "GroupTypes.findAll", query = "SELECT gt FROM GroupType gt")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupType {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(length = 40, unique = true)
    private String name;
}
