package com.techelevator.npgeek.dao;

import java.util.List;

import com.techelevator.npgeek.model.User;

public interface UserDao {
	
	public User saveUser(String username, String password);
	
	public void changePassword(User user, String newPassword);
	
	public User getValidUserWithPassword(String username, String password);
	
	public List<User> getAllUsers();
	
	

}
