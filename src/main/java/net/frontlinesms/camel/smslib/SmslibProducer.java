/**
 * 
 */
package net.frontlinesms.camel.smslib;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.smslib.COutgoingMessage;

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
		OutgoingSmslibCamelMessage msg = new OutgoingSmslibCamelMessage((COutgoingMessage) exchange.getIn().getBody());
		this.smslibService.send(msg);
	}
	
	@Override
	protected void doStart() throws Exception {
		super.doStart();
		this.smslibService.startForProducer();
	}
	
	@Override
	public void doStop() throws Exception {
		super.doStop();
		this.smslibService.stopForProducer();
	}
}
