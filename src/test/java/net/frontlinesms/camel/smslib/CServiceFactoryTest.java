/**
 * 
 */
package net.frontlinesms.camel.smslib;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.smslib.CService;

/**
 * Unit tests for {@link CServiceFactory}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(CServiceFactory.class)
public class CServiceFactoryTest {
	private static final Map<String, Object> NO_PARAMS = Collections.emptyMap();
	
	private CServiceFactory factory;
	
	@Before
	public void setUp() {
		factory = new CServiceFactory();
	}

	@Test
	public void testDefaultParameterValues() throws Exception {
		// when
		whenNew(CService.class).withArguments("COM1", 57600,
				"", "", "").thenReturn(mock(CService.class));
		factory.create("smslib://COM1", "COM1", NO_PARAMS);
		
		// then
		verifyNew(CService.class).withArguments("COM1", 57600, "", "", "");
	}
	
	@Test
	public void testParameterPassing() throws Exception {
		// when
		whenNew(CService.class).withArguments(anyString(), anyInt(),
				anyString(), anyString(), anyString()).thenReturn(mock(CService.class));
		factory.create("smslib://COM1", "COM1", new ParameterMap(
				"baud", "9600",
				"manufacturer", "Nokia",
				"model", "1200",
				"handler", "Nokia_S40_3ed",
				"pin", null));
		
		// then
		verifyNew(CService.class).withArguments("COM1", 9600, "Nokia", "1200", "Nokia_S40_3ed");
	}
}

@SuppressWarnings("serial")
class ParameterMap extends HashMap<String, Object> {
	ParameterMap(String... keysAndValues) {
		assert((keysAndValues.length & 1) == 0);
		for (int i = 0; i < keysAndValues.length; i+=2) {
			put(keysAndValues[i], keysAndValues[i+1]);
		}
	}
}