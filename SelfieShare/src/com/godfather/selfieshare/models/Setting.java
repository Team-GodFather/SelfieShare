package com.godfather.selfieshare.models;

import java.util.UUID;

import com.telerik.everlive.sdk.core.serialization.ServerProperty;
import com.telerik.everlive.sdk.core.serialization.ServerType;

@ServerType("Settings")
public class Setting {
    @ServerProperty("CreatedBy")
    private UUID createdBy;
    
    @ServerProperty("Female")
    private boolean female;
 
    @ServerProperty("Male")
    private boolean male;
    
    @ServerProperty("MaximumAge")
    private int maximumAge;

    @ServerProperty("MinimumAge")
    private int minimumAge;

	public UUID getCreatedBy() {
		return createdBy;
	}

	public boolean isFemale() {
		return female;
	}

	public boolean isMale() {
		return male;
	}

	public int getMaximumAge() {
		return maximumAge;
	}

	public int getMinimumAge() {
		return minimumAge;
	}

	public void setCreatedBy(UUID createdBy) {
		this.createdBy = createdBy;
	}

	public void setFemale(boolean female) {
		this.female = female;
	}

	public void setMale(boolean male) {
		this.male = male;
	}

	public void setMaximumAge(int maximumAge) {
		this.maximumAge = maximumAge;
	}

	public void setMinimumAge(int minimumAge) {
		this.minimumAge = minimumAge;
	}
}
