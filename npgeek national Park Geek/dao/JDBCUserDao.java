package com.techelevator.npgeek.dao;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.sql.DataSource;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import com.techelevator.npgeek.authentication.PasswordHasher;
import com.techelevator.npgeek.model.User;

@Component
public class JDBCUserDao implements UserDao{
	
	private JdbcTemplate jdbcTemplate;
	private PasswordHasher passwordHasher;
	
	
	@Autowired
	public JDBCUserDao(DataSource dataSource, PasswordHasher passwordHasher) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.passwordHasher = passwordHasher;
		
		
	}

	@Override
	public User saveUser(String username, String password) {
		byte[] salt = passwordHasher.generateRandomSalt();
		String hashedPass = passwordHasher.computeHash(password, salt);
		String saltString = new String(Base64.getEncoder().encode(salt));
		 long newId = jdbcTemplate.queryForObject("INSERT INTO users(username, password, salt) VALUES (?, ?, ?) RETURNING id", Long.class, username,
	                hashedPass, saltString);


	   User newUser = new User();
	   newUser.setId(newId);
	   newUser.setUsername(username);
	   
	   return newUser;	 
	}

	@Override
	public void changePassword(User user, String newPassword) {
		byte[] salt = passwordHasher.generateRandomSalt();
		String hashedPass = passwordHasher.computeHash(newPassword, salt);
		String saltString = new String(Base64.getEncoder().encode(salt));
		jdbcTemplate.update("UPDATE users SET password=?, salt=? WHERE id=?,",hashedPass, saltString, user.getId());

;		
	}

	@Override
	public User getValidUserWithPassword(String username, String password) {
		String sqlSearchForUser = "SELECT * FROM users WHERE UPPER(username) = ?";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlSearchForUser, username.toUpperCase());
        if (results.next()) {
            String storedSalt = results.getString("salt");
            String storedPassword = results.getString("password");
            String hashedPassword = passwordHasher.computeHash(password, Base64.getDecoder().decode(storedSalt));
            if(storedPassword.equals(hashedPassword)) {
                return mapResultToUser(results);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

	@Override
	public List<User> getAllUsers() {
		List<User> users = new ArrayList<User>();
        String sqlSelectAllUsers = "SELECT id, username FROM users";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlSelectAllUsers);

        while(results.next()) {
            User user = mapResultToUser(results);
            users.add(user);
        }

        return users;
    }

    private User mapResultToUser(SqlRowSet results) {
        User user = new User();
        user.setId(results.getLong("id"));
        user.setUsername(results.getString("username"));
        return user;
    }

}
