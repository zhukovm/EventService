package org.eventservice.hibernate.reactive.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "users")
@NamedQuery(name = "Users.findAll", query = "SELECT u FROM User u LEFT JOIN FETCH u.role LEFT JOIN FETCH u.groupes")
@NamedQuery(name = "Users.findByNames", query = "SELECT u FROM User u LEFT JOIN FETCH u.role LEFT JOIN FETCH u.groupes where u.firstName=:firstName and u.lastName=:lastName")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    // @SequenceGenerator(name = "usersSequence", sequenceName = "users_id_seq", allocationSize = 1, initialValue = 1)
    // @GeneratedValue(generator = "usersSequence")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(optional = false)
    //@JoinColumn(name = "ROLE_ID")
    private Role role;

    @Column(length = 50, nullable = false)
    private String firstName;

    @Column(length = 50, nullable = false)
    private String lastName;

    @Column(length = 50)
    private String patronymic;
    @Column(length = 11, nullable = false)
    private String phone;

    @Column
    //@Temporal(TemporalType.DATE)
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date birthDate;

    @Column(length = 50, nullable = false)
    private String password;

    @Column(length = 50, nullable = false)
    private String email;

    @ManyToMany(fetch = FetchType.EAGER)
    @JsonIgnore
    @ToString.Exclude
    private List<Group> groupes = new ArrayList<>();

    @Column
    private boolean isEmailVerified;

}
