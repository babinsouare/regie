package com.api.regie.repository;

import com.api.regie.models.Profils;
import com.api.regie.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsersRepository extends JpaRepository<Users, UUID> {

    Optional<Users> findByEmail(String email);

    Optional<Users> findByEmailAndIdNot(String email, UUID id);

    Optional<Users> findByMsisdn(String msisdn);

    List<Users> findByProfils(Profils profils);

    Optional<Users> findByMsisdnAndIdNot(String msisdn, UUID id);
}
