/**
 * 
 */
package net.frontlinesms.camel.smslib;

import java.util.Map;
import org.apache.camel.CamelContext;

import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultEndpoint;

/**
 * @author Alex Anderson
 */
public class SmslibEndpoint extends DefaultEndpoint {
	private final SmslibService smslibService;
	
	SmslibEndpoint(String uri, CamelContext camelContext, String remaining, Map<String, Object> parameters) {
		super(uri, camelContext);
		smslibService = new SmslibService(new CServiceFactory(), uri, remaining, parameters);
	}

	public SmslibConsumer createConsumer(Processor processor) throws Exception {
		// FIXME discussion on #camel IRC suggests that throwing an exception here would be incorrect, and
		// instead the instance of Consumer should be returned
		SmslibConsumer consumer = new SmslibConsumer(this, smslibService, processor);
		smslibService.setConsumer(consumer);
		return consumer;
	}

	public SmslibProducer createProducer() throws Exception {
		// FIXME discussion on #camel IRC suggests that throwing an exception here would be incorrect, and
		// instead the instance of Producer should be returned
		SmslibProducer producer = new SmslibProducer(this, smslibService);
		smslibService.setProducer(producer);
		return producer;
	}

	public boolean isSingleton() {
		return true; // only instance of SmslibEndpoint per URI, please
	}
	
	@Override
	public boolean isLenientProperties() {
		return true;
	}
}
