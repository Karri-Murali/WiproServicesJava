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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.MalformedURLException;
import java.net.SocketException;

public class HttpService {

	private static final Logger LOGGER = Logger.getLogger(HttpService.class.getName());

	private static final int CONNECT_TIMEOUT = 5000;
	private static final int READ_TIMEOUT = 5000;

	public static void fetchDataUsingHttpURLConnection(String API_URL) throws IOException {
		HttpURLConnection connection = null;
		BufferedReader in = null;

		try {
			URL url = new URL(API_URL);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(CONNECT_TIMEOUT);
			connection.setReadTimeout(READ_TIMEOUT);

			int responseCode = connection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String inputLine;
				StringBuilder response = new StringBuilder();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}

				LOGGER.log(Level.INFO, "Data: {0}", response.toString());
			} else {
				LOGGER.log(Level.WARNING, "GET request failed with response code: {0}", responseCode);
			}
		} catch (SocketException e) {
			throw new IOException("Connection reset error", e);
		} catch (MalformedURLException e) {
			LOGGER.log(Level.SEVERE, "Invalid URL format: {0}", e.toString());
			throw e;
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "I/O error: {0}", e.getMessage());
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
				LOGGER.log(Level.SEVERE, "Error closing resources: {0}", e.getMessage());
			}
		}
	}

	public static void fetchDataUsingHttpClient(String API_URL) throws MalformedURLException {
		try {
			HttpClient client = HttpClient.newBuilder().connectTimeout(java.time.Duration.ofMillis(CONNECT_TIMEOUT))
					.build();

			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(API_URL))
					.timeout(java.time.Duration.ofMillis(READ_TIMEOUT)).build();

			CompletableFuture<HttpResponse<String>> response = client.sendAsync(request,
					HttpResponse.BodyHandlers.ofString());

			response.thenApply(HttpResponse::body).thenAccept(body -> LOGGER.log(Level.INFO, "Products: {0}", body))
					.exceptionally(e -> {
						LOGGER.log(Level.SEVERE, "Error during async HTTP call: {0}", e.getMessage());
						return null;
					}).join();

		} catch (IllegalArgumentException e) {
			LOGGER.log(Level.SEVERE, "Invalid URI: {0}", e.getMessage());
			throw e;
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "An unexpected error occurred: {0}", e.getMessage());
			throw e;
		}
	}

	public static void fetchHomePageWithHeaders(String G_URL) throws Exception {
		try {
			HttpClient client = HttpClient.newBuilder().connectTimeout(java.time.Duration.ofMillis(CONNECT_TIMEOUT))
					.build();

			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(G_URL)).header("User-Agent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
					.header("Accept-Language", "en-US,en;q=0.5").header("Accept", "text/html")
					.timeout(java.time.Duration.ofMillis(READ_TIMEOUT)).build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() == 200) {
				LOGGER.log(Level.INFO, "Google Page HTML:");
				LOGGER.log(Level.INFO, response.body());
			} else {
				LOGGER.log(Level.WARNING, "Failed to fetch page. Status Code: {0}", response.statusCode());
			}

		} catch (IOException | InterruptedException e) {
			LOGGER.log(Level.SEVERE, "Error fetching Google page: {0}", e.getMessage());
			throw e;
		}
	}

	public static String parseResponse(String response) {

		return response.trim();
	}
}
