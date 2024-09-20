package com.wipro.person;

public abstract class Address implements IAddress {
	private final String street;
	private final String city;
	private final String state;
	private final String country;
	private final String pinCode;

	public Address(String street, String city, String state, String country, String pinCode) {

		this.street = street;
		this.city = city;
		this.state = state;
		this.country = country;
		this.pinCode = pinCode;
	}

	public String getStreet() {
		return street;
	}

	public String getCity() {
		return city;
	}

	public String getState() {
		return state;
	}

	public String getCountry() {
		return country;
	}

	public String getPinCode() {
		return pinCode;
	}

	

	public abstract String getAddressType();

	public abstract String getAddressDetails();
}
