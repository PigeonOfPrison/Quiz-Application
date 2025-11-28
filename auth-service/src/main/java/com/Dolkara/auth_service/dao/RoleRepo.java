package com.Dolkara.auth_service.dao;

import com.Dolkara.auth_service.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepo extends JpaRepository<Role, Integer> {

    Role findByRoleName(String roleName);
}
