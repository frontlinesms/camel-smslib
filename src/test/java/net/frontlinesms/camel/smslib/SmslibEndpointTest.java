package net.frontlinesms.camel.smslib;

import java.util.Collections;
import java.util.Map;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static org.powermock.api.mockito.PowerMockito.verifyNew;

/** JUnit tests for {@link SmslibEndpoint} */
@RunWith(PowerMockRunner.class)
@PrepareForTest(SmslibEndpoint.class)
public class SmslibEndpointTest {
	private static final String URI = "smslib://COM1";
	private static final String REMAINING = "";
	private static final Map<String, Object> NO_PARAMS = Collections.emptyMap();
	
	SmslibService mockSmslibService;
	CServiceFactory mockCServiceFactory;
	
	SmslibEndpoint endpoint;
	
	@Before
	public void setUp() throws Exception {
		mockCServiceFactory = mock(CServiceFactory.class);
		whenNew(CServiceFactory.class).withNoArguments()
				.thenReturn(mockCServiceFactory);

		mockSmslibService = mock(SmslibService.class);
		whenNew(SmslibService.class)
				.withArguments(mockCServiceFactory, URI, REMAINING, NO_PARAMS)
				.thenReturn(mockSmslibService);
		
		endpoint = new SmslibEndpoint(URI, null, REMAINING, NO_PARAMS);
	}
	
	@Test
	public void testConstructor() throws Exception {
		// then
		verifyNew(SmslibService.class)
				.withArguments(mockCServiceFactory, URI, REMAINING, NO_PARAMS);
	}
	
	@Test
	public void testCreateConsumer() throws Exception {
		// given
		SmslibConsumer mockConsumer = mock(SmslibConsumer.class);
		Processor mockProcessor = mock(Processor.class);
		whenNew(SmslibConsumer.class)
				.withArguments(endpoint, mockSmslibService, mockProcessor )
				.thenReturn(mockConsumer);
		
		// when 
		Consumer c = endpoint.createConsumer(mockProcessor);
		
		// then
		assertSame(mockConsumer, c);
	}
	
	@Test
	public void testCreateProducer() throws Exception {
		// given
		SmslibProducer mockProducer = mock(SmslibProducer.class);
		whenNew(SmslibProducer.class)
				.withArguments(endpoint, mockSmslibService)
				.thenReturn(mockProducer);
		
		// when 
		Producer p = endpoint.createProducer();
		
		// then
		assertSame(mockProducer, p);
	}
	
	@Test
	public void testPropertiesAreLenient() {
		assertTrue(endpoint.isLenientProperties());
	}
}
