package com.wipro.person;

public class PermanentAddress extends Address {

	public PermanentAddress(String street, String city, String state, String country, String pinCode) {
		super(street, city, state, country, validateZipCode(country, pinCode));
	}

	@Override
	public String getAddressType() {
		return "Permanent Address";
	}

	@Override
	public String getAddressDetails() {
		return String.join(", ", getStreet(), getCity(), getState(), getCountry(), getPinCode());
	}

	private static String validateZipCode(String country, String pinCode) {
		if ("US".equalsIgnoreCase(country)) {
			if (!pinCode.matches("^[1-9]{1}[0-9]{2}\\s{1}[0-9]{3}$")) {
				throw new IllegalArgumentException(
						"Invalid ZIP code format for US: " + pinCode + ". Must be in the format 545 564.");
			}
		} else {
			if (!validateZipCode(pinCode)) {
				throw new IllegalArgumentException("Invalid pinCode format: " + pinCode
						+ ". Must contain exactly 6 digits for countries other than the US.");
			}
		}
		return pinCode;
	}

	private static boolean validateZipCode(String pinCode) {

		if (!pinCode.matches("^[1-9]{1}[0-9]{5}$"))
			return false;
		return true;

	}
}
