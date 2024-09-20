package com.wipro.person;

import java.util.List;

public class Person {
	private String firstName;
	private String lastName;
	private List<IAddress> addresses;

	public Person(String firstName, String lastName, List<IAddress> addresses) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.addresses = addresses;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public List<IAddress> getAddresses() {
		return addresses;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(firstName).append(" | ").append(lastName);

		if (!addresses.isEmpty()) {
			for (IAddress address : addresses) {
				sb.append(" | ").append(address.getAddressType()).append(" | ").append(address.getAddressDetails());
			}
		}

		return sb.toString().trim();
	}
}
