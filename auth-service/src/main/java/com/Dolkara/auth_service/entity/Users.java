package com.Dolkara.auth_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;


//  When Hibernate tried to load the collections:
//
//  It loaded a User with their roles
//  To add Role objects to the HashSet<Role>, it called role.hashCode()
//  Role.hashCode() tried to access the users field
//  This triggered loading of the users collection for that role
//  Which in turn tried to calculate hashCode() for those users
//  Infinite circular dependency!

@Data
@EqualsAndHashCode(exclude = "roles")
@ToString(exclude = "roles")
@Entity
@Table(name = "users")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;


    @Column(name = "password")
    private String password;

    @Column
    private Boolean enabled;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    public Users(String username, String password, String email, Set<Role> roles) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.enabled = true;
        this.roles = roles == null? new HashSet<>() : roles;
    }

    public Users() {

    }


}
