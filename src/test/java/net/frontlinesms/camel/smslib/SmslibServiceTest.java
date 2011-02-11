package net.frontlinesms.camel.smslib;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import serial.*;
import serial.mock.MockSerial;

import static org.mockito.Mockito.*;

/** Unit test for {@link SmslibService} */
public class SmslibServiceTest {
	CommPortIdentifier com1mock;
	
	@Before
	public void setUp() {
		MockSerial.init();
		com1mock = mock(CommPortIdentifier.class);
		MockSerial.setIdentifier("COM1", com1mock);
	}
	
	@Test
	public void testCServiceCreation() throws NoSuchPortException {
		Map<String, Object> parameters = null;
		String remaining = null;
		String uri = null;
		
		SmslibService s = new SmslibService(uri, remaining, parameters);
		verify(CommPortIdentifier.getPortIdentifier("COM1"));
	}
}
