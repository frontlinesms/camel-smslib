package net.frontlinesms.camel.smslib.integration;

import static org.junit.Assert.*;
import net.frontlinesms.camel.smslib.SmslibComponent;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.Before;
import org.junit.Test;

public class SmslibEndpointIntegrationTest {
	private CamelContext context;
	
	@Before
	public void setUp() {
		context = new DefaultCamelContext();
	}
	
	@Test
	public void testSingletonism() {
		Endpoint a1 = context.getEndpoint("smslib://asdf");
		Endpoint a2 = context.getEndpoint("smslib://asdf");
		assertTrue(a1 == a2);
		
		Endpoint b = context.getEndpoint("smslib://bcde");
		assertFalse(a1 == b);
	}
}
