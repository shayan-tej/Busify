package com.sip.busify;

public class Users {

	String fullName, dateOfBirth, gender, time, contact, mail, photoUrl;

	public Users() {
	}

	public Users(String fullName, String dateOfBirth, String gender, String time, String contact, String mail, String photoUrl) {
		this.fullName = fullName;
		this.dateOfBirth = dateOfBirth;
		this.gender = gender;
		this.time = time;
		this.contact = contact;
		this.mail = mail;
		this.photoUrl = photoUrl;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) { this.mail= mail;	}

	public String getPhotoUrl() { return photoUrl; }

	public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
}