package com.bazaarx.bazaarxbackend.repo;

import com.bazaarx.bazaarxbackend.entity.user.ApplicationUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface UserRepository extends MongoRepository<ApplicationUser, String> {

    Optional<ApplicationUser> findByEmail(String email);


    Boolean existsByEmail(String email);


}