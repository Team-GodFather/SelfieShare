package com.godfather.selfieshare.models;

import java.util.Date;
import java.util.UUID;

import com.telerik.everlive.sdk.core.model.base.ItemBase;
import com.telerik.everlive.sdk.core.serialization.ServerProperty;
import com.telerik.everlive.sdk.core.serialization.ServerType;

@ServerType("Selfies")
public class Selfie extends ItemBase {
    @ServerProperty("To")
    private UUID to;
 
    @ServerProperty("Picture")
    private UUID picture;

	public UUID getTo() {
		return to;
	}

	public UUID getPicture() {
		return picture;
	}

	public void setTo(UUID to) {
		this.to = to;
	}

	public void setPicture(UUID picture) {
		this.picture = picture;
	}
}
