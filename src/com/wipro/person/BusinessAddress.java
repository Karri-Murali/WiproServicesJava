package com.wipro.person;

public class BusinessAddress extends Address {

    public BusinessAddress(String street, String city, String state, String country, String pinCode) {
        super(street, city, state, country, pinCode);
        validateZipCode(country, pinCode);
    }

    @Override
    public String getAddressType() {
        return "Business Address";
    }

    @Override
    public String getAddressDetails() {
        return String.join(", ", getStreet(), getCity(), getState(), getCountry(), getPinCode());
    }

    private void validateZipCode(String country, String pinCode) {
        if(!validatePinCode(country, pinCode)) {
        	throw new IllegalArgumentException("Invalid PIN Code format for your country");
        }  
    }
    public boolean validatePinCode(String country, String pinCode) {

		if ("US".equalsIgnoreCase(country)) {
			if (pinCode.matches("^[1-9]{1}[0-9]{2}\\s{1}[0-9]{3}$"))
				return true;
			else
				return false;
		} else if (!pinCode.matches("^[1-9]{1}[0-9]{5}$"))
			return false;
		else
			return true;
	}
}
