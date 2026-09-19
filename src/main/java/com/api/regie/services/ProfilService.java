package com.api.regie.services;


import com.api.regie.models.Profils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProfilService {

    List<Profils> getAll();

    Optional<Profils> getProfilsById(UUID id);

    List<Profils> getProfilssByBtEnabled(Boolean btEnabled);

    Optional<Profils> getProfilsByProfil(String profil);

    Optional<Profils> getProfilsByProfilAndIdNot(String profil, UUID id);

    Profils save(Profils profils);
}
