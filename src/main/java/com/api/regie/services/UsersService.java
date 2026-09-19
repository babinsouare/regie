package com.api.regie.services;


import com.api.regie.models.Profils;
import com.api.regie.models.Users;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsersService {

    public List<Users> getAll();

    public Users save(Users users);

    public Optional<Users> findById(UUID id);

    public List<Users> findByProfile(Profils profile);

    public Optional<Users> findByEmail(String email);

    public Optional<Users> findByEmailAndIdNot(String email, UUID id);

    public Optional<Users> findByMsisdn(String msisdn);

    public Optional<Users> findByMsisdnAndIdNot(String msisdn, UUID id);

    public void lockOrUnlockUsers(Long id, Boolean btEnabled);
}
