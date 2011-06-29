package net.frontlinesms.camel.smslib;

import org.smslib.CMessage;

abstract class SmslibCamelMessage<E extends CMessage> extends org.apache.camel.impl.DefaultMessage implements org.apache.camel.Message {
	private final E cMessage;
	
	SmslibCamelMessage(E cMessage) {
		this.cMessage = cMessage;
		this.setBody(cMessage);
	}
	
	public E getCMessage() {
		return this.cMessage;
	}

	public String toString() {
		return "SMSLib Camel message wrapping: " + cMessage;
	}
}
