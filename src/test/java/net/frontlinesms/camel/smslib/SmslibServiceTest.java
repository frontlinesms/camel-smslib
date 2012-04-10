package net.frontlinesms.camel.smslib;

import static net.frontlinesms.camel.smslib.CamelSmslibTestUtils.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import net.frontlinesms.camel.smslib.SmslibService.ReceiveThread;

import org.apache.camel.spi.ExceptionHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.smslib.CIncomingMessage;
import org.smslib.COutgoingMessage;
import org.smslib.CService;
import org.smslib.NotConnectedException;
import org.smslib.service.MessageClass;

import serial.mock.MockSerial;
import serial.NoSuchPortException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/** Unit test for {@link SmslibService} */
public class SmslibServiceTest {
	CService cServiceMock;
	CService cServiceMock2;
	CServiceFactory cServiceFactory;

	Map<String, Object> parameters;
	String remaining;
	String uri;
	
	/** Class under test */
	SmslibService service;
	
	@Before
	public void setUp() {
		MockSerial.init();

		uri = "asdf";
		remaining = "hjkl";
		parameters = new HashMap<String, Object>();

		cServiceMock = mock(CService.class);
		cServiceMock2 = mock(CService.class);
		
		cServiceFactory = mock(CServiceFactory.class);
		when(cServiceFactory.create(uri, remaining, parameters)).thenReturn(cServiceMock, cServiceMock2);
		
		service = new SmslibService(cServiceFactory, uri, remaining, parameters);
	}
	
	@After
	public void tearDown() {
		cServiceMock = null;
		cServiceFactory = null;
		
		parameters = null;
		remaining = null;
		uri = null;
		
		service = null;
	}

	@Test
	public void cServiceShouldBeInitialisedNull() {
		assertNull(service.getCService());
	}
	
	@Test
	public void startingProducerShouldInitialiseCService() throws Exception {
		// when
		service.setProducer(mockProducer());
		service.startForProducer();
		
		// then
		assertEquals(cServiceMock, service.getCService());
	}
	
	@Test
	public void startingConsumerShouldInitialiseCService() throws Exception {
		// when
		service.setConsumer(mockConsumer());
		service.startForConsumer();
		
		// then
		assertEquals(cServiceMock, service.getCService());
	}
	
	@Test
	public void startingConsumerWhenProducerAlreadyStartedShouldNotReinitialiseCservice() throws Exception {
		// given
		service.setProducer(mockProducer());
		service.startForProducer();
		
		// when
		service.setConsumer(mockConsumer());
		service.startForConsumer();

		// then
		verify(cServiceFactory).create(uri, remaining, parameters);
		assertEquals(cServiceMock, service.getCService());
	}
	
	@Test
	public void startingProducerWhenConsumerAlreadyStartedShouldNotReinitialiseCservice() throws Exception {
		// given
		service.setConsumer(mockConsumer());
		service.startForConsumer();
		
		// when
		service.setProducer(mockProducer());
		service.startForProducer();

		// then
		verify(cServiceFactory).create(uri, remaining, parameters);
		assertEquals(cServiceMock, service.getCService());
	}
	
	@Test
	public void startingProducerAfterCserviceDiscardShouldCreateNewCservice() throws Exception {
		// given
		SmslibProducer mockProducer = mockProducer();
		service.setProducer(mockProducer);
		service.startForProducer();
		service.stopForProducer(mockProducer);
		
		// when
		service.setProducer(mockProducer());
		service.startForProducer();
		
		// then
		verify(cServiceFactory, times(2)).create(uri, remaining, parameters);
		assertEquals(cServiceMock2, service.getCService());
	}
	
	@Test
	public void settingConsumerAfterCserviceDiscardShouldCreateNewCservice() throws Exception {
		// given
		SmslibConsumer mockConsumer = mockConsumer();
		service.setConsumer(mockConsumer);
		service.startForConsumer();
		service.stopForConsumer(mockConsumer);
		
		// when
		service.setConsumer(mockConsumer());
		service.startForConsumer();
		
		// then
		verify(cServiceFactory, times(2)).create(uri, remaining, parameters);
		assertEquals(cServiceMock2, service.getCService());
	}
	
	@Test
	public void receiveThreadShouldBeInitialisedNull() throws Exception {
		assertNull(service.getReceiveThread());
	}
	
	public void settingConsumerShouldInitialiseReceiveThread() throws Exception {
		// when
		service.setConsumer(mockConsumer());
		
		// then
		assertNotNull(service.getReceiveThread());
	}
	
	@Test
	public void stoppingConsumerShouldStopReceiveThread() throws Exception {
		// given
		SmslibConsumer mockConsumer = mockConsumer();
		service.setConsumer(mockConsumer);
		service.startForConsumer();
		ReceiveThread receiveThread = service.getReceiveThread();
		assertFalse(receiveThread.isFinished());
		
		// when
		service.stopForConsumer(mockConsumer);
		
		// then
		assertTrue(receiveThread.isFinished());
	}
	
