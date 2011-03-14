package net.frontlinesms.camel.smslib;

import org.smslib.COutgoingMessage;

public class OutgoingSmslibCamelMessage extends SmslibCamelMessage<COutgoingMessage> {
	OutgoingSmslibCamelMessage(COutgoingMessage cMessage) {
		super(cMessage);
	}
}
