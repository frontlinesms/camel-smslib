package net.frontlinesms.camel.smslib;

import org.smslib.COutgoingMessage;

@SuppressWarnings("serial")
class MessageRejectedException extends SmslibServiceException {
	public MessageRejectedException(COutgoingMessage m) {
		super("Failed to send message " + m + " - got refNo " + m.getRefNo());
	}

}
