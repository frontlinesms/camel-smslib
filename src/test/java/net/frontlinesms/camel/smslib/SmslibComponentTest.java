/**
 * 
 */
package net.frontlinesms.camel.smslib;

import org.apache.camel.CamelContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 * JUnit tests for {@link SmslibComponent}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(SmslibComponent.class)
public class SmslibComponentTest {
	SmslibComponent comp;
	
	@Before
	public void setUp() {
		CamelContext mockContext = mock(CamelContext.class);
		comp = new SmslibComponent(mockContext);
	}
	
	@Test
	public void testCreateEndpoint() throws Exception {
		// given
		SmslibEndpoint mockEndpoint = mock(SmslibEndpoint.class);
		when(mockEndpoint.isLenientProperties()).thenReturn(true);
		whenNew(SmslibEndpoint.class).withArguments(anyString(), anyString(), anyMap()).thenReturn(mockEndpoint);
		
		// when
		comp.createEndpoint("smslib://whatever?baud=1");

		// then
		verifyNew(SmslibEndpoint.class);
	}
}
