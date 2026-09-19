package com.api.regie.implementations;

import com.api.regie.models.Region;
import com.api.regie.repository.RegionRepository;
import com.api.regie.services.RegionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RegionImplementation implements RegionService {

    private final RegionRepository regionRepository;

    public RegionImplementation(RegionRepository regionRepository) {
        this.regionRepository = regionRepository;
    }

    @Override
    public List<Region> getAllRegions() {
        return regionRepository.findAll();
    }

    @Override
    public Region addRegion(Region region) {
        return regionRepository.save(region);
    }

    @Override
    public Optional<Region> findByRegion(String region) {
        return regionRepository.findByRegion(region);
    }

    @Override
    public Optional<Region> findByRegionAndIdNot(String region, UUID id) {
        return regionRepository.findByRegionAndIdNot(region, id);
    }

    @Override
    public Optional<Region> findById(UUID id) {
        return regionRepository.findById(id);
    }


}
