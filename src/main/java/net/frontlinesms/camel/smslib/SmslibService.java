package net.frontlinesms.camel.smslib;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SmslibService {
	private final Set<SmslibServiceUser> users = Collections.synchronizedSet(new HashSet<SmslibServiceUser>());

	public SmslibService(String uri, String remaining, Map<String, Object> parameters) {
		// TODO Auto-generated constructor stub
	}

	public void startFor(SmslibServiceUser user) {
		users.add(user);
	}

	public void stopFor(SmslibServiceUser user) {
		users.remove(user);
	}

	public void send(SmslibCamelMessage message) {
		// TODO Auto-generated method stub
	}
}
