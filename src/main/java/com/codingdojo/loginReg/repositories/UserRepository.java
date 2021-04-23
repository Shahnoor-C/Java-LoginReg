package com.codingdojo.loginReg.repositories;

import org.springframework.data.repository.CrudRepository;

import com.codingdojo.loginReg.models.User;

public interface UserRepository extends CrudRepository<User, Long> {
	User findByEmail(String email);
	
}

