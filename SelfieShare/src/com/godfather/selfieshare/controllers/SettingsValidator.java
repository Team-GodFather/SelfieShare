package com.godfather.selfieshare.controllers;

public class SettingsValidator {
	public static boolean validateFields(int male, int female, int minAge,
			int maxAge) {
		if (-1 < male && male < 2 && -1 < male && male < 2 && 0 < minAge
				&& 0 < maxAge && maxAge < 200) {
			return true;
		}

		return false;
	}

	public static boolean checkMinAge(int minAge) {
		if (0 < minAge) {
			return true;
		}

		return false;
	}

	public static boolean checkMaxAge(int maxAge) {
		if (0 < maxAge && maxAge < 200) {
			return true;
		}

		return false;
	}
	
	public static boolean checkRangeAge(int minAge, int maxAge) {
		if (minAge < maxAge) {
			return true;
		}

		return false;
	}	
}
