package com.api.regie.services;

import com.api.regie.models.Remise;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RemiseService {

     List<Remise> getAllRemises();

     Remise addRemise(Remise remise);

     Optional<Remise> findByTypeRemise(String typeRemise);

     Optional<Remise> findByTypeRemiseAndIdNot(String typeRemise, UUID id);

     Optional<Remise> findById(UUID id);
}