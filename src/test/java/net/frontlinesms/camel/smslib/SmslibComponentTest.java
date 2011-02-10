/**
 * 
 */
package net.frontlinesms.camel.smslib;

import static org.junit.Assert.*;

import java.util.Map;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultComponent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Junit test for {@link SmslibComponent}.
 * @author Alex Anderson
 */
public class SmslibComponentTest {
	private SmslibComponent component;
	
	@Before
	public void setUp() {
		component = new SmslibComponent(new DefaultCamelContext());
	}
	
	@After
	public void tearDown() {
		component = null;
	}
	
	@Test
	public void testCreateSerialEndpoint() throws Exception {
		Endpoint e = component.createEndpoint("smslib://serial/COM5");
		assertTrue(e instanceof SmslibSerialEndpoint);
	}
	
	@Test
	public void testCreateHttpEndpoint() throws Exception {
		Endpoint e = component.createEndpoint("smslib://http/Clickatell");
		assertTrue(e instanceof SmslibHttpEndpoint);
	}
	
	public void testCreateBadEndpoint() throws Exception {
		try {
			component.createEndpoint("smslib://whatever");
			fail();
		} catch(RuntimeException ex) {
			// expected this exception
		}
	}
}