	public void stoppingConsumerShouldDiscardOldReceiveThread() throws Exception {
		// given
		SmslibConsumer mockConsumer = mockConsumer();
		service.setConsumer(mockConsumer);
		service.startForConsumer();
		
		// when
		service.stopForConsumer(mockConsumer);
		
		// then
		assertNull(service.getReceiveThread());
	}
	
	@Test
	public void stoppingConsumerShouldBlockUntilReceiveThreadDies() {
		// TODO we should test this... when we've had more coffee
	}
	
	@Test
	public void stoppingForConsumerShouldDiscardTheConsumer() throws Exception {
		// given
		SmslibConsumer mockConsumer = mockConsumer();
		service.setConsumer(mockConsumer);
		service.startForConsumer();
		
		// when
		service.stopForConsumer(mockConsumer);
		
		// then
		assertNull(service.getConsumer());
	}
	
	@Test
	public void stoppingForProducerShouldDiscardTheProducer() throws Exception {
		// given
		SmslibProducer mockProducer = mockProducer();
		service.setProducer(mockProducer);
		service.startForProducer();
		
		// when
		service.stopForProducer(mockProducer);
		
		// then
		assertNull(service.getProducer());
	}
	
	@Test
	public void testCServiceStart() throws Exception {
		// given
		SmslibProducer mockProducer = mockProducer();
		service.setProducer(mockProducer);
		
		// when
		service.startForProducer();
		
		// then
		verify(cServiceMock).connect();
	}
	
	@Test
	public void testCServiceNoStartIfAlreadyStarted() throws Exception {
		// given
		SmslibProducer mockProducer = mockProducer();
		service.setProducer(mockProducer);
		when(cServiceMock.isConnected()).thenReturn(true); // TODO this assumes that CService.isConnected() returns true while TRYING to conenct...
		
		// when
		service.startForProducer();
		
		// then
		verify(cServiceMock, never()).connect();
	}
	
	@Test
	public void whenProducerAndConsumerBothStoppedCserviceShouldDisconnect() throws Exception {
		// given
		SmslibProducer mockProducer = mockProducer();
		service.setProducer(mockProducer);
		when(cServiceMock.isConnected()).thenReturn(true);
		service.startForProducer();
		
		// when
		service.stopForProducer(mockProducer);
		
		// verify
		verify(cServiceMock).disconnect();
	}
	
	@Test
	public void whenProducerAndConsumerBothStoppedCserviceShouldBeDiscarded() throws Exception {
		// given
		SmslibProducer mockProducer = mockProducer();
		service.setProducer(mockProducer);
		when(cServiceMock.isConnected()).thenReturn(true);
		service.startForProducer();
		
		// when
		service.stopForProducer(mockProducer);
		
		// then
		assertNull(service.getCService());
	}
	
	@Test
	public void testCServiceNoStopIfOtherUsers() throws Exception {
		// given
		SmslibProducer mockProducer = mockProducer();
		service.setProducer(mockProducer);
		service.startForProducer();
		service.setConsumer(mockConsumer());
		service.startForConsumer();
		when(cServiceMock.isConnected()).thenReturn(true);
		
		// when
		service.stopForProducer(mockProducer);
		
		// verify
		verify(cServiceMock, never()).disconnect();
	}

	@Test
	public void testCServiceStopIfNoMoreUsers() throws Exception {
		// given		
		SmslibProducer producer = setupAndStartProducer();
		SmslibConsumer consumer = setupAndStartConsumer();
		when(cServiceMock.isConnected()).thenReturn(true);
		
		// when
		service.stopForProducer(producer);
		verify(cServiceMock, never()).disconnect();
		
		// and
		service.stopForConsumer(consumer);
		
		// verify
		verify(cServiceMock).disconnect();
	}
	
	@Test
	public void testMaxOneProducer() throws Exception {
		// given
		service.setProducer(mockProducer());
		
		// when then
		try {
			service.setProducer(mockProducer());
			fail("Should not be able to start for more than one producer.");
		} catch(SmslibServiceException ex) {
			// expected
		}
	}
	
	@Test
	public void testMaxOneConsumer() throws Exception {
		// given
		service.setConsumer(mockConsumer());
		
		// when then
		try {
			service.setConsumer(mockConsumer());
			fail("Should not be able to start for more than one producer.");
		} catch(SmslibServiceException ex) {
			// expected
		}
	}
	
	@Test
	public void testSend() throws Exception {
		// given
		COutgoingMessage cOutgoingMessageMock = mock(COutgoingMessage.class);
		OutgoingSmslibCamelMessage camelMessage = new OutgoingSmslibCamelMessage(cOutgoingMessageMock);
		setupAndStartProducer();
		
		// when
		service.send(camelMessage);
		
		// then
		verify(cServiceMock).sendMessage(cOutgoingMessageMock);
	}

