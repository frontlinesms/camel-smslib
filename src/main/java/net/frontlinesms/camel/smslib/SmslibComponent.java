/**
 * 
 */
package net.frontlinesms.camel.smslib;

import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;

/**
 * @author Alex Anderson
 */
public class SmslibComponent extends DefaultComponent {
	public SmslibComponent() {}
	
	public SmslibComponent(CamelContext context) {
		super(context);
	}
	
	/** @see DefaultComponent#createEndpoint(String, String, Map) */
	@Override
	protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
		return new SmslibEndpoint(uri, remaining, parameters);
	}
}
