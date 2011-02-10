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
	public static final String URI_PROTOCOL = "smslib://";
	private static final String ENDPOINT_TYPE_HTTP = "http";
	private static final String ENDPOINT_TYPE_SERIAL = "serial";
	
	public SmslibComponent(CamelContext context) {
		super(context);
	}
	
	/** @see DefaultComponent#createEndpoint(String, String, Map) */
	@Override
	protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
		assert(uri.startsWith(remaining));
		assert(remaining.startsWith(URI_PROTOCOL));
		
		if(isSerialAddress(remaining)) {
			return getSerialEndpoint(uri, remaining, parameters);
		} else if(isHttpAddress(remaining)) {
			return getHttpEndpoint(uri, remaining, parameters);
		} else {
			throw new RuntimeException("Unable to create endpoint for uri: " + uri);
		}
	}

	private Endpoint getSerialEndpoint(String uri, String remaining,
			Map<String, Object> parameters) {
		// TODO Auto-generated method stub
		return null;
	}

	private Endpoint getHttpEndpoint(String uri, String remaining,
			Map<String, Object> parameters) {
		// TODO Auto-generated method stub
		return null;
	}

	private boolean isHttpAddress(String remaining) {
		return getEndpointType(remaining).equals(ENDPOINT_TYPE_HTTP);
	}

	private boolean isSerialAddress(String remaining) {
		return getEndpointType(remaining).equals(ENDPOINT_TYPE_SERIAL);
	}

	private Object getEndpointType(String remaining) {
		int slash = remaining.indexOf('/');
		return slash == -1 ? remaining : remaining.substring(0, slash);
	}
}
