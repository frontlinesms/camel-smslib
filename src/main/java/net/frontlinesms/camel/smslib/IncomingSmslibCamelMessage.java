package net.frontlinesms.camel.smslib;

import org.smslib.CIncomingMessage;

public class IncomingSmslibCamelMessage extends SmslibCamelMessage<CIncomingMessage> {
	public IncomingSmslibCamelMessage(CIncomingMessage cMessage) {
		super(cMessage);
	}
}
