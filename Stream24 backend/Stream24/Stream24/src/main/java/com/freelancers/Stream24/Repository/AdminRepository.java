package com.freelancers.Stream24.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.freelancers.Stream24.Entity.Admin;


@Repository
public interface AdminRepository extends MongoRepository<Admin, String>{
	Admin findByEmail(String email);

}
