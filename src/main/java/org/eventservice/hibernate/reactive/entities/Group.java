package org.eventservice.hibernate.reactive.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "groups")
@NamedQuery(name = "Group.findAll", query = "SELECT e FROM Group e LEFT JOIN FETCH e.members")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(length = 200, nullable = false)
    private String name;
    @Column(length = 1000, nullable = false)
    private String shortDescription;
    @Column(length = 5000, nullable = false)
    private String description;
    @ManyToOne(optional = false)
    GroupType groupType;
    @ManyToMany(mappedBy = "groupes", fetch = FetchType.EAGER)
    @JsonIgnore
    @ToString.Exclude
    private List<User> members = new ArrayList<>();

    @OneToMany(mappedBy = "group", fetch = FetchType.EAGER)
    @JsonIgnore
    @ToString.Exclude
    @Fetch(value = FetchMode.SUBSELECT)
    private List<Subscription> subscriptions = new ArrayList<>();

    @Lob
    //@Column(columnDefinition="BLOB")
    private byte[] image;
}
