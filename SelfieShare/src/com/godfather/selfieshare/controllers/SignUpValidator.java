package com.godfather.selfieshare.controllers;

import com.godfather.selfieshare.models.SexType;

public class SignUpValidator {
    private final static int MIN_USERNAME_LENGTH = 5;
    private final static int MIN_PASSWORD_LENGTH = 5;
    private final static int MIN_AGE = 0;
    private final static long MIN_PHONENUMBER = 0;
    private final static String[] SEX_TYPES = new String[]{"Male", "Female"};

    public static boolean validateUsername(String inputUsername) {
        return inputUsername.length() > MIN_USERNAME_LENGTH;

    }

    public static boolean validatePassword(String inputPassword) {
        return inputPassword.length() > MIN_PASSWORD_LENGTH;
    }

    public static boolean validateAge(String inputAge) {
        String age = inputAge.trim();

        return !age.equals("") && Integer.parseInt(age) > MIN_AGE;
    }

    public static boolean validatePhoneNumber(String inputPhoneNumber) {
        String phoneNumber = inputPhoneNumber.trim();

        return !phoneNumber.equals("") && Long.parseLong(phoneNumber) > MIN_PHONENUMBER;
    }

    public static boolean validatePasswords(String inputPassword, String inputConfirmPassword) {
        String password = inputPassword.trim();
        String confirmPassword = inputConfirmPassword.trim();

        return !password.equals("") && password.equals(confirmPassword);
    }
}
