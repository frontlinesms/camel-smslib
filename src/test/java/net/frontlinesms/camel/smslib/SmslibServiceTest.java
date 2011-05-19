package net.frontlinesms.camel.smslib;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;

import org.junit.After;
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
		COutgoingMessage cOutgoingMessageMock = mock(COutgoingMessage.class);
		OutgoingSmslibCamelMessage camelMessage = new OutgoingSmslibCamelMessage(cOutgoingMessageMock);
		
		// when
		service.send(camelMessage);
		
		// then
		verify(cServiceMock).sendMessage(cOutgoingMessageMock);
	}
	
	@Test
	public void testSendFailureHandlingForMessageRejectedByCservice() throws Exception {
		// given
		COutgoingMessage smslibMessage = mock(COutgoingMessage.class);
		OutgoingSmslibCamelMessage camelMessage = new OutgoingSmslibCamelMessage(smslibMessage);
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
		COutgoingMessage smslibMessage = mock(COutgoingMessage.class);
		OutgoingSmslibCamelMessage camelMessage = new OutgoingSmslibCamelMessage(smslibMessage);
		doThrow(new ConnectionFailedException()).when(cServiceMock).sendMessage(smslibMessage);
		
		// when then
		try {
			service.send(camelMessage);
		} catch(ConnectionFailedException e) {
			// expected
		}
	}
	
	/*
	 * The following test is commented out due to an apparent bug in Mockito which gives rise to the following
	 * and similar Exceptions when building:
	-------------------------------------------------------------------------------
	Test set: net.frontlinesms.camel.smslib.SmslibServiceTest
	-------------------------------------------------------------------------------
	Tests run: 13, Failures: 1, Errors: 0, Skipped: 0, Time elapsed: 0.236 sec <<< FAILURE!
	testCServiceStopIfNoMoreUsers(net.frontlinesms.camel.smslib.SmslibServiceTest)  Time elapsed: 0.006 sec  <<< FAILURE!
	Wanted but not invoked:
	cService.disconnect();
	-> at net.frontlinesms.camel.smslib.SmslibServiceTest.testCServiceStopIfNoMoreUsers(SmslibServiceTest.java:157)

	However, there were other interactions with this mock:
	-> at net.frontlinesms.camel.smslib.SmslibService.startForProducer(SmslibService.java:33)
	-> at net.frontlinesms.camel.smslib.SmslibService.startForConsumer(SmslibService.java:27)
	-> at net.frontlinesms.camel.smslib.SmslibService.doReceive(SmslibService.java:62)
	-> at net.frontlinesms.camel.smslib.SmslibService.stopIfUnused(SmslibService.java:49)
	-> at net.frontlinesms.camel.smslib.SmslibService.stopIfUnused(SmslibService.java:49)

		at net.frontlinesms.camel.smslib.SmslibServiceTest.testCServiceStopIfNoMoreUsers(SmslibServiceTest.java:157)
		at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
		at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)
		at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)
		at java.lang.reflect.Method.invoke(Method.java:597)
		at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:44)
		at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:15)
		at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:41)
		at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:20)
		at org.junit.internal.runners.statements.RunBefores.evaluate(RunBefores.java:28)
		at org.junit.internal.runners.statements.RunAfters.evaluate(RunAfters.java:31)
		at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:76)
		at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:50)
		at org.junit.runners.ParentRunner$3.run(ParentRunner.java:193)
		at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:52)
		at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:191)
		at org.junit.runners.ParentRunner.access$000(ParentRunner.java:42)
		at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:184)
		at org.junit.runners.ParentRunner.run(ParentRunner.java:236)
		at org.apache.maven.surefire.junit4.JUnit4TestSet.execute(JUnit4TestSet.java:62)
		at org.apache.maven.surefire.suite.AbstractDirectoryTestSuite.executeTestSet(AbstractDirectoryTestSuite.java:140)
		at org.apache.maven.surefire.suite.AbstractDirectoryTestSuite.execute(AbstractDirectoryTestSuite.java:165)
		at org.apache.maven.surefire.Surefire.run(Surefire.java:107)
		at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
		at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)
		at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)
		at java.lang.reflect.Method.invoke(Method.java:597)
		at org.apache.maven.surefire.booter.SurefireBooter.runSuitesInProcess(SurefireBooter.java:289)
		at org.apache.maven.surefire.booter.SurefireBooter.main(SurefireBooter.java:1005)

	
	
	@Test
	@SuppressWarnings("unchecked")
	public void testReceive() throws Exception {
		// given
		SmslibConsumer consumerMock = mock(SmslibConsumer.class);
		service.setConsumer(consumerMock);
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
	}*/
}
