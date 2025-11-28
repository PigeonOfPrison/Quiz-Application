package com.Dolkara.auth_service.dao;

import com.Dolkara.auth_service.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<Users, Integer> {

    Users findByUsername(String username);

    Users findByEmail(String email);
}
