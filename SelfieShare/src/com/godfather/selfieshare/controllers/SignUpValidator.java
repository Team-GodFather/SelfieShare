package com.godfather.selfieshare.controllers;

public class SignUpValidator {
	private final static int MIN_USERNAME_LENGTH = 5;
	private final static int MIN_PASSWORD_LENGTH = 5;
	private final static int MIN_AGE = 0;
	private final static long MIN_PHONENUMBER = 0;
	private final static String[] SEX_TYPES = new String[] { "Male", "Female" };

	public static boolean validateUsername(String inputUsername) {

		if (inputUsername.length() > MIN_USERNAME_LENGTH) {
			return true;
		}

		return false;
	}

	public static boolean validatePassword(String inputPassword) {
		if (inputPassword.length() > MIN_PASSWORD_LENGTH) {
			return true;
		}

		return false;
	}

	public static boolean validateAge(int inputAge) {
		if (inputAge > MIN_AGE) {
			return true;
		}

		return false;
	}

	public static boolean validatePhoneNumber(long inputPhoneNumber) {
		if (inputPhoneNumber > MIN_PHONENUMBER) {
			return true;
		}

		return false;
	}

	public static boolean validateRange(String inputUsername,
			String inputPassword, String inputConfirmPassword, int inputAge,
			String inputSex, long inputPhoneNumber) {

		if (inputUsername.trim().equals("") && inputPassword.trim().equals("")
				&& inputConfirmPassword.trim().equals("")
				&& inputAge <= MIN_AGE && inputSex.trim().equals("")
				&& inputPhoneNumber <= MIN_PHONENUMBER) {
			return false;
		}

		return true;
	}

	public static boolean validatePasswords(String inputPassword,
			String inputConfirmPassword) {
		if (inputPassword.trim().equals(inputConfirmPassword.trim())) {
			return true;
		}

		return false;
	}

	public static boolean validateSexType(String inputSex) {
		if (inputSex == SEX_TYPES[0] || inputSex == SEX_TYPES[1]) {
			return true;
		}

		return false;
	}
}
