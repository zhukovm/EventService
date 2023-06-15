package org.eventservice.hibernate.reactive.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "events")
@NamedQuery(name = "Events.findAll", query = "SELECT e FROM Event e LEFT JOIN FETCH e.subscriptions LEFT JOIN FETCH e.registrations LEFT JOIN FETCH e.comments LEFT JOIN FETCH e.group")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    //@SequenceGenerator(name = "eventsSequence", sequenceName = "events_id_seq", allocationSize = 1, initialValue = 1)
    //@GeneratedValue(generator = "eventsSequence")
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
    private String status;
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
    private List<Subscription> subscriptions = new ArrayList<>();

    @OneToMany(mappedBy = "event", fetch = FetchType.EAGER)
    @JsonIgnore
    @ToString.Exclude
    private List<Registration> registrations = new ArrayList<>();

    @OneToMany(mappedBy = "event", fetch = FetchType.EAGER)
    @JsonIgnore
    @ToString.Exclude
    private List<Comment> comments = new ArrayList<>();

    @ManyToOne(optional = false)
    private Group group;

    @Lob
    //@Column(columnDefinition="BLOB")
    private byte[] image;
}
