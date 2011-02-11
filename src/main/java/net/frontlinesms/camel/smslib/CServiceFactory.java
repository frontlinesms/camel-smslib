/**
 * 
 */
package net.frontlinesms.camel.smslib;

import java.util.Map;

import org.smslib.CService;

class CServiceFactory {
	private static final int DEFAULT_BAUD = 57600;

	public CService create(String uri, String remaining, Map<String, Object> parameters) {
		return new CService(remaining,
				getBaud(parameters),
				getString(parameters, "manufacturer"),
				getString(parameters, "model"),
				getString(parameters, "handler"));
	}
	
	private String getString(Map<String, Object> parameters, String key) {
		String m  = (String) parameters.get(key);
		return m == null ? "" : m;
	}

	private int getBaud(Map<String, Object> parameters) {
		String baud = (String) parameters.get("baud");
		try {
			return Integer.parseInt(baud);
		} catch(Exception e) {
			return DEFAULT_BAUD;
		}
	}
}
