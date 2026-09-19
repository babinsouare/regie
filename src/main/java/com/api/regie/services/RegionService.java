package com.api.regie.services;

import com.api.regie.models.Region;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RegionService {

     List<Region> getAllRegions();

     Region addRegion(Region region);

     Optional<Region> findByRegion(String region);

     Optional<Region> findByRegionAndIdNot(String region, UUID id);

     Optional<Region> findById(UUID id);

}
