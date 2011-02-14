package net.frontlinesms.camel.smslib;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.smslib.CIncomingMessage;
import org.smslib.COutgoingMessage;
import org.smslib.CService;
import org.smslib.CService.MessageClass;

import serial.mock.MockSerial;
import serial.mock.NoSuchPortException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/** Unit test for {@link SmslibService} */
public class SmslibServiceTest {
	CService cServiceMock;
	CServiceFactory cServiceFactory;
	SmslibMessageTranslator translator;
	Log logMock;

	Map<String, Object> parameters;
	String remaining;
	String uri;
	
	SmslibService service;
	
	@Before
	public void setUp() {
		MockSerial.init();

		uri = "asdf";
		remaining = "hjkl";
		parameters = Collections.emptyMap();
		
		cServiceMock = mock(CService.class);
		
		cServiceFactory = mock(CServiceFactory.class);
		when(cServiceFactory.create(uri, remaining, parameters)).thenReturn(cServiceMock);
		
		service = new SmslibService(cServiceFactory, uri, remaining, parameters);

		translator = mock(SmslibMessageTranslator.class);
		service.setTranslator(translator);
		
		logMock = mock(Log.class);
		service.setLog(logMock);
	}
	
	@Test
	public void testCServiceCreation() throws NoSuchPortException {
		verify(cServiceFactory).create(uri, remaining, parameters);
	}
	
	@Test
	public void testCServiceStart() throws Exception {
		// given
		SmslibProducer mockProducer = mock(SmslibProducer.class);
		service.setProducer(mockProducer);
		
		// when
		service.startForProducer();
		
		// then
		verify(cServiceMock).connect();
	}
	
	@Test
	public void testCServiceNoStartIfAlreadyStarted() throws Exception {
		// given
		SmslibProducer mockProducer = mock(SmslibProducer.class);
		service.setProducer(mockProducer);
		when(cServiceMock.isConnected()).thenReturn(true); // TODO this assumes that CService.isConnected() returns true while TRYING to conenct...
		
		// when
		service.startForProducer();
		
		// then
		verify(cServiceMock).connect();
	}
	
	@Test
	public void testCServiceStop() throws Exception {
		// given
		service.setProducer(mock(SmslibProducer.class));
		when(cServiceMock.isConnected()).thenReturn(true);
		
		// when
		service.stopForProducer();
		
		// verify
		verify(cServiceMock).disconnect();
	}
	
	@Test
	public void testCServiceNoStopIfNotStarted() throws Exception {
		// given
		service.setProducer(mock(SmslibProducer.class));
		
		// when
		service.stopForProducer();
		
		// verify
		verify(cServiceMock, never()).disconnect();
	}
	
	@Test
	public void testCServiceNoStopIfOtherUsers() throws Exception {
		// given
		service.setProducer(mock(SmslibProducer.class));
		service.startForProducer();
		service.setConsumer(mock(SmslibConsumer.class));
		service.startForConsumer();
		when(cServiceMock.isConnected()).thenReturn(true);
		
		// when
		service.stopForProducer();
		
		// verify
		verify(cServiceMock, never()).disconnect();
	}
	
	@Test
	public void testCServiceStopIfNoMoreUsers() throws Exception {
		// given
		SmslibProducer mockProducer = mock(SmslibProducer.class);
		service.setProducer(mockProducer);
		service.startForProducer();
		SmslibConsumer mockConsumer = mock(SmslibConsumer.class);
		service.setConsumer(mockConsumer);
		service.startForConsumer();
		when(cServiceMock.isConnected()).thenReturn(true);
		
		// when
		service.stopForProducer();
		verify(cServiceMock, never()).disconnect();
		
		// and
		service.stopForConsumer();
		
		// verify
		verify(cServiceMock).disconnect();
	}
	
	@Test
	public void testMaxOneProducer() throws Exception {
		// given
		service.setProducer(mock(SmslibProducer.class));
		
		// when then
		try {
			service.setProducer(mock(SmslibProducer.class));
			fail("Should not be able to start for more than one producer.");
		} catch(SmslibServiceException ex) {
			// expected
		}
	}
	
	@Test
	public void testMaxOneConsumer() throws Exception {
		// given
		service.setConsumer(mock(SmslibConsumer.class));
		
		// when then
		try {
			service.setConsumer(mock(SmslibConsumer.class));
			fail("Should not be able to start for more than one producer.");
		} catch(SmslibServiceException ex) {
			// expected
		}
	}
	
