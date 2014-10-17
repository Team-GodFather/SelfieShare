package com.godfather.selfieshare.models;

import java.util.Date;
import java.util.UUID;

import com.telerik.everlive.sdk.core.model.base.ItemBase;
import com.telerik.everlive.sdk.core.model.system.File;
import com.telerik.everlive.sdk.core.serialization.ServerProperty;
import com.telerik.everlive.sdk.core.serialization.ServerType;

@ServerType("Selfies")
public class Selfie extends ItemBase {
    @ServerProperty("To")
    private UUID to;
 
    @ServerProperty("Picture")
    private UUID picture;

    @ServerProperty(value="_Owner", writeToServer = false)
    private SelfieUser  _Owner;

    @ServerProperty(value="_To", writeToServer = false)
    private SelfieUser  _To;

    @ServerProperty(value="_Picture", writeToServer = false)
    private File  _Picture;

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

    public SelfieUser get_Owner() {
        return _Owner;
    }
    public SelfieUser get_To() {
        return _To;
    }
    public File get_Picture() {
        return _Picture;
    }
}
