package io.github.alanabarbosa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.alanabarbosa.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {


}
