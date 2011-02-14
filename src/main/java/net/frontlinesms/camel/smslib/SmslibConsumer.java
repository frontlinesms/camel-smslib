/**
 * 
 */
package net.frontlinesms.camel.smslib;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultConsumer;

/**
 * @author aga
 *
 */
public class SmslibConsumer extends DefaultConsumer implements SmslibServiceProducer {
	private final SmslibService smslibService;
	
	public SmslibConsumer(SmslibEndpoint endpoint, SmslibService smslibService, Processor processor) {
		super(endpoint, processor);
		this.smslibService = smslibService;
	}
	
	@Override
	protected void doStart() throws Exception {
		this.smslibService.startForConsumer();
		super.doStart();
	}
	
	@Override
	public void stop() throws Exception {
		super.stop();
		this.smslibService.stopForConsumer();
	}
	
	public void accept(SmslibCamelMessage message) {
		Exchange exchange = getEndpoint().createExchange();
		exchange.setIn(message);
		Processor processor = this.getProcessor();
		
		try {
			processor.process(exchange);
		} catch(Exception e) {
			getExceptionHandler().handleException("Exception thrown when calling processor.process()", exchange, e);
		}
	}
}