	@Test
	public void testSendFailureHandlingCmsError() throws Exception {
		// given
		COutgoingMessage smslibMessage = mock(COutgoingMessage.class);
		when(smslibMessage.getRefNo()).thenReturn(-1);
		OutgoingSmslibCamelMessage camelMessage = new OutgoingSmslibCamelMessage(smslibMessage);
		setupAndStartProducer();
		
		// when
		try {
			service.send(camelMessage);
			fail();
		} catch(MessageRejectedException expected) {
			// then exception expected
		}
	}
	
	@Test
	public void testSendFailureHandlingForCserviceStopped() throws Exception {
		// given
		COutgoingMessage smslibMessage = mock(COutgoingMessage.class);
		OutgoingSmslibCamelMessage camelMessage = new OutgoingSmslibCamelMessage(smslibMessage);
		doThrow(new ConnectionFailedException()).when(cServiceMock).sendMessage(smslibMessage);
		setupAndStartProducer();
		
		// when then
		try {
			service.send(camelMessage);
		} catch(ConnectionFailedException e) {
			// expected
		}
	}
	
	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testReceive() throws Exception {
		// given
		SmslibConsumer consumerMock = mockConsumer();
		service.setConsumer(consumerMock);
		inject(service, "cService", cServiceMock);
		doAnswer(new Answer() {
			public Object answer(InvocationOnMock inv) {
				LinkedList<CIncomingMessage> messageList =
						(LinkedList<CIncomingMessage>) inv.getArguments()[0];
				for(int i=0; i<3; ++i) messageList.add(mock(CIncomingMessage.class));
				return null;
			}
		}).when(cServiceMock).readMessages(any(LinkedList.class), eq(MessageClass.ALL));
		
		// when
		service.doReceive();
		// then
		verify(consumerMock, times(3)).accept(any(IncomingSmslibCamelMessage.class));
		
		// when
		reset(consumerMock);
		service.setReceiveMessageClass(MessageClass.UNREAD);
		service.doReceive();
		// then
		verify(consumerMock, never()).accept(any(IncomingSmslibCamelMessage.class));
		
		// when
		reset(consumerMock);
		service.setReceiveMessageClass(MessageClass.ALL);
		service.doReceive();
		// then
		verify(consumerMock, times(3)).accept(any(IncomingSmslibCamelMessage.class));
	}

	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testReceiveUnread() throws Exception {
		// given
		parameters.put("receiveMessageClass", MessageClass.UNREAD);
		service = new SmslibService(cServiceFactory, uri, remaining, parameters);
		
		// given
		SmslibConsumer consumerMock = setupAndStartConsumer();;
		doAnswer(new Answer() {
			public Object answer(InvocationOnMock inv) {
				LinkedList<CIncomingMessage> messageList =
						(LinkedList<CIncomingMessage>) inv.getArguments()[0];
				for(int i=0; i<3; ++i) messageList.add(mock(CIncomingMessage.class));
				return null;
			}
		}).when(cServiceMock).readMessages(any(LinkedList.class), eq(MessageClass.UNREAD));
		
		// when
		service.doReceive();
		// then
		verify(consumerMock, times(3)).accept(any(IncomingSmslibCamelMessage.class));
	}

	@Test
	public void ifStartForProducerFailsThenProducerShouldBeNull() throws Exception {
		// given
		SmslibProducer prod = mockProducer();
		service.setProducer(prod);
		doThrow(new NoSuchPortException(new RuntimeException())).when(cServiceMock).connect();
		
		// when
		try {
			service.startForProducer();
			fail(".startForProducer() should have thrown serial Exception");
		} catch(NoSuchPortException expected) {}
		
		// then
		assertNull(service.getProducer());
		assertFalse(service.isProducerRunning());
	}
	
	@Test
	public void notConnectedExceptionShouldPropogateToExceptionHandler() throws Exception {
		// given
		SmslibConsumer consumer = mockConsumer();
		service.setConsumer(consumer);
		NotConnectedException notConnectedException = new NotConnectedException();
		doThrow(notConnectedException)
				.when(cServiceMock)
				.readMessages(any(LinkedList.class), any(MessageClass.class));
		inject(service, "cService", cServiceMock);
		
		// when
		service.doReceive();
		
		// then
		verify(consumer).handleDisconnection(notConnectedException);
	}
	
//> TEST HELPER METHODS
	private SmslibProducer mockProducer() {
		return mock(SmslibProducer.class);
	}

	private SmslibConsumer mockConsumer() {
		return mock(SmslibConsumer.class);
	}
	
	private SmslibProducer setupAndStartProducer() throws Exception {
		SmslibProducer producer = mockProducer();
		service.setProducer(producer);
		service.startForProducer();
		return producer;
	}
	
	private SmslibConsumer setupAndStartConsumer() throws Exception {
		SmslibConsumer consumer = mockConsumer();
		service.setConsumer(consumer);
		service.startForConsumer();
		return consumer;
	}
}
