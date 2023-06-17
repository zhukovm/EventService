package org.eventservice.hibernate.reactive.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.eventservice.hibernate.reactive.enums.EventStatus;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "events")
@NamedQuery(name = "Events.findAll", query = "SELECT e FROM Event e LEFT JOIN FETCH e.group"/* LEFT JOIN FETCH e.subscriptions LEFT JOIN FETCH e.registrations LEFT JOIN FETCH e.comments"*/)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @ManyToOne(optional = false)
    private User creator;
    @Column(length = 200, nullable = false)
    private String name;
    @Column(length = 1000, nullable = false)
    private String shortDescription;
    @Column(length = 5000, nullable = false)
    private String description;
    @Column(length = 50, nullable = false)
    private EventStatus status;
    @Column
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private java.util.Date createdAt;
    @Column
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date plannedDateTime;
    @OneToMany(mappedBy = "event", fetch = FetchType.EAGER)
    @JsonIgnore
    @ToString.Exclude
    @Fetch(value = FetchMode.SUBSELECT)
    private List<Subscription> subscriptions = new ArrayList<>();

    @OneToMany(mappedBy = "event", fetch = FetchType.EAGER)
    @JsonIgnore
    @ToString.Exclude
    @Fetch(value = FetchMode.SUBSELECT)
    private List<Registration> registrations = new ArrayList<>();

    @OneToMany(mappedBy = "event", fetch = FetchType.EAGER)
    @JsonIgnore
    @ToString.Exclude
    @Fetch(value = FetchMode.SUBSELECT)
    private List<Comment> comments = new ArrayList<>();

    @ManyToOne(optional = false)
    private Group group;

    @Lob
    //@Column(columnDefinition="BLOB")
    private byte[] image;

    @Column
    private Boolean isConfirmedByAdministrator;
}
