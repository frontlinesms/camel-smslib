package net.frontlinesms.camel.smslib.integration;

import net.frontlinesms.camel.smslib.SmslibConsumer;
import net.frontlinesms.camel.smslib.SmslibEndpoint;
import net.frontlinesms.camel.smslib.SmslibService;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 */
public class SmslibConsumerIntegrationTest {
	@Test
	public void testConsumption() throws Exception {
		Processor mockProcessor = mock(Processor.class);
		SmslibEndpoint mockEndpoint = mock(SmslibEndpoint.class);
		SmslibService mockSmslibService = mock(SmslibService.class);
		
		SmslibConsumer c = new SmslibConsumer(mockEndpoint , mockSmslibService  , mockProcessor );
		
		Exchange exchange = mock(Exchange.class);
		c.getAsyncProcessor().process(exchange);
	}
}
