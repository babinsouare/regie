package com.api.regie.services;

import com.api.regie.models.ModePaiement;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ModePaiementService {

     List<ModePaiement> getAllModePaiements();

     ModePaiement addModePaiement(ModePaiement modePaiement);

     Optional<ModePaiement> findByMode(String mode);

     Optional<ModePaiement> findByModeAndIdNot(String mode, UUID id);

     Optional<ModePaiement> findById(UUID id);
}