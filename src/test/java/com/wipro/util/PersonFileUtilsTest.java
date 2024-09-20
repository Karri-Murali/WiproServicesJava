package test.java.com.wipro.util;

import com.wipro.person.*;
import com.wipro.util.PersonFileUtils;
import com.wipro.exception.InvalidFormatStringException;
import com.wipro.exception.InvalidPersonInfoException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PersonFileUtilsTest {

	private static final String TEST_FILE = "/Users/karrimurali/Documents/NGA/Persons";
	private List<Person> persons;

	@BeforeEach
	void setUp() throws IOException {
		persons = new ArrayList<>();
		IAddress permAddress = new PermanentAddress("A", "A", "A", "A", "123123");
		IAddress busAddress = new BusinessAddress("A", "A", "A", "A", "123123");
		IAddress email = new EmailAddress("karri@gmail.com");
		Person person = new Person("Murali", "K", List.of(permAddress, busAddress, email));
		persons.add(person);
		Files.deleteIfExists(Paths.get(TEST_FILE));
	}

	@Test
	void testSavePersonsToFileSuccess() throws InvalidFormatStringException, InvalidPersonInfoException {
		PersonFileUtils.savePersonsToFile(persons, TEST_FILE);
		assertTrue(Files.exists(Paths.get(TEST_FILE)));
	}

	@Test
	void testReadPersonDetailsThrowsUnknownAddressTypeException() throws IOException {

		Files.write(Paths.get(TEST_FILE), "Murali | KK | Unknown Address | 123 Main St".getBytes());

		InvalidFormatStringException exception = assertThrows(InvalidFormatStringException.class,
				() -> PersonFileUtils.readPersonDetails(TEST_FILE));

		assertEquals("Unknown address type: Unknown Address", exception.getMessage());
	}

	@Test
	void testSavePersonsToFileThrowsInvalidFormatStringException() {
		Person personWithoutAddress = new Person("Murali", "K", new ArrayList<>());
		persons.add(personWithoutAddress);

		InvalidPersonInfoException exception = assertThrows(InvalidPersonInfoException.class,
				() -> PersonFileUtils.savePersonsToFile(persons, TEST_FILE));

		assertEquals("Person must contain at least one address.", exception.getMessage());
	}

	@Test
	void testReadPersonDetailsSuccess() throws InvalidFormatStringException, IOException, InvalidPersonInfoException {

		PersonFileUtils.savePersonsToFile(persons, TEST_FILE);
		List<Person> readPersons = PersonFileUtils.readPersonDetails(TEST_FILE);
		assertEquals(1, readPersons.size());
		Person readPerson = readPersons.get(0);
		assertEquals("Murali", readPerson.getFirstName());
		assertEquals("K", readPerson.getLastName());
		assertEquals(3, readPerson.getAddresses().size());

	}

	@Test
	void testReadPersonDetailsThrowsIOException() throws InvalidFormatStringException, IOException {
		assertThrows(IOException.class, () -> PersonFileUtils.readPersonDetails("non_existent_file.txt"));
	}

	@Test
	void testInvalidEmailAddressThrowsException() {
		Exception exception = assertThrows(IllegalArgumentException.class,
				() -> new EmailAddress("invalid-email-format"));
		assertEquals("Invalid email format: invalid-email-format", exception.getMessage());
	}

	@Test
	void testInvalidUSZipCodeThrowsException() {
		Exception exception = assertThrows(IllegalArgumentException.class,
				() -> new PermanentAddress("A", "A", "A", "US", "12345"));
		assertEquals("Invalid ZIP code format for US: 12345. Must be in the format 545 564.", exception.getMessage());
	}

	@Test
	void testInvalidNonUSZipCodeThrowsException() {
		Exception exception = assertThrows(IllegalArgumentException.class,
				() -> new PermanentAddress("A", "A", "A", "India", "1234"));
		assertEquals("Invalid pinCode format: 1234. Must contain exactly 6 digits for countries other than the US.",
				exception.getMessage());
	}

	@Test
	void testValidUSZipCode() {
		PermanentAddress permAddress = new PermanentAddress("A", "A", "A", "US", "123 456");
		assertEquals("123 456", permAddress.getPinCode());
	}

	@Test
	void testValidNonUSZipCode() {
		PermanentAddress permAddress = new PermanentAddress("A", "A", "A", "India", "123456");
		assertEquals("123456", permAddress.getPinCode());
	}

	@Test
	void testInvalidBusinessAddressZipCodeThrowsException() {
		Exception exception = assertThrows(IllegalArgumentException.class,
				() -> new BusinessAddress("A", "A", "A", "India", "12345"));
		assertEquals("Invalid PIN Code format for your country", exception.getMessage());
	}

	@Test
	void testBusinessAddressWithValidUSPinCode() {
		BusinessAddress busAddress = new BusinessAddress("Street", "City", "State", "US", "123 456");
		assertEquals("123 456", busAddress.getPinCode());
	}

	@Test
	void testValidBusinessAddressZipCode() {
		BusinessAddress busAddress = new BusinessAddress("A", "A", "A", "India", "123456");
		assertEquals("123456", busAddress.getPinCode());
	}
}
