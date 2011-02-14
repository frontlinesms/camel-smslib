package net.frontlinesms.camel.smslib;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.smslib.COutgoingMessage;
import org.smslib.CService;

public class SmslibService {
	private final CService cService;
	private final Set<SmslibServiceUser> users = Collections.synchronizedSet(new HashSet<SmslibServiceUser>());
	private SmslibMessageTranslator translator;

	public SmslibService(CServiceFactory cServiceFactory, String uri, String remaining, Map<String, Object> parameters) {
		this.cService = cServiceFactory.create(uri, remaining, parameters);
		this.translator = new SmslibMessageTranslator();
	}
	
	public void setTranslator(SmslibMessageTranslator translator) {
		this.translator = translator;
	}

	public void startFor(SmslibServiceUser user) throws Exception {
		users.add(user);
		
		cService.connect();
	}

	public void stopFor(SmslibServiceUser user) throws Exception {
		users.remove(user);
		
		if(cService.isConnected() && users.isEmpty()) {
			cService.disconnect();
		}
	}

	public void send(SmslibCamelMessage message) throws Exception {
		COutgoingMessage cOutgoingMessage = this.translator.translateOutgoing(message);
		this.cService.sendMessage(cOutgoingMessage);
	}
}
