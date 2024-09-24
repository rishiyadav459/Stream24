package com.freelancers.Stream24.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.freelancers.Stream24.Entity.Video;

public interface VideoRepository extends MongoRepository<Video, String> {
	
}

