package org.eventservice.hibernate.reactive.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "groupType", fetch = FetchType.EAGER)
    @JsonIgnore
    @ToString.Exclude
    @Fetch(value = FetchMode.SUBSELECT)
    private List<Subscription> subscriptions = new ArrayList<>();
}
