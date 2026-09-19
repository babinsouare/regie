package com.api.regie.repository;

import com.api.regie.models.Profils;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProfilsRepository extends JpaRepository<Profils, UUID> {

    List<Profils> findAll();

    Optional<Profils> findById(UUID id);

    Optional<Profils> findByProfil(String profil);

    List<Profils> findByBtEnabled(Boolean btEnabled);

    Optional<Profils> findByProfilAndIdNot(String code, UUID id);
}
