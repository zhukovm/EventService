package org.eventservice.hibernate.reactive.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

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
    @Lob
    //@Column(columnDefinition="BLOB")
    private byte[] image;
}
