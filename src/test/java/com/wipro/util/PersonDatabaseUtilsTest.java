package test.java.com.wipro.util;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import com.wipro.person.BusinessAddress;
import com.wipro.person.EmailAddress;
import com.wipro.person.IAddress;
import com.wipro.person.PermanentAddress;
import com.wipro.person.Person;
import com.wipro.util.PersonDatabaseUtils;

class PersonDatabaseUtilsTest {

	@Mock
	private Connection mockConnection;
	@Mock
	PreparedStatement mockPersonStatement;
	@Mock
	private PreparedStatement mockAddressStatement;
	@Mock
	private ResultSet mockGeneratedKeys;
	@Mock
	private ResultSet mockPersonResultSet;
	@Mock
	private ResultSet mockAddressResultSet;

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);
		when(mockConnection.prepareStatement(any(String.class))).thenReturn(mockPersonStatement);
	}

	@Test
	    public void testReadPersonsFromDatabase_Success() throws Exception {

	        when(mockPersonResultSet.next()).thenReturn(true).thenReturn(false);
	        when(mockPersonResultSet.getInt("id")).thenReturn(1);
	        when(mockPersonResultSet.getString("first_name")).thenReturn("Murali");
	        when(mockPersonResultSet.getString("last_name")).thenReturn("K");
	 
	        when(mockConnection.prepareStatement("SELECT * FROM Address WHERE person_id = ?"))
	            .thenReturn(mockAddressStatement);
	        when(mockAddressStatement.executeQuery()).thenReturn(mockAddressResultSet);
	        
	        when(mockAddressResultSet.next()).thenReturn(true).thenReturn(false);
	        when(mockAddressResultSet.getString("address_type")).thenReturn("Permanent Address");
	        when(mockAddressResultSet.getString("address_details")).thenReturn("Koneru Street, PPM, AP, IN, 535525");
	 	    
	        List<Person> persons = PersonDatabaseUtils.readPersonsFromDatabase();
	 
	        assertNotNull(persons);
	 
	        Person person = persons.get(0);
	        assertEquals("Murali", person.getFirstName());
	        assertEquals("K", person.getLastName());
	 
	        IAddress address = person.getAddresses().get(0);
	        assertTrue(address instanceof PermanentAddress);
	        assertEquals("Koneru Street, PPM, AP, IN, 535525", address.getAddressDetails());
	    }

	@Test
	void testSavePersonsToDatabase() throws SQLException {

		Connection mockConnection = mock(Connection.class);
		PreparedStatement mockPersonStatement = mock(PreparedStatement.class);
		PreparedStatement mockAddressStatement = mock(PreparedStatement.class);
		ResultSet mockGeneratedKeys = mock(ResultSet.class);

		List<IAddress> addresses = new ArrayList<>();
		addresses.add(new PermanentAddress("Koneru Street", "PPM", "AP", "IN", "535525"));
		addresses.add(new BusinessAddress("WIPRO CIRCLE", "HYD", "TG", "IN", "500019"));
		addresses.add(new EmailAddress("karri@wipro.com"));

		List<Person> persons = new ArrayList<>();
		persons.add(new Person("Murali", "K", addresses));

		when(mockConnection.prepareStatement(anyString(), anyInt())).thenReturn(mockPersonStatement);
		when(mockConnection.prepareStatement(anyString())).thenReturn(mockAddressStatement);
		when(mockPersonStatement.getGeneratedKeys()).thenReturn(mockGeneratedKeys);
		when(mockGeneratedKeys.next()).thenReturn(true);
		when(mockGeneratedKeys.getInt(1)).thenReturn(1);

		boolean result = PersonDatabaseUtils.savePersonsToDatabase(persons, mockConnection);

		verify(mockPersonStatement, times(1)).setString(1, "Murali");
		verify(mockPersonStatement, times(1)).setString(2, "K");
		verify(mockPersonStatement, times(1)).executeUpdate();

		verify(mockAddressStatement, times(3)).setInt(1, 1);
		verify(mockAddressStatement, times(1)).setString(2, "Permanent Address");
		verify(mockAddressStatement, times(1)).setString(2, "Business Address");
		verify(mockAddressStatement, times(1)).setString(2, "Email");

		verify(mockAddressStatement, times(3)).addBatch();
		verify(mockAddressStatement, times(1)).executeBatch();

		assertTrue(result);
	}

}
