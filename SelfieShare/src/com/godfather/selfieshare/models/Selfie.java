package com.godfather.selfieshare.models;

import java.util.Date;
import java.util.UUID;

import com.telerik.everlive.sdk.core.serialization.ServerProperty;
import com.telerik.everlive.sdk.core.serialization.ServerType;

@ServerType("Selfies")
public class Selfie {
    @ServerProperty("CreatedBy")
    private UUID createdBy;
    
    @ServerProperty("To")
    private UUID to;
 
    @ServerProperty("Picture")
    private UUID picture;
    
    @ServerProperty("CreatedAt")
    private Date createdAt;

	public UUID getCreatedBy() {
		return createdBy;
	}

	public UUID getTo() {
		return to;
	}

	public UUID getPicture() {
		return picture;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedBy(UUID createdBy) {
		this.createdBy = createdBy;
	}

	public void setTo(UUID to) {
		this.to = to;
	}

	public void setPicture(UUID picture) {
		this.picture = picture;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
}
