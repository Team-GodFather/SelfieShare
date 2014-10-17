package com.godfather.selfieshare.models;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.UUID;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import com.telerik.everlive.sdk.core.model.base.ItemBase;
import com.telerik.everlive.sdk.core.model.system.File;
import com.telerik.everlive.sdk.core.serialization.ServerProperty;
import com.telerik.everlive.sdk.core.serialization.ServerType;

@ServerType("Selfies")
public class Selfie extends ItemBase implements Parcelable {
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

    @ServerProperty(value="PictureBitmap", writeToServer = false)
    private Bitmap pictureBitmap;

    @ServerProperty(value="ContactData", writeToServer = false)
    private String[] contactData;

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

    public Bitmap getPictureBitmap() {
        return pictureBitmap;
    }
    public void setPictureBitmap(Bitmap pictureBitmap) {
        this.pictureBitmap = pictureBitmap;
    }
    public String[] getContactData() {
        return this.contactData;
    }
    public void setContactData(String name, String number) {
        this.contactData = new String[] { name, number };
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        pictureBitmap.writeToParcel(out, 0);
        out.writeStringArray(this.contactData);
    }

    public static final Parcelable.Creator<Selfie> CREATOR = new Parcelable.Creator<Selfie>() {
        public Selfie createFromParcel(Parcel in) {
            return new Selfie(in);
        }
        public Selfie[] newArray(int size) {
            return new Selfie[size];
        }
    };

    public Selfie(){}

    private Selfie(Parcel in) {
        pictureBitmap = Bitmap.CREATOR.createFromParcel(in);

        this.contactData = new String[2];
        in.readStringArray(this.contactData);
    }
}
