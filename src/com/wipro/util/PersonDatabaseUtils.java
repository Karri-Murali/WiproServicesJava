package com.wipro.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.wipro.person.BusinessAddress;
import com.wipro.person.EmailAddress;
import com.wipro.person.IAddress;
import com.wipro.person.PermanentAddress;
import com.wipro.person.Person;

public class PersonDatabaseUtils {

	private static final String DB_URL = "jdbc:mysql://localhost:3306/Murali";
	private static final String DB_USERNAME = "root";
	private static final String DB_PASSWORD = "Pavan@8641";

	public static boolean savePersonsToDatabase(List<Person> persons, Connection connection) throws SQLException {
		String personSQL = "INSERT INTO Person (first_name, last_name) VALUES (?, ?)";
		String addressSQL = "INSERT INTO Address (person_id, address_type, address_details) VALUES (?, ?, ?)";

		try (PreparedStatement personStatement = connection.prepareStatement(personSQL,
				Statement.RETURN_GENERATED_KEYS);
				PreparedStatement addressStatement = connection.prepareStatement(addressSQL);) {
			connection.setAutoCommit(false);

			for (Person person : persons) {

				personStatement.setString(1, person.getFirstName());
				personStatement.setString(2, person.getLastName());
				personStatement.executeUpdate();

				try (ResultSet generatedKeys = personStatement.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						int personId = generatedKeys.getInt(1);

						for (IAddress address : person.getAddresses()) {
							addressStatement.setInt(1, personId);
							addressStatement.setString(2, address.getAddressType());
							addressStatement.setString(3, address.getAddressDetails());
							addressStatement.addBatch();
						}

						addressStatement.executeBatch();
					} else {
						throw new SQLException("Failed to retrieve person ID.");
					}
				}
			}

			connection.commit();
			return true;

		} catch (SQLException e) {
			if (connection != null) {
				try {
					connection.rollback();
				} catch (SQLException rollbackEx) {
					e.addSuppressed(rollbackEx);
				}
			}
			throw e;
		}
	}

	public static List<Person> readPersonsFromDatabase() throws SQLException {
		Connection connection = null;
		PreparedStatement personStatement = null;
		PreparedStatement addressStatement = null;
		ResultSet personResultSet = null;
		ResultSet addressResultSet = null;

		List<Person> persons = new ArrayList<>();

		try {

			Class.forName("com.mysql.cj.jdbc.Driver");

			connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
			String personSQL = "SELECT * FROM Person";
			personStatement = connection.prepareStatement(personSQL);
			personResultSet = personStatement.executeQuery();

			String addressSQL = "SELECT * FROM Address WHERE person_id = ?";
			addressStatement = connection.prepareStatement(addressSQL);

			while (personResultSet.next()) {
				int personId = personResultSet.getInt("id");
				String firstName = personResultSet.getString("first_name");
				String lastName = personResultSet.getString("last_name");

				List<IAddress> addresses = new ArrayList<>();

				addressStatement.setInt(1, personId);
				addressResultSet = addressStatement.executeQuery();

				while (addressResultSet.next()) {
					String addressType = addressResultSet.getString("address_type");
					String addressDetails = addressResultSet.getString("address_details");
					// System.out.println(addressType + " " + addressDetails);
					String[] addressArguments = addressDetails.split(", ");
					switch (addressType) {
					case "Permanent Address":
						addresses.add(new PermanentAddress(addressArguments[0], addressArguments[1],
								addressArguments[2], addressArguments[3], addressArguments[4]));
						break;
					case "Business Address":
						addresses.add(new BusinessAddress(addressArguments[0], addressArguments[1], addressArguments[2],
								addressArguments[3], addressArguments[4]));
						break;
					case "Email":
						addresses.add(new EmailAddress(addressDetails));
						break;
					}
				}

				persons.add(new Person(firstName, lastName, addresses));
			}

		} catch (ClassNotFoundException e) {
			throw new SQLException("MySQL Driver not found. Ensure the JAR is added to your classpath.", e);
		} finally {
			if (addressResultSet != null)
				addressResultSet.close();
			if (personResultSet != null)
				personResultSet.close();
			if (addressStatement != null)
				addressStatement.close();
			if (personStatement != null)
				personStatement.close();
			if (connection != null)
				connection.close();
		}

		return persons;
	}

}
