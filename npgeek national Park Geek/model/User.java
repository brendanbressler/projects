package com.techelevator.npgeek.model;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;

public class User {
	
@NotBlank(message="Username required")	
private String username;

@NotBlank(message="Password required")
private String password;


private String confirmPassword;

private long id;



@AssertTrue(message="Password must match")
public boolean isPassWordMatching() {
	if(password != null) {
		return password.equals(confirmPassword);
	}
	return true;
}



public String getUsername() {
	return username;
}



public void setUsername(String username) {
	this.username = username;
}



public String getPassword() {
	return password;
}



public void setPassword(String password) {
	this.password = password;
}



public String getConfirmPassword() {
	return confirmPassword;
}



public void setConfirmPassword(String confirmPassword) {
	this.confirmPassword = confirmPassword;
}



public long getId() {
	return id;
}



public void setId(long id) {
	this.id = id;
}

}


