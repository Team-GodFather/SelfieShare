package com.godfather.selfieshare.models;

public class Settings {
	private final String settings = "settings";
	
	private long id;
	private int male;
	private int female;
	private int minAge;
	private int maxAge;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getMale() {
		return male;
	}

	public int getFemale() {
		return female;
	}

	public int getMinAge() {
		return minAge;
	}

	public int getMaxAge() {
		return maxAge;
	}

	public void setMale(int male) {
		this.male = male;
	}

	public void setFemale(int female) {
		this.female = female;
	}

	public void setMinAge(int minAge) {
		this.minAge = minAge;
	}

	public void setMaxAge(int maxAge) {
		this.maxAge = maxAge;
	}

	// Will be used by the ArrayAdapter in the ListView
	@Override
	public String toString() {
		return settings;
	}
}