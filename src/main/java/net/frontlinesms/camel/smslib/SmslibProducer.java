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
class SmslibProducer extends DefaultProducer implements SmslibServiceProducer {
	private final SmslibService smslibService;
	
	public SmslibProducer(SmslibEndpoint endpoint, SmslibService smslibService) {
		super(endpoint);
		this.smslibService = smslibService;
	}

	/** @see Processor#process(Exchange) */
	public void process(Exchange exchange) throws Exception {
		Message in = exchange.getIn();
		assert(in instanceof OutgoingSmslibCamelMessage);
		this.smslibService.send((OutgoingSmslibCamelMessage) in);
	}
	
	@Override
	protected void doStart() throws Exception {
		this.smslibService.startForProducer();
		super.doStart();
	}
	
	@Override
	public void stop() throws Exception {
		super.stop();
		this.smslibService.stopForProducer();
	}
}
