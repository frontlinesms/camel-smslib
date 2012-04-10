package net.frontlinesms.camel.smslib;

import static org.mockito.Mockito.*;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.smslib.NotConnectedException;

public class SmslibConsumerTest {
	Processor mockProcessor;
	SmslibEndpoint mockEndpoint;
	SmslibService mockSmslibService;
	SmslibConsumer c;
	
	@Before
	public void setUp() {
		mockProcessor = mock(Processor.class);
		mockEndpoint = mock(SmslibEndpoint.class);
		mockSmslibService = mock(SmslibService.class);
		c = new SmslibConsumer(mockEndpoint, mockSmslibService, mockProcessor);
	}
	
	@After
	public void tearDown() {
		mockProcessor = null;
		mockEndpoint = null;
		mockSmslibService = null;
		c = null;
	}
	
	@Test
	public void testDoStartShouldStartService() throws Exception {
		// when
		c.doStart();

		// then
		verify(mockSmslibService).startForConsumer();
	}
	
	@Test
	public void testDoStopShouldStopService() throws Exception {
		// when
		c.stop();
		
		// then
		verify(mockSmslibService).stopForConsumer(c);
	}
	
	@Test
	public void testAccept() throws Exception {
		// given
		IncomingSmslibCamelMessage message = mock(IncomingSmslibCamelMessage.class);
		Exchange exchange = mock(Exchange.class);
		when(mockEndpoint.createExchange()).thenReturn(exchange);
		
		// when
		c.accept(message);
		
		// then
		verify(exchange).setIn(message);
		verify(mockProcessor).process(exchange);
	}
	
	public void testHandleDisconnection() throws Exception {
		// given
		NotConnectedException exception = mock(NotConnectedException.class);
		Exchange exchange = mock(Exchange.class);
		when(mockEndpoint.createExchange()).thenReturn(exchange);
		
		// when
		c.handleDisconnection(exception);
		
		// then
		verify(exchange).setException(exception);
		verify(mockProcessor).process(exchange);
	}
}
