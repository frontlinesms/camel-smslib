/**
 * 
 */
package net.frontlinesms.camel.smslib;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultConsumer;

/**
 * TODO this should be an idempotent consumer (https://camel.apache.org/idempotent-consumer.html) to swallow any repeated messages
 * recovered from a device (e.g. if messages are not deleted off the device).
 * TODO this should also be a polling consumer, and then {@link SmslibService} would not need to run a thread.
 * @author aga
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
	
	public void accept(IncomingSmslibCamelMessage message) {
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
