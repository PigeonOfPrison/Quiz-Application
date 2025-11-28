package com.Dolkara.auth_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

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
@EqualsAndHashCode(exclude = "users")
@ToString(exclude = "users")
@Entity
@Table(name = "role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "roleName")
    private String roleName;

    @ManyToMany(mappedBy = "roles")
    private Set<Users> users;

    public Role(Integer id, String role, Set<Users> users) {
        this.id = id;
        this.roleName = role;
        this.users = users;
    }

    public Role() {

    }

}
