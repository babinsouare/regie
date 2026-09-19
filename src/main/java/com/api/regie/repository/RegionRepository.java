package com.api.regie.repository;

import com.api.regie.models.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RegionRepository extends JpaRepository<Region, UUID> {

    List<Region> findAll();

    Optional<Region> findByRegion(String region);

    Optional<Region> findByRegionAndIdNot(String region, UUID id);

    Region save(Region region);

    Optional<Region> findById(UUID id);

}
