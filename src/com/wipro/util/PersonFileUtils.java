package com.wipro.util;

import com.wipro.person.Person;
import com.wipro.person.IAddress;
import com.wipro.person.PermanentAddress;
import com.wipro.exception.InvalidFormatStringException;
import com.wipro.exception.InvalidPersonInfoException;
import com.wipro.person.BusinessAddress;
import com.wipro.person.EmailAddress;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersonFileUtils {

	public static void savePersonsToFile(List<Person> persons, String fileName) throws InvalidFormatStringException, InvalidPersonInfoException {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(fileName, true));
			for (Person person : persons) {
				if (person.getAddresses().isEmpty()) {
					throw new InvalidPersonInfoException("Person must contain at least one address.");
				}
				for (IAddress address : person.getAddresses()) {
					writer.write(person.getFirstName() + " | " + person.getLastName() + " | " + address.getAddressType()
							+ " | " + address.getAddressDetails());
					writer.newLine();
				}
			}
		} catch (IOException e) {
			System.out.println("Error saving persons to file: " + e.getMessage());
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					System.out.println("Error closing BufferedWriter: " + e.getMessage());
				}
			}
		}
	}

	public static List<Person> readPersonDetails(String filePath) throws IOException, InvalidFormatStringException {
		Map<String, Person> personMap = new HashMap<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split("\\|");
				if (parts.length != 4) {
					throw new InvalidFormatStringException(
							"Invalid line format Found (May be File corrupted): " + line);
				}

				String firstName = parts[0].trim();
				String lastName = parts[1].trim();
				String addressType = parts[2].trim();
				String addressDetails = parts[3].trim();

				String[] addressParts = addressDetails.split(", ");
				IAddress address;
				switch (addressType) {
				case "Permanent Address":
					if (addressParts.length != 5) {
						throw new InvalidFormatStringException("Invalid Permanent Address format: " + addressDetails);
					}
					address = new PermanentAddress(addressParts[0], addressParts[1], addressParts[2], addressParts[3],
							addressParts[4]);
					break;
				case "Business Address":
					if (addressParts.length != 5) {
						throw new InvalidFormatStringException("Invalid Business Address format: " + addressDetails);
					}
					address = new BusinessAddress(addressParts[0], addressParts[1], addressParts[2], addressParts[3],
							addressParts[4]);
					break;
				case "Email":
					address = new EmailAddress(addressDetails);
					break;
				default:
					throw new InvalidFormatStringException("Unknown address type: " + addressType);
				}
    
				String key = firstName + "|" + lastName;
				if (!personMap.containsKey(key)) {
					personMap.put(key, new Person(firstName, lastName, new ArrayList<>()));
				}
				personMap.get(key).getAddresses().add(address);
			}
		} catch (IOException e) {
			throw new IOException("Error reading person details from file: " + e.getMessage(), e);
		}

		return new ArrayList<>(personMap.values());
	}
}
