/**
 * 
 */
package net.frontlinesms.camel.smslib;

import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultConsumer;

/**
 * @author aga
 *
 */
public class SmslibConsumer extends DefaultConsumer implements SmslibServiceUser {
	private final SmslibService smslibService;
	
	public SmslibConsumer(SmslibEndpoint endpoint, SmslibService smslibService, Processor processor) {
		super(endpoint, processor);
		this.smslibService = smslibService;
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
