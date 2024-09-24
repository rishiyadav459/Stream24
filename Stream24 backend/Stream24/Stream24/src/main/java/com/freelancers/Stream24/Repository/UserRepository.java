package com.freelancers.Stream24.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.freelancers.Stream24.Entity.User;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

	User findByEmail(String email);
    User findByPhoneNumber(String phoneNumber);
}
