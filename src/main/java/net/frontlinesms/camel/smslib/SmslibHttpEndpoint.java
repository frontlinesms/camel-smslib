/**
 * 
 */
package net.frontlinesms.camel.smslib;

import org.apache.camel.Consumer;
import org.apache.camel.Endpoint;
import org.apache.camel.IsSingleton;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;

/**
 * @author Alex Anderson
 */
public class SmslibHttpEndpoint extends DefaultEndpoint {
	/** @see Endpoint#createConsumer(Processor) */
	public Consumer createConsumer(Processor p) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/** @see Endpoint#createProducer() */
	public Producer createProducer() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/** @see IsSingleton#isSingleton() */
	public boolean isSingleton() {
		// This is NOT a singleton
		return false;
	}
}
