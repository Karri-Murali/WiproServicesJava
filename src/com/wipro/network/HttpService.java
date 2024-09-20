package com.wipro.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.net.MalformedURLException;

public class HttpService {

	// private static final String API_URL =
	// "http://jsonplaceholder.typicode.com/posts";
	// private static final String GOOGLE_URL = "http://www.google.com";

	public static void fetchDataUsingHttpURLConnection(String API_URL) throws IOException {
		HttpURLConnection connection = null;
		BufferedReader in = null;

		try {
			URL url = new URL(API_URL);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");

			int responseCode = connection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String inputLine;
				StringBuilder response = new StringBuilder();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}

				System.out.println("Data: " + response.toString());
			} else {
				System.out.println("GET request failed with response code: " + responseCode);
			}
		} catch (MalformedURLException e) {
			System.err.println("Invalid URL format: " + e.toString());
			throw e;
		} catch (IOException e) {
			System.err.println("I/O error: " + e.getMessage());
			throw e;
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (connection != null) {
					connection.disconnect();
				}
			} catch (IOException e) {
				System.err.println("Error closing resources: " + e.getMessage());
			}
		}
	}

	public static void fetchDataUsingHttpClient(String API_url) throws MalformedURLException {
		try {
			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(API_url)).build();

			CompletableFuture<HttpResponse<String>> response = client.sendAsync(request,
					HttpResponse.BodyHandlers.ofString());

			response.thenApply(HttpResponse::body).thenAccept(body -> System.out.println("Products: " + body))
					.exceptionally(e -> {
						System.err.println("Error during async HTTP call: " + e.getMessage());
						return null;
					}).join();

		} catch (IllegalArgumentException e) {
			System.err.println("Invalid URI: " + e.getMessage());
			throw e;
		} catch (Exception e) {
			System.err.println("An unexpected error occurred: " + e.getMessage());
			throw e;
		}
	}

	public static void fetcheHomePageWithHeaders(String G_URL) throws Exception {
		try {
			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(G_URL)).header("User-Agent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
					.header("Accept-Language", "en-US,en;q=0.5").header("Accept", "text/html").build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			System.out.println("Google Page HTML:");
			System.out.println(response.body());

		} catch (IOException | InterruptedException e) {
			System.err.println("Error fetching Google page: " + e.getMessage());
			throw e;
		}
	}
}
