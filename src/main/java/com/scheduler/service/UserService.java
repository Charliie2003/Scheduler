package com.scheduler.service;

import com.scheduler.service.entity.User;
import com.scheduler.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

	@Autowired
	UserRepository userRepository;

	public List<User> findAllUsers(){
		return  userRepository.findAll();
	}

}