	@Test
	public void testSend() throws Exception {
		// given
		SmslibCamelMessage smslibCamelMessageMock = mock(SmslibCamelMessage.class);
		COutgoingMessage cOutgoingMessageMock = mock(COutgoingMessage.class);
		when(translator.translateOutgoing(smslibCamelMessageMock)).thenReturn(cOutgoingMessageMock);
		
		// when
		service.send(smslibCamelMessageMock);
		
		// then
		verify(translator).translateOutgoing(smslibCamelMessageMock);
		verify(cServiceMock).sendMessage(cOutgoingMessageMock);
	}
	
	@Test
	public void testSendFailureHandlingForMessageTranslateFailure() throws Exception {
		// given
		SmslibCamelMessage camelMessage = mock(SmslibCamelMessage.class);
		when(translator.translateOutgoing(camelMessage)).thenThrow(new TranslateException());
		
		// when then
		try {
			service.send(camelMessage);
			fail();
		} catch(TranslateException ex) {
			// expected
		}
	}
	
	@Test
	public void testSendFailureHandlingForMessageRejectedByCservice() throws Exception {
		// given
		SmslibCamelMessage camelMessage = mock(SmslibCamelMessage.class);
		COutgoingMessage smslibMessage = mock(COutgoingMessage.class);
		when(translator.translateOutgoing(camelMessage)).thenReturn(smslibMessage);
		doThrow(new MessageRejectedException()).when(cServiceMock).sendMessage(smslibMessage);
		
		// when then
		try {
			service.send(camelMessage);
			fail();
		} catch(MessageRejectedException ex) {
			// expected
		}
	}
	
	@Test
	public void testSendFailureHandlingForCserviceStopped() throws Exception {
		// given
		SmslibCamelMessage camelMessage = mock(SmslibCamelMessage.class);
		COutgoingMessage smslibMessage = mock(COutgoingMessage.class);
		when(translator.translateOutgoing(camelMessage)).thenReturn(smslibMessage);
		doThrow(new ConnectionFailedException()).when(cServiceMock).sendMessage(smslibMessage);
		
		// when then
		try {
			service.send(camelMessage);
		} catch(ConnectionFailedException e) {
			// expected
		}
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testReceive() throws Exception {
		// given
		SmslibConsumer consumerMock = mock(SmslibConsumer.class);
		service.setConsumer(consumerMock);
		service.startForConsumer();
		doAnswer(new Answer() {
			public Object answer(InvocationOnMock inv) {
				LinkedList<CIncomingMessage> messageList =
						(LinkedList<CIncomingMessage>) inv.getArguments()[0];
				for(int i=0; i<3; ++i) messageList.add(mock(CIncomingMessage.class));
				return null;
			}
		}).when(cServiceMock).readMessages(any(LinkedList.class), eq(MessageClass.UNREAD));
		SmslibCamelMessage camelMessage = mock(SmslibCamelMessage.class);
		when(translator.translateIncoming(any(CIncomingMessage.class))).thenReturn(camelMessage);
		
		// when
		service.doReceive();
		
		// then
		verify(consumerMock, times(3)).accept(any(SmslibCamelMessage.class));
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testReceiveWithOneBadMessage() throws Exception {
		// given
		SmslibConsumer consumerMock = mock(SmslibConsumer.class);
		service.setConsumer(consumerMock);
		service.startForConsumer();
		final CIncomingMessage badMessage = mock(CIncomingMessage.class);
		doAnswer(new Answer() {
			public Object answer(InvocationOnMock inv) {
				LinkedList<CIncomingMessage> messageList =
						(LinkedList<CIncomingMessage>) inv.getArguments()[0];
				messageList.add(badMessage);
				for(int i=0; i<3; ++i) messageList.add(mock(CIncomingMessage.class));
				return null;
			}
		}).when(cServiceMock).readMessages(any(LinkedList.class), eq(MessageClass.UNREAD));
		TranslateException translateException = new TranslateException();
		when(translator.translateIncoming(badMessage)).thenThrow(translateException);
		
		// when
		service.doReceive();
		
		// then
		verify(logMock).warn(translateException);
		verify(consumerMock, times(3)).accept(any(SmslibCamelMessage.class));
	}
}
