package net.frontlinesms.camel.smslib;

import static org.mockito.Mockito.*;
import org.mockito.ArgumentMatcher;

import org.apache.camel.*;
import org.junit.*;
import org.smslib.COutgoingMessage;

/** JUnit tests for {@link SmslibProducer} */
public class SmslibProducerTest {
	SmslibEndpoint mockEndpoint;
	SmslibService mockSmslibService;
	SmslibProducer p;
	
	@Before
	public void setUp() {
		mockEndpoint = mock(SmslibEndpoint.class);
		mockSmslibService = mock(SmslibService.class);
		p = new SmslibProducer(mockEndpoint, mockSmslibService);
	}
	
	@After
	public void tearDown() {
		mockEndpoint = null;
		mockSmslibService = null;
		p = null;
	}
	
	@Test
	public void testDoStartShouldStartService() throws Exception {
		p.doStart();
		verify(mockSmslibService).startForProducer();
	}
	
	@Test
	public void testDoStopShouldStopService() throws Exception {
		p.stop();
		verify(mockSmslibService).stopForProducer(p);
	}
	
	@Test
	public void testProcessing() throws Exception {
		// given
		COutgoingMessage smslibMessage = mock(COutgoingMessage.class);
		Exchange sendMessageExchange = exchangeWithInBody(smslibMessage);
		
		// when
		p.process(sendMessageExchange);
		
		// then
		verify(mockSmslibService).send(messageWithBody(smslibMessage));
	}
	
	private Exchange exchangeWithInBody(COutgoingMessage inBody) {
		Message m = mock(Message.class);
		when(m.getBody()).thenReturn(inBody);
		
		Exchange x = mock(Exchange.class);
		when(x.getIn()).thenReturn(m);

		return x;
	}

	private OutgoingSmslibCamelMessage messageWithBody(final COutgoingMessage m) {
		return argThat(new ArgumentMatcher<OutgoingSmslibCamelMessage>() {
			@Override
			public boolean matches(Object argument) {
				return ((OutgoingSmslibCamelMessage) argument).getBody() == m;
			}
		});
	}
}
