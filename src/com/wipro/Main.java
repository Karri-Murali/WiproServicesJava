package com.wipro;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.wipro.exception.InvalidFormatStringException;
import com.wipro.exception.InvalidPersonInfoException;
import com.wipro.person.BusinessAddress;
import com.wipro.person.EmailAddress;
import com.wipro.person.IAddress;
import com.wipro.person.PermanentAddress;
import com.wipro.person.Person;
import com.wipro.util.PersonDatabaseUtils;
import com.wipro.util.PersonFileUtils;

public class Main {

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter Your Details: ");
		String c;

		try {
			List<Person> persons = new ArrayList<>();

			do {
				System.out.println("Enter First Name:");
				String firstName = scanner.nextLine();

				System.out.println("Enter Last Name:");
				String lastName = scanner.nextLine();

				List<IAddress> addresses = new ArrayList<>();
				String choice;

				do {
					System.out.println(
							"Enter 'P' for Permanent Address, 'B' for Business Address, 'E' for Email Address, or 'Q' to quit:");
					choice = scanner.nextLine().toUpperCase();

					switch (choice) {
					case "P":
						System.out.println("Enter Street:");
						String streetP = scanner.nextLine();
						System.out.println("Enter City:");
						String cityP = scanner.nextLine();
						System.out.println("Enter State:");
						String stateP = scanner.nextLine();
						System.out.println("Enter Country:");
						String countryP = scanner.nextLine();
						System.out.println("Enter PIN Code:");
						String pinP = scanner.nextLine();
						try {
							addresses.add(new PermanentAddress(streetP, cityP, stateP, countryP, pinP));
						} catch (IllegalArgumentException e) {
							System.err.println("Invalid Permanent Address: " + e.getMessage());
						}
						break;
					case "B":
						System.out.println("Enter Street:");
						String streetB = scanner.nextLine();
						System.out.println("Enter City:");
						String cityB = scanner.nextLine();
						System.out.println("Enter State:");
						String stateB = scanner.nextLine();
						System.out.println("Enter Country:");
						String countryB = scanner.nextLine();
						System.out.println("Enter Pin Code:");
						String pinB = scanner.nextLine();
						addresses.add(new BusinessAddress(streetB, cityB, stateB, countryB, pinB));
						break;
					case "E":
						System.out.println("Enter Email:");
						String email = scanner.nextLine();
						addresses.add(new EmailAddress(email));
						break;
					case "Q":
						break;
					default:
						System.out.println("Invalid choice. Please try again.");
						break;
					}
				} while (!choice.equals("Q"));

				persons.add(new Person(firstName, lastName, addresses));

				System.out.println("Press 'A' to Add Another Person Details or 'Q' to Exit");
				c = scanner.nextLine().toUpperCase();
			} while (!c.equals("Q"));

			String filePath = "/Users/karrimurali/Documents/NGA/Persons";
			System.out.print("Are you sure want to Add Details into Application : Y / N :");
			String confirm = scanner.nextLine().toUpperCase();
			if (!confirm.equals("Y")) {
				System.out.println("Thank you");
				System.exit(0);
			}
			try {
				PersonFileUtils.savePersonsToFile(persons, filePath);
				System.out.println("Person details saved successfully.");

				List<Person> readPersons = PersonFileUtils.readPersonDetails(filePath);
				System.out.println("Person details read from file: " + readPersons.size());

				readPersons.forEach(p -> System.out.println(p.toString()));

				String targetLastName = "Murali";
				List<Person> filteredPersons = readPersons.stream()
						.filter(p -> p.getLastName().equalsIgnoreCase(targetLastName)).collect(Collectors.toList());

				System.out.println("Persons with last name Murali:");
				filteredPersons.forEach(p -> System.out.println(p.toString()));

				List<String> fullNames = readPersons.stream().map(p -> p.getFirstName() + " " + p.getLastName())
						.collect(Collectors.toList());

				System.out.println("List of Full Names:");
				fullNames.forEach(System.out::println);

				System.out.println("Addresses of all persons:");
				readPersons.forEach(p -> p.getAddresses().forEach(a -> System.out.println(a.getAddressDetails())));

				int addressCount = readPersons.stream().flatMap(p -> p.getAddresses().stream()).reduce(0,
						(count, address) -> count + 1, Integer::sum);
				System.out.println("Total number of addresses: " + addressCount);

				//PersonDatabaseUtils.savePersonsToDatabase(persons);
				System.out.println("Person details saved to the database.");

				List<Person> people = PersonDatabaseUtils.readPersonsFromDatabase();

				System.out.println("Person Reading from Database : ");
				for (Person p : people) {
					System.out.print(p.getFirstName() + " " + p.getLastName() + "|");
					p.getAddresses().forEach(
							address -> System.out.print(address.getAddressType() + "|" + address.getAddressDetails()));
					System.out.println();
				}

				HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

				server.createContext("/files", new FileHandler());
				server.start();

				System.out.println("Server started on port 8080");

			} catch (IOException | InvalidFormatStringException | SQLException e) {
				System.err.println("Error: " + e.toString());
			}

		} catch (InvalidPersonInfoException e) {
			e.toString();
		}

		finally {
			scanner.close();
			System.out.println("Scanner closed.");
		}
	}
	
	static class FileHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange exchange) throws IOException {
			String filePath = "/Users/karrimurali/Documents/NGA";
			String requestPath = exchange.getRequestURI().getPath().replace("/files/", "");
			String fullPath = filePath + "/" + requestPath;
			if (Files.exists(Paths.get(fullPath))) {
				byte[] fileBytes = Files.readAllBytes(Paths.get(fullPath));
				exchange.sendResponseHeaders(200, fileBytes.length);
				OutputStream outputStream = exchange.getResponseBody();
				outputStream.write(fileBytes);
				outputStream.close();
			} else {
				String response = "404 (File Not Found)\n";
				exchange.sendResponseHeaders(404, response.length());
				exchange.getResponseBody().write(response.getBytes());
				exchange.getResponseBody().close();
			}
		}
	}

}
