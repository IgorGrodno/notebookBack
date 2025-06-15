package com.example.notebookback.repositories;


import com.example.notebookback.models.ntities.ERole;
import com.example.notebookback.models.ntities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}
