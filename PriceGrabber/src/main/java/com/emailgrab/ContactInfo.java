package com.emailgrab;

public class ContactInfo {
	public String name;
	public String email;
	public String phone;
	public String organization;
	
	public ContactInfo(String name, String email, String phone,
			String organization) {
		super();
		this.name = name;
		this.email = email;
		this.phone = phone;
		this.organization = organization;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getOrganization() {
		return organization;
	}
	public void setOrganization(String organization) {
		this.organization = organization;
	}
	
}
