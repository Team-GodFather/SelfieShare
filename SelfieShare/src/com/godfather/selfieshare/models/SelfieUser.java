package com.godfather.selfieshare.models;

import com.telerik.everlive.sdk.core.model.system.User;
import com.telerik.everlive.sdk.core.serialization.ServerProperty;
import com.telerik.everlive.sdk.core.serialization.ServerType;
import com.telerik.everlive.sdk.core.model.system.GeoPoint;

@ServerType("Users")
public class SelfieUser extends User {
    @ServerProperty("Sex")
    private int sex;
    
    @ServerProperty("PhoneNumber")
    private Number phoneNumber;
 
    @ServerProperty("Age")
    private Number age;
    
    @ServerProperty("Location")
    private GeoPoint location;

    public Number getPhoneNumber() {
		return phoneNumber;
	}

	public Number getAge() {
		return age;
	}

	public GeoPoint getLocation() {
		return location;
	}

	public void setPhoneNumber(Number phoneNumber) {
        this.onPropertyChanged("phoneNumber", this.phoneNumber, this.phoneNumber = phoneNumber);
	}

	public void setAge(Number age) {
        this.onPropertyChanged("age", this.age, this.age = age);
	}

	public void setLocation(GeoPoint location) {
        this.onPropertyChanged("location", this.location, this.location = location);
	}

	public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.onPropertyChanged("sex", this.sex, this.sex = sex);
    }
}