package serial.mock;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MockSerialTest {
	@Before
	public void setUp() {
		// TODO this init is only required because of the way CommPortIdentifier is mocked.  If there was a
		// common interface which serial.CommPortIdentifier and serial.mock.CommPortIdentifier inherited, we
		// could mock that instead, and have much less to worry about :¬(  perhaps... there may be 
		// unforeseen complications
		MockSerial.init();
	}
	
	@After
	public void tearDown() {
		MockSerial.clearIdentifiers();
	}
	
	@Test
	public void testIdentifierAccessors() {
		serial.CommPortIdentifier a = mock(serial.CommPortIdentifier.class);
		MockSerial.setIdentifier("a", a);
		assertEquals(a, MockSerial.getIdentifier("a"));
	}
	
	@Test
	public void testMissingIdentifierAccessors() {
		assertNull(MockSerial.getIdentifier("a"));
	}
}
