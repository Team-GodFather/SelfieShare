package com.god.father.selfieshare.controllers;

public class LoginValidator {
	
	public static boolean validate(String inputUsername, String inputPassword) {
		if (inputUsername == null || inputUsername.length() == 0
				|| inputPassword == null || inputPassword.length() == 0) {
			return false;
		}
		
		return true;
	}
}
