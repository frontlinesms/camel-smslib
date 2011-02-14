package net.frontlinesms.camel.smslib.integration;

import static org.junit.Assert.*;

import net.frontlinesms.camel.smslib.SmslibEndpoint;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

/** Integration tests relating to {@link SmslibEndpoint} */
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
	
	@Test
	public void testProducerSingletonism() throws Exception {
		// given
		Endpoint endpoint = context.getEndpoint("smslib://asdf");
		endpoint.createProducer();
		
		// when then
		try {
			endpoint.createProducer();
			fail();
		} catch(Exception e) {
			// expected
		}
	}
	
	public void testConsumerSingletonism() throws Exception {
		// given
		Endpoint endpoint = context.getEndpoint("smslib://asdf");
		Processor mockProcessor = mock(Processor.class);
		endpoint.createConsumer(mockProcessor);
		Processor anotherMockProcessor = mock(Processor.class);
		
		// when then
		try {
			endpoint.createConsumer(anotherMockProcessor);
			fail("Should only be able to create a single consumer.");
		} catch(Exception e) {
			// expected
		}
	}
}
