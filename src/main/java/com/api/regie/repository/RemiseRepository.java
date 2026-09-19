package com.api.regie.repository;

import com.api.regie.models.Remise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RemiseRepository extends JpaRepository<Remise, UUID> {

    List<Remise> findAll();

    Optional<Remise> findByTypeRemise(String typeRemise);

    Optional<Remise> findByTypeRemiseAndIdNot(String typeRemise, UUID id);

    Remise save(Remise remise);

    Optional<Remise> findById(UUID id);
}