package com.api.regie.implementations;


import com.api.regie.models.Profils;
import com.api.regie.models.Users;
import com.api.regie.repository.UsersRepository;
import com.api.regie.services.UsersService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class Userslmplementation implements UsersService {

    private final UsersRepository usersRepository;

    public Userslmplementation(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public List<Users> getAll() {
        return usersRepository.findAll();
    }

    @Override
    public Users save(Users users) {
        return usersRepository.save(users);
    }

    @Override
    public Optional<Users> findById(UUID id) {
        return usersRepository.findById(id);
    }

    @Override
    public List<Users> findByProfile(Profils profile) {
        return usersRepository.findByProfils(profile);
    }

    @Override
    public Optional<Users> findByEmail(String email) {
        return usersRepository.findByEmail(email);
    }

    @Override
    public Optional<Users> findByEmailAndIdNot(String email, UUID id) {
        return usersRepository.findByEmailAndIdNot(email, id);
    }

    @Override
    public Optional<Users> findByMsisdn(String msisdn) {
        return usersRepository.findByMsisdn(msisdn);
    }

    @Override
    public Optional<Users> findByMsisdnAndIdNot(String msisdn, UUID id) {
        return usersRepository.findByMsisdnAndIdNot(msisdn, id);
    }

    @Override
    public void lockOrUnlockUsers(Long id, Boolean btEnabled) {

    }
}
