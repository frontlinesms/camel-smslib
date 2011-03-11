package net.frontlinesms.camel.smslib;

import java.util.LinkedList;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.smslib.CIncomingMessage;
import org.smslib.COutgoingMessage;
import org.smslib.CService;
import org.smslib.CService.MessageClass;

public class SmslibService {
	private Log log;
	
	private final CService cService;
	private SmslibMessageTranslator translator;
	
	private SmslibProducer producer;
	private SmslibConsumer consumer;
	
	private boolean consumerRunning;
	private boolean producerRunning;

	public SmslibService(CServiceFactory cServiceFactory, String uri, String remaining, Map<String, Object> parameters) {
		this.cService = cServiceFactory.create(uri, remaining, parameters);
		this.translator = new SmslibMessageTranslator();
	}
	
	public void setTranslator(SmslibMessageTranslator translator) {
		this.translator = translator;
	}

	public synchronized void startForConsumer() throws Exception {
		assert(consumer != null);
		consumerRunning = true;
		new ReceiveThread().start();
		cService.connect();
	}

	public synchronized void startForProducer() throws Exception {
		assert(producer != null);
		producerRunning = true;
		cService.connect();
	}
	
	public synchronized void stopForProducer() throws Exception {
		assert(producer != null);
		producerRunning = false;
		stopFor(producer);
	}
	
	public synchronized void stopForConsumer() throws Exception {
		assert(consumer != null);
		consumerRunning = false;
		stopFor(consumer);
	}

	private synchronized void stopFor(SmslibServiceProducer user) throws Exception {
		if(cService.isConnected()
				&& !producerRunning
				&& !consumerRunning) {
			cService.disconnect();
		}
	}

	public void send(SmslibCamelMessage message) throws Exception {
		COutgoingMessage cOutgoingMessage = this.translator.translateOutgoing(message);
		this.cService.sendMessage(cOutgoingMessage);
	}

	public void doReceive() throws Exception {
		LinkedList<CIncomingMessage> messageList = new LinkedList<CIncomingMessage>();
		this.cService.readMessages(messageList, MessageClass.UNREAD);
		for(CIncomingMessage m : messageList) {
			try {
				this.consumer.accept(translator.translateIncoming(m));
			} catch(TranslateException ex) {
				log.warn(ex);
			}
		}
	}

	public void setLog(Log log) {
		this.log = log;
	}

	public synchronized SmslibProducer getProducer() {
		return producer;
	}
	public synchronized void setProducer(SmslibProducer producer) throws SmslibServiceException {
		if(this.producer != null) {
			throw new SmslibServiceException("Producer already set.");
		}
		this.producer = producer;
	}

	public synchronized SmslibConsumer getConsumer() {
		return consumer;
	}
	public synchronized void setConsumer(SmslibConsumer consumer) throws SmslibServiceException {
		if(this.consumer != null) {
			throw new SmslibServiceException("Consumer already set.");
		}
		this.consumer = consumer;
	}
	
	class ReceiveThread extends Thread {
		public void run() {
			while(consumerRunning) {
				try {
					doReceive();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					Thread.sleep(30000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
