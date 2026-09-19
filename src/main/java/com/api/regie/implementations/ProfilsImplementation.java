package com.api.regie.implementations;

import com.api.regie.models.Profils;
import com.api.regie.repository.ProfilsRepository;
import com.api.regie.services.ProfilService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProfilsImplementation implements ProfilService {

    private final ProfilsRepository profilsRepository;

    public ProfilsImplementation(ProfilsRepository profilsRepository) {
        this.profilsRepository = profilsRepository;
    }

    @Override
    public List<Profils> getAll() {
        return profilsRepository.findAll();
    }

    @Override
    public Optional<Profils> getProfilsById(UUID id) {
        return profilsRepository.findById(id);
    }

    @Override
    public List<Profils> getProfilssByBtEnabled(Boolean btEnabled) {
        return profilsRepository.findByBtEnabled(btEnabled);
    }

    @Override
    public Optional<Profils> getProfilsByProfil(String profil) {
        return profilsRepository.findByProfil(profil);
    }

    @Override
    public Optional<Profils> getProfilsByProfilAndIdNot(String profil, UUID id) {
        return profilsRepository.findByProfilAndIdNot(profil, id);
    }

    @Override
    public Profils save(Profils profils) {
        return profilsRepository.save(profils);
    }
}
