package com.fmisser.gtc.auth.repository;

import com.fmisser.gtc.auth.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    @Query(value = "SELECT * FROM t_role WHERE name = 'ROLE_USER' LIMIT 1", nativeQuery = true)
    Role getUserRole();

    @Query(value = "SELECT * FROM t_role WHERE name = 'ROLE_ADMIN' LIMIT 1", nativeQuery = true)
    Role getAdminRole();
}
