/**
 * 
 */
package net.frontlinesms.camel.smslib;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultProducer;

/**
 * @author Alex Anderson
 */
public class SmslibProducer extends DefaultProducer implements SmslibServiceUser {
	private final SmslibService smslibService;
	
	public SmslibProducer(SmslibEndpoint endpoint, SmslibService smslibService) {
		super(endpoint);
		this.smslibService = smslibService;
	}

	/** @see Processor#process(Exchange) */
	public void process(Exchange exchange) throws Exception {
		Message in = exchange.getIn();
		assert(in instanceof SmslibCamelMessage);
		this.smslibService.send((SmslibCamelMessage) in);
	}
	
	@Override
	protected void doStart() throws Exception {
		this.smslibService.startFor(this);
		super.doStart();
	}
	
	@Override
	public void stop() throws Exception {
		super.stop();
		this.smslibService.stopFor(this);
	}
}
