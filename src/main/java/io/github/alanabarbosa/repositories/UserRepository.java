package io.github.alanabarbosa.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.github.alanabarbosa.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	
	@Query("SELECT u FROM User u WHERE u.userName =:userName")
	User findByUsername(@Param("userName") String userName);
	
	@Modifying
	@Query("UPDATE User u SET u.enabled = false WHERE u.id =:id")
	void disableUser(@Param("id") Long id);	
	
	@Query("SELECT u FROM User u WHERE u.firstName LIKE LOWER(CONCAT ('%',:firstName, '%'))")
	Page<User> findUsersByName(@Param("firstName") String firstName, Pageable pageable);	
	
	 Optional<User> findById(Long id);
}