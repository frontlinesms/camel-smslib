///**
// * 
// */
//package net.frontlinesms.camel.smslib;
//
//import static org.junit.Assert.*;
//
//import org.apache.camel.Endpoint;
//import org.apache.camel.impl.DefaultCamelContext;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//
///**
// * Junit test for {@link SmslibComponent}.
// * @author Alex Anderson
// */
//public class SmslibComponentTest {
//	private SmslibComponent component;
//	
//	@Before
//	public void setUp() {
//		component = new SmslibComponent(new DefaultCamelContext());
//	}
//	
//	@After
//	public void tearDown() {
//		component = null;
//	}
//	
//	@Test
//	public void testCreateSerialEndpoint() throws Exception {
//		Endpoint e = component.createEndpoint("smslib://serial/COM5?direction=send&options=a,b,c");
//		assertTrue(e instanceof SmslibSerialEndpoint);
//	}
//	
//	@Test
//	public void testCreateHttpEndpoint() throws Exception {
//		Endpoint e = component.createEndpoint("smslib://http/provider/username?direction=receive&options=a,b,c");
//		assertTrue(e instanceof SmslibHttpEndpoint);
//	}
//
//	@Test
//	public void testCreateMultipleEndpointsWithSameDetails() throws Exception {
//		SmslibEndpoint send = (SmslibEndpoint) component.createEndpoint("smslib://http/provider/username?direction=receive&options=a,b,c");
//		assertTrue(send.isSendingEnabled());
//		assertFalse(send.isReceivingEnabled());
//		
//		SmslibEndpoint receive = (SmslibEndpoint) component.createEndpoint("smslib://http/provider/username?direction=send&options=a,b,c");
//		assertTrue(send == receive);
//		assertTrue(receive.isSendingEnabled());
//		assertTrue(receive.isReceivingEnabled());
//		assertTrue(send.isSendingEnabled());
//		
//	}
//	
//	public void testCreateBadEndpoint() throws Exception {
//		try {
//			component.createEndpoint("smslib://whatever?options=a,b,c");
//			fail();
//		} catch(RuntimeException ex) {
//			// expected this exception
//		}
//	}
//}
