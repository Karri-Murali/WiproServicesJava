package test.java.com.wipro.network;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.Test;

import com.wipro.network.HttpService;

public class HttpConnectionTest {

	@Test
	public void testFetchGooglePageWithHeaders() throws IOException, InterruptedException {
		String googleUrl = "http://www.google.com";

		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(googleUrl)).header("User-Agent", "Mozilla/5.0")
				.header("Accept-Language", "en-US,en;q=0.5").build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

		int responseCode = response.statusCode();
		assertEquals(200, responseCode);
	}

	@Test
	public void testFetchGooglePageWithHeaders_IOError1() {
		String invalidGoogleUrl = "http://invalid.google.com";

		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(invalidGoogleUrl))
				.header("User-Agent", "Mozilla/5.0").header("Accept-Language", "en-US,en;q=0.5").build();

		assertThrows(IOException.class, () -> client.send(request, HttpResponse.BodyHandlers.ofString()));
	}

	@Test
	public void testFetchDataUsingHttpURLConnection_Success1() {
		assertDoesNotThrow(() -> HttpService.fetchDataUsingHttpClient("https://thedot.wipro.com/"));
	}

	@Test
	public void testFetchDataUsingHttpURLConnection_MalformedURLException() {
		assertThrows(MalformedURLException.class,
				() -> HttpService.fetchDataUsingHttpURLConnection("htp://www.google.com"));
	}

	@Test
	public void testFetchDataUsingHttpClient_Success() {
		assertDoesNotThrow(() -> HttpService.fetchDataUsingHttpClient("https://jsonplaceholder.typicode.com/posts"));
	}

	@Test
	public void testFetchDataUsingHttpClient_IllegalArgumentException() {
		assertThrows(IllegalArgumentException.class, () -> HttpService.fetchDataUsingHttpClient("htp://invalid-uri"));
	}

	@Test
	public void testFetchDataUsingHttpURLConnection_Success() throws IOException {
		assertDoesNotThrow(
				() -> HttpService.fetchDataUsingHttpURLConnection("http://jsonplaceholder.typicode.com/posts"));
	}

	@Test
	public void testFetchDataUsingHttpURLConnection_InvalidUrlIOException() {
		assertThrows(IOException.class,
				() -> HttpService.fetchDataUsingHttpURLConnection("http://invalid.invalidurl.com"));
	}

	@Test
	public void testFetchGooglePageWithHeaders_Success() {
		assertDoesNotThrow(() -> HttpService.fetchHomePageWithHeaders("http://www.google.com"));
	}

	@Test
	public void testFetchGooglePageWithHeaders_InvalidUrlIOException() {
		assertThrows(IOException.class, () -> HttpService.fetchHomePageWithHeaders("http://invalid.google.com"));
	}

	@Test
	public void testFetchGooglePageWithHeaders_IOError() {
		String invalidGoogleUrl = "http://invalid.google.com";
		assertThrows(IOException.class, () -> HttpService.fetchHomePageWithHeaders(invalidGoogleUrl));
	}
}
