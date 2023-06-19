package org.eventservice.hibernate.reactive.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eventservice.hibernate.reactive.enums.NotificationStatus;
import org.eventservice.hibernate.reactive.enums.NotificationType;

import java.util.Date;

@Entity
@Table(name = "notifications")
@NamedQuery(name = "Notifications.findAll", query = "SELECT n FROM Notification n LEFT JOIN FETCH n.subscription LEFT JOIN FETCH n.registration")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    @Id
    //@SequenceGenerator(name = "notificationsSequence", sequenceName = "events_id_seq", allocationSize = 1, initialValue = 1)
    //@GeneratedValue(generator = "notificationsSequence")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @OneToOne(optional = true)
    private Subscription subscription;
    @OneToOne(optional = true)
    private Registration registration;
    @Column(length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;
    @Column(length = 5000, nullable = false)
    private String userMessage;
    @Column(length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationStatus status;
    @Column
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Column
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date sentAt;
}
